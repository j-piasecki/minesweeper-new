package com.github.breskin.minesweeper.home;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.github.breskin.minesweeper.RenderView;
import com.github.breskin.minesweeper.generic.Button;

public class FieldSizeButton extends Button {

    private Paint paint;
    private Path foreground, background;
    private PointF size;
    private String text;
    private Bitmap icon;

    public FieldSizeButton(String text) {
        super();

        this.text = text;
        this.paint = new Paint();

        this.size = new PointF(RenderView.SIZE * 0.6f - RenderView.SIZE * 0.14f * 0.6f, RenderView.SIZE * 0.28f);
    }

    @Override
    public void update() {
        super.update();

        if (foreground == null) init();
    }

    @Override
    public void render(Canvas canvas) {
        canvas.save();
        canvas.translate(getPosition().x, getPosition().y);

        paint.setColor(Color.rgb(0.6f + getSaturation() * 0.25f, 0.6f + getSaturation() * 0.25f, 0.6f + getSaturation() * 0.25f));
        canvas.drawPath(background, paint);
        paint.setColor(Color.BLACK);
        paint.setTextSize(size.y * 0.2f);
        canvas.drawText(text, size.y * 1.05f, size.y * 0.57f, paint);

        paint.setColor(Color.rgb(0.75f + getSaturation() * 0.25f, 0.75f + getSaturation() * 0.25f, 0.75f + getSaturation() * 0.25f));
        canvas.drawPath(foreground, paint);

        if (icon != null)
            canvas.drawBitmap(icon, new Rect(0, 0, icon.getWidth(), icon.getHeight()), new RectF(size.y * 0.1f, size.y * 0.1f, size.y * 0.9f, size.y * 0.9f), paint);

        canvas.restore();
    }

    @Override
    protected boolean isPointInside(PointF point) {
        float length = RenderView.SIZE * 0.14f;

        return (point.x > getPosition().x && point.x < getPosition().x + length * 2 && point.y > getPosition().y && point.y < getPosition().y + length * 2) ||
                (point.x > getPosition().x + length && point.x < getPosition().x + size.x + length * 2 && point.y > getPosition().y + length * 0.6f && point.y < getPosition().y + length * 1.4f);
    }

    private void init() {
        float length = RenderView.SIZE * 0.14f;
        this.size = new PointF(RenderView.SIZE * 0.6f - length * 0.6f, RenderView.SIZE * 0.28f);

        foreground = new Path();
        foreground.moveTo(length, 0);
        foreground.lineTo(length * 2, length);
        foreground.lineTo(length, length * 2);
        foreground.lineTo(0, length);
        foreground.close();

        background = new Path();
        background.moveTo(length, length * 0.6f);
        background.lineTo(length + RenderView.SIZE * 0.6f, length * 0.6f);
        background.lineTo(length + RenderView.SIZE * 0.6f + length * 0.4f, length);
        background.lineTo(length + RenderView.SIZE * 0.6f, length * 1.4f);
        background.lineTo(length, length * 1.4f);
        background.close();
    }

    public void setIcon(Context context, int resource) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, resource);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());

        icon = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(icon);
        vectorDrawable.draw(canvas);
    }

    public PointF getSize() {
        return size;
    }
}
