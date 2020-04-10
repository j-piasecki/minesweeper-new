package com.github.breskin.minesweeper.home;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.MotionEvent;

import com.github.breskin.minesweeper.DataManager;
import com.github.breskin.minesweeper.RenderView;
import com.github.breskin.minesweeper.Theme;
import com.github.breskin.minesweeper.generic.buttons.DefaultButton;
import com.github.breskin.minesweeper.generic.buttons.Button;
import com.github.breskin.minesweeper.generic.CheckBox;
import com.github.breskin.minesweeper.generic.View;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsView extends View {

    private Paint paint;
    private CheckBox vibrationsCheckBox, flagAnimationsCheckBox, reduceParticlesCheckBox, darkThemeCheckBox;

    private DefaultButton signInButton;

    private float offset, targetOffset;

    public SettingsView(final RenderView renderView) {
        super(renderView);

        this.offset = this.targetOffset = 0;

        this.paint = new Paint();
        paint.setAntiAlias(true);

        vibrationsCheckBox = new CheckBox(DataManager.SETTINGS_VIEW_VIBRATIONS);
        vibrationsCheckBox.setChecked(DataManager.VIBRATIONS_ENABLED);
        vibrationsCheckBox.setCallback(new CheckBox.CheckChangeCallback() {
            @Override
            public void onChecked(boolean checked) {
                DataManager.setVibrationsEnabled(checked);
            }
        });

        flagAnimationsCheckBox = new CheckBox(DataManager.SETTINGS_VIEW_FLAG_ANIMATIONS);
        flagAnimationsCheckBox.setChecked(DataManager.FLAG_ANIMATIONS_ENABLED);
        flagAnimationsCheckBox.setCallback(new CheckBox.CheckChangeCallback() {
            @Override
            public void onChecked(boolean checked) {
                DataManager.setFlagAnimationsEnabled(checked);
            }
        });

        reduceParticlesCheckBox = new CheckBox(DataManager.SETTINGS_VIEW_REDUCE_PARTICLES);
        reduceParticlesCheckBox.setChecked(DataManager.REDUCE_PARTICLES_ON);
        reduceParticlesCheckBox.setCallback(new CheckBox.CheckChangeCallback() {
            @Override
            public void onChecked(boolean checked) {
                DataManager.setReduceParticlesOn(checked);
            }
        });

        darkThemeCheckBox = new CheckBox(DataManager.SETTINGS_DARK_THEME);
        darkThemeCheckBox.setChecked(DataManager.DARK_THEME);
        darkThemeCheckBox.setCallback(new CheckBox.CheckChangeCallback() {
            @Override
            public void onChecked(boolean checked) {
                DataManager.setDarkTheme(checked);
                renderView.themeChanged();
            }
        });

        signInButton = new DefaultButton(DataManager.SETTINGS_SIGN_IN, true);
        signInButton.setCallback(new Button.ClickCallback() {
            @Override
            public void onClick() {
                renderView.showLoginScreen();
            }
        });
    }

    @Override
    public void update() {
        super.update();
        this.offset += (targetOffset - offset) * 0.1f;

        vibrationsCheckBox.update();
        flagAnimationsCheckBox.update();
        reduceParticlesCheckBox.update();
        darkThemeCheckBox.update();
        signInButton.update();

        vibrationsCheckBox.setPosition(new PointF(0, RenderView.SIZE * 0.45f + offset));
        flagAnimationsCheckBox.setPosition(new PointF(0, vibrationsCheckBox.getPosition().y + vibrationsCheckBox.getSize().y));
        reduceParticlesCheckBox.setPosition(new PointF(0, flagAnimationsCheckBox.getPosition().y + flagAnimationsCheckBox.getSize().y));
        darkThemeCheckBox.setPosition(new PointF(0, reduceParticlesCheckBox.getPosition().y + reduceParticlesCheckBox.getSize().y));

        signInButton.setPosition(new PointF((RenderView.VIEW_WIDTH - signInButton.getSize().x) * 0.5f, RenderView.VIEW_HEIGHT - signInButton.getSize().y * 1.75f + offset));
    }

    @Override
    public void render(Canvas canvas) {
        super.render(canvas);

        paint.setTextSize(RenderView.SIZE * 0.125f);
        paint.setColor(Theme.getColor(Theme.ColorType.Header));
        canvas.drawText(DataManager.SETTINGS_VIEW_HEADER, (RenderView.VIEW_WIDTH - paint.measureText(DataManager.SETTINGS_VIEW_HEADER)) * 0.5f, RenderView.SIZE * 0.15f + paint.getTextSize() + offset, paint);

        vibrationsCheckBox.render(canvas);
        flagAnimationsCheckBox.render(canvas);
        reduceParticlesCheckBox.render(canvas);
        darkThemeCheckBox.render(canvas);

        if (FirebaseAuth.getInstance().getCurrentUser() == null)
            signInButton.render(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (vibrationsCheckBox.onTouchEvent(event)) return true;
        if (flagAnimationsCheckBox.onTouchEvent(event)) return true;
        if (reduceParticlesCheckBox.onTouchEvent(event)) return true;
        if (darkThemeCheckBox.onTouchEvent(event)) return true;
        if (FirebaseAuth.getInstance().getCurrentUser() == null && signInButton.onTouchEvent(event)) return true;

        return super.onTouchEvent(event);
    }

    @Override
    public void open() {
        this.offset = RenderView.VIEW_HEIGHT * 0.15f;
    }
}
