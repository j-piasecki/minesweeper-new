package com.github.breskin.minesweeper.generic;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.MotionEvent;

import com.github.breskin.minesweeper.RenderView;
import com.github.breskin.minesweeper.game.hub.InfoHub;

public class HubView {

    protected InfoHub parent;
    protected PointF position;
    protected Paint paint;

    public HubView(InfoHub parent) {
        this.parent = parent;

        this.position = new PointF();
        this.paint = new Paint();
        this.paint.setAntiAlias(true);
    }

    public void update(PointF position) {
        this.position = position;
    }

    public void render(Canvas canvas) {
        if (position.x > RenderView.VIEW_WIDTH * 0.96f || position.y > RenderView.VIEW_HEIGHT * 0.99f) return;

        drawContent(canvas);
    }

    protected void drawContent(Canvas canvas) {

    }

    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    public int getHeight() {
        return 0;
    }
}
