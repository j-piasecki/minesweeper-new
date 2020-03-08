package com.github.breskin.minesweeper.generic;

import android.graphics.Canvas;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;

public class Button {

    private PointF position;
    private PointF touchDownPosition;
    private ClickCallback callback;

    private boolean hovered = false;

    private float saturation;
    private float targetSaturation;

    public Button() {
        position = new PointF();
        touchDownPosition = new PointF();

        saturation = targetSaturation = 0;
    }

    public void update() {
        saturation += (targetSaturation - saturation) * 0.1f;
    }

    public void render(Canvas canvas) {

    }

    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX(), y = event.getY();
        PointF touchPoint = new PointF(x, y);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isPointInside(touchPoint)) {
                    touchDownPosition = touchPoint;
                    hovered = true;

                    targetSaturation = 1;

                    return true;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (isPointInside(touchPoint) && hovered) {
                    touchDownPosition = touchPoint;
                } else {
                    hovered = false;

                    targetSaturation = 0;
                }
                break;

            case MotionEvent.ACTION_UP:
                if (isPointInside(touchPoint) && hovered) {
                    if (callback != null) callback.onClick();
                    hovered = false;

                    targetSaturation = 0;

                    return true;
                }
                break;
        }

        return false;
    }

    protected boolean isPointInside(PointF point) {
        return false;
    }

    protected float getSaturation() {
        return saturation;
    }

    public PointF getPosition() {
        return position;
    }

    public void setPosition(PointF position) {
        this.position = position;
    }

    public void setCallback(ClickCallback callback) {
        this.callback = callback;
    }

    public interface ClickCallback {
        void onClick();
    }
}
