package com.github.breskin.minesweeper.profile.friends;

import android.util.Log;

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
        if (Calendar.getInstance().getTimeInMillis() - lastSeen < 7500)
            return true;

        return false;
    }

    public void update() {
        Log.w("A", "Update friend uid: " + uid);
    }
}
