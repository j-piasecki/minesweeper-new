package com.github.breskin.minesweeper.profile.friends;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class Friend {
    private String displayName;
    private String uid;
    private long lastSeen;

    public Friend(String name, String uid) {
        this.displayName = name;
        this.uid = uid;

        this.lastSeen = 0;
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
        if (Calendar.getInstance().getTimeInMillis() - lastSeen < 10000)
            return true;

        return false;
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
                    lastSeen = ((Number)dataSnapshot.getValue()).longValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }
}
