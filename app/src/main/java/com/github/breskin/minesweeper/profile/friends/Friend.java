package com.github.breskin.minesweeper.profile.friends;

import android.util.Log;

import androidx.annotation.NonNull;

import com.github.breskin.minesweeper.R;
import com.github.breskin.minesweeper.RenderView;
import com.github.breskin.minesweeper.game.FieldSize;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class Friend {
    private String displayName;
    private String uid;
    private long lastSeen;
    private State state;
    private FieldSize currentField;

    public Friend(String name, String uid) {
        this.displayName = name;
        this.uid = uid;

        this.lastSeen = 0;
        this.state = State.Unknown;
    }

    public String getDisplayName() {
        return (displayName.length() > 0) ? displayName : uid;
    }

    public String getUid() {
        return uid;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public boolean isActive() {
        if (Calendar.getInstance().getTimeInMillis() - lastSeen < 12000)
            return true;

        return false;
    }

    public String getStateText() {
        if (isActive())
            switch (state) {
                case InGame: return RenderView.CONTEXT.getString(R.string.friend_state_in_game, currentField.getVisibleName());

                default: return RenderView.CONTEXT.getString(R.string.friend_state_menu);
            }

        return RenderView.CONTEXT.getString(R.string.friend_state_unknown);
    }

    public void update() {
        updateName();
        updateStatus();
    }

    public void updateName() {
        FirebaseDatabase.getInstance().getReference("users").child(uid).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.getValue().toString();

                    if (!name.equals(displayName)) {
                        displayName = name;

                        FriendManager.updateFriendName(Friend.this, name);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    public void updateStatus() {
        FirebaseDatabase.getInstance().getReference("users").child(uid).child("active").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    DataSnapshot timeSeen = dataSnapshot.child("t"), stateData = dataSnapshot.child("s");

                    if (timeSeen.exists())
                        lastSeen = ((Number)timeSeen.getValue()).longValue();

                    if (stateData.exists()) {
                        String s = stateData.getValue().toString();

                        if (s.equals("m"))
                            Friend.this.state = State.Menu;
                        else if (s.startsWith("g:")) {
                            Friend.this.state = State.InGame;

                            Friend.this.currentField = FieldSize.fromString(s.substring(2));
                        }
                    } else
                        Friend.this.state = State.Unknown;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    public enum State {
        Unknown, Menu, InGame
    }
}
