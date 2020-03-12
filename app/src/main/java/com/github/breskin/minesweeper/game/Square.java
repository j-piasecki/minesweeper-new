package com.github.breskin.minesweeper.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
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

        if (revealed) {
            paint.setColor(Color.rgb(96, 96, 96));
            canvas.drawRoundRect(new RectF(position.x + size * 0.05f, position.y + size * 0.05f, position.x + size * 0.9f, position.y + size * 0.9f), size * 0.1f, size * 0.1f, paint);

            if (type > 0) {
                paint.setTextSize(size * 0.75f);

                switch (type) {
                    case TYPE_1: paint.setColor(Color.rgb(0, 64, 255)); break;
                    case TYPE_2: paint.setColor(Color.rgb(0, 200, 0)); break;
                    case TYPE_3: paint.setColor(Color.rgb(255, 0, 0)); break;
                    case TYPE_4: paint.setColor(Color.rgb(0, 0, 192)); break;
                    case TYPE_5: paint.setColor(Color.rgb(192, 0, 0)); break;
                    case TYPE_6: paint.setColor(Color.rgb(0, 255, 255)); break;
                    case TYPE_7: paint.setColor(Color.rgb(0, 0, 0)); break;
                    case TYPE_8: paint.setColor(Color.rgb(200, 200, 200)); break;
                }

                canvas.drawText(type+"",position.x + (size - paint.measureText(type+"")) / 2, (int)(position.y + paint.getTextSize()), paint);
            } else if (type == TYPE_MINE) {
                drawMine(logic, canvas, position);
            }
        } else {
            paint.setColor(Color.rgb(64, 64, 64));
            canvas.drawRoundRect(new RectF(position.x + size * 0.05f, position.y + size * 0.05f, position.x + size * 0.9f, position.y + size * 0.9f), size * 0.1f, size * 0.1f, paint);

            if (flagged) {
                drawFlag(logic, canvas, position);
            }
        }
    }

    private void drawFlag(GameLogic logic, Canvas canvas, PointF position) {
        float size = logic.getCamera().getBlockSize();

        paint.setColor(Color.rgb(200, 0, 0));

        Path triangle = new Path();
        triangle.moveTo(position.x + size * 3f / 8f + size * 0.02f, position.y + size * 3f / 16f);
        triangle.lineTo(position.x + size * 6f / 8f + size * 0.02f, position.y + size * 6f / 16f);
        triangle.lineTo(position.x + size * 3f / 8f + size * 0.02f, position.y + size * 9f / 16f);
        triangle.close();
        canvas.drawPath(triangle, paint);


        paint.setColor(Color.rgb(0, 0, 0));
        paint.setStrokeWidth(size * 0.05f);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawLine(position.x + size * 3f / 8f, position.y + size * 3f / 16, position.x + size * 3f / 8f, position.y + size * 13f / 16f, paint);
        canvas.drawLine(position.x + size * 2f / 8f, position.y + size * 13f / 16, position.x + size * 5f / 8f, position.y + size * 13f / 16f, paint);
        paint.setStyle(Paint.Style.FILL);
    }

    private void drawMine(GameLogic logic, Canvas canvas, PointF position) {
        float size = logic.getCamera().getBlockSize();

        paint.setColor(Color.rgb(255, 255, 255));
        canvas.drawCircle(position.x + size * 0.475f, position.y + size * 0.475f, size * 0.25f, paint);

        float halfX = size * 0.05f, halfY = size * 0.3f, cornerRadius = size * 0.1f;

        canvas.save();
        canvas.translate(position.x + size * 0.475f, position.y + size * 0.475f);
        canvas.drawRoundRect(new RectF(-halfX, -halfY, halfX, halfY), cornerRadius, cornerRadius, paint);
        canvas.restore();

        canvas.save();
        canvas.translate(position.x + size * 0.475f, position.y + size * 0.475f);
        canvas.rotate(45);
        canvas.drawRoundRect(new RectF(-halfX, -halfY, halfX, halfY), cornerRadius, cornerRadius, paint);
        canvas.restore();

        canvas.save();
        canvas.translate(position.x + size * 0.475f, position.y + size * 0.475f);
        canvas.rotate(90);
        canvas.drawRoundRect(new RectF(-halfX, -halfY, halfX, halfY), cornerRadius, cornerRadius, paint);
        canvas.restore();

        canvas.save();
        canvas.translate(position.x + size * 0.475f, position.y + size * 0.475f);
        canvas.rotate(135);
        canvas.drawRoundRect(new RectF(-halfX, -halfY, halfX, halfY), cornerRadius, cornerRadius, paint);
        canvas.restore();
    }

    public void reveal(GameLogic logic) {
        if (flagged || revealed) {
            return;
        }

        this.revealed = true;

        if (type == TYPE_EMPTY) {
            if (x > 0) {
                if (logic.getMinefield().getSquare(x - 1, y).getType() != Square.TYPE_MINE)
                    logic.getMinefield().getSquare(x - 1, y).reveal(logic);
                if (y > 0 && logic.getMinefield().getSquare(x - 1, y - 1).getType() != Square.TYPE_MINE)
                    logic.getMinefield().getSquare(x - 1, y - 1).reveal(logic);
                if (y < logic.getMinefield().getHeight() - 1 && logic.getMinefield().getSquare(x - 1, y + 1).getType() != Square.TYPE_MINE)
                    logic.getMinefield().getSquare(x - 1, y + 1).reveal(logic);
            }

            if (x < logic.getMinefield().getWidth() - 1) {
                if (logic.getMinefield().getSquare(x + 1, y).getType() != Square.TYPE_MINE)
                    logic.getMinefield().getSquare(x + 1, y).reveal(logic);
                if (y > 0 && logic.getMinefield().getSquare(x + 1, y - 1).getType() != Square.TYPE_MINE)
                    logic.getMinefield().getSquare(x + 1, y - 1).reveal(logic);
                if (y < logic.getMinefield().getHeight() - 1 && logic.getMinefield().getSquare(x + 1, y + 1).getType() != Square.TYPE_MINE)
                    logic.getMinefield().getSquare(x + 1, y + 1).reveal(logic);
            }

            if (y > 0)
                if (logic.getMinefield().getSquare(x, y - 1).getType() != Square.TYPE_MINE)
                    logic.getMinefield().getSquare(x, y - 1).reveal(logic);

            if (y < logic.getMinefield().getHeight() - 1)
                if (logic.getMinefield().getSquare(x, y + 1).getType() != Square.TYPE_MINE)
                    logic.getMinefield().getSquare(x, y + 1).reveal(logic);
        }
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
