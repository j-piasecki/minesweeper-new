package com.github.breskin.minesweeper.generic.buttons;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

import com.github.breskin.minesweeper.RenderView;

public class DefaultButton extends Button {

    private Paint paint;
    private String text;
    private boolean filled;

    public DefaultButton(String text, boolean filled) {
        super();

        this.text = text;
        this.filled = filled;
        this.paint = new Paint();
        this.paint.setAntiAlias(true);
    }

    @Override
    public void render(Canvas canvas) {
        PointF size = getSize();
        paint.setColor(Color.rgb(0.8f + getSaturation() * 0.2f, 0.8f + getSaturation() * 0.2f, 0.8f + getSaturation() * 0.2f));
        paint.setTextSize(size.y * 0.6f);

        if (filled) {
            canvas.drawRoundRect(getPosition().x, getPosition().y, getPosition().x + size.x, getPosition().y + size.y, size.y * 0.25f, size.y * 0.25f, paint);

            paint.setColor(Color.BLACK);
        } else {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(size.y * 0.05f);
            canvas.drawRoundRect(getPosition().x, getPosition().y, getPosition().x + size.x, getPosition().y + size.y, size.y * 0.25f, size.y * 0.25f, paint);
            paint.setStyle(Paint.Style.FILL);
        }

        canvas.drawText(text, getPosition().x + (size.x - paint.measureText(text)) * 0.5f, getPosition().y + paint.getTextSize() * 1.15f, paint);
    }

    @Override
    public PointF getSize() {
        return new PointF(RenderView.SIZE * 0.4f, RenderView.SIZE * 0.08f);
    }
}
