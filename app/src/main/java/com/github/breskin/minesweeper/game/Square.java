package com.github.breskin.minesweeper.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;

import com.github.breskin.minesweeper.DataManager;
import com.github.breskin.minesweeper.RenderView;
import com.github.breskin.minesweeper.Theme;

public class Square {

    private enum Animation { None, PlaceFlag, DropFlag }

    public static final int TYPE_MINE = -1, TYPE_EMPTY = 0, TYPE_1 = 1, TYPE_2 = 2, TYPE_3 = 3, TYPE_4 = 4, TYPE_5 = 5, TYPE_6 = 6, TYPE_7 = 7, TYPE_8 = 8;

    private static Paint paint = new Paint();
    private Animation currentAnimation = Animation.None;

    private float flagAnimation = 0;

    private float visibleX, visibleY;
    private int x, y, targetX, targetY;
    private int type;
    private boolean revealed, flagged, tintedRed, tintedGreen;

    public Square(int x, int y) {
        this.visibleX = this.targetX = this.x = x;
        this.visibleY = this.targetY = this.y = y;

        this.revealed = false;
        this.flagged = false;
    }

    public void update() {
        this.visibleX += (targetX - visibleX) * 0.1f;
        this.visibleY += (targetY - visibleY) * 0.1f;

        if (currentAnimation == Animation.PlaceFlag || currentAnimation == Animation.DropFlag)
            flagAnimation += (1 - flagAnimation) * 0.15f;

        if (flagAnimation > 0.995f) {
            flagAnimation = 0;
            currentAnimation = Animation.None;
        }
    }

    public void render(GameLogic logic, Canvas canvas) {
        PointF position = logic.getCamera().calculateOnScreenPosition(new PointF(visibleX, visibleY));
        float size = logic.getCamera().getBlockSize();

        if (revealed) {
            if (tintedRed)
                paint.setColor(Theme.getColor(Theme.ColorType.SquareRed));
            else if (tintedGreen)
                paint.setColor(Theme.getColor(Theme.ColorType.SquareGreen));
            else
                paint.setColor(Theme.getColor(Theme.ColorType.SquareRevealed));
            canvas.drawRoundRect(new RectF(position.x + size * 0.04f, position.y + size * 0.04f, position.x + size * 0.92f, position.y + size * 0.92f), size * 0.1f, size * 0.1f, paint);

            if (type > 0) {
                paint.setTextSize(size * 0.75f);

                switch (type) {
                    case TYPE_1: paint.setColor(Theme.getColor(Theme.ColorType.FieldType1)); break;
                    case TYPE_2: paint.setColor(Theme.getColor(Theme.ColorType.FieldType2)); break;
                    case TYPE_3: paint.setColor(Theme.getColor(Theme.ColorType.FieldType3)); break;
                    case TYPE_4: paint.setColor(Theme.getColor(Theme.ColorType.FieldType4)); break;
                    case TYPE_5: paint.setColor(Theme.getColor(Theme.ColorType.FieldType5)); break;
                    case TYPE_6: paint.setColor(Theme.getColor(Theme.ColorType.FieldType6)); break;
                    case TYPE_7: paint.setColor(Theme.getColor(Theme.ColorType.FieldType7)); break;
                    case TYPE_8: paint.setColor(Theme.getColor(Theme.ColorType.FieldType8)); break;
                }

                canvas.drawText(String.valueOf(type),position.x + (size - paint.measureText(String.valueOf(type))) / 2, (int)(position.y + paint.getTextSize()), paint);
            } else if (type == TYPE_MINE) {
                drawMine(logic, canvas, position);
            }
        } else {
            paint.setColor(Theme.getColor(Theme.ColorType.Square));
            canvas.drawRoundRect(new RectF(position.x + size * 0.04f, position.y + size * 0.04f, position.x + size * 0.92f, position.y + size * 0.92f), size * 0.1f, size * 0.1f, paint);

            if (flagged && currentAnimation != Animation.PlaceFlag && currentAnimation != Animation.DropFlag) {
                drawFlag(logic, canvas, position);
            }
        }
    }

    public void drawOverlay(GameLogic logic, Canvas canvas) {
        PointF position;

        if (currentAnimation == Animation.PlaceFlag) {
            position = logic.getCamera().calculateOnScreenPosition(new PointF(visibleX, visibleY - (1 - flagAnimation) * 2));
            drawFlag(logic, canvas, position, flagAnimation);
        } else {
            position = logic.getCamera().calculateOnScreenPosition(new PointF(visibleX, visibleY + flagAnimation * 2));
            float size = logic.getCamera().getBlockSize();
            paint.setColor(Theme.getColor(Theme.ColorType.Flag, 1 - flagAnimation));

            canvas.save();
            canvas.translate(position.x + size * 3f / 8f + size * 0.02f, position.y + size * 13f / 16f);
            canvas.rotate(60 * flagAnimation);

            Path triangle = new Path();
            triangle.moveTo(0, -size * 10f / 16f);
            triangle.lineTo(size * 3f / 8f, -size * 7f / 16f);
            triangle.lineTo(0, -size * 4f / 16f);
            triangle.close();
            canvas.drawPath(triangle, paint);


            paint.setColor(Theme.getColor(Theme.ColorType.FlagHandle, 1 - flagAnimation));
            paint.setStrokeWidth(size * 0.05f);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawLine(0, -size * 10f / 16, 0, 0, paint);
            paint.setStyle(Paint.Style.FILL);

            canvas.restore();
        }
    }

