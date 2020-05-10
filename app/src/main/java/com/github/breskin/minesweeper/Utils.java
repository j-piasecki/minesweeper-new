package com.github.breskin.minesweeper;

public class Utils {

    public static String getTimeString(Integer t) {
        if (t == null || t < 0)
            return "-";

        int time = t / 1000;
        int minutes = time / 60;
        time -= minutes * 60;

        return ((minutes < 10) ? "0" : "") + minutes + ":" + ((time < 10) ? "0" : "") +  time;
    }
}
