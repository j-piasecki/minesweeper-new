package com.github.breskin.minesweeper.profile.friends;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.github.breskin.minesweeper.DataManager;
import com.github.breskin.minesweeper.MainActivity;
import com.github.breskin.minesweeper.RenderView;
import com.github.breskin.minesweeper.Theme;
import com.github.breskin.minesweeper.generic.ListEntry;
import com.github.breskin.minesweeper.generic.ListRenderer;
import com.github.breskin.minesweeper.generic.View;
import com.github.breskin.minesweeper.generic.buttons.Button;
import com.github.breskin.minesweeper.generic.buttons.DefaultButton;

import java.util.ArrayList;

public class FriendsRequestsView extends View {

    private float offset = 0;
    private Paint paint;

    private DefaultButton sendInviteButton;

    private ArrayList<ListEntry> listEntries;
    private ListRenderer listRenderer;

    public FriendsRequestsView(final RenderView renderView) {
        super(renderView);

        paint = new Paint();
        paint.setAntiAlias(true);

        listEntries = new ArrayList<>();
        listRenderer = new ListRenderer(listEntries);

        sendInviteButton = new DefaultButton(DataManager.INVITE_FRIENDS_VIEW_INVITE_BUTTON, true);
        sendInviteButton.setCallback(new Button.ClickCallback() {
            @Override
            public void onClick() {
                ((MainActivity)renderView.getContext()).showInviteFriendUI();
            }
        });
    }

    @Override
    public void update() {
        super.update();

        offset += (0 - offset) * 0.1f;

        for (int i = 0; i < listEntries.size(); i++) {
            FriendRequestListEntry entry = (FriendRequestListEntry)listEntries.get(i);

            if (entry.isCompleted()) {
                listEntries.remove(i);
                i--;
            }
        }

        listRenderer.setMarginTop(RenderView.VIEW_WIDTH * 0.325f + offset);
        listRenderer.setMarginBottom(RenderView.VIEW_WIDTH * 0.185f);
        listRenderer.update();

        sendInviteButton.setWidth(RenderView.VIEW_WIDTH * 0.55f);
        sendInviteButton.update();
        sendInviteButton.setPosition(new PointF((RenderView.VIEW_WIDTH - sendInviteButton.getSize().x) * 0.5f, RenderView.VIEW_HEIGHT - RenderView.VIEW_WIDTH * 0.05f - sendInviteButton.getSize().y + offset));
    }

    @Override
    public void render(Canvas canvas) {
        paint.setTextSize(RenderView.VIEW_WIDTH * 0.1f);

        listRenderer.render(canvas);

        paint.setColor(Theme.getColor(Theme.ColorType.Background));
        canvas.drawRect(0, 0, RenderView.VIEW_WIDTH, RenderView.VIEW_WIDTH * 0.325f, paint);

        paint.setColor(Theme.getColor(Theme.ColorType.HubText));
        canvas.drawText(DataManager.INVITE_FRIENDS_VIEW_HEADER, (RenderView.VIEW_WIDTH - paint.measureText(DataManager.INVITE_FRIENDS_VIEW_HEADER)) * 0.5f, RenderView.VIEW_WIDTH * 0.15f + paint.getTextSize() + offset, paint);

        paint.setColor(Theme.getColor(Theme.ColorType.Background));
        canvas.drawRect(0, RenderView.VIEW_HEIGHT - RenderView.VIEW_WIDTH * 0.185f, RenderView.VIEW_WIDTH, RenderView.VIEW_HEIGHT, paint);
        sendInviteButton.render(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (sendInviteButton.onTouchEvent(event)) return true;
        if (listRenderer.onTouchEvent(event)) return true;

        return false;
    }

    @Override
    public void open() {
        super.open();

        offset = RenderView.VIEW_WIDTH * 0.1f;

        listEntries.clear();

        FriendManager.getRequestsLock().lock();
        for (Friend friend : FriendManager.getFriendRequests())
            listEntries.add(new FriendRequestListEntry(friend));
        FriendManager.getRequestsLock().unlock();
    }
}
