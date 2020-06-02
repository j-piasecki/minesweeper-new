package com.github.breskin.minesweeper.profile.friends;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.MotionEvent;

import com.github.breskin.minesweeper.DataManager;
import com.github.breskin.minesweeper.MainActivity;
import com.github.breskin.minesweeper.R;
import com.github.breskin.minesweeper.RenderView;
import com.github.breskin.minesweeper.Theme;
import com.github.breskin.minesweeper.generic.ListEntry;
import com.github.breskin.minesweeper.generic.ListRenderer;
import com.github.breskin.minesweeper.generic.View;
import com.github.breskin.minesweeper.generic.buttons.Button;
import com.github.breskin.minesweeper.generic.buttons.ImageButton;
import com.github.breskin.minesweeper.profile.ListBestTimesEntry;

import java.util.ArrayList;
import java.util.Calendar;

public class FriendDetailsView extends View {

    private float offset = 0;
    private Paint paint;

    private Friend friend;

    private ArrayList<ListEntry> listEntries;
    private ListRenderer listRenderer;

    private ImageButton deleteButton;

    private long lastUpdate;

    public FriendDetailsView(final RenderView renderView) {
        super(renderView);

        paint = new Paint();
        paint.setAntiAlias(true);

        listEntries = new ArrayList<>();
        listRenderer = new ListRenderer(listEntries);

        listEntries.add(new FriendNameEntry());
        listEntries.add(new ListBestTimesEntry());

        deleteButton = new ImageButton(renderView.getContext(), R.drawable.ic_delete);
        deleteButton.setCallback(new Button.ClickCallback() {
            @Override
            public void onClick() {
                ((MainActivity)renderView.getContext()).showFriendDeletionConfirmationUI(friend);
            }
        });
    }

    @Override
    public void update() {
        super.update();

        offset += (0 - offset) * 0.1f;

        listRenderer.setMarginTop(RenderView.VIEW_WIDTH * 0.325f + offset);
        listRenderer.update();

        if (Calendar.getInstance().getTimeInMillis() - lastUpdate > 3000) {
            friend.updateStatus();
            lastUpdate = Calendar.getInstance().getTimeInMillis();
        }

        deleteButton.setPosition(new PointF(RenderView.VIEW_WIDTH - deleteButton.getSize().x - RenderView.SIZE * 0.025f, RenderView.SIZE * 0.15f + offset));
        deleteButton.update();
    }

    @Override
    public void render(Canvas canvas) {
        paint.setTextSize(RenderView.VIEW_WIDTH * 0.1f);

        listRenderer.render(canvas);

        paint.setColor(Theme.getColor(Theme.ColorType.Background));
        canvas.drawRect(0, 0, RenderView.VIEW_WIDTH, RenderView.VIEW_WIDTH * 0.325f, paint);

        paint.setColor(Theme.getColor(Theme.ColorType.HubText));
        canvas.drawText(DataManager.FRIEND_DETAILS_VIEW_HEADER, (RenderView.VIEW_WIDTH - paint.measureText(DataManager.FRIEND_DETAILS_VIEW_HEADER)) * 0.5f, RenderView.VIEW_WIDTH * 0.15f + paint.getTextSize() + offset, paint);

        deleteButton.render(canvas);
    }

    public void setFriend(Friend friend) {
        this.friend = friend;

        ((FriendNameEntry)listEntries.get(0)).setFriend(friend);
        ((ListBestTimesEntry)listEntries.get(1)).setUser(friend.getUid());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (deleteButton.onTouchEvent(event)) return true;
        if (listRenderer.onTouchEvent(event)) return true;

        return false;
    }

    @Override
    public void onThemeChanged() {
        deleteButton.setIcon(renderView.getContext(), R.drawable.ic_delete);
    }

    @Override
    public void open() {
        super.open();

        offset = RenderView.VIEW_WIDTH * 0.1f;
    }
}
