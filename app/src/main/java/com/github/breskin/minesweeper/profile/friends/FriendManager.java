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

    private static final int MAX_NUMBER_OF_FRIENDS = 30;

    private static SharedPreferences friendsPreferences;

    private static ReentrantLock friendsLock = new ReentrantLock();
    private static ReentrantLock requestsLock = new ReentrantLock();

    private static ArrayList<Friend> friends = new ArrayList<>();
    private static ArrayList<Friend> friendRequests = new ArrayList<>();

    private static ChildEventListener requestsListener;
    private static ChildEventListener friendAddedListener, friendRemovedListener;


    public static void load(Context context) {
        friendsPreferences = context.getSharedPreferences("friend-list", Context.MODE_PRIVATE);

        if (FirebaseAuth.getInstance().getCurrentUser() == null)
            return;

        requestsLock.lock();
        friendRequests.clear();
        requestsLock.unlock();


        friendsLock.lock();
        friends.clear();

        for (Map.Entry<String, ?> entry : friendsPreferences.getAll().entrySet()) {
            Friend f = new Friend(entry.getValue().toString(), entry.getKey());
            friends.add(f);

            f.update();
        }
        friendsLock.unlock();


        setupRequestsListener();
        setupFriendsChangeListener();
    }

    public static void setupRequestsListener() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (requestsListener != null)
            FirebaseDatabase.getInstance().getReference("users").child(uid).child("friends").child("requests").removeEventListener(requestsListener);

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

        FirebaseDatabase.getInstance().getReference("users").child(uid).child("friends").child("requests").addChildEventListener(requestsListener);
    }

    public static void setupFriendsChangeListener() {
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (friendAddedListener != null)
            FirebaseDatabase.getInstance().getReference("users").child(uid).child("friends").child("toadd").removeEventListener(friendAddedListener);

        friendAddedListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    FirebaseDatabase.getInstance().getReference("users").child(uid).child("friends").child("list").child(dataSnapshot.getKey()).setValue(dataSnapshot.getValue().toString());

                    friendsLock.lock();

                    Friend friend = new Friend(dataSnapshot.getValue().toString(), dataSnapshot.getKey());
                    friends.add(friend);
                    updateFriendName(friend, friend.getDisplayName());

                    friendsLock.unlock();
                    friend.updateStatus();

                    FirebaseDatabase.getInstance().getReference("users").child(uid).child("friends").child("toadd").child(dataSnapshot.getKey()).removeValue();
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

        FirebaseDatabase.getInstance().getReference("users").child(uid).child("friends").child("toadd").addChildEventListener(friendAddedListener);


        if (friendRemovedListener != null)
            FirebaseDatabase.getInstance().getReference("users").child(uid).child("friends").child("toremove").removeEventListener(friendRemovedListener);

        friendRemovedListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    FirebaseDatabase.getInstance().getReference("users").child(uid).child("friends").child("list").child(dataSnapshot.getKey()).removeValue();

                    friendsLock.lock();

                    for (int i = 0; i < friends.size(); i++) {
                        if (friends.get(i).getUid().equals(dataSnapshot.getKey())) {
                            friends.remove(i);
                            break;
                        }
                    }

                    friendsPreferences.edit().remove(dataSnapshot.getKey()).apply();

                    friendsLock.unlock();

                    FirebaseDatabase.getInstance().getReference("users").child(uid).child("friends").child("toremove").child(dataSnapshot.getKey()).removeValue();
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

        FirebaseDatabase.getInstance().getReference("users").child(uid).child("friends").child("toremove").addChildEventListener(friendRemovedListener);
    }

    public static void removeListeners() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (requestsListener != null)
            FirebaseDatabase.getInstance().getReference("users").child(uid).child("friends").child("requests").removeEventListener(requestsListener);

        if (friendAddedListener != null)
            FirebaseDatabase.getInstance().getReference("users").child(uid).child("friends").child("toadd").removeEventListener(friendAddedListener);

        if (friendRemovedListener != null)
            FirebaseDatabase.getInstance().getReference("users").child(uid).child("friends").child("toremove").removeEventListener(friendRemovedListener);
    }

    public static void updateStatuses() {
        friendsLock.lock();
        for (Friend friend : friends)
            friend.updateStatus();
        friendsLock.unlock();
    }

    public static void acceptInvite(final Friend friend) {
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase.getInstance().getReference("users").child(uid).child("friends").child("list").child(friend.getUid()).setValue(friend.getDisplayName());
        FirebaseDatabase.getInstance().getReference("users").child(friend.getUid()).child("friends").child("toadd").child(uid).setValue(UserProfile.getName(), new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                FirebaseDatabase.getInstance().getReference("users").child(uid).child("friends").child("requests").child(friend.getUid()).removeValue();
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
        FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("friends").child("requests").child(friend.getUid()).removeValue();

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

        FirebaseDatabase.getInstance().getReference("users").child(uid).child("friends").child("requests").child(currentUID).setValue(UserProfile.getName());
    }

    public static void removeFriend(final Friend friend) {
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase.getInstance().getReference("users").child(friend.getUid()).child("friends").child("toremove").child(uid).setValue(UserProfile.getName(), new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                FirebaseDatabase.getInstance().getReference("users").child(uid).child("friends").child("list").child(friend.getUid()).removeValue();

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
        FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("friends").child("list").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                friendsLock.lock();

                SharedPreferences.Editor editor = friendsPreferences.edit();
                friends.clear();

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    if (child.exists()) {
                        Friend friend = new Friend(child.getValue().toString(), child.getKey());
                        friends.add(friend);

                        editor.putString(child.getKey(), child.getValue().toString());

                        friend.update();
                    }
                }

                editor.apply();

                friendsLock.unlock();
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
        return friends.size() < MAX_NUMBER_OF_FRIENDS;
    }
}
