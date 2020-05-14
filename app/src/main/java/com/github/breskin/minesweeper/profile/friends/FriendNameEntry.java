package com.github.breskin.minesweeper.profile.friends;

import android.graphics.Canvas;

import com.github.breskin.minesweeper.RenderView;
import com.github.breskin.minesweeper.Theme;
import com.github.breskin.minesweeper.Utils;
import com.github.breskin.minesweeper.generic.ListEntry;

public class FriendNameEntry extends ListEntry {

    private String displayName;
    private Friend friend;

    public FriendNameEntry() {

    }

    @Override
    public void render(Canvas canvas) {
        super.render(canvas);

        paint.setTextSize(getHeight() * 0.3f);
        paint.setColor(Theme.getColor(Theme.ColorType.ListEntryText));

        canvas.drawText(displayName, (RenderView.VIEW_WIDTH - paint.measureText(displayName)) * 0.5f, translation + (getHeight() - paint.getTextSize()) * 0.275f + paint.getTextSize(), paint);

        drawActiveIndicator(canvas);
    }

    private void drawActiveIndicator(Canvas canvas) {
        if (friend.isActive())
            paint.setColor(Theme.getColor(Theme.ColorType.FriendListEntryActive));
        else
            paint.setColor(Theme.getColor(Theme.ColorType.FriendListEntryInactive));
        paint.setTextSize(getHeight() * 0.15f);

        String state = friend.getStateText();
        float indicatorSize = RenderView.VIEW_WIDTH * 0.007f;
        float width = (RenderView.VIEW_WIDTH - (indicatorSize * 2 + paint.measureText(state))) * 0.5f;

        canvas.save();
        canvas.translate(width, translation + getHeight() * 0.65f);
        canvas.rotate(45);
        canvas.drawRect(-indicatorSize, -indicatorSize, indicatorSize, indicatorSize, paint);
        canvas.restore();

        canvas.drawText(state, width + RenderView.VIEW_WIDTH * 0.025f, translation + getHeight() * 0.65f + paint.getTextSize() * 0.35f, paint);
    }

    public void setFriend(Friend friend) {
        this.friend = friend;

        paint.setTextSize(getHeight() * 0.3f);
        displayName = Utils.trimText(friend.getDisplayName(), RenderView.VIEW_WIDTH * 0.7f, paint);
    }

    @Override
    public float getHeight() {
        return RenderView.VIEW_WIDTH * 0.2f;
    }
}
