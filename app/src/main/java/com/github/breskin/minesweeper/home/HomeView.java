package com.github.breskin.minesweeper.home;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.view.MotionEvent;

import com.github.breskin.minesweeper.DataManager;
import com.github.breskin.minesweeper.R;
import com.github.breskin.minesweeper.RenderView;
import com.github.breskin.minesweeper.Theme;
import com.github.breskin.minesweeper.generic.buttons.Button;
import com.github.breskin.minesweeper.generic.buttons.FancyButton;
import com.github.breskin.minesweeper.generic.View;
import com.github.breskin.minesweeper.generic.buttons.ImageButton;

public class HomeView extends View {

    private FancyButton smallButton, mediumButton, largeButton, customButton;
    private SecondLivesWidget secondLivesWidget;
    private ImageButton settingsButton;

    private Paint paint;

    private float offset, targetOffset;

    public HomeView(final RenderView renderView) {
        super(renderView);

        this.offset = this.targetOffset = 0;

        this.paint = new Paint();
        this.paint.setAntiAlias(true);

        smallButton = new FancyButton(DataManager.FIELD_SIZE_SMALL);
        smallButton.setIcon(renderView.getContext(), R.drawable.ic_field_small_button);
        smallButton.setCallback(new Button.ClickCallback() {
            @Override
            public void onClick() {
                Transition transition = new HomeView.Transition(RenderView.ViewType.Game);
                transition.setOrigin(new PointF(mediumButton.getPosition().x + smallButton.getSize().y / 2, smallButton.getPosition().y + smallButton.getSize().y / 2));
                renderView.switchView(transition);

                renderView.getGameView().getGameLogic().init(10, 16, 25);
            }
        });

        mediumButton = new FancyButton(DataManager.FIELD_SIZE_MEDIUM);
        mediumButton.setIcon(renderView.getContext(), R.drawable.ic_field_medium_button);
        mediumButton.setCallback(new Button.ClickCallback() {
            @Override
            public void onClick() {
                Transition transition = new HomeView.Transition(RenderView.ViewType.Game);
                transition.setOrigin(new PointF(mediumButton.getPosition().x + mediumButton.getSize().y / 2, mediumButton.getPosition().y + mediumButton.getSize().y / 2));
                renderView.switchView(transition);

                renderView.getGameView().getGameLogic().init(14, 20, 40);
            }
        });

        largeButton = new FancyButton(DataManager.FIELD_SIZE_LARGE);
        largeButton.setIcon(renderView.getContext(), R.drawable.ic_field_large_button);
        largeButton.setCallback(new Button.ClickCallback() {
            @Override
            public void onClick() {
                Transition transition = new HomeView.Transition(RenderView.ViewType.Game);
                transition.setOrigin(new PointF(largeButton.getPosition().x + largeButton.getSize().y / 2, largeButton.getPosition().y + largeButton.getSize().y / 2));
                renderView.switchView(transition);

                renderView.getGameView().getGameLogic().init(16, 26, 70);
            }
        });

        customButton = new FancyButton(DataManager.FIELD_SIZE_CUSTOM);
        customButton.setIcon(renderView.getContext(), R.drawable.ic_field_custom_button);
        customButton.setCallback(new Button.ClickCallback() {
            @Override
            public void onClick() {
                Transition transition = new HomeView.Transition(RenderView.ViewType.CustomField);
                transition.setOrigin(new PointF(customButton.getPosition().x + customButton.getSize().y / 2, customButton.getPosition().y + customButton.getSize().y / 2));
                renderView.switchView(transition);
            }
        });

        secondLivesWidget = new SecondLivesWidget();

        settingsButton = new ImageButton(renderView.getContext(), R.drawable.ic_settings);
        settingsButton.setCallback(new Button.ClickCallback() {
            @Override
            public void onClick() {
                Transition transition = new HomeView.Transition(RenderView.ViewType.Settings);
                transition.setOrigin(new PointF(settingsButton.getPosition().x + settingsButton.getSize().y / 2, settingsButton.getPosition().y + settingsButton.getSize().y / 2));
                renderView.switchView(transition);
            }
        });
    }

    @Override
    public void update() {
        super.update();

        this.offset += (targetOffset - offset) * 0.1f;

        secondLivesWidget.update();
        secondLivesWidget.setPosition(new PointF(RenderView.SIZE * 0.025f, RenderView.VIEW_HEIGHT - RenderView.SIZE * 0.025f - secondLivesWidget.getSize().y + offset));

        settingsButton.update();
        settingsButton.setPosition(new PointF(RenderView.VIEW_WIDTH - settingsButton.getSize().x - RenderView.SIZE * 0.025f, RenderView.VIEW_HEIGHT - RenderView.SIZE * 0.025f - settingsButton.getSize().y + offset));

        smallButton.update();
        mediumButton.update();
        largeButton.update();
        customButton.update();

        float buttonStart = (secondLivesWidget.getPosition().y - RenderView.SIZE * 0.275f - smallButton.getSize().y * 4f) * 0.5f + offset;

        smallButton.setPosition(new PointF(smallButton.getSize().y * 0.1f, RenderView.SIZE * 0.275f + buttonStart));
        mediumButton.setPosition(new PointF(smallButton.getSize().y * 0.5f, smallButton.getPosition().y + smallButton.getSize().y * 0.75f));
        largeButton.setPosition(new PointF(smallButton.getSize().y * 0.1f, mediumButton.getPosition().y + smallButton.getSize().y * 0.75f));
        customButton.setPosition(new PointF(smallButton.getSize().y * 0.5f, largeButton.getPosition().y + smallButton.getSize().y * 0.75f));
    }

