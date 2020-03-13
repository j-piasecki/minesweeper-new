package com.github.breskin.minesweeper.game;

import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.github.breskin.minesweeper.RenderView;

public class Camera {

    private static final float BLOCK_SIZE_MULTIPLIER = 0.1f;

    private ScaleGestureDetector gestureDetector;

    private PointF position;
    private float scale, targetScale;

    private PointF touchDownPoint, lastTouchPoint, translationChange, scaleFocus, velocity;
    private boolean touchDown, scaling, move;

    public Camera() {
        this.gestureDetector = new ScaleGestureDetector(RenderView.CONTEXT, new GestureListener());

        this.position = new PointF();
        this.scale = this.targetScale = 0.8f;

        this.touchDownPoint = new PointF();
        this.lastTouchPoint = new PointF();
        this.translationChange = new PointF();
        this.scaleFocus = new PointF();
        this.velocity = new PointF();
        touchDown = false;
        scaling = false;
    }

    public PointF getPosition() {
        return position;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.targetScale = scale;
    }

    public float getBlockSize() {
        return RenderView.SIZE * BLOCK_SIZE_MULTIPLIER * scale;
    }

    public PointF calculateOnScreenPosition(PointF pos) {
        float blockSize = getBlockSize();
        return new PointF((pos.x - position.x) * blockSize + (RenderView.VIEW_WIDTH - blockSize) * 0.5f,
                (pos.y - position.y) * blockSize + (RenderView.VIEW_HEIGHT - blockSize) * 0.5f);
    }

    public PointF calculatePositionFromScreen(PointF pos) {
        float blockSize = getBlockSize();
        return new PointF((pos.x - (RenderView.VIEW_WIDTH - blockSize) * 0.5f) / blockSize + position.x,
                (pos.y - (RenderView.VIEW_HEIGHT - blockSize) * 0.5f) / blockSize + position.y);
    }

    public void move(float x, float y) {
        position.x -= x / getBlockSize() * (RenderView.FRAME_TIME / 16f);
        position.y -= y / getBlockSize() * (RenderView.FRAME_TIME / 16f);
    }

    public void scale(float change) {
        targetScale *= change;
    }

    public void reset() {
        position.x = position.y = 0;
        scale = targetScale = 0.8f;
        touchDownPoint.x = touchDownPoint.y = 0;
        lastTouchPoint.x = lastTouchPoint.y = 0;
        translationChange.x = translationChange.y = 0;
        scaleFocus.x = scaleFocus.y = 0;
        velocity.x = velocity.y = 0;

        touchDown = scaling = false;
    }

    public void update() {
        if (touchDown || scaling) {
            if (move) {
                velocity.x = translationChange.x;
                velocity.y = translationChange.y;

                move(translationChange.x, translationChange.y);
            }

            translationChange.x = translationChange.y = 0;
        } else {
            move(velocity.x, velocity.y);

            velocity.x *= 0.88;
            velocity.y *= 0.88;
        }

        PointF previous = calculatePositionFromScreen(scaleFocus);

        scale += (targetScale - scale) * 0.2f;

        PointF current = calculatePositionFromScreen(scaleFocus);

        position.x += (previous.x - current.x);
        position.y += (previous.y - current.y);

        if (Math.abs(velocity.x) < 0.01) velocity.x = 0;
        if (Math.abs(velocity.y) < 0.01) velocity.y = 0;
    }

    public boolean onTouchEvent(MotionEvent event) {
        scaling = false;

        if (event.getPointerCount() > 1) {
            gestureDetector.onTouchEvent(event);
            touchDown = false;
            scaling = true;
        } else {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touchDown = true;
                    move = false;

                    touchDownPoint.x = x;
                    touchDownPoint.y = y;
                    break;

                case MotionEvent.ACTION_MOVE:
                    if (touchDown) {
                        translationChange.x = x - lastTouchPoint.x;
                        translationChange.y = y - lastTouchPoint.y;

                        if (!move && (Math.abs(x - touchDownPoint.x) > RenderView.SIZE * 0.01 || Math.abs(y - touchDownPoint.y) > RenderView.SIZE * 0.01))
                            move = true;
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    touchDown = false;
                    break;
            }

            lastTouchPoint.x = x;
            lastTouchPoint.y = y;
        }

        return false;
    }

    private class GestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            velocity.x = detector.getFocusX() - scaleFocus.x;
            velocity.y = detector.getFocusY() - scaleFocus.y;

            translationChange.x = velocity.x;
            translationChange.y = velocity.y;
            scale(detector.getScaleFactor());

            scaleFocus.x = detector.getFocusX();
            scaleFocus.y = detector.getFocusY();

            move = true;

            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            scaleFocus.x = detector.getFocusX();
            scaleFocus.y = detector.getFocusY();

            velocity.x = velocity.y = 0;

            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            scaling = false;

            velocity.x *= 0.3;
            velocity.y *= 0.3;
        }
    }
}
