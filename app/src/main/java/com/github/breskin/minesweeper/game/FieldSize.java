package com.github.breskin.minesweeper.game;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.breskin.minesweeper.DataManager;

public class FieldSize implements Comparable {

    public static final FieldSize SMALL = new FieldSize(10, 16, 25);
    public static final FieldSize MEDIUM = new FieldSize(14, 20, 40);
    public static final FieldSize LARGE = new FieldSize(16, 26, 70);

    private int width, height, minesCount;

    public FieldSize(int width, int height, int mines) {
        this.width = width;
        this.height = height;
        this.minesCount = mines;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getMinesCount() {
        return minesCount;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof FieldSize))
            return false;

        FieldSize f = (FieldSize)obj;

        return width == f.width && height == f.height && minesCount == f.minesCount;
    }

    public String getVisibleName() {
        if (this.equals(SMALL)) return DataManager.FIELD_SIZE_SMALL;
        if (this.equals(MEDIUM)) return DataManager.FIELD_SIZE_MEDIUM;
        if (this.equals(LARGE)) return DataManager.FIELD_SIZE_LARGE;

        return this.toString();
    }


    @Override
    public int compareTo(Object o) {
        if (o instanceof FieldSize) {
            FieldSize other = (FieldSize) o;

            if (this.equals(SMALL)) return -1;
            if (other.equals(SMALL)) return 1;

            if (this.equals(MEDIUM) && !other.equals(SMALL)) return -1;
            if (other.equals(MEDIUM) && !this.equals(SMALL)) return 1;

            if (this.equals(LARGE) && !other.equals(SMALL) && !other.equals(MEDIUM)) return -1;
            if (other.equals(LARGE) && !this.equals(SMALL) && !this.equals(MEDIUM)) return 1;

            return this.getValue() - other.getValue();
        }

        return -1;
    }

    private int getValue() {
        return width * height * minesCount;
    }

    @NonNull
    @Override
    public String toString() {
        return width + "x" + height + "x" + minesCount;
    }

    public static FieldSize fromString(String s) {
        if (s == null) return null;

        String[] data = s.split("x");

        try {
            if (data.length == 3) {
                return new FieldSize(Integer.parseInt(data[0]), Integer.parseInt(data[1]), Integer.parseInt(data[2]));
            }
        } catch (Exception e) {
            Log.d("FieldSize", "Wrong argument.");
        }

        return null;
    }
}
