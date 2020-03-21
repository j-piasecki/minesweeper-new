package com.github.breskin.minesweeper.game;

import android.graphics.Canvas;
import android.graphics.PointF;

import com.github.breskin.minesweeper.RenderView;

import java.util.ArrayList;
import java.util.Random;

public class Minefield {

    private static final int WIN_ANIMATION_FRAMES_DELAY = 2;

    private Square[][] field;
    private int width, height, minesCount;
    private long seed;
    private boolean minesPlaced = false;
    private boolean winAnimation = false, loseAnimation = false;
    private int animationDelay = 0;

    private ArrayList<Square> overlayQueue;

    public Minefield() {
        overlayQueue = new ArrayList<>();
    }

    public void update(GameLogic logic) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (field[x][y] != null)
                    field[x][y].update();
            }
        }

        if (winAnimation || loseAnimation) {
            updateAnimation(logic);
        }
    }

    public void render(GameLogic logic, Canvas canvas) {
        PointF topLeft = logic.getCamera().calculatePositionFromScreen(new PointF(0, 0));
        PointF bottomRight = logic.getCamera().calculatePositionFromScreen(new PointF(RenderView.VIEW_WIDTH, RenderView.VIEW_HEIGHT));

        overlayQueue.clear();

        int startX = (int)Math.floor(topLeft.x - 1); if (startX < 0) startX = 0;
        int startY = (int)Math.floor(topLeft.y - 1); if (startY < 0) startY = 0;
        int endX = (int)Math.ceil(bottomRight.x + 1); if (endX > logic.getMinefield().getWidth()) endX = logic.getMinefield().getWidth();
        int endY = (int)Math.ceil(bottomRight.y + 1); if (endY > logic.getMinefield().getHeight()) endY = logic.getMinefield().getHeight();

        for (int x = startX; x < endX; x++) {
            for (int y = startY; y < endY; y++) {
                if (field[x][y] != null) {
                    field[x][y].render(logic, canvas);

                    if (field[x][y].hasOverlay())
                        overlayQueue.add(field[x][y]);
                }
            }
        }

        for (Square square : overlayQueue)
            square.drawOverlay(logic, canvas);
    }

    public void reveal(GameLogic logic, int x, int y) {
        reveal(logic, x, y, true);
    }

    public void reveal(GameLogic logic, int x, int y, boolean userGenerated) {
        if (logic.isGamePaused() || x < 0 || y < 0 || x >= width || y >= height) return;

        if (!minesPlaced) {
            placeMines(x, y);
            logic.onGameStarted();
        }

        if (field[x][y].getType() == Square.TYPE_MINE && !field[x][y].isFlagged() && !field[x][y].isRevealed()) {
            if (userGenerated) {
                field[x][y].setTintedRed();
                logic.increaseFlaggedMines();

                field[x][y].reveal(logic);

                logic.onGameLost();
            }
            else {
                field[x][y].setTintedGreen();
                field[x][y].reveal(logic);
            }
        } else {
            field[x][y].reveal(logic);
        }
    }

    public boolean flag(GameLogic logic, int x, int y) {
        if (logic.isGamePaused()) return false;

        Square square = getSquare(x, y);

        if (square != null && !square.isRevealed()) {
            square.flag(logic);

            if (square.isFlagged())
                logic.increaseFlaggedMines();
            else
                logic.decreaseFlaggedMines();
            return true;
        }

        return false;
    }

    public Square getSquare(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            return field[x][y];
        }

        return null;
    }

    public boolean isGameWon() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (field[x][y].getType() != Square.TYPE_MINE && !field[x][y].isRevealed())
                    return false;
            }
        }

        return true;
    }

    public void updateAnimation(GameLogic logic) {
        if (animationDelay > 0) {
            animationDelay--;
            return;
        } else {
            animationDelay = WIN_ANIMATION_FRAMES_DELAY;
        }

        for (int y = 0; y < height; y++) {
            boolean stop = false;

            for (int x = 0; x < width; x++) {
                if (field[x][y].getType() == Square.TYPE_MINE && !field[x][y].isRevealed()) {
                    if (field[x][y].isFlagged()) field[x][y].flag(logic);

                    if (winAnimation)
                        field[x][y].setTintedGreen();
                    else if (loseAnimation)
                        field[x][y].setTintedRed();

                    field[x][y].reveal(logic);

                    stop = true;
                }
            }

            if (stop)
                break;
        }
    }

    public void startWinAnimation() {
        winAnimation = true;
    }

    public void startLoseAnimation() {
        loseAnimation = true;
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

    public long getSeed() {
        return seed;
    }

    public void placeMines(int freeX, int freeY) {
        Random random = new Random(seed);
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
        winAnimation = false;
        loseAnimation = false;
        animationDelay = 0;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                field[x][y] = new Square(x, y);

                field[x][y].setVisiblePosition((x - width * 0.5f) * 4 + width * 0.5f, (y - height) * 2);
            }
        }
    }
}
