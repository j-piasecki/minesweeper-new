package com.github.breskin.minesweeper.generic;

import android.graphics.Canvas;
import android.view.MotionEvent;

import com.github.breskin.minesweeper.RenderView;

public class View {

    protected RenderView renderView;

    public View(RenderView renderView) {
        this.renderView = renderView;
    }

    public void update() {

    }

    public void render(Canvas canvas) {

    }

    public void open() {

    }

    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }
}
