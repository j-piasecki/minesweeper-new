package com.github.breskin.minesweeper.profile.friends;

import android.graphics.Canvas;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;

import com.github.breskin.minesweeper.R;
import com.github.breskin.minesweeper.RenderView;
import com.github.breskin.minesweeper.Theme;
import com.github.breskin.minesweeper.generic.ListEntry;
import com.github.breskin.minesweeper.generic.buttons.Button;
import com.github.breskin.minesweeper.generic.buttons.ImageButton;

public class FriendRequestListEntry extends ListEntry {

    private Friend friend;

    private ImageButton acceptButton, declineButton;

    private boolean completed = false;

    public FriendRequestListEntry(final Friend friend) {
        this.friend = friend;

        acceptButton = new ImageButton(RenderView.CONTEXT, R.drawable.ic_accept_invite);
        acceptButton.setCallback(new Button.ClickCallback() {
            @Override
            public void onClick() {
                FriendManager.acceptInvite(friend);
                completed = true;
            }
        });

        declineButton = new ImageButton(RenderView.CONTEXT, R.drawable.ic_decline_invite);
        declineButton.setCallback(new Button.ClickCallback() {
            @Override
            public void onClick() {
                FriendManager.declineInvite(friend);
                completed = true;
            }
        });
    }

    public void update(float translation) {
        super.update(translation);

        declineButton.setPosition(new PointF(RenderView.VIEW_WIDTH * 0.975f - declineButton.getSize().x, translation + (getHeight() - declineButton.getSize().y) * 0.5f));
        acceptButton.setPosition(new PointF(declineButton.getPosition().x - acceptButton.getSize().x - RenderView.VIEW_WIDTH * 0.01f, declineButton.getPosition().y));

        acceptButton.update();
        declineButton.update();
    }

    @Override
    public void render(Canvas canvas) {
        super.render(canvas);

        paint.setTextSize(getHeight() * 0.3f);
        paint.setColor(Theme.getColor(Theme.ColorType.ListEntryText));

        canvas.drawText(friend.getDisplayName(), RenderView.VIEW_WIDTH * 0.085f, translation + (getHeight() - paint.getTextSize()) * 0.4f + paint.getTextSize(), paint);

        acceptButton.render(canvas);
        declineButton.render(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (acceptButton.onTouchEvent(event)) return true;
        if (declineButton.onTouchEvent(event)) return true;

        return super.onTouchEvent(event);
    }

    public float getHeight() {
        return RenderView.VIEW_WIDTH * 0.2f;
    }

    public Friend getFriend() {
        return friend;
    }

    public boolean isCompleted() {
        return completed;
    }
}
