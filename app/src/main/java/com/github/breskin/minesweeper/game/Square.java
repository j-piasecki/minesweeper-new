package com.github.breskin.minesweeper.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;

public class Square {

    public static final int TYPE_MINE = -1, TYPE_EMPTY = 0, TYPE_1 = 1, TYPE_2 = 2, TYPE_3 = 3, TYPE_4 = 4, TYPE_5 = 5, TYPE_6 = 6, TYPE_7 = 7, TYPE_8 = 8;

    private static Paint paint = new Paint();

    private float visibleX, visibleY;
    private int x, y, targetX, targetY;
    private int type;
    private boolean revealed, flagged;

    public Square(int x, int y) {
        this.visibleX = this.targetX = this.x = x;
        this.visibleY = this.targetY = this.y = y;

        this.revealed = false;
        this.flagged = false;
    }

    public void update() {
        this.visibleX += (targetX - visibleX) * 0.1f;
        this.visibleY += (targetY - visibleY) * 0.1f;
    }

    public void render(GameLogic logic, Canvas canvas) {
        PointF position = logic.getCamera().calculateOnScreenPosition(new PointF(visibleX, visibleY));
        float size = logic.getCamera().getBlockSize();

        if (revealed || flagged) {
            paint.setColor(Color.rgb(96, 96, 96));
            canvas.drawRoundRect(new RectF(position.x + size * 0.05f, position.y + size * 0.05f, position.x + size * 0.9f, position.y + size * 0.9f), size * 0.1f, size * 0.1f, paint);

            if (flagged) {
                paint.setColor(Color.RED);
                canvas.drawCircle(position.x + size * 0.5f, position.y + size * 0.5f, size * 0.3f, paint);
            }
        } else {
            paint.setColor(Color.rgb(64, 64, 64));
            canvas.drawRoundRect(new RectF(position.x + size * 0.05f, position.y + size * 0.05f, position.x + size * 0.9f, position.y + size * 0.9f), size * 0.1f, size * 0.1f, paint);

        }
    }

    public void reveal(GameLogic logic) {
        if (flagged) {
            return;
        }

        this.revealed = true;

        if (x > 0) {
            if (logic.getMinefield().getSquare(x - 1, y).getType() != Square.TYPE_MINE) logic.getMinefield().getSquare(x - 1, y).reveal(logic);
            if (y > 0 && logic.getMinefield().getSquare(x - 1, y - 1).getType() != Square.TYPE_MINE) logic.getMinefield().getSquare(x - 1, y - 1).reveal(logic);
            if (y < logic.getMinefield().getHeight() - 1 && logic.getMinefield().getSquare(x - 1, y + 1).getType() != Square.TYPE_MINE) logic.getMinefield().getSquare(x - 1, y + 1).reveal(logic);
        }

        if (x < logic.getMinefield().getWidth() - 1) {
            if (logic.getMinefield().getSquare(x + 1, y).getType() != Square.TYPE_MINE) logic.getMinefield().getSquare(x + 1, y).reveal(logic);
            if (y > 0 && logic.getMinefield().getSquare(x + 1, y - 1).getType() != Square.TYPE_MINE) logic.getMinefield().getSquare(x + 1, y - 1).reveal(logic);
            if (y < logic.getMinefield().getHeight() - 1 && logic.getMinefield().getSquare(x + 1, y + 1).getType() != Square.TYPE_MINE) logic.getMinefield().getSquare(x + 1, y + 1).reveal(logic);
        }

        if (y > 0)
            if (logic.getMinefield().getSquare(x, y - 1).getType() != Square.TYPE_MINE) logic.getMinefield().getSquare(x, y - 1).reveal(logic);

        if (y < logic.getMinefield().getHeight() - 1)
            if (logic.getMinefield().getSquare(x, y + 1).getType() != Square.TYPE_MINE) logic.getMinefield().getSquare(x, y + 1).reveal(logic);
    }

    public void flag() {
        this.flagged = !this.flagged;
    }

    public void setVisiblePosition(float x, float y) {
        this.visibleX = x;
        this.visibleY = y;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
