package com.github.breskin.minesweeper;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.MotionEvent;

import com.github.breskin.minesweeper.generic.View;

public class GameView extends View {


    private static Paint paint = new Paint();
    private Camera camera;

    public GameView(RenderView renderView) {
        super(renderView);

        camera = new Camera();

    }

    @Override
    public void update() {
        super.update();

        camera.update();
    }

    @Override
    public void render(Canvas canvas) {
        paint.setColor(Color.RED);

        PointF pos = camera.calculateOnScreenPosition(new PointF(0, 0));
        canvas.drawRect(pos.x, pos.y, pos.x + camera.getBlockSize(), pos.y + camera.getBlockSize(), paint);
        pos = camera.calculateOnScreenPosition(new PointF(3, 3));
        canvas.drawRect(pos.x, pos.y, pos.x + camera.getBlockSize(), pos.y + camera.getBlockSize(), paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        camera.onTouchEvent(event);

        return false;
    }
}
