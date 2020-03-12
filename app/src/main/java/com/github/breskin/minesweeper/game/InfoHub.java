package com.github.breskin.minesweeper.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;

import com.github.breskin.minesweeper.RenderView;

public class InfoHub {

    private Paint paint;

    public InfoHub() {
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    public void update() {

    }

    public void render(GameLogic logic, Canvas canvas) {
        paint.setColor(Color.argb(160, 0, 0, 0));

        canvas.drawRect(0, RenderView.VIEW_HEIGHT - RenderView.SIZE * 0.1f, RenderView.VIEW_WIDTH, RenderView.VIEW_HEIGHT, paint);

        paint.setColor(Color.WHITE);
        paint.setTextSize(RenderView.SIZE * 0.07f);

        canvas.drawText(getTimeString(logic.getGameDuration()), RenderView.VIEW_WIDTH * 0.975f - paint.measureText(getTimeString(logic.getGameDuration())), RenderView.VIEW_HEIGHT - RenderView.SIZE * 0.02f, paint);
        canvas.drawText(logic.getFlaggedMines() + "/" + logic.getMinefield().getMinesCount(), RenderView.VIEW_WIDTH * 0.025f, RenderView.VIEW_HEIGHT - RenderView.SIZE * 0.02f, paint);
    }

    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    private String getTimeString(int t) {
        int time = t / 1000;
        int minutes = time / 60;
        time -= minutes * 60;

        return ((minutes < 10) ? "0" : "") + minutes + ":" + ((time < 10) ? "0" : "") +  time;
    }
}
