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
    private String displayName;

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

        displayName = friend.getDisplayName();

        paint.setTextSize(getHeight() * 0.3f);
        if (paint.measureText(displayName) > RenderView.VIEW_WIDTH * 0.7f) {
            while (paint.measureText(displayName + "…")  > RenderView.VIEW_WIDTH * 0.7f) {
                displayName = displayName.substring(0, displayName.length() - 1);
            }

            displayName += "…";
        }
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

        canvas.drawText(displayName, RenderView.VIEW_WIDTH * 0.085f, translation + (getHeight() - paint.getTextSize()) * 0.45f + paint.getTextSize(), paint);

        deleteButton.render(canvas);
    }

    private void drawActiveIndicator(Canvas canvas) {
        if (friend.isActive())
            paint.setColor(Theme.getColor(Theme.ColorType.FriendListEntryActive));
        else
            paint.setColor(Theme.getColor(Theme.ColorType.FriendListEntryInactive));

        float indicatorSize = RenderView.VIEW_WIDTH * 0.008f;
        canvas.save();
        canvas.translate(RenderView.VIEW_WIDTH * 0.0425f, translation + getHeight() * 0.5f);
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
