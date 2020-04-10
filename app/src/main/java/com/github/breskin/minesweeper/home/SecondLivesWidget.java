package com.github.breskin.minesweeper.home;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.view.MotionEvent;

import com.github.breskin.minesweeper.DataManager;
import com.github.breskin.minesweeper.RenderView;
import com.github.breskin.minesweeper.Theme;

public class SecondLivesWidget {

    private float heartSize;

    private PointF position;
    private Paint paint;

    private Path heartPath;

    public SecondLivesWidget() {
        this.position = new PointF();

        this.paint = new Paint();
        this.paint.setAntiAlias(true);
    }

    public void update() {
        paint.setTextSize(RenderView.SIZE * 0.056f);
        paint.setStrokeWidth(RenderView.SIZE * 0.004f);
    }

    public void render(Canvas canvas) {
        if (heartPath == null) init();

        PointF size = getSize();

        paint.setColor(Theme.getColor(Theme.ColorType.SecondLivesWidget));
        paint.setStyle(Paint.Style.STROKE);

        canvas.drawRoundRect(position.x, position.y, position.x + size.x, position.y + size.y, size.y * 0.3f, size.y * 0.3f, paint);

        paint.setStrokeWidth(1);
        canvas.drawLine(position.x + heartSize * 1.6f, position.y + size.y * 0.2f, position.x + heartSize * 1.6f, position.y + size.y * 0.8f, paint);

        paint.setStyle(Paint.Style.FILL);

        canvas.save();
        canvas.translate(position.x + heartSize * 0.85f, position.y + heartSize * 0.45f);
        canvas.drawPath(heartPath, paint);
        canvas.restore();

        canvas.drawText(String.valueOf(DataManager.SECOND_LIVES_COUNT), position.x + heartSize * 1.85f, position.y + paint.getTextSize() * 1.05f, paint);
    }

    private void init() {
        heartSize = RenderView.SIZE * 0.05f;
        heartPath = new Path();

        heartPath.cubicTo(heartSize * 0.2464f, -heartSize * 0.3036f, heartSize * 0.6f, 0, heartSize * 0.478f, heartSize * 0.2959f);
        heartPath.cubicTo(heartSize * 0.3786f, heartSize * 0.4892f, heartSize * 0.2057f, heartSize * 0.6571f, 0, heartSize * 0.8119f);
        heartPath.cubicTo(-heartSize * 0.2057f, heartSize * 0.6571f, -heartSize * 0.3786f, heartSize * 0.4892f, -heartSize * 0.478f, heartSize * 0.2959f);
        heartPath.cubicTo(-heartSize * 0.6f, 0, -heartSize * 0.2464f, -heartSize * 0.3036f, 0, 0);
    }

    public boolean onTouchEvent(MotionEvent event) {

        return false;
    }

    public PointF getSize() {
        return new PointF(paint.measureText(String.valueOf(DataManager.SECOND_LIVES_COUNT)) + heartSize * 2.2f, RenderView.SIZE * 0.08f);
    }

    public void setPosition(PointF position) {
        this.position = position;
    }

    public PointF getPosition() {
        return position;
    }
}
