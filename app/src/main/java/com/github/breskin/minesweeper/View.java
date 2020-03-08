package com.github.breskin.minesweeper;

import android.graphics.Canvas;
import android.view.MotionEvent;

public class View {

    protected RenderView renderView;

    public View(RenderView renderView) {
        this.renderView = renderView;
    }

    public void update() {

    }

    public void render(Canvas canvas) {

    }

    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }
}
