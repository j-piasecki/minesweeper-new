package com.github.breskin.minesweeper.game.hub;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.MotionEvent;

import com.github.breskin.minesweeper.DataManager;
import com.github.breskin.minesweeper.RenderView;
import com.github.breskin.minesweeper.Theme;
import com.github.breskin.minesweeper.game.GameLogic;
import com.github.breskin.minesweeper.generic.HubView;

public class InfoHub {

    enum View { None, GameLost, GameWon, SecondChance }

    private Paint paint;
    private GameLogic gameLogic;

    private float offset = 0, targetOffset = 0, viewSwitchProgress = 0, viewSwitchProgressTarget = 0;

    private View currentView, targetView;

    private HubGameWonView gameWonView;
    private HubGameLostView gameLostView;
    private HubSecondChanceView secondChanceView;

    public InfoHub(GameLogic logic) {
        paint = new Paint();
        paint.setAntiAlias(true);

        this.gameLogic = logic;

        this.currentView = this.targetView = View.None;

        gameWonView = new HubGameWonView(this);
        gameLostView = new HubGameLostView(this);
        secondChanceView = new HubSecondChanceView(this);
    }

    public void update(GameLogic logic) {
        offset += (targetOffset - offset) * 0.12f;
        viewSwitchProgress += (viewSwitchProgressTarget - viewSwitchProgress) * 0.12f;

        if (targetView != View.None) {
            if (viewSwitchProgress > 0.99) {
                viewSwitchProgress = viewSwitchProgress - 1;
                viewSwitchProgressTarget = 0;

                currentView = targetView;
                targetView = View.None;
            }
        }

        if (currentView != View.None)
            getView(currentView).update(new PointF(RenderView.VIEW_WIDTH * viewSwitchProgress, RenderView.VIEW_HEIGHT - offset));

        if (targetView != View.None)
            getView(targetView).update(new PointF(RenderView.VIEW_WIDTH * (viewSwitchProgress - 1), RenderView.VIEW_HEIGHT - offset));
    }

    public void render(GameLogic logic, Canvas canvas) {
        paint.setColor(Theme.getColor(Theme.ColorType.HubBackground));

        canvas.drawRect(0, RenderView.VIEW_HEIGHT - RenderView.SIZE * 0.1f - offset, RenderView.VIEW_WIDTH, RenderView.VIEW_HEIGHT, paint);

        paint.setColor(Theme.getColor(Theme.ColorType.HubText));
        paint.setTextSize(RenderView.SIZE * 0.07f);

        canvas.drawText(getTimeString(logic.getGameDuration()), RenderView.VIEW_WIDTH * 0.975f - paint.measureText(getTimeString(logic.getGameDuration())), RenderView.VIEW_HEIGHT - RenderView.SIZE * 0.02f - offset, paint);
        canvas.drawText(logic.getFlaggedMines() + "/" + logic.getMinefield().getMinesCount(), RenderView.VIEW_WIDTH * 0.025f, RenderView.VIEW_HEIGHT - RenderView.SIZE * 0.02f - offset, paint);

        if (logic.getBestTime() != -1) {
            paint.setTextSize(RenderView.SIZE * 0.035f);

            canvas.drawText(DataManager.HUB_BEST_TIME, (RenderView.VIEW_WIDTH - paint.measureText(DataManager.HUB_BEST_TIME)) * 0.5f, RenderView.VIEW_HEIGHT - RenderView.SIZE * 0.06f - offset, paint);
            canvas.drawText(getTimeString(logic.getBestTime()), (RenderView.VIEW_WIDTH - paint.measureText(getTimeString(logic.getBestTime()))) * 0.5f, RenderView.VIEW_HEIGHT - RenderView.SIZE * 0.015f - offset, paint);
        }

        if (currentView == View.GameWon || targetView == View.GameWon) gameWonView.render(canvas);
        if (currentView == View.GameLost || targetView == View.GameLost) gameLostView.render(canvas);
        if (currentView == View.SecondChance || targetView == View.SecondChance) secondChanceView.render(canvas);
    }

    public boolean onTouchEvent(MotionEvent event) {
        if ((currentView == View.GameWon || targetView == View.GameWon) && gameWonView.onTouchEvent(event)) return true;
        if ((currentView == View.GameLost || targetView == View.GameLost) && gameLostView.onTouchEvent(event)) return true;
        if ((currentView == View.SecondChance || targetView == View.SecondChance) && secondChanceView.onTouchEvent(event)) return true;

        return false;
    }

    public void askForSecondLife() {
        setView(View.SecondChance);
    }

    public void onGameLost() {
        setView(View.GameLost);
    }

    public void onGameWon() {
        setView(View.GameWon);
    }

    public void switchView(View view) {
        if (viewSwitchProgressTarget > 0.1) return;

        targetOffset = getViewHeight(view);

        targetView = view;

        viewSwitchProgressTarget = 1;
    }

    public void setView(View view) {
        targetOffset = getViewHeight(view);

        viewSwitchProgress = 0;
        viewSwitchProgressTarget = 0;

        currentView = view;
        targetView = View.None;
    }

    private HubView getView(View view) {
        switch (view) {
            case GameWon: return gameWonView;
            case GameLost: return gameLostView;
            case SecondChance: return secondChanceView;

            default: return null;
        }
    }

    private int getViewHeight(View view) {
        switch (view) {
            case GameWon: return gameWonView.getHeight();
            case GameLost: return gameLostView.getHeight();
            case SecondChance: return secondChanceView.getHeight();
            default: return 0;
        }
    }

    public void reset() {
        offset = targetOffset = viewSwitchProgress = viewSwitchProgressTarget = 0;

        this.currentView = this.targetView = View.None;
    }

    public GameLogic getGameLogic() {
        return gameLogic;
    }

    private String getTimeString(int t) {
        int time = t / 1000;
        int minutes = time / 60;
        time -= minutes * 60;

        return ((minutes < 10) ? "0" : "") + minutes + ":" + ((time < 10) ? "0" : "") +  time;
    }
}
