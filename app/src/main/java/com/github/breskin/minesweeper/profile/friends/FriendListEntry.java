package com.github.breskin.minesweeper.profile.friends;

import android.graphics.Canvas;
import android.graphics.PointF;
import android.view.MotionEvent;

import com.github.breskin.minesweeper.R;
import com.github.breskin.minesweeper.RenderView;
import com.github.breskin.minesweeper.Theme;
import com.github.breskin.minesweeper.generic.ListEntry;
import com.github.breskin.minesweeper.generic.buttons.Button;
import com.github.breskin.minesweeper.generic.buttons.ImageButton;

public class FriendListEntry extends ListEntry {

    private Friend friend;

    private ImageButton deleteButton;

    private boolean deleted = false;

    public FriendListEntry(final Friend friend) {
        this.friend = friend;

        deleteButton = new ImageButton(RenderView.CONTEXT, R.drawable.ic_delete);
        deleteButton.setCallback(new Button.ClickCallback() {
            @Override
            public void onClick() {
                FriendManager.removeFriend(friend);
                deleted = true;
            }
        });
    }

    @Override
    public void update(float translation) {
        super.update(translation);

        deleteButton.setPosition(new PointF(RenderView.VIEW_WIDTH * 0.975f - deleteButton.getSize().x, translation + (getHeight() - deleteButton.getSize().y) * 0.5f));
        deleteButton.update();
    }

    @Override
    public void render(Canvas canvas) {
        super.render(canvas);

        drawActiveIndicator(canvas);

        paint.setTextSize(getHeight() * 0.3f);
        paint.setColor(Theme.getColor(Theme.ColorType.ListEntryText));

        canvas.drawText(friend.getDisplayName(), RenderView.VIEW_WIDTH * 0.085f, translation + (getHeight() - paint.getTextSize()) * 0.4f + paint.getTextSize(), paint);

        deleteButton.render(canvas);
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (deleteButton.onTouchEvent(event)) return true;

        return super.onTouchEvent(event);
    }

    @Override
    public float getHeight() {
        return RenderView.VIEW_WIDTH * 0.2f;
    }

    public Friend getFriend() {
        return friend;
    }

    public boolean isDeleted() {
        return deleted;
    }
}
