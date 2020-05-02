package com.github.breskin.minesweeper.profile.friends;

import android.content.Context;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class FriendManager {
    private static ReentrantLock lock = new ReentrantLock();

    private static ArrayList<Friend> friends = new ArrayList<>();
    private static ArrayList<Friend> friendRequests = new ArrayList<>();


    public static void load(Context context) {
        for (int i = 0; i < 10; i++) {
            friends.add(new Friend("friend no. " + i, "uid no. + " + i));
        }
    }

    public static ReentrantLock getLock() {
        return lock;
    }

    public static ArrayList<Friend> getFriends() {
        return friends;
    }

    public static ArrayList<Friend> getFriendRequests() {
        return friendRequests;
    }
}
