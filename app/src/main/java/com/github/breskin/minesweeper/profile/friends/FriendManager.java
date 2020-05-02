package com.github.breskin.minesweeper.profile.friends;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class FriendManager {
    private static ReentrantLock friendsLock = new ReentrantLock();
    private static ReentrantLock requestsLock = new ReentrantLock();

    private static ArrayList<Friend> friends = new ArrayList<>();
    private static ArrayList<Friend> friendRequests = new ArrayList<>();


    public static void load(Context context) {
        for (int i = 0; i < 10; i++) {
            friends.add(new Friend("friend no. " + i, "uid no. + " + i));
        }

        for (int i = 0; i < 10; i++) {
            friendRequests.add(new Friend("request no. " + i, "uid no. + " + i));
        }
    }

    public static void acceptInvite(Friend friend) {
        Log.w("A", "accept " + friend.getUid());

        requestsLock.lock();
        friendRequests.remove(friend);
        requestsLock.unlock();
    }

    public static void declineInvite(Friend friend) {
        Log.w("A", "decline " + friend.getUid());

        requestsLock.lock();
        friendRequests.remove(friend);
        requestsLock.unlock();
    }

    public static void sendInvite(String target) {
        if (target.contains("@")) {
            Log.w("A", "invite e-mail: " + target);
        } else if (target.startsWith("u:")) {
            Log.w("A", "invite uid: " + target.substring(2));
        }
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
}