    private void drawFlag(GameLogic logic, Canvas canvas, PointF position) {
        drawFlag(logic, canvas, position, 1);
    }

    private void drawFlag(GameLogic logic, Canvas canvas, PointF position, float alpha) {
        float size = logic.getCamera().getBlockSize();

        paint.setColor(Theme.getColor(Theme.ColorType.Flag, alpha));

        Path triangle = new Path();
        triangle.moveTo(position.x + size * 3f / 8f + size * 0.02f, position.y + size * 3f / 16f);
        triangle.lineTo(position.x + size * 6f / 8f + size * 0.02f, position.y + size * 6f / 16f);
        triangle.lineTo(position.x + size * 3f / 8f + size * 0.02f, position.y + size * 9f / 16f);
        triangle.close();
        canvas.drawPath(triangle, paint);


        paint.setColor(Theme.getColor(Theme.ColorType.FlagHandle, alpha));
        paint.setStrokeWidth(size * 0.05f);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawLine(position.x + size * 3f / 8f, position.y + size * 3f / 16, position.x + size * 3f / 8f, position.y + size * 13f / 16f, paint);
        canvas.drawLine(position.x + size * 2f / 8f, position.y + size * 13f / 16, position.x + size * 5f / 8f, position.y + size * 13f / 16f, paint);
        paint.setStyle(Paint.Style.FILL);
    }

    private void drawMine(GameLogic logic, Canvas canvas, PointF position) {
        float size = logic.getCamera().getBlockSize();

        paint.setColor(Theme.getColor(Theme.ColorType.Mine));
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

    public boolean hasOverlay() {
        return currentAnimation == Animation.PlaceFlag || currentAnimation == Animation.DropFlag;
    }

    public int getAdjacentFlagsCount(GameLogic logic) {
        int flags = 0;

        for (int x = this.x - 1; x <= this.x + 1; x++) {
            for (int y = this.y - 1; y <= this.y + 1; y++) {
                if (x == this.x && y == this.y)
                    continue;

                Square square = logic.getMinefield().getSquare(x, y);

                if (square != null && (square.flagged || (square.revealed && square.type == TYPE_MINE)))
                    flags++;
            }
        }

        return flags;
    }


    public void reveal(GameLogic logic) {
        if (flagged) {
            return;
        }

        if (revealed) {
            revealAdjacent(logic);
            return;
        }

        this.revealed = true;

        int particlesCount = (DataManager.REDUCE_PARTICLES_ON) ? 4 : 8;

        if (type == TYPE_MINE) {
            if (tintedRed)
                logic.getParticleSystem().createInPoint(logic, new PointF(visibleX + 0.5f, visibleY + 0.5f), logic.getCamera().getBlockSize() * 0.7f, particlesCount, 192, 0, 0);
            else
                logic.getParticleSystem().createInPoint(logic, new PointF(visibleX + 0.5f, visibleY + 0.5f), logic.getCamera().getBlockSize() * 0.7f, particlesCount, 0, 192, 0);
        } else {
            if (DataManager.DARK_THEME)
                logic.getParticleSystem().createInPoint(logic, new PointF(visibleX + 0.5f, visibleY + 0.5f), logic.getCamera().getBlockSize() * 0.7f, particlesCount, 48, 48, 48);
            else
                logic.getParticleSystem().createInPoint(logic, new PointF(visibleX + 0.5f, visibleY + 0.5f), logic.getCamera().getBlockSize() * 0.7f, particlesCount, 128, 128, 128);
        }

        if (type == TYPE_EMPTY) {
            for (int x = this.x - 1; x <= this.x + 1; x++) {
                for (int y = this.y - 1; y <= this.y + 1; y++) {
                    if (x == this.x && y == this.y)
                        continue;

                    Square square = logic.getMinefield().getSquare(x, y);

                    if (square != null && !square.revealed)
                        square.reveal(logic);
                }
            }
        }
    }

    private void revealAdjacent(GameLogic logic) {
        if (getAdjacentFlagsCount(logic) == type) {
            for (int x = this.x - 1; x <= this.x + 1; x++) {
                for (int y = this.y - 1; y <= this.y + 1; y++) {
                    if (x == this.x && y == this.y)
                        continue;

                    Square square = logic.getMinefield().getSquare(x, y);

                    if (square != null && !square.revealed)
                        logic.getMinefield().reveal(logic, square.x, square.y);
                }
            }
        }
    }

    public void flag(GameLogic logic) {
        this.flagged = !this.flagged;

        flagAnimation = 0;

        if (DataManager.FLAG_ANIMATIONS_ENABLED) {
            if (flagged)
                currentAnimation = Animation.PlaceFlag;
            else
                currentAnimation = Animation.DropFlag;
        }

        if (logic.isGamePaused()) return;

        RenderView.vibrate(25);
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

    public void setTintedRed() {
        this.tintedRed = true;
    }

    public void setTintedGreen() {
        this.tintedGreen = true;
    }

    public boolean isRevealed() {
        return revealed;
    }

    public boolean isFlagged() {
        return flagged;
    }
}
