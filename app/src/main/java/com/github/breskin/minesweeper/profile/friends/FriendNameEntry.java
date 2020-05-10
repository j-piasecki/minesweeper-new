package com.github.breskin.minesweeper.profile.friends;

import android.graphics.Canvas;

import com.github.breskin.minesweeper.RenderView;
import com.github.breskin.minesweeper.Theme;
import com.github.breskin.minesweeper.Utils;
import com.github.breskin.minesweeper.generic.ListEntry;

public class FriendNameEntry extends ListEntry {

    private String displayName = "";

    public FriendNameEntry() {

    }

    @Override
    public void render(Canvas canvas) {
        super.render(canvas);

        paint.setTextSize(getHeight() * 0.3f);
        paint.setColor(Theme.getColor(Theme.ColorType.ListEntryText));

        canvas.drawText(displayName, (RenderView.VIEW_WIDTH - paint.measureText(displayName)) * 0.5f, translation + (getHeight() - paint.getTextSize()) * 0.4f + paint.getTextSize(), paint);
    }

    public void setName(String name) {
        paint.setTextSize(getHeight() * 0.3f);
        displayName = Utils.trimText(name, RenderView.VIEW_WIDTH * 0.7f, paint);
    }

    @Override
    public float getHeight() {
        return RenderView.VIEW_WIDTH * 0.2f;
    }
}
