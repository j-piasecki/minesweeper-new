package com.github.breskin.minesweeper.game;

import android.graphics.Canvas;
import android.graphics.PointF;
import android.view.MotionEvent;

import com.github.breskin.minesweeper.RenderView;
import com.github.breskin.minesweeper.generic.View;

public class GameView extends View {

    private GameLogic gameLogic;

    private PointF touchDownPoint;
    private long touchDownTime;
    private boolean touchPressed;

    public GameView(RenderView renderView) {
        super(renderView);

        gameLogic = new GameLogic(renderView);
        touchDownPoint = new PointF();
    }

    @Override
    public void update() {
        super.update();

        gameLogic.update();

        if (touchPressed && System.currentTimeMillis() - touchDownTime > 175) {
            PointF position = gameLogic.getCamera().calculatePositionFromScreen(touchDownPoint);
            gameLogic.getMinefield().flag((int) position.x, (int) position.y);

            touchPressed = false;
        }
    }

    @Override
    public void render(Canvas canvas) {
        gameLogic.getMinefield().render(gameLogic, canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
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

    public GameLogic getGameLogic() {
        return gameLogic;
    }
}
