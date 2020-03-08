package com.github.breskin.minesweeper.generic;

import android.graphics.Canvas;

import com.github.breskin.minesweeper.RenderView;

public class Transition {

    private RenderView.ViewType targetView;

    protected ViewChangeCallback viewChangeCallback;
    protected TransitionFinishedCallback transitionFinishedCallback;

    public Transition(RenderView.ViewType target) {
        this.targetView = target;
    }

    public void update() {

    }

    public void render(Canvas canvas) {

    }

    public RenderView.ViewType getTargetView() {
        return targetView;
    }

    public void setTransitionFinishedCallback(TransitionFinishedCallback transitionFinishedCallback) {
        this.transitionFinishedCallback = transitionFinishedCallback;
    }

    public void setViewChangeCallback(ViewChangeCallback viewChangeCallback) {
        this.viewChangeCallback = viewChangeCallback;
    }

    public boolean passTouchEvents() {
        return false;
    }

    public interface ViewChangeCallback {
        void onViewChange();
    }

    public interface TransitionFinishedCallback {
        void onFinished();
    }
}
