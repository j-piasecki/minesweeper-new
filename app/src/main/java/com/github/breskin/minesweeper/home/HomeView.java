package com.github.breskin.minesweeper.home;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.MotionEvent;

import com.github.breskin.minesweeper.DataManager;
import com.github.breskin.minesweeper.RenderView;
import com.github.breskin.minesweeper.generic.Button;
import com.github.breskin.minesweeper.generic.View;

public class HomeView extends View {

    private FieldSizeButton smallButton, mediumButton, largeButton, customButton;

    public HomeView(final RenderView renderView) {
        super(renderView);

        smallButton = new FieldSizeButton(DataManager.FIELD_SIZE_SMALL);
        smallButton.setCallback(new Button.ClickCallback() {
            @Override
            public void onClick() {
                Transition transition = new HomeView.Transition(RenderView.ViewType.Game);
                transition.setOrigin(new PointF(mediumButton.getPosition().x + smallButton.getSize().y / 2, smallButton.getPosition().y + smallButton.getSize().y / 2));
                renderView.switchView(transition);

                renderView.getGameView().getGameLogic().init(10, 16, 25);
            }
        });

        mediumButton = new FieldSizeButton(DataManager.FIELD_SIZE_MEDIUM);
        mediumButton.setCallback(new Button.ClickCallback() {
            @Override
            public void onClick() {
                Transition transition = new HomeView.Transition(RenderView.ViewType.Game);
                transition.setOrigin(new PointF(mediumButton.getPosition().x + mediumButton.getSize().y / 2, mediumButton.getPosition().y + mediumButton.getSize().y / 2));
                renderView.switchView(transition);

                renderView.getGameView().getGameLogic().init(14, 20, 40);
            }
        });

        largeButton = new FieldSizeButton(DataManager.FIELD_SIZE_LARGE);
        largeButton.setCallback(new Button.ClickCallback() {
            @Override
            public void onClick() {
                Transition transition = new HomeView.Transition(RenderView.ViewType.Game);
                transition.setOrigin(new PointF(largeButton.getPosition().x + largeButton.getSize().y / 2, largeButton.getPosition().y + largeButton.getSize().y / 2));
                renderView.switchView(transition);

                renderView.getGameView().getGameLogic().init(16, 26, 70);
            }
        });

        customButton = new FieldSizeButton(DataManager.FIELD_SIZE_CUSTOM);
        customButton.setCallback(new Button.ClickCallback() {
            @Override
            public void onClick() {
                Transition transition = new HomeView.Transition(RenderView.ViewType.CustomField);
                transition.setOrigin(new PointF(customButton.getPosition().x + customButton.getSize().y / 2, customButton.getPosition().y + customButton.getSize().y / 2));
                renderView.switchView(transition);
            }
        });
    }

    @Override
    public void update() {
        super.update();

        smallButton.update();
        smallButton.setPosition(new PointF(smallButton.getSize().y * 0.1f, RenderView.VIEW_HEIGHT * 0.3f));

        mediumButton.update();
        mediumButton.setPosition(new PointF(smallButton.getSize().y * 0.5f, smallButton.getPosition().y + smallButton.getSize().y * 0.75f));

        largeButton.update();
        largeButton.setPosition(new PointF(smallButton.getSize().y * 0.1f, mediumButton.getPosition().y + smallButton.getSize().y * 0.75f));

        customButton.update();
        customButton.setPosition(new PointF(smallButton.getSize().y * 0.5f, largeButton.getPosition().y + smallButton.getSize().y * 0.75f));
    }

    @Override
    public void render(Canvas canvas) {
        super.render(canvas);

        smallButton.render(canvas);
        mediumButton.render(canvas);
        largeButton.render(canvas);
        customButton.render(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (smallButton.onTouchEvent(event)) return true;
        if (mediumButton.onTouchEvent(event)) return true;
        if (largeButton.onTouchEvent(event)) return true;
        if (customButton.onTouchEvent(event)) return true;

        return false;
    }

    public static class Transition extends com.github.breskin.minesweeper.generic.Transition {

        private Paint paint;
        private PointF origin;
        private float progress;

        private boolean viewChanged = false;

        public Transition(RenderView.ViewType target) {
            super(target);

            this.paint = new Paint();
            this.progress = 0;
            this.origin = new PointF(RenderView.VIEW_WIDTH / 2, RenderView.VIEW_HEIGHT / 2);
        }

        @Override
        public void update() {
            this.progress += (1 - progress) * 0.04f;

            if (!viewChanged && progress > 0.5) {
                viewChangeCallback.onViewChange();
                viewChanged = true;

                if (viewExitCallback != null)
                    viewExitCallback.onExit();
            }

            if (progress > 0.97)
                transitionFinishedCallback.onFinished();
        }

        @Override
        public void render(Canvas canvas) {
            paint.setColor(Color.argb((progress < 0.5) ? (progress * 2) : 1, 1, 1, 1));

            canvas.save();
            canvas.translate(origin.x, origin.y);
            canvas.rotate(-45);

            canvas.drawRect(-RenderView.VIEW_WIDTH * 2.5f * progress, (progress > 0.5) ? RenderView.VIEW_HEIGHT * 2f * (progress - 0.5f) * 1.05f : 0, RenderView.VIEW_WIDTH * 2.5f * progress, RenderView.VIEW_HEIGHT * 2f * progress,  paint);
            canvas.drawRect(-RenderView.VIEW_WIDTH * 2.5f * progress, -RenderView.VIEW_HEIGHT * 2f * progress, RenderView.VIEW_WIDTH * 2.5f * progress, (progress > 0.5) ? -RenderView.VIEW_HEIGHT * 2f * (progress - 0.5f) * 1.05f: 0,  paint);

            canvas.restore();
        }

        @Override
        public boolean passTouchEvents() {
            return progress > 0.7f;
        }

        public void setOrigin(PointF origin) {
            this.origin = origin;
        }
    }
}
