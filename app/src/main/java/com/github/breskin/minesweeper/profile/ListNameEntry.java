package com.github.breskin.minesweeper.profile;

import android.graphics.Canvas;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;

import com.github.breskin.minesweeper.MainActivity;
import com.github.breskin.minesweeper.R;
import com.github.breskin.minesweeper.RenderView;
import com.github.breskin.minesweeper.Theme;
import com.github.breskin.minesweeper.Utils;
import com.github.breskin.minesweeper.generic.ListEntry;
import com.github.breskin.minesweeper.generic.buttons.Button;
import com.github.breskin.minesweeper.generic.buttons.ImageButton;

public class ListNameEntry extends ListEntry {

    private ImageButton editButton;
    private String displayName;

    public ListNameEntry() {
        editButton = new ImageButton(RenderView.CONTEXT, R.drawable.ic_edit);
        editButton.setCallback(new Button.ClickCallback() {
            @Override
            public void onClick() {
                ((MainActivity)RenderView.CONTEXT).showNameChangeUI();
            }
        });
    }

    @Override
    public void update(float translation) {
        super.update(translation);

        editButton.setPosition(new PointF(RenderView.VIEW_WIDTH * 0.975f - editButton.getSize().x, translation + (getHeight() - editButton.getSize().y) * 0.5f));
        editButton.update();

        paint.setTextSize(getHeight() * 0.3f);
        displayName = Utils.trimText(UserProfile.getName(), RenderView.VIEW_WIDTH * 0.7f, paint);
    }

    @Override
    public void render(Canvas canvas) {
        super.render(canvas);

        paint.setTextSize(getHeight() * 0.3f);
        paint.setColor(Theme.getColor(Theme.ColorType.ListEntryText));

        canvas.drawText(displayName, RenderView.VIEW_WIDTH * 0.085f, translation + (getHeight() - paint.getTextSize()) * 0.4f + paint.getTextSize(), paint);

        editButton.render(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (editButton.onTouchEvent(event)) return true;

        return super.onTouchEvent(event);
    }

    @Override
    public void onThemeChanged() {
        editButton.setIcon(RenderView.CONTEXT, R.drawable.ic_edit);
    }

    @Override
    public float getHeight() {
        return RenderView.VIEW_WIDTH * 0.2f;
    }
}
