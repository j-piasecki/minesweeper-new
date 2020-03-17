package com.github.breskin.minesweeper.generic;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;

import com.github.breskin.minesweeper.RenderView;

public class Slider {

    private Paint paint;
    private String text;
    private PointF position;

    private int minValue;
    private int maxValue;
    private int value;

    private float visibleValue;

    private boolean touchDown = false;

    public Slider(String text) {
        this.text = text;
        this.paint = new Paint();
        this.position = new PointF();

        minValue = 0;
        maxValue = 1;
        value = 0;
    }

    public Slider(String text, int min, int max) {
        this(text);

        minValue = min;
        maxValue = max;
    }

    public void update() {
        if (value < 0) value = 0;
        if (value > (maxValue - minValue)) value = maxValue - minValue;

        visibleValue += (value - visibleValue) * 0.1f;

        if (visibleValue < 0) visibleValue = 0;
        if (visibleValue > (maxValue - minValue)) visibleValue = maxValue - minValue;
    }

    public void render(Canvas canvas) {
        paint.setColor(Color.WHITE);
        paint.setTextSize(RenderView.SIZE * 0.065f);

        float centerPosition = position.x + RenderView.SIZE * 0.05f + visibleValue / (float)(maxValue - minValue) * RenderView.SIZE * 0.9f;

        canvas.drawText(text, position.x + (RenderView.VIEW_WIDTH - paint.measureText(text)) * 0.5f, position.y + paint.getTextSize(), paint);

        canvas.drawRect(position.x + RenderView.SIZE * 0.05f, position.y + paint.getTextSize() + RenderView.SIZE * 0.05f, centerPosition, position.y + paint.getTextSize() + RenderView.SIZE * 0.06f, paint);
        canvas.drawCircle(centerPosition, position.y + paint.getTextSize() + RenderView.SIZE * 0.055f, RenderView.SIZE * 0.015f, paint);

        paint.setColor(Color.argb(128, 255, 255, 255));
        canvas.drawRect(centerPosition, position.y + paint.getTextSize() + RenderView.SIZE * 0.05f, position.x + RenderView.SIZE * 0.95f, position.y + paint.getTextSize() + RenderView.SIZE * 0.06f, paint);

        paint.setTextSize(RenderView.SIZE * 0.04f);
        canvas.drawText(String.valueOf(getValue()), position.x + (RenderView.VIEW_WIDTH - paint.measureText(String.valueOf(getValue()))) * 0.5f, position.y + RenderView.SIZE * 0.14f + paint.getTextSize(), paint);
    }

    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (y > position.y + RenderView.SIZE * 0.095f && y < position.y + RenderView.SIZE * 0.145f) {
                    touchDown = true;

                    float normalizedX = (x - RenderView.VIEW_WIDTH * 0.05f) / (RenderView.VIEW_WIDTH * 0.9f);
                    value = Math.round(normalizedX * (maxValue - minValue));
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (touchDown) {
                    float normalizedX = (x - RenderView.VIEW_WIDTH * 0.05f) / (RenderView.VIEW_WIDTH * 0.9f);
                    value = Math.round(normalizedX * (maxValue - minValue));
                }
                break;

            case MotionEvent.ACTION_UP:
                touchDown = false;
                break;
        }

        return false;
    }

    public float getHeight() {
        return RenderView.SIZE * 0.2f;
    }

    public PointF getPosition() {
        return position;
    }

    public void setPosition(PointF position) {
        this.position = position;
    }

    public void setMinValue(int minValue) {
        value = value + this.minValue - minValue;

        this.minValue = minValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public void setValue(int value) {
        this.visibleValue = this.value = value - minValue;
    }

    public int getValue() {
        return value + minValue;
    }

    public float getPercentage() {
        return value / (float)(maxValue - minValue);
    }
}
