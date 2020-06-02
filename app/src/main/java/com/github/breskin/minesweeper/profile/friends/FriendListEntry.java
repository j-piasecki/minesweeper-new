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
import com.github.breskin.minesweeper.home.HomeView;

public class FriendListEntry extends ListEntry {

    private RenderView renderView;
    private Friend friend;

    private String displayName;

    public FriendListEntry(final Friend friend, RenderView renderView) {
        this.renderView = renderView;
        this.friend = friend;

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
    }

    @Override
    public void render(Canvas canvas) {
        super.render(canvas);

        drawActiveIndicator(canvas);

        paint.setTextSize(getHeight() * 0.3f);
        paint.setColor(Theme.getColor(Theme.ColorType.ListEntryText));

        canvas.drawText(displayName, RenderView.VIEW_WIDTH * 0.055f, translation + (getHeight() - paint.getTextSize()) * 0.35f + paint.getTextSize(), paint);
    }

    private void drawActiveIndicator(Canvas canvas) {
        if (friend.isActive())
            paint.setColor(Theme.getColor(Theme.ColorType.FriendListEntryActive));
        else
            paint.setColor(Theme.getColor(Theme.ColorType.FriendListEntryInactive));

        float indicatorSize = RenderView.VIEW_WIDTH * 0.007f;
        canvas.save();
        canvas.translate(RenderView.VIEW_WIDTH * 0.1f, translation + getHeight() * 0.7f);
        canvas.rotate(45);
        canvas.drawRect(-indicatorSize, -indicatorSize, indicatorSize, indicatorSize, paint);
        canvas.restore();

        paint.setTextSize(getHeight() * 0.15f);
        canvas.drawText(friend.getStateText(), RenderView.VIEW_WIDTH * 0.125f, translation + getHeight() * 0.7f + paint.getTextSize() * 0.35f, paint);
    }

    @Override
    protected boolean onTouch(float x, float y) {
        HomeView.Transition transition = new HomeView.Transition(RenderView.ViewType.FriendDetails);
        transition.setOrigin(new PointF(x, y));
        renderView.switchView(transition);
        renderView.getFriendDetailsView().setFriend(friend);

        return true;
    }

    @Override
    public float getHeight() {
        return RenderView.VIEW_WIDTH * 0.2f;
    }

    public Friend getFriend() {
        return friend;
    }
}
