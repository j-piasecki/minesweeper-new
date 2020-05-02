package com.github.breskin.minesweeper.profile.friends;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.breskin.minesweeper.MainActivity;
import com.github.breskin.minesweeper.R;
import com.github.breskin.minesweeper.RenderView;
import com.github.breskin.minesweeper.profile.UserProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class FriendManager {

    private static SharedPreferences friendsPreferences;

    private static ReentrantLock friendsLock = new ReentrantLock();
    private static ReentrantLock requestsLock = new ReentrantLock();

    private static ArrayList<Friend> friends = new ArrayList<>();
    private static ArrayList<Friend> friendRequests = new ArrayList<>();

    private static ChildEventListener requestsListener;


    public static void load(Context context) {
        friendsPreferences = context.getSharedPreferences("friend-list", Context.MODE_PRIVATE);

        if (FirebaseAuth.getInstance().getCurrentUser() == null)
            return;

        friendsLock.lock();
        requestsLock.lock();

        friends.clear();
        friendRequests.clear();

        for (Map.Entry<String, ?> entry : friendsPreferences.getAll().entrySet()) {
            Friend f = new Friend(entry.getValue().toString(), entry.getKey());
            friends.add(f);

            f.update();
        }

        requestsLock.unlock();
        friendsLock.unlock();


        setupRequestsListener();
    }

    public static void setupRequestsListener() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (requestsListener != null)
            FirebaseDatabase.getInstance().getReference("users").child(uid).child("frequests").removeEventListener(requestsListener);

        requestsListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    requestsLock.lock();
                    friendRequests.add(new Friend(dataSnapshot.getValue().toString(), dataSnapshot.getKey()));
                    requestsLock.unlock();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };

        FirebaseDatabase.getInstance().getReference("users").child(uid).child("frequests").addChildEventListener(requestsListener);
    }

    public static void updateStatuses() {
        friendsLock.lock();
        for (Friend friend : friends)
            friend.updateStatus();
        friendsLock.unlock();
    }

    public static void acceptInvite(final Friend friend) {
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase.getInstance().getReference("users").child(uid).child("friends").child(friend.getUid()).setValue(friend.getDisplayName());
        FirebaseDatabase.getInstance().getReference("users").child(friend.getUid()).child("friends").child(uid).setValue(UserProfile.getName(), new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                FirebaseDatabase.getInstance().getReference("users").child(uid).child("frequests").child(friend.getUid()).removeValue();
            }
        });

        friendsPreferences.edit().putString(friend.getUid(), friend.getDisplayName()).apply();

        requestsLock.lock();
        friendRequests.remove(friend);
        requestsLock.unlock();

        friendsLock.lock();
        friends.add(friend);
        friendsLock.unlock();
    }

    public static void declineInvite(Friend friend) {
        FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("frequests").child(friend.getUid()).removeValue();

        requestsLock.lock();
        friendRequests.remove(friend);
        requestsLock.unlock();
    }

    public static void sendInviteByEmail(String mail) {
        if (mail.equals(FirebaseAuth.getInstance().getCurrentUser().getEmail()))
            return;

        FirebaseDatabase.getInstance().getReference("user2uid").child(mail.substring(0, mail.indexOf("@"))).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    sendInviteByUid(dataSnapshot.getValue().toString());
                } else {
                    ((MainActivity)RenderView.CONTEXT).showToast(RenderView.CONTEXT.getString(R.string.message_email_does_not_exist));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    public static void sendInviteByUid(String uid) {
        String currentUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (uid.equals(currentUID))
            return;

        Friend invited = getInviteFrom(uid);
        if (invited != null) {
            acceptInvite(invited);
            return;
        }

        FirebaseDatabase.getInstance().getReference("users").child(uid).child("frequests").child(currentUID).setValue(UserProfile.getName());
    }

    public static void removeFriend(final Friend friend) {
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase.getInstance().getReference("users").child(friend.getUid()).child("friends").child(uid).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                FirebaseDatabase.getInstance().getReference("users").child(uid).child("friends").child(friend.getUid()).removeValue();

                friendsLock.lock();
                friends.remove(friend);
                friendsLock.unlock();

                friendsPreferences.edit().remove(friend.getUid()).apply();
            }
        });
    }

    public static Friend getInviteFrom(String uid) {
        requestsLock.lock();

        for (Friend friend : friendRequests) {
            if (friend.getUid().equals(uid)) {
                requestsLock.unlock();
                return friend;
            }
        }

        requestsLock.unlock();

        return null;
    }

    public static void syncFriendsWithCloud() {
        FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("friends").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    friendsLock.lock();

                    SharedPreferences.Editor editor = friendsPreferences.edit();

                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        if (child.exists()) {
                            friends.add(new Friend(child.getValue().toString(), child.getKey()));

                            editor.putString(child.getKey(), child.getValue().toString());
                        }
                    }

                    editor.apply();

                    friendsLock.unlock();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    public static void updateFriendName(Friend friend, String newName) {
        friendsPreferences.edit().putString(friend.getUid(), newName).apply();
    }

    public static ReentrantLock getFriendsLock() {
        return friendsLock;
    }

    public static ReentrantLock getRequestsLock() {
        return requestsLock;
    }

    public static ArrayList<Friend> getFriends() {
        return friends;
    }

    public static ArrayList<Friend> getFriendRequests() {
        return friendRequests;
    }

    public static boolean canSendInvites() {
        return friends.size() < 30;
    }
}
