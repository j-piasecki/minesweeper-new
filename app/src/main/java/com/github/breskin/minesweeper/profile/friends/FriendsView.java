package com.github.breskin.minesweeper.profile.friends;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.MotionEvent;

import com.github.breskin.minesweeper.DataManager;
import com.github.breskin.minesweeper.R;
import com.github.breskin.minesweeper.RenderView;
import com.github.breskin.minesweeper.Theme;
import com.github.breskin.minesweeper.generic.ListRenderer;
import com.github.breskin.minesweeper.generic.View;
import com.github.breskin.minesweeper.generic.buttons.Button;
import com.github.breskin.minesweeper.generic.buttons.ImageButton;
import com.github.breskin.minesweeper.generic.ListEntry;
import com.github.breskin.minesweeper.home.HomeView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;

public class FriendsView extends View {

    private float offset = 0;
    private Paint paint;

    private ImageButton requestsButton;

    private ArrayList<ListEntry> listEntries;
    private ListRenderer listRenderer;

    private long lastUpdate;

    public FriendsView(final RenderView renderView) {
        super(renderView);

        paint = new Paint();
        paint.setAntiAlias(true);

        listEntries = new ArrayList<>();
        listRenderer = new ListRenderer(listEntries);

        requestsButton = new ImageButton(renderView.getContext(), R.drawable.ic_friend_requests_button);
        requestsButton.setCallback(new Button.ClickCallback() {
            @Override
            public void onClick() {
                HomeView.Transition transition = new HomeView.Transition(RenderView.ViewType.FriendsRequests);
                transition.setOrigin(new PointF(requestsButton.getPosition().x + requestsButton.getSize().y / 2, requestsButton.getPosition().y + requestsButton.getSize().y / 2));
                renderView.switchView(transition);
            }
        });
    }

    @Override
    public void update() {
        super.update();

        offset += (0 - offset) * 0.1f;

        requestsButton.update();
        requestsButton.setPosition(new PointF(RenderView.VIEW_WIDTH - requestsButton.getSize().x - RenderView.SIZE * 0.025f, RenderView.SIZE * 0.15f + offset));

        listRenderer.setMarginTop(RenderView.VIEW_WIDTH * 0.325f + offset);
        listRenderer.update();

        if (Calendar.getInstance().getTimeInMillis() - lastUpdate > 3900)
            updateStatus();
    }

    private void updateStatus() {
        FriendManager.getFriendsLock().lock();
        for (Friend friend : FriendManager.getFriends()) {
            if (friend.isActive())
                friend.updateStatus();
        }
        FriendManager.getFriendsLock().unlock();

        lastUpdate = Calendar.getInstance().getTimeInMillis();
    }

    @Override
    public void render(Canvas canvas) {
        paint.setTextSize(RenderView.VIEW_WIDTH * 0.1f);

        listRenderer.render(canvas);

        paint.setColor(Theme.getColor(Theme.ColorType.Background));
        canvas.drawRect(0, 0, RenderView.VIEW_WIDTH, RenderView.VIEW_WIDTH * 0.325f, paint);

        paint.setColor(Theme.getColor(Theme.ColorType.HubText));
        canvas.drawText(DataManager.FRIENDS_VIEW_HEADER, (RenderView.VIEW_WIDTH - paint.measureText(DataManager.FRIENDS_VIEW_HEADER)) * 0.5f, RenderView.VIEW_WIDTH * 0.15f + paint.getTextSize() + offset, paint);

        requestsButton.render(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (requestsButton.onTouchEvent(event)) return true;

        if (listRenderer.onTouchEvent(event)) return true;

        return false;
    }

    @Override
    public void onThemeChanged() {
        requestsButton.setIcon(renderView.getContext(), R.drawable.ic_friend_requests_button);
    }

    @Override
    public void open() {
        super.open();

        offset = RenderView.VIEW_WIDTH * 0.1f;

        listEntries.clear();

        FriendManager.getFriendsLock().lock();
        for (Friend friend : FriendManager.getFriends()) {
            if (!friend.isRemoved()) {
                friend.updateStatus();
                listEntries.add(new FriendListEntry(friend, renderView));
            }
        }
        FriendManager.getFriendsLock().unlock();

        lastUpdate = Calendar.getInstance().getTimeInMillis();

        listEntries.sort(new Comparator<ListEntry>() {
            @Override
            public int compare(ListEntry o1, ListEntry o2) {
                return (int)Math.signum(((FriendListEntry)o2).getFriend().getLastSeen() - ((FriendListEntry)o1).getFriend().getLastSeen());
            }
        });
    }
}
