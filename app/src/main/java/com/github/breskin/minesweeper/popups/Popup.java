package com.github.breskin.minesweeper.popups;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.MotionEvent;

import com.github.breskin.minesweeper.RenderView;
import com.github.breskin.minesweeper.Theme;

public class Popup {

    protected static Paint paint = new Paint();

    protected boolean touchDown = false, closing = false;

    protected PointF size, targetPosition, currentPosition;
    protected float progress, targetProgress;

    public Popup() {
        this.size = new PointF(RenderView.VIEW_WIDTH * 0.9f, RenderView.VIEW_WIDTH * 0.2f);
        this.targetPosition = new PointF(RenderView.VIEW_WIDTH * 0.05f, RenderView.VIEW_WIDTH * 0.075f);
        this.currentPosition = new PointF(RenderView.VIEW_WIDTH * 0.05f, -RenderView.VIEW_WIDTH * 0.3f);

        progress = 0;
        targetProgress = 1;
    }

    public void update() {
        currentPosition.x += (targetPosition.x - currentPosition.x) * 0.12f;
        currentPosition.y += (targetPosition.y - currentPosition.y) * 0.12f;

        progress += (targetProgress - progress) * 0.12f;
    }

    public void render(Canvas canvas) {
        PointF size = getSize();
        PointF position = getPosition();

        paint.setColor(Theme.getColor(Theme.ColorType.PopupBackground));
        canvas.drawRoundRect(position.x, position.y, position.x + size.x, position.y + size.y, RenderView.VIEW_WIDTH * 0.06f, RenderView.VIEW_WIDTH * 0.06f, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(RenderView.VIEW_WIDTH * 0.005f);
        paint.setColor(Theme.getColor(Theme.ColorType.PopupOutline));
        canvas.drawRoundRect(position.x, position.y, position.x + size.x, position.y + size.y, RenderView.VIEW_WIDTH * 0.06f, RenderView.VIEW_WIDTH * 0.06f, paint);

        paint.setStyle(Paint.Style.FILL);
    }

    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX(), y = event.getY();

        if (isPointInside(x, y)) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touchDown = true;
                    onTouchDown(x, y);

                    return true;

                case MotionEvent.ACTION_MOVE:
                    if (touchDown) {
                        onTouchMove(x, y);

                        return true;
                    }
                    break;
            }
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (touchDown) {
                touchDown = false;
                onTouchUp(x, y);
            }
        }

        return false;
    }

    public boolean isDismissed() {
        return closing && progress < 0.05f;
    }

    public void onThemeChanged() {

    }

    protected boolean isPointInside(float x, float y) {
        PointF size = getSize();
        PointF position = getPosition();

        return x > position.x && y > position.y && x < position.x + size.x && y < position.y + size.y;
    }

    protected void onTouchDown(float x, float y) {

    }

    protected void onTouchMove(float x, float y) {

    }

    protected void onTouchUp(float x, float y) {
        if (isPointInside(x, y))
            close();
    }

    protected void close() {
        targetProgress = 0;

        targetPosition = new PointF(RenderView.VIEW_WIDTH * 0.05f, -RenderView.VIEW_WIDTH * 0.3f);

        closing = true;
    }

    protected PointF getSize() {
        return size;
    }

    protected PointF getPosition() {
        return currentPosition;
    }
}
