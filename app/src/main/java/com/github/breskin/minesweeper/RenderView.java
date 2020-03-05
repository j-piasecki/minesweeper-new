package com.github.breskin.minesweeper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class RenderView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    public static int VIEW_WIDTH, VIEW_HEIGHT, FRAME_TIME;

    private static Context CONTEXT;

    private Thread gameThread;
    private boolean threadRunning;

    public RenderView(Context context) {
        super(context);
        CONTEXT = context;

        getHolder().addCallback(this);
    }

    private void render(Canvas canvas) {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event)  {


        return true;
    }

    @Override
    public void run() {
        Canvas canvas;

        while (threadRunning) {
            if (getHolder().getSurface().isValid()) {
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