    @Override
    public void render(Canvas canvas) {
        super.render(canvas);

        drawLogo(canvas);

        smallButton.render(canvas);
        mediumButton.render(canvas);
        largeButton.render(canvas);
        customButton.render(canvas);
        settingsButton.render(canvas);

        secondLivesWidget.render(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (smallButton.onTouchEvent(event)) return true;
        if (mediumButton.onTouchEvent(event)) return true;
        if (largeButton.onTouchEvent(event)) return true;
        if (customButton.onTouchEvent(event)) return true;
        if (secondLivesWidget.onTouchEvent(event)) return true;
        if (settingsButton.onTouchEvent(event)) return true;

        return false;
    }

    private void drawLogo(Canvas canvas) {
        paint.setTextSize(RenderView.SIZE * 0.125f);
        paint.setColor(Theme.getColor(Theme.ColorType.Header));

        PointF position = new PointF((RenderView.VIEW_WIDTH - paint.measureText(DataManager.HOME_VIEW_LOGO_FIRST) - paint.measureText(DataManager.HOME_VIEW_LOGO_SECOND) - RenderView.SIZE * 0.08f) * 0.5f, RenderView.SIZE * 0.15f + offset);

        canvas.drawText(DataManager.HOME_VIEW_LOGO_FIRST, position.x, position.y + paint.getTextSize(), paint);
        canvas.drawText(DataManager.HOME_VIEW_LOGO_SECOND, position.x + paint.measureText(DataManager.HOME_VIEW_LOGO_FIRST) + RenderView.SIZE * 0.08f, position.y + paint.getTextSize(), paint);

        drawFlag(canvas, new PointF(position.x + paint.measureText(DataManager.HOME_VIEW_LOGO_FIRST) - RenderView.SIZE * 0.055f, position.y + RenderView.SIZE * 0.025f));
    }

    @Override
    public void open() {
        this.offset = RenderView.VIEW_HEIGHT * 0.15f;
    }

    private void drawFlag(Canvas canvas, PointF position) {
        float size = RenderView.SIZE * 0.175f;

        paint.setColor(Theme.getColor(Theme.ColorType.Flag));

        Path triangle = new Path();
        triangle.moveTo(position.x + size * 3f / 8f + size * 0.02f, position.y + size * 3f / 16f);
        triangle.lineTo(position.x + size * 6f / 8f + size * 0.02f, position.y + size * 6f / 16f);
        triangle.lineTo(position.x + size * 3f / 8f + size * 0.02f, position.y + size * 9f / 16f);
        triangle.close();
        canvas.drawPath(triangle, paint);


        paint.setColor(Theme.getColor(Theme.ColorType.Header));
        paint.setStrokeWidth(size * 0.05f);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawLine(position.x + size * 3f / 8f, position.y + size * 3f / 16, position.x + size * 3f / 8f, position.y + size * 13f / 16f, paint);
        canvas.drawLine(position.x + size * 2f / 8f, position.y + size * 13f / 16, position.x + size * 5f / 8f, position.y + size * 13f / 16f, paint);
        paint.setStyle(Paint.Style.FILL);
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
            this.origin = new PointF(RenderView.VIEW_WIDTH * 0.5f, RenderView.VIEW_HEIGHT * 0.5f);
        }

        @Override
        public void update() {
            this.progress += 0.0075f + (1 - Math.abs(progress - 0.275) * 2) * 0.02;

            if (!viewChanged && progress > 0.4) {
                viewChangeCallback.onViewChange();
                viewChanged = true;

                if (viewExitCallback != null)
                    viewExitCallback.onExit();
            }

            if (progress > 0.99)
                transitionFinishedCallback.onFinished();
        }

        @Override
        public void render(Canvas canvas) {
            paint.setColor(Theme.getColor(Theme.ColorType.HomeTransition, (progress < 0.4) ? (progress * 2.5f) : 1));

            canvas.save();
            canvas.translate(origin.x, origin.y);
            canvas.rotate(-45);

            canvas.drawRect(-RenderView.VIEW_WIDTH * 3.25f * progress, (progress > 0.4) ? RenderView.VIEW_HEIGHT * 2f * (progress - 0.4f) * 1.05f : 0, RenderView.VIEW_WIDTH * 3.25f * progress, RenderView.VIEW_HEIGHT * 2f * progress,  paint);
            canvas.drawRect(-RenderView.VIEW_WIDTH * 3.25f * progress, -RenderView.VIEW_HEIGHT * 2f * progress, RenderView.VIEW_WIDTH * 3.25f * progress, (progress > 0.4) ? -RenderView.VIEW_HEIGHT * 2f * (progress - 0.4f) * 1.05f: 0,  paint);

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
