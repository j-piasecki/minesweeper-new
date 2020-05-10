package com.github.breskin.minesweeper.game;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.breskin.minesweeper.DataManager;

public class FieldSize {

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

    @NonNull
    @Override
    public String toString() {
        return width + "x" + height + "x" + minesCount;
    }
}
