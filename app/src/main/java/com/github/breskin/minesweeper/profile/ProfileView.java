package com.github.breskin.minesweeper.profile;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.MotionEvent;

import com.github.breskin.minesweeper.DataManager;
import com.github.breskin.minesweeper.R;
import com.github.breskin.minesweeper.RenderView;
import com.github.breskin.minesweeper.Theme;
import com.github.breskin.minesweeper.generic.ListEntry;
import com.github.breskin.minesweeper.generic.ListRenderer;
import com.github.breskin.minesweeper.generic.View;
import com.github.breskin.minesweeper.generic.buttons.Button;
import com.github.breskin.minesweeper.generic.buttons.ImageButton;
import com.github.breskin.minesweeper.home.HomeView;
import com.github.breskin.minesweeper.profile.friends.ListUIDEntry;

import java.util.ArrayList;

public class ProfileView extends View {

    private float offset = 0;
    private Paint paint;

    private ImageButton friendsButton;

    private ArrayList<ListEntry> listEntries;
    private ListRenderer listRenderer;

    public ProfileView(final RenderView renderView) {
        super(renderView);

        paint = new Paint();
        paint.setAntiAlias(true);

        listEntries = new ArrayList<>();
        listRenderer = new ListRenderer(listEntries);

        listEntries.add(new ListNameEntry());
        listEntries.add(new ListUIDEntry());

        friendsButton = new ImageButton(renderView.getContext(), R.drawable.ic_friends);
        friendsButton.setCallback(new Button.ClickCallback() {
            @Override
            public void onClick() {
                HomeView.Transition transition = new HomeView.Transition(RenderView.ViewType.Friends);
                transition.setOrigin(new PointF(friendsButton.getPosition().x + friendsButton.getSize().y / 2, friendsButton.getPosition().y + friendsButton.getSize().y / 2));
                renderView.switchView(transition);
            }
        });
    }

    @Override
    public void update() {
        super.update();
        offset += (0 - offset) * 0.1f;

        listRenderer.setMarginTop(RenderView.VIEW_WIDTH * 0.325f + offset);
        listRenderer.update();

        friendsButton.update();
        friendsButton.setPosition(new PointF(RenderView.VIEW_WIDTH - friendsButton.getSize().x - RenderView.SIZE * 0.025f, RenderView.SIZE * 0.15f + offset));
    }

    @Override
    public void render(Canvas canvas) {
        paint.setTextSize(RenderView.VIEW_WIDTH * 0.1f);

        listRenderer.render(canvas);

        paint.setColor(Theme.getColor(Theme.ColorType.Background));
        canvas.drawRect(0, 0, RenderView.VIEW_WIDTH, RenderView.VIEW_WIDTH * 0.325f, paint);

        paint.setColor(Theme.getColor(Theme.ColorType.HubText));
        canvas.drawText(DataManager.PROFILE_VIEW_HEADER, (RenderView.VIEW_WIDTH - paint.measureText(DataManager.PROFILE_VIEW_HEADER)) * 0.5f, RenderView.VIEW_WIDTH * 0.15f + paint.getTextSize() + offset, paint);

        friendsButton.render(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (friendsButton.onTouchEvent(event)) return true;
        if (listRenderer.onTouchEvent(event)) return true;

        return super.onTouchEvent(event);
    }

    @Override
    public void open() {
        super.open();

        offset = RenderView.VIEW_WIDTH * 0.1f;
    }
}
