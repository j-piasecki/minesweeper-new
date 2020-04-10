package com.github.breskin.minesweeper.game.hub;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.view.MotionEvent;

import com.github.breskin.minesweeper.DataManager;
import com.github.breskin.minesweeper.RenderView;
import com.github.breskin.minesweeper.Theme;
import com.github.breskin.minesweeper.generic.buttons.Button;
import com.github.breskin.minesweeper.generic.buttons.DefaultButton;
import com.github.breskin.minesweeper.generic.HubView;
import com.github.breskin.minesweeper.generic.Transition;
import com.github.breskin.minesweeper.home.HomeView;

public class HubGameWonView extends HubView {

    private DefaultButton backButton, replayButton;

    public HubGameWonView(final InfoHub parent) {
        super(parent);

        backButton = new DefaultButton(DataManager.HUB_BUTTON_OK, false);
        replayButton = new DefaultButton(DataManager.HUB_BUTTON_REPLAY, true);

        backButton.setCallback(new Button.ClickCallback() {
            @Override
            public void onClick() {
                Transition transition = new HomeView.Transition(RenderView.ViewType.Home);
                parent.getGameLogic().getRenderView().switchView(transition);
            }
        });

        replayButton.setCallback(new Button.ClickCallback() {
            @Override
            public void onClick() {
                HomeView.Transition transition = new HomeView.Transition(RenderView.ViewType.Game);
                transition.setOrigin(new PointF(replayButton.getPosition().x + replayButton.getSize().x * 0.5f, replayButton.getPosition().y + replayButton.getSize().y * 0.5f));
                parent.getGameLogic().getRenderView().switchView(transition);

                transition.setViewExitCallback(new Transition.ViewExitCallback() {
                    @Override
                    public void onExit() {
                        parent.getGameLogic().init(parent.getGameLogic().getMinefield().getWidth(), parent.getGameLogic().getMinefield().getHeight(), parent.getGameLogic().getMinefield().getMinesCount());
                    }
                });
            }
        });
    }

    @Override
    public void update(PointF position) {
        super.update(position);

        backButton.setPosition(new PointF(position.x + RenderView.VIEW_WIDTH * 0.06f, position.y + RenderView.VIEW_WIDTH * 0.22f));
        replayButton.setPosition(new PointF(position.x + RenderView.VIEW_WIDTH * 0.14f + backButton.getSize().x, position.y + RenderView.VIEW_WIDTH * 0.22f));

        backButton.update();
        replayButton.update();
    }

    @Override
    protected void drawContent(Canvas canvas) {
        paint.setColor(Theme.getColor(Theme.ColorType.HubText));
        paint.setTextSize(RenderView.SIZE * 0.1f);

        canvas.drawText(DataManager.HUB_GAME_WON, position.x + (RenderView.VIEW_WIDTH - paint.measureText(DataManager.HUB_GAME_WON)) * 0.5f, position.y + paint.getTextSize() * 1.2f, paint);

        backButton.render(canvas);
        replayButton.render(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (backButton.onTouchEvent(event)) return true;
        if (replayButton.onTouchEvent(event)) return true;

        return false;
    }

    @Override
    public int getHeight() {
        return (int)(RenderView.SIZE * 0.4f);
    }
}
