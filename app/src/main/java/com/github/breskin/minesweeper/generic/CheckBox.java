package com.github.breskin.minesweeper.generic;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;

import com.github.breskin.minesweeper.RenderView;

public class CheckBox {

    private Paint paint;
    private PointF position;
    private String text;
    private boolean checked;

    private CheckChangeCallback callback;

    private PointF touchDownPoint;
    private boolean touchDown;

    private float saturation = 0;

    public CheckBox(String text) {
        this.paint = new Paint();
        this.paint.setAntiAlias(true);
        this.position = new PointF();
        this.text = text;

        checked = false;

        touchDown = false;
        touchDownPoint = new PointF(0, 0);
    }

    public void update() {
        if (touchDown) {
            saturation += (1 - saturation) * 0.1f;
        } else {
            saturation += (0 - saturation) * 0.1f;
        }
    }

    public void render(Canvas canvas) {
        PointF size = getSize();

        if (saturation > 0.01f) {
            paint.setColor(Color.argb((int) (saturation * 24), 255, 255, 255));
            canvas.drawRect(position.x, position.y, position.x + size.x, position.y + size.y, paint);
        }

        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(RenderView.SIZE * 0.0075f);
        paint.setStyle(Paint.Style.STROKE);

        RectF rect = new RectF(position.x + RenderView.VIEW_WIDTH * 0.88f, position.y + (size.y - RenderView.SIZE * 0.07f) * 0.5f, position.x + RenderView.VIEW_WIDTH * 0.88f + RenderView.SIZE * 0.07f, position.y + (size.y - RenderView.SIZE * 0.07f) * 0.5f + RenderView.SIZE * 0.07f);
        canvas.drawRoundRect(rect, RenderView.SIZE * 0.02f, RenderView.SIZE * 0.02f, paint);

        paint.setStrokeWidth(1);
        canvas.drawLine(position.x + RenderView.VIEW_WIDTH * 0.025f, position.y, position.x + RenderView.VIEW_WIDTH * 0.975f, position.y, paint);
        canvas.drawLine(position.x + RenderView.VIEW_WIDTH * 0.025f, position.y + size.y, position.x + RenderView.VIEW_WIDTH * 0.975f, position.y + size.y, paint);

        paint.setStyle(Paint.Style.FILL);

        if (checked || touchDown) {
            paint.setColor(Color.argb(((checked) ? 96 : 0) + ((touchDown) ? (int)(saturation * 48) : 0), 255, 255, 255));
            canvas.drawRoundRect(rect, RenderView.SIZE * 0.02f, RenderView.SIZE * 0.02f, paint);
            paint.setColor(Color.WHITE);

            if (checked)
                canvas.drawCircle(rect.left + rect.width() * 0.5f, rect.top + rect.height() * 0.5f, RenderView.SIZE * 0.015f, paint);
        }

        if (text != null) {
            paint.setColor(Color.WHITE);
            paint.setTextSize(RenderView.SIZE * 0.05f);
            canvas.drawText(text, RenderView.VIEW_WIDTH * 0.05f + position.x, position.y + (size.y - paint.getTextSize()) * 0.44f + paint.getTextSize(), paint);
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        PointF size = getSize();

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (x > position.x && y > position.y && x < position.x + size.x && y < position.y + size.y) {
                    touchDownPoint.x = x;
                    touchDownPoint.y = y;
                    touchDown = true;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (x < position.x || y < position.y || x > position.x + size.x || y > position.y + size.y) {
                    touchDown = false;
                }
                break;

            case MotionEvent.ACTION_UP:
                if (touchDown) {
                    this.checked = !this.checked;

                    if (callback != null)
                        callback.onChecked(checked);

                    touchDown = false;
                }
                break;
        }

        return false;
    }

    public void setCallback(CheckChangeCallback callback) {
        this.callback = callback;
    }

    public PointF getSize() {
        return new PointF(RenderView.VIEW_WIDTH, RenderView.SIZE * 0.15f);
    }

    public PointF getPosition() {
        return position;
    }

    public void setPosition(PointF position) {
        this.position = position;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public void setText(String text) {
        this.text = text;
    }

    public interface CheckChangeCallback {
        void onChecked(boolean checked);
    }
}
