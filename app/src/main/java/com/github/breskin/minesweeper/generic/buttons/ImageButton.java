package com.github.breskin.minesweeper.generic.buttons;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.github.breskin.minesweeper.RenderView;

public class ImageButton extends Button {

    private Bitmap icon;
    private Paint paint;

    public ImageButton(Context context, int resource) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, resource);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());

        icon = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(icon);
        vectorDrawable.draw(canvas);

        paint = new Paint();
    }

    @Override
    public void render(Canvas canvas) {
        PointF size = getSize();

        paint.setColor(Color.argb((int)(96 * getSaturation()), 255, 255, 255));
        canvas.drawRoundRect(getPosition().x, getPosition().y, getPosition().x + size.x, getPosition().y + size.y, size.x * 0.2f, size.y * 0.2f, paint);

        paint.setColor(Color.WHITE);
        canvas.drawBitmap(icon, new Rect(0, 0, icon.getWidth(), icon.getHeight()), new RectF(getPosition().x + size.x * 0.2f, getPosition().y + size.y * 0.2f, getPosition().x + size.x * 0.8f, getPosition().y + size.y * 0.8f), paint);
    }

    @Override
    public PointF getSize() {
        return new PointF(RenderView.SIZE * 0.125f, RenderView.SIZE * 0.125f);
    }
}
