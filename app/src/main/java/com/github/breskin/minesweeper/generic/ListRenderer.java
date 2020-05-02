package com.github.breskin.minesweeper.generic;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;

import com.github.breskin.minesweeper.DataManager;
import com.github.breskin.minesweeper.RenderView;
import com.github.breskin.minesweeper.Theme;

import java.util.List;

public class ListRenderer {

    private static Paint paint = new Paint();

    private List<ListEntry> source;
    private float marginTop = 0, height = 0, marginBottom = 0;
    private float translation = 0;

    private PointF touchDownPoint, lastTouchPoint, translationChange, velocity;
    private boolean touchDown, moving;

    private String listEmptyMessage;

    public ListRenderer(List<ListEntry> list) {
        source = list;

        paint.setAntiAlias(true);

        this.touchDownPoint = new PointF();
        this.lastTouchPoint = new PointF();
        this.translationChange = new PointF();
        this.velocity = new PointF();
        touchDown = false;

        setListEmptyMessage(DataManager.LIST_RENDERER_EMPTY_MESSAGE);
    }

    private void updateScrolling() {
        if (touchDown) {
            if (moving) {
                velocity.x = translationChange.x;
                velocity.y = translationChange.y;

                translation += translationChange.y;
            }

            translationChange.x = translationChange.y = 0;
        } else {
            translation += velocity.y;

            velocity.x *= 0.88;
            velocity.y *= 0.88;
        }


        float maxTranslation = RenderView.VIEW_HEIGHT - marginTop - height - marginBottom;

        if (translation > 0 || maxTranslation > 0) {
            translation = 0;
            velocity.y = 0;
        } else if (maxTranslation < 0 && translation < maxTranslation) {
            translation = maxTranslation;
            velocity.y = 0;
        }
    }

    public void update() {
        updateScrolling();

        float margin = marginTop + translation;

        for (ListEntry entry : source) {
            entry.update(margin);

            margin += entry.getHeight();
        }

        height = margin - marginTop - translation;
    }

    public void render(Canvas canvas) {
        if (source.isEmpty()) {
            if (listEmptyMessage != null && listEmptyMessage.length() > 0) {
                paint.setTextSize(RenderView.VIEW_WIDTH * 0.05f);
                paint.setColor(Theme.getColor(Theme.ColorType.ListEmptyText));

                canvas.drawText(listEmptyMessage, (RenderView.VIEW_WIDTH - paint.measureText(listEmptyMessage)) * 0.5f, marginTop + (RenderView.VIEW_HEIGHT - marginTop - marginBottom) * 0.5f, paint);
            }
        } else {
            for (ListEntry entry : source)
                if (entry.isVisible())
                    entry.render(canvas);
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchDown = true;
                moving = false;

                touchDownPoint.x = x;
                touchDownPoint.y = y;
                break;

            case MotionEvent.ACTION_MOVE:
                if (touchDown) {
                    translationChange.x = x - lastTouchPoint.x;
                    translationChange.y = y - lastTouchPoint.y;

                    if (!moving && (Math.abs(x - touchDownPoint.x) > RenderView.SIZE * 0.01 || Math.abs(y - touchDownPoint.y) > RenderView.SIZE * 0.01))
                        moving = true;
                }
                break;

            case MotionEvent.ACTION_UP:
                touchDown = false;
                break;
        }

        lastTouchPoint.x = x;
        lastTouchPoint.y = y;

        for (ListEntry entry : source)
            if (entry.onTouchEvent(event))
                return true;

        return false;
    }

    public void setMarginTop(float marginTop) {
        this.marginTop = marginTop;
    }

    public void setMarginBottom(float marginBottom) {
        this.marginBottom = marginBottom;
    }

    public void setListEmptyMessage(String listEmptyMessage) {
        this.listEmptyMessage = listEmptyMessage;
    }
}
