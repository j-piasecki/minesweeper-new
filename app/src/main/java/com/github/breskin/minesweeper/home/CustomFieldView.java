package com.github.breskin.minesweeper.home;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.MotionEvent;

import com.github.breskin.minesweeper.DataManager;
import com.github.breskin.minesweeper.RenderView;
import com.github.breskin.minesweeper.generic.Button;
import com.github.breskin.minesweeper.generic.Slider;
import com.github.breskin.minesweeper.generic.View;

import java.util.Collections;

public class CustomFieldView extends View {

    private Paint paint;

    private Slider widthSlider, heightSlider, minesSlider;
    private FieldSizeButton startButton;

    public CustomFieldView(final RenderView renderView) {
        super(renderView);

        this.paint = new Paint();

        this.widthSlider = new Slider(DataManager.CUSTOM_VIEW_WIDTH, 10, 50);
        this.heightSlider = new Slider(DataManager.CUSTOM_VIEW_HEIGHT, 10, 50);
        this.minesSlider = new Slider(DataManager.CUSTOM_VIEW_MINES);

        startButton = new FieldSizeButton(DataManager.CUSTOM_VIEW_START);
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

        startButton.setPosition(new PointF(RenderView.SIZE * 0.075f, RenderView.VIEW_HEIGHT * 0.9f - startButton.getSize().y));
        startButton.update();

        minesSlider.setMinValue(Math.round(widthSlider.getValue() * heightSlider.getValue() * 0.12f));
        minesSlider.setMaxValue(Math.round(widthSlider.getValue() * heightSlider.getValue() * 0.25f));

        minesSlider.getPosition().y = startButton.getPosition().y - minesSlider.getHeight() - RenderView.SIZE * 0.225f;
        heightSlider.getPosition().y = minesSlider.getPosition().y - minesSlider.getHeight() - RenderView.SIZE * 0.075f;
        widthSlider.getPosition().y = heightSlider.getPosition().y - heightSlider.getHeight() - RenderView.SIZE * 0.075f;

        widthSlider.update();
        heightSlider.update();
        minesSlider.update();
    }

    @Override
    public void render(Canvas canvas) {
        paint.setColor(Color.WHITE);
        paint.setTextSize(RenderView.SIZE * 0.1f);
        canvas.drawText(DataManager.CUSTOM_VIEW_HEADER, (RenderView.VIEW_WIDTH - paint.measureText(DataManager.CUSTOM_VIEW_HEADER)) * 0.5f, widthSlider.getPosition().y * 0.5f, paint);

        widthSlider.render(canvas);
        heightSlider.render(canvas);
        minesSlider.render(canvas);
        startButton.render(canvas);
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
