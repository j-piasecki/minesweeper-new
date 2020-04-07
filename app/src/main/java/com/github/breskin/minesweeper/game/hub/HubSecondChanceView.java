package com.github.breskin.minesweeper.game.hub;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.view.MotionEvent;

import com.github.breskin.minesweeper.DataManager;
import com.github.breskin.minesweeper.RenderView;
import com.github.breskin.minesweeper.generic.buttons.Button;
import com.github.breskin.minesweeper.generic.buttons.DefaultButton;
import com.github.breskin.minesweeper.generic.HubView;

public class HubSecondChanceView extends HubView {

    private DefaultButton noButton, yesButton;

    public HubSecondChanceView(final InfoHub parent) {
        super(parent);

        noButton = new DefaultButton(DataManager.HUB_BUTTON_NO, false);
        yesButton = new DefaultButton(DataManager.HUB_BUTTON_YES, true);

        noButton.setCallback(new Button.ClickCallback() {
            @Override
            public void onClick() {
                parent.switchView(InfoHub.View.GameLost);
                parent.getGameLogic().onGameLost();
            }
        });

        yesButton.setCallback(new Button.ClickCallback() {
            @Override
            public void onClick() {
                parent.switchView(InfoHub.View.None);
                parent.getGameLogic().useSecondLife();
            }
        });
    }

    @Override
    public void update(PointF position) {
        super.update(position);

        noButton.setPosition(new PointF(position.x + RenderView.VIEW_WIDTH * 0.06f, position.y + RenderView.VIEW_WIDTH * 0.3f));
        yesButton.setPosition(new PointF(position.x + RenderView.VIEW_WIDTH * 0.14f + noButton.getSize().x, position.y + RenderView.VIEW_WIDTH * 0.3f));

        noButton.update();
        yesButton.update();
    }

    @Override
    protected void drawContent(Canvas canvas) {
        paint.setColor(Color.WHITE);

        paint.setTextSize(RenderView.SIZE * 0.06f);
        canvas.drawText(DataManager.HUB_DO_YOU_WANT_TO_CONTINUE, position.x + (RenderView.VIEW_WIDTH - paint.measureText(DataManager.HUB_DO_YOU_WANT_TO_CONTINUE)) * 0.5f, position.y + paint.getTextSize() * 1.3f, paint);

        paint.setTextSize(RenderView.SIZE * 0.045f);
        canvas.drawText(DataManager.HUB_SECOND_LIFE_AVAILABLE.replace("%1$d", String.valueOf(DataManager.SECOND_LIVES_COUNT)), position.x + (RenderView.VIEW_WIDTH - paint.measureText(DataManager.HUB_SECOND_LIFE_AVAILABLE.replace("%1$d", String.valueOf(DataManager.SECOND_LIVES_COUNT)))) * 0.5f, position.y + paint.getTextSize() * 3.25f, paint);

        paint.setTextSize(RenderView.SIZE * 0.035f);
        canvas.drawText(DataManager.HUB_SECOND_LIFE_ONCE_REMINDER, position.x + (RenderView.VIEW_WIDTH - paint.measureText(DataManager.HUB_SECOND_LIFE_ONCE_REMINDER)) * 0.5f, position.y + RenderView.SIZE * 0.2f + paint.getTextSize(), paint);

        noButton.render(canvas);
        yesButton.render(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (noButton.onTouchEvent(event)) return true;
        if (yesButton.onTouchEvent(event)) return true;

        return false;
    }

    @Override
    public int getHeight() {
        return (int)(RenderView.SIZE * 0.45f);
    }
}
