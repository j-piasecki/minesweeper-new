package com.github.breskin.minesweeper.profile.friends;

import android.util.Log;

public class Friend {
    private String displayName;
    private String uid;
    private long lastSeen;

    public Friend(String name, String uid) {
        this.displayName = name;
        this.uid = uid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getUid() {
        return uid;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public void update() {
        Log.w("A", "Update friend uid: " + uid);
    }
}
