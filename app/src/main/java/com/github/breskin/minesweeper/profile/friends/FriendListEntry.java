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

        paint.setTextSize(getHeight() * 0.35f);
        paint.setColor(Theme.getColor(Theme.ColorType.HubText));

        canvas.drawText(friend.getDisplayName(), 30, translation + paint.getTextSize() * 1.8f, paint);
    }

    public float getHeight() {
        return RenderView.VIEW_WIDTH * 0.2f;
    }
}
