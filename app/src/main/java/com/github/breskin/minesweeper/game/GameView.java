package com.github.breskin.minesweeper.game;

import android.graphics.Canvas;
import android.graphics.PointF;
import android.view.MotionEvent;

import com.github.breskin.minesweeper.RenderView;
import com.github.breskin.minesweeper.generic.View;

public class GameView extends View {

    private GameLogic gameLogic;

    private InfoHub infoHub;

    private PointF touchDownPoint;
    private long touchDownTime;
    private boolean touchPressed;

    public GameView(RenderView renderView) {
        super(renderView);

        infoHub = new InfoHub();

        gameLogic = new GameLogic(renderView);
        touchDownPoint = new PointF();
    }

    @Override
    public void update() {
        super.update();

        gameLogic.update();
        infoHub.update();

        if (touchPressed && System.currentTimeMillis() - touchDownTime > 175) {
            PointF position = gameLogic.getCamera().calculatePositionFromScreen(touchDownPoint);
            if (gameLogic.getMinefield().flag(gameLogic, (int) position.x, (int) position.y))
                touchPressed = false;
        }
    }

    @Override
    public void render(Canvas canvas) {
        gameLogic.getMinefield().render(gameLogic, canvas);
        infoHub.render(gameLogic, canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (infoHub.onTouchEvent(event)) return true;

        gameLogic.getCamera().onTouchEvent(event);

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchDownPoint.x = x;
                touchDownPoint.y = y;

                touchDownTime = System.currentTimeMillis();
                touchPressed = true;
                break;

            case MotionEvent.ACTION_MOVE:
                if (Math.abs(touchDownPoint.x - x) > RenderView.VIEW_WIDTH * 0.01f || Math.abs(touchDownPoint.y - y) > RenderView.VIEW_WIDTH * 0.01f) {
                    touchPressed = false;
                }
                break;

            case MotionEvent.ACTION_UP:
                if (Math.abs(touchDownPoint.x - x) < RenderView.VIEW_WIDTH * 0.01f && Math.abs(touchDownPoint.y - y) < RenderView.VIEW_WIDTH * 0.01f && touchPressed) {
                    PointF position = gameLogic.getCamera().calculatePositionFromScreen(touchDownPoint);
                    gameLogic.getMinefield().reveal(gameLogic, (int) position.x, (int) position.y);

                    touchPressed = false;
                }
                break;
        }

        return false;
    }

    public void reset() {
        infoHub.reset();
    }

    public GameLogic getGameLogic() {
        return gameLogic;
    }
}
