package com.github.breskin.minesweeper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.breskin.minesweeper.game.GameView;
import com.github.breskin.minesweeper.generic.Transition;
import com.github.breskin.minesweeper.home.CustomFieldView;
import com.github.breskin.minesweeper.home.HomeView;
import com.github.breskin.minesweeper.particles.ParticleSystem;

public class RenderView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    public static int VIEW_WIDTH, VIEW_HEIGHT, FRAME_TIME, SIZE;

    public enum ViewType { None, Home, Game, CustomField }

    public static Context CONTEXT;

    private Thread gameThread;
    private boolean threadRunning;

    private ViewType currentView;

    private Transition transition;
    private ParticleSystem particleSystem;

    private HomeView homeView;
    private GameView gameView;
    private CustomFieldView customFieldView;

    public RenderView(Context context) {
        super(context);
        DataManager.load(context);

        CONTEXT = context;

        currentView = ViewType.None;

        particleSystem = new ParticleSystem();

        homeView = new HomeView(this);
        gameView = new GameView(this);
        customFieldView = new CustomFieldView(this);

        getHolder().addCallback(this);

        if (DataManager.FIRST_LAUNCH_TODAY) {
            Toast toast = Toast.makeText(context, context.getString(R.string.daily_second_lives, DataManager.SECOND_LIFES_DAILY_BONUS), Toast.LENGTH_SHORT);
            ((TextView)(toast.getView().findViewById(android.R.id.message))).setGravity(Gravity.CENTER);
            toast.show();
        }
    }

    private void render(Canvas canvas) {
        switch (currentView) {
            case Home: homeView.update(); break;
            case Game: gameView.update(); break;
            case CustomField: customFieldView.update(); break;
        }

        switch (currentView) {
            case Home: homeView.render(canvas); break;
            case Game: gameView.render(canvas); break;
            case CustomField: customFieldView.render(canvas); break;
        }

        particleSystem.update(gameView.getGameLogic());
        particleSystem.render(gameView.getGameLogic(), canvas);

        if (transition != null) {
            transition.update();

            if (transition != null) transition.render(canvas);
        }
    }

    public ParticleSystem getParticleSystem() {
        return particleSystem;
    }

    public GameView getGameView() {
        return gameView;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)  {
        if (transition != null && !transition.passTouchEvents()) return true;

        switch (currentView) {
            case Home: if (homeView.onTouchEvent(event)) return true; break;
            case Game: if (gameView.onTouchEvent(event)) return true; break;
            case CustomField: if (customFieldView.onTouchEvent(event)) return true; break;
        }

        return true;
    }

    public void switchView(Transition t) {
        transition = t;
        transition.setTransitionFinishedCallback(new Transition.TransitionFinishedCallback() {
            @Override
            public void onFinished() {
                transition = null;
            }
        });

        transition.setViewChangeCallback(new Transition.ViewChangeCallback() {
            @Override
            public void onViewChange() {
                currentView = transition.getTargetView();

                if (currentView == ViewType.Game)
                    gameView.reset();
            }
        });
    }

    @Override
    public void run() {
        Canvas canvas;

        while (threadRunning) {
            if (getHolder().getSurface().isValid()) {
                if (currentView == ViewType.None) {
                    currentView = ViewType.Home;
                }

                long time = System.nanoTime() / 1000000;

                canvas = getHolder().lockHardwareCanvas();

                if (canvas == null)
                    continue;

                canvas.save();
                canvas.drawColor(Color.BLACK);

                render(canvas);

                canvas.restore();
                getHolder().unlockCanvasAndPost(canvas);

                if (System.nanoTime() / 1000000 - time < 16)
                    try { Thread.sleep(16 - System.nanoTime() / 1000000 + time); } catch (Exception e) {}

                FRAME_TIME = (int)(System.nanoTime() / 1000000 - time);
            }
        }
    }

    public boolean onBackPressed() {
        if (currentView == ViewType.Game || currentView == ViewType.CustomField) {
            Transition transition = new HomeView.Transition(ViewType.Home);
            switchView(transition);

            return true;
        }

        return false;
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        threadRunning = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) { }
    }

    public void pause() {
        threadRunning = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) { }
    }

    public void resume() {
        threadRunning = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        VIEW_WIDTH = w;
        VIEW_HEIGHT = h;

        getHolder().setFixedSize(VIEW_WIDTH, VIEW_HEIGHT);

        SIZE = Math.min(VIEW_WIDTH, VIEW_HEIGHT);
    }

    public static void vibrate(int time) {
        /*if  (!Config.VIBRATIONS_ENABLED)
            return;*/

        Vibrator v = (Vibrator) CONTEXT.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(time, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(time);
        }
    }
}
