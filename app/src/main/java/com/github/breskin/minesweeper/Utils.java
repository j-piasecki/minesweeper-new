package com.github.breskin.minesweeper;

import android.graphics.Paint;

public class Utils {

    public static String getTimeString(Integer t) {
        if (t == null || t < 0)
            return "-";

        int time = t / 1000;
        int minutes = time / 60;
        time -= minutes * 60;

        return ((minutes < 10) ? "0" : "") + minutes + ":" + ((time < 10) ? "0" : "") +  time;
    }

    public static String trimText(String text, float maxWidth, Paint paint) {
        if (paint.measureText(text) > maxWidth) {
            while (paint.measureText(text + "…")  > maxWidth) {
                text = text.substring(0, text.length() - 1);
            }

            text += "…";
        }

        return text;
    }
}
