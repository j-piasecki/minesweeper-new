package com.github.breskin.minesweeper.game;

import android.graphics.Canvas;
import android.util.Log;

import java.util.Random;

public class Minefield {

    private Square[][] field;
    private int width, height, minesCount;
    private long seed;
    private boolean minesPlaced = false;

    public Minefield() {

    }

    public void update() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                field[x][y].update();
            }
        }
    }

    public void render(GameLogic logic, Canvas canvas) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                field[x][y].render(logic, canvas);
            }
        }
    }

    public void reveal(GameLogic logic, int x, int y) {
        if (!minesPlaced) {
            placeMines(x, y);
        }

        field[x][y].reveal(logic);
    }

    public void flag(int x, int y) {
        Square square = getSquare(x, y);

        if (square != null) {
            square.flag();
        }
    }

    public Square getSquare(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            return field[x][y];
        }

        return null;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void placeMines(int freeX, int freeY) {
        Random random = new Random();
        int placed = 0;
        while (placed < minesCount) {
            int mineX = random.nextInt(width), mineY = random.nextInt(height);

            if (field[mineX][mineY].getType() != Square.TYPE_MINE) {
                if (!((mineX == freeX && mineY == freeY) || (mineX == freeX - 1 && mineY == freeY) || (mineX == freeX - 1 && mineY == freeY - 1) || (mineX == freeX - 1 && mineY == freeY + 1) || (mineX == freeX && mineY == freeY - 1) ||
                        (mineX == freeX + 1 && mineY == freeY) || (mineX == freeX + 1 && mineY == freeY + 1) || (mineX == freeX + 1 && mineY == freeY - 1) || (mineX == freeX && mineY == freeY + 1))) {
                    field[mineX][mineY].setType(Square.TYPE_MINE);
                    placed++;
                }
            }
        }

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (field[x][y].getType() != Square.TYPE_MINE)
                    field[x][y].setType(getAdjacentMinesCount(x, y));
            }
        }

        minesPlaced = true;
    }

    int getAdjacentMinesCount(int x, int y) {
        int mines = 0;

        if (x > 0) {
            if (field[x - 1][y].getType() == Square.TYPE_MINE) mines++;
            if (y > 0 && field[x - 1][y - 1].getType() == Square.TYPE_MINE) mines++;
            if (y < height - 1 && field[x - 1][y + 1].getType() == Square.TYPE_MINE) mines++;
        }

        if (x < width - 1) {
            if (field[x + 1][y].getType() == Square.TYPE_MINE) mines++;
            if (y > 0 && field[x + 1][y - 1].getType() == Square.TYPE_MINE) mines++;
            if (y < height - 1 && field[x + 1][y + 1].getType() == Square.TYPE_MINE) mines++;
        }

        if (y > 0)
            if (field[x][y - 1].getType() == Square.TYPE_MINE) mines++;

        if (y < height - 1)
            if (field[x][y + 1].getType() == Square.TYPE_MINE) mines++;

        return mines;
    }

    public void init(int width, int height, int mines) {
        init(width, height, mines, System.currentTimeMillis());
    }

    public void init(int width, int height, int mines, long seed) {
        this.field = new Square[width][height];
        this.minesCount = mines;
        this.width = width;
        this.height = height;
        this.seed = seed;

        minesPlaced = false;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                field[x][y] = new Square(x, y);

                field[x][y].setVisiblePosition((x - width * 0.5f) * 4 + width * 0.5f, (y - height) * 2);
            }
        }
    }
}
