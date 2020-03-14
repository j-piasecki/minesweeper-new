package com.github.breskin.minesweeper.game;

import android.util.Log;

import com.github.breskin.minesweeper.DataManager;
import com.github.breskin.minesweeper.RenderView;
import com.github.breskin.minesweeper.generic.View;
import com.github.breskin.minesweeper.particles.ParticleSystem;

public class GameLogic {

    private RenderView renderView;
    private Callback callback;

    private Camera camera;
    private Minefield minefield;

    private int flaggedMines;
    private boolean gameWon;
    private boolean gameLost;
    private boolean secondLifeUsed = false;

    private int gameDuration;

    public GameLogic(RenderView renderView) {
        this.renderView = renderView;

        this.camera = new Camera();
        this.minefield = new Minefield();
    }

    public void update() {
        camera.update();
        minefield.update();

        if (!isGameFinished() && minefield.isGameWon()) {
            onGameWon();
        }

        if (!isGamePaused()) {
            gameDuration += RenderView.FRAME_TIME;
        }
    }

    public Camera getCamera() {
        return camera;
    }

    public Minefield getMinefield() {
        return minefield;
    }

    public void init(int width, int height, int mines) {
        minefield.init(width, height, mines);

        flaggedMines = 0;
        gameDuration = 0;
        gameLost = gameWon = secondLifeUsed = false;

        camera.reset();
        camera.getPosition().x = width * 0.5f - 0.5f;
        camera.getPosition().y = height * 0.5f - 0.5f;
    }

    public void onGameLost() {
        gameLost = true;

        if (callback != null)
            callback.onGameLost();
    }

    public void onGameWon() {
        gameWon = true;

        if (callback != null)
            callback.onGameWon();
    }

    public void useSecondLife() {
        gameLost = false;
        secondLifeUsed = true;

        DataManager.onSecondLifeUsed();
    }

    public ParticleSystem getParticleSystem() {
        return renderView.getParticleSystem();
    }

    public void increaseFlaggedMines() {
        flaggedMines++;
    }

    public void decreaseFlaggedMines() {
        flaggedMines--;
    }

    public int getFlaggedMines() {
        return flaggedMines;
    }

    public int getGameDuration() {
        return gameDuration;
    }

    public boolean isGameWon() {
        return gameWon;
    }

    public boolean isGameLost() {
        return gameLost;
    }

    public boolean isGameFinished() {
        return isGameLost() || isGameWon();
    }

    public boolean hasSecondLife() {
        return !secondLifeUsed && DataManager.SECOND_LIFES_COUNT > 0;
    }

    public boolean isGamePaused() {
        return isGameFinished();
    }


    public RenderView getRenderView() {
        return renderView;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {
        void onGameLost();
        void onGameWon();
    }
}
