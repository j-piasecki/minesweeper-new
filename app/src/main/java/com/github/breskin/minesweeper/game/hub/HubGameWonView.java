package com.github.breskin.minesweeper.game.hub;

import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;

import com.github.breskin.minesweeper.RenderView;
import com.github.breskin.minesweeper.generic.HubView;

public class HubGameWonView extends HubView {

    public HubGameWonView(InfoHub parent) {
        super(parent);
    }

    @Override
    protected void drawContent(Canvas canvas) {
        paint.setColor(Color.GREEN);

        canvas.drawRect(position.x, position.y, position.x + RenderView.VIEW_WIDTH, position.y + getHeight(), paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getY() > position.y && event.getAction() == MotionEvent.ACTION_UP) {
            parent.switchView(InfoHub.View.GameLost);
            return true;
        }

        return super.onTouchEvent(event);
    }

    @Override
    public int getHeight() {
        return (int)(RenderView.SIZE * 0.3f);
    }
}
