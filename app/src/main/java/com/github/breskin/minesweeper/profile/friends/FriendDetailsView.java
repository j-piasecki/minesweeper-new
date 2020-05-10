package com.github.breskin.minesweeper.profile.friends;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;

import com.github.breskin.minesweeper.DataManager;
import com.github.breskin.minesweeper.RenderView;
import com.github.breskin.minesweeper.Theme;
import com.github.breskin.minesweeper.generic.ListEntry;
import com.github.breskin.minesweeper.generic.ListRenderer;
import com.github.breskin.minesweeper.generic.View;
import com.github.breskin.minesweeper.profile.ListBestTimesEntry;

import java.util.ArrayList;

public class FriendDetailsView extends View {

    private float offset = 0;
    private Paint paint;

    private Friend friend;

    private ArrayList<ListEntry> listEntries;
    private ListRenderer listRenderer;

    public FriendDetailsView(final RenderView renderView) {
        super(renderView);

        paint = new Paint();
        paint.setAntiAlias(true);

        listEntries = new ArrayList<>();
        listRenderer = new ListRenderer(listEntries);

        listEntries.add(new FriendNameEntry());
        listEntries.add(new ListBestTimesEntry());
    }

    @Override
    public void update() {
        super.update();

        offset += (0 - offset) * 0.1f;

        listRenderer.setMarginTop(RenderView.VIEW_WIDTH * 0.325f + offset);
        listRenderer.update();
    }

    @Override
    public void render(Canvas canvas) {
        paint.setTextSize(RenderView.VIEW_WIDTH * 0.1f);

        listRenderer.render(canvas);

        paint.setColor(Theme.getColor(Theme.ColorType.Background));
        canvas.drawRect(0, 0, RenderView.VIEW_WIDTH, RenderView.VIEW_WIDTH * 0.325f, paint);

        paint.setColor(Theme.getColor(Theme.ColorType.HubText));
        canvas.drawText(DataManager.FRIEND_DETAILS_VIEW_HEADER, (RenderView.VIEW_WIDTH - paint.measureText(DataManager.FRIEND_DETAILS_VIEW_HEADER)) * 0.5f, RenderView.VIEW_WIDTH * 0.15f + paint.getTextSize() + offset, paint);
    }

    public void setFriend(Friend friend) {
        this.friend = friend;

        ((FriendNameEntry)listEntries.get(0)).setName(friend.getDisplayName());
        ((ListBestTimesEntry)listEntries.get(1)).setUser(friend.getUid());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (listRenderer.onTouchEvent(event)) return true;

        return false;
    }

    @Override
    public void open() {
        super.open();

        offset = RenderView.VIEW_WIDTH * 0.1f;
    }
}
