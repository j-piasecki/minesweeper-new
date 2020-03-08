package com.github.breskin.minesweeper.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;

public class Square {

    public static final int TYPE_MINE = -1, TYPE_EMPTY = 0, TYPE_1 = 1, TYPE_2 = 2, TYPE_3 = 3, TYPE_4 = 4, TYPE_5 = 5, TYPE_6 = 6, TYPE_7 = 7, TYPE_8 = 8;

    private static Paint paint = new Paint();

    private float x, y;
    private int targetX, targetY;
    private int type;
    private boolean revealed;

    public Square(int x, int y) {
        this.x = this.targetX = x;
        this.y = this.targetY = y;

        this.revealed = false;
    }

    public void update() {
        this.x += (targetX - x) * 0.1f;
        this.y += (targetY - y) * 0.1f;
    }

    public void render(GameLogic logic, Canvas canvas) {
        PointF position = logic.getCamera().calculateOnScreenPosition(new PointF(x, y));
        float size = logic.getCamera().getBlockSize();

        if (revealed) {
            paint.setColor(Color.rgb(96, 96, 96));
            canvas.drawRoundRect(new RectF(position.x + size * 0.05f, position.y + size * 0.05f, position.x + size * 0.9f, position.y + size * 0.9f), size * 0.1f, size * 0.1f, paint);

        } else {
            paint.setColor(Color.rgb(64, 64, 64));
            canvas.drawRoundRect(new RectF(position.x + size * 0.05f, position.y + size * 0.05f, position.x + size * 0.9f, position.y + size * 0.9f), size * 0.1f, size * 0.1f, paint);

        }
    }

    public void reveal() {
        this.revealed = true;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
