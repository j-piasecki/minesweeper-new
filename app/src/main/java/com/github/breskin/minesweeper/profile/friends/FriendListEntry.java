package com.github.breskin.minesweeper.profile.friends;

import android.graphics.Canvas;

import com.github.breskin.minesweeper.RenderView;
import com.github.breskin.minesweeper.Theme;
import com.github.breskin.minesweeper.generic.ListEntry;

public class FriendListEntry extends ListEntry {

    private Friend friend;

    public FriendListEntry(Friend friend) {
        this.friend = friend;
    }

    public void update(float translation) {
        super.update(translation);
    }

    @Override
    public void render(Canvas canvas) {
        super.render(canvas);

        drawActiveIndicator(canvas);

        paint.setTextSize(getHeight() * 0.3f);
        paint.setColor(Theme.getColor(Theme.ColorType.ListEntryText));

        canvas.drawText(friend.getDisplayName(), RenderView.VIEW_WIDTH * 0.085f, translation + (getHeight() - paint.getTextSize()) * 0.4f + paint.getTextSize(), paint);
    }

    private void drawActiveIndicator(Canvas canvas) {
        if (friend.isActive())
            paint.setColor(Theme.getColor(Theme.ColorType.FriendListEntryActive));
        else
            paint.setColor(Theme.getColor(Theme.ColorType.FriendListEntryInactive));

        float indicatorSize = RenderView.VIEW_WIDTH * 0.008f;
        canvas.save();
        canvas.translate(RenderView.VIEW_WIDTH * 0.0425f, translation + (getHeight() - indicatorSize * 2) * 0.5f);
        canvas.rotate(45);
        canvas.drawRect(-indicatorSize, -indicatorSize, indicatorSize, indicatorSize, paint);
        canvas.restore();
    }

    public float getHeight() {
        return RenderView.VIEW_WIDTH * 0.2f;
    }

    public Friend getFriend() {
        return friend;
    }
}
