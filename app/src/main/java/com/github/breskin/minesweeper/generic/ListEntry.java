package com.github.breskin.minesweeper.generic;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.MotionEvent;

import com.github.breskin.minesweeper.RenderView;
import com.github.breskin.minesweeper.Theme;

public class ListEntry {

    protected static Paint paint = new Paint();

    protected float translation = 0;

    private boolean touchDown;
    private PointF touchDownPoint;

    private float saturation = 0;

    public ListEntry() {
        touchDownPoint = new PointF(0, 0);
        paint.setAntiAlias(true);
    }

    public void update(float translation) {
        this.translation = translation;

        if (touchDown)
            saturation += (1 - saturation) * 0.1f;
        else
            saturation += (0 - saturation) * 0.1f;
    }

    public void render(Canvas canvas) {
        if (saturation > 0.01) {
            paint.setColor(Theme.getColor(Theme.ColorType.ListEntryBackground, saturation));
            canvas.drawRect(0, translation, RenderView.VIEW_WIDTH, translation + getHeight(), paint);
        }

        paint.setColor(Theme.getColor(Theme.ColorType.ListEntrySeparator));
        paint.setStrokeWidth(1);
        canvas.drawLine(RenderView.VIEW_WIDTH * 0.05f, translation, RenderView.VIEW_WIDTH * 0.95f, translation, paint);
    }

    protected boolean onTouch(float x, float y) {
        return false;
    }

    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (y > translation && y < translation + getHeight()) {
                    touchDown = true;

                    touchDownPoint.x = x;
                    touchDownPoint.y = y;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (touchDown && (Math.abs(x - touchDownPoint.x) > RenderView.SIZE * 0.01 || Math.abs(y - touchDownPoint.y) > RenderView.SIZE * 0.01))
                    touchDown = false;
                break;

            case MotionEvent.ACTION_UP:
                if (touchDown) {
                    touchDown = false;

                    if (onTouch(x, y)) return true;
                }
                break;
        }

        return false;
    }

    public void refresh() {

    }

    public void onThemeChanged() {

    }

    public boolean isVisible() {
        if (translation > RenderView.VIEW_HEIGHT || translation < 0)
            return false;

        return true;
    }

    public float getHeight() {
        return RenderView.VIEW_WIDTH * 0.2f;
    }
}
