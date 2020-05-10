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
import com.github.breskin.minesweeper.home.SettingsView;
import com.github.breskin.minesweeper.particles.ParticleSystem;
import com.github.breskin.minesweeper.profile.ProfileView;
import com.github.breskin.minesweeper.profile.UserProfile;
import com.github.breskin.minesweeper.profile.friends.FriendDetailsView;
import com.github.breskin.minesweeper.profile.friends.FriendManager;
import com.github.breskin.minesweeper.profile.friends.FriendsRequestsView;
import com.github.breskin.minesweeper.profile.friends.FriendsView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class RenderView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    public static int VIEW_WIDTH, VIEW_HEIGHT, FRAME_TIME, SIZE;

    public enum ViewType { None, Home, Game, CustomField, Settings, Friends, FriendsRequests, Profile, FriendDetails }

    public static Context CONTEXT;

    private Thread gameThread;
    private boolean threadRunning;

    private ViewType currentView;

    private Transition transition;
    private ParticleSystem particleSystem;

    private HomeView homeView;
    private GameView gameView;
    private CustomFieldView customFieldView;
    private SettingsView settingsView;
    private FriendsView friendsView;
    private FriendsRequestsView friendsRequestsView;
    private ProfileView profileView;
    private FriendDetailsView friendDetailsView;

    private int timeSinceAccountUpdate = 10000;
    private int friendStatusUpdateCounter = 0;

    public RenderView(Context context) {
        super(context);
        DataManager.load(context);
        FriendManager.load(context);
        UserProfile.load(context);

        CONTEXT = context;

        currentView = ViewType.None;

        particleSystem = new ParticleSystem();

        homeView = new HomeView(this);
        gameView = new GameView(this);
        customFieldView = new CustomFieldView(this);
        settingsView = new SettingsView(this);
        friendsView = new FriendsView(this);
        friendsRequestsView = new FriendsRequestsView(this);
        profileView = new ProfileView(this);
        friendDetailsView = new FriendDetailsView(this);

        getHolder().addCallback(this);

        if (DataManager.FIRST_LAUNCH_TODAY) {
            Toast toast = Toast.makeText(context, context.getString(R.string.daily_second_lives, DataManager.SECOND_LIVES_DAILY_BONUS), Toast.LENGTH_SHORT);
            ((TextView)(toast.getView().findViewById(android.R.id.message))).setGravity(Gravity.CENTER);
            toast.show();
        }
    }

    private void render(Canvas canvas) {
        switch (currentView) {
            case Home: homeView.update(); break;
            case Game: gameView.update(); break;
            case CustomField: customFieldView.update(); break;
            case Settings: settingsView.update(); break;
            case Friends: friendsView.update(); break;
            case FriendsRequests: friendsRequestsView.update(); break;
            case Profile: profileView.update(); break;
            case FriendDetails: friendDetailsView.update(); break;
        }

        switch (currentView) {
            case Home: homeView.render(canvas); break;
            case Game: gameView.render(canvas); break;
            case CustomField: customFieldView.render(canvas); break;
            case Settings: settingsView.render(canvas); break;
            case Friends: friendsView.render(canvas); break;
            case FriendsRequests: friendsRequestsView.render(canvas); break;
            case Profile: profileView.render(canvas); break;
            case FriendDetails: friendDetailsView.render(canvas); break;
        }

        particleSystem.update(gameView.getGameLogic());
        particleSystem.render(gameView.getGameLogic(), canvas);

        if (transition != null) {
            transition.update();

            if (transition != null) transition.render(canvas);
        }
    }

    private void updateAccount() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("active").setValue(Calendar.getInstance().getTimeInMillis());

            friendStatusUpdateCounter++;

            if ((friendStatusUpdateCounter == 15 && currentView == ViewType.Game) || (currentView != ViewType.Game && friendStatusUpdateCounter == 5)) {
                FriendManager.updateStatuses();

                friendStatusUpdateCounter = 0;
            }
        }
    }

    public ParticleSystem getParticleSystem() {
        return particleSystem;
    }

    public GameView getGameView() {
        return gameView;
    }

    public FriendDetailsView getFriendDetailsView() {
        return friendDetailsView;
    }

    public void themeChanged() {
        homeView.onThemeChanged();
        gameView.onThemeChanged();
        customFieldView.onThemeChanged();
        settingsView.onThemeChanged();
        friendsView.onThemeChanged();
        friendsRequestsView.onThemeChanged();
        profileView.onThemeChanged();
        friendDetailsView.onThemeChanged();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)  {
        if (transition != null && !transition.passTouchEvents()) return true;

        switch (currentView) {
            case Home: if (homeView.onTouchEvent(event)) return true; break;
            case Game: if (gameView.onTouchEvent(event)) return true; break;
            case CustomField: if (customFieldView.onTouchEvent(event)) return true; break;
            case Settings: if (settingsView.onTouchEvent(event)) return true; break;
            case Friends: if (friendsView.onTouchEvent(event)) return true; break;
            case FriendsRequests: if (friendsRequestsView.onTouchEvent(event)) return true; break;
            case Profile: if (profileView.onTouchEvent(event)) return true; break;
            case FriendDetails: if (friendDetailsView.onTouchEvent(event)) return true; break;
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

                switch (currentView) {
                    case Game:
                        gameView.open();
                        gameView.reset();
                        break;

                    case Home:
                        homeView.open();
                        break;

                    case CustomField:
                        customFieldView.open();
                        break;

                    case Settings:
                        settingsView.open();
                        break;

                    case Friends:
                        friendsView.open();
                        break;

                    case FriendsRequests:
                        friendsRequestsView.open();
                        break;

                    case Profile:
                        profileView.open();
                        break;

                    case FriendDetails:
                        friendDetailsView.open();
                        break;
                }
            }
        });
    }

    public void showLoginScreen() {
        MainActivity mainActivity = (MainActivity)CONTEXT;

        if (mainActivity != null)
            mainActivity.showLoginScreen();
    }

    @Override
    public void run() {
        Canvas canvas;

        while (threadRunning) {
            if (getHolder().getSurface().isValid()) {
                if (currentView == ViewType.None) {
                    currentView = ViewType.Home;
                    homeView.open();
                }

                long time = System.nanoTime() / 1000000;

                canvas = getHolder().lockHardwareCanvas();

                if (canvas == null)
                    continue;

                canvas.save();
                canvas.drawColor(Theme.getColor(Theme.ColorType.Background));

                render(canvas);

                canvas.restore();
                getHolder().unlockCanvasAndPost(canvas);

                if (System.nanoTime() / 1000000 - time < 16)
                    try { Thread.sleep(16 - System.nanoTime() / 1000000 + time); } catch (Exception e) {}

                FRAME_TIME = (int)(System.nanoTime() / 1000000 - time);

                timeSinceAccountUpdate += FRAME_TIME;

                if (timeSinceAccountUpdate > 4000) {
                    timeSinceAccountUpdate = 0;

                    updateAccount();
                }
            }
        }
    }

    public boolean onBackPressed() {
        if (currentView == ViewType.Game || currentView == ViewType.CustomField || currentView == ViewType.Settings || currentView == ViewType.Profile) {
            Transition transition = new HomeView.Transition(ViewType.Home);

            if (currentView == ViewType.Game) {
                gameView.getGameLogic().disableSecondLive();
                gameView.getGameLogic().onGameLost();

                transition.setViewExitCallback(new Transition.ViewExitCallback() {
                    @Override
                    public void onExit() {
                        particleSystem.clear();
                    }
                });
            }

            switchView(transition);

            return true;
        } else if (currentView == ViewType.Friends) {
            Transition transition = new HomeView.Transition(ViewType.Profile);
            switchView(transition);

            return true;
        } else if (currentView == ViewType.FriendsRequests || currentView == ViewType.FriendDetails) {
            Transition transition = new HomeView.Transition(ViewType.Friends);
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
        if  (!DataManager.VIBRATIONS_ENABLED)
            return;

        Vibrator v = (Vibrator) CONTEXT.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(VibrationEffect.createOneShot(time, VibrationEffect.DEFAULT_AMPLITUDE));
    }
}
