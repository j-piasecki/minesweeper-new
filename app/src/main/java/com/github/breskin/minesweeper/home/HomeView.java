package com.github.breskin.minesweeper.home;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.MotionEvent;

import com.github.breskin.minesweeper.RenderView;
import com.github.breskin.minesweeper.generic.Button;
import com.github.breskin.minesweeper.generic.View;

public class HomeView extends View {

    private SizeButton test, test2;

    public HomeView(final RenderView renderView) {
        super(renderView);

        test = new SizeButton("test");
        test.setCallback(new Button.ClickCallback() {
            @Override
            public void onClick() {
                Transition transition = new HomeView.Transition(RenderView.ViewType.Home);
                transition.setOrigin(new PointF(test.getPosition().x + test.getSize().y / 2, test.getPosition().y + test.getSize().y / 2));
                renderView.switchView(transition);
            }
        });

        test2 = new SizeButton("Niestandardowa");
        test2.setCallback(new Button.ClickCallback() {
            @Override
            public void onClick() {
                Transition transition = new HomeView.Transition(RenderView.ViewType.Game);
                transition.setOrigin(new PointF(test2.getPosition().x + test2.getSize().y / 2, test2.getPosition().y + test2.getSize().y / 2));
                renderView.switchView(transition);

                renderView.getGameView().getGameLogic().init(20, 20, 40);
            }
        });
    }

    @Override
    public void update() {
        super.update();

        test.update();
        test.setPosition(new PointF(test.getSize().y * 0.1f, test.getSize().y));

        test2.update();
        test2.setPosition(new PointF(test.getSize().y * 0.5f, test.getSize().y * 1.75f));
    }

    @Override
    public void render(Canvas canvas) {
        super.render(canvas);

        test.render(canvas);

        test2.render(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (test.onTouchEvent(event)) return true;
        if (test2.onTouchEvent(event)) return true;

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
            }

            if (progress > 0.95)
                transitionFinishedCallback.onFinished();
        }

        @Override
        public void render(Canvas canvas) {
            paint.setColor(Color.argb((progress < 0.5) ? (progress * 2) : 1, 1, 1, 1));

            canvas.save();
            canvas.translate(origin.x, origin.y);
            canvas.rotate(-45);

            canvas.drawRect(-RenderView.VIEW_WIDTH * 2f * progress, (progress > 0.5) ? RenderView.VIEW_HEIGHT * 2f * (progress - 0.5f) : 0, RenderView.VIEW_WIDTH * 2f * progress, RenderView.VIEW_HEIGHT * 2f * progress,  paint);
            canvas.drawRect(-RenderView.VIEW_WIDTH * 2f * progress, -RenderView.VIEW_HEIGHT * 2f * progress, RenderView.VIEW_WIDTH * 2f * progress, (progress > 0.5) ? -RenderView.VIEW_HEIGHT * 2f * (progress - 0.5f) : 0,  paint);

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
