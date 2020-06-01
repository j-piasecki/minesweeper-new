package com.github.breskin.minesweeper.popups;

import android.graphics.Canvas;
import android.view.MotionEvent;

import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;

public class PopupManager {

    private ReentrantLock lock;
    private LinkedList<Popup> popupQueue;

    public PopupManager() {
        lock = new ReentrantLock();
        popupQueue = new LinkedList<>();
    }

    public void update() {
        lock.lock();

        if (!popupQueue.isEmpty() && popupQueue.peek().isDismissed())
            popupQueue.poll();

        if (!popupQueue.isEmpty())
            popupQueue.peek().update();

        lock.unlock();
    }

    public void render(Canvas canvas) {
        lock.lock();

        if (!popupQueue.isEmpty())
            popupQueue.peek().render(canvas);

        lock.unlock();
    }

    public boolean onTouchEvent(MotionEvent event) {
        lock.lock();

        if (!popupQueue.isEmpty()) {
            boolean result = popupQueue.peek().onTouchEvent(event);

            lock.unlock();
            return result;
        }

        lock.unlock();
        return false;
    }

    public void addPopup(Popup p) {
        lock.lock();
        popupQueue.add(p);
        lock.unlock();
    }

    public void onThemeChanged() {
        lock.lock();

        for (Popup p : popupQueue)
            p.onThemeChanged();

        lock.unlock();
    }
}
