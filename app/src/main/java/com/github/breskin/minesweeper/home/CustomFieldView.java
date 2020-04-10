package com.github.breskin.minesweeper.home;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.MotionEvent;

import com.github.breskin.minesweeper.DataManager;
import com.github.breskin.minesweeper.RenderView;
import com.github.breskin.minesweeper.Theme;
import com.github.breskin.minesweeper.generic.buttons.Button;
import com.github.breskin.minesweeper.generic.buttons.FancyButton;
import com.github.breskin.minesweeper.generic.Slider;
import com.github.breskin.minesweeper.generic.View;

public class CustomFieldView extends View {

    private Paint paint;

    private Slider widthSlider, heightSlider, minesSlider;
    private FancyButton startButton;

    private float offset, targetOffset;

    public CustomFieldView(final RenderView renderView) {
        super(renderView);

        this.offset = this.targetOffset = 0;

        this.paint = new Paint();

        this.widthSlider = new Slider(DataManager.CUSTOM_VIEW_WIDTH, 10, 50);
        this.heightSlider = new Slider(DataManager.CUSTOM_VIEW_HEIGHT, 10, 50);
        this.minesSlider = new Slider(DataManager.CUSTOM_VIEW_MINES);

        startButton = new FancyButton(DataManager.CUSTOM_VIEW_START);
        startButton.setCallback(new Button.ClickCallback() {
            @Override
            public void onClick() {
                HomeView.Transition transition = new HomeView.Transition(RenderView.ViewType.Game);
                transition.setOrigin(new PointF(startButton.getPosition().x + startButton.getSize().y / 2, startButton.getPosition().y + startButton.getSize().y / 2));
                renderView.switchView(transition);

                renderView.getGameView().getGameLogic().init(widthSlider.getValue(), heightSlider.getValue(), minesSlider.getValue());
            }
        });
    }

    @Override
    public void update() {
        super.update();

        this.offset += (targetOffset - offset) * 0.1f;

        startButton.setPosition(new PointF(RenderView.SIZE * 0.075f, RenderView.VIEW_HEIGHT * 0.925f - startButton.getSize().y + offset));
        startButton.update();

        minesSlider.setMinValue(Math.round(widthSlider.getValue() * heightSlider.getValue() * 0.12f));
        minesSlider.setMaxValue(Math.round(widthSlider.getValue() * heightSlider.getValue() * 0.25f));

        float buttonStart = (startButton.getPosition().y - RenderView.SIZE * 0.25f - minesSlider.getHeight() * 3 - RenderView.SIZE * 0.15f) * 0.5f + offset;

        widthSlider.getPosition().y =  RenderView.SIZE * 0.25f + buttonStart;
        heightSlider.getPosition().y = widthSlider.getPosition().y + widthSlider.getHeight() + RenderView.SIZE * 0.075f;
        minesSlider.getPosition().y = heightSlider.getPosition().y + heightSlider.getHeight() + RenderView.SIZE * 0.075f;

        widthSlider.update();
        heightSlider.update();
        minesSlider.update();
    }

    @Override
    public void render(Canvas canvas) {
        paint.setColor(Theme.getColor(Theme.ColorType.Header));
        paint.setTextSize(RenderView.SIZE * 0.1f);
        canvas.drawText(DataManager.CUSTOM_VIEW_HEADER, (RenderView.VIEW_WIDTH - paint.measureText(DataManager.CUSTOM_VIEW_HEADER)) * 0.5f, RenderView.SIZE * 0.25f + offset, paint);

        widthSlider.render(canvas);
        heightSlider.render(canvas);
        minesSlider.render(canvas);
        startButton.render(canvas);
    }

    @Override
    public void open() {
        this.offset = RenderView.SIZE * 0.25f;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (widthSlider.onTouchEvent(event)) return true;
        if (heightSlider.onTouchEvent(event)) return true;
        if (minesSlider.onTouchEvent(event)) return true;
        if (startButton.onTouchEvent(event)) return true;

        return false;
    }
}
