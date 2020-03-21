package com.github.breskin.minesweeper.game;

import com.github.breskin.minesweeper.DataManager;
import com.github.breskin.minesweeper.RenderView;
import com.github.breskin.minesweeper.particles.ParticleSystem;

public class GameLogic {

    private RenderView renderView;

    private GameStartedCallback gameStartedCallback;
    private GameLostCallback gameLostCallback;
    private GameWonCallback gameWonCallback;

    private Camera camera;
    private Minefield minefield;

    private int flaggedMines;
    private boolean gameWon;
    private boolean gameLost;
    private boolean gameStarted;
    private boolean secondLifeUsed = false;
    private boolean secondLifeDisabled = false;

    private int gameDuration, bestTime;

    public GameLogic(RenderView renderView) {
        this.renderView = renderView;

        this.camera = new Camera();
        this.minefield = new Minefield();
    }

    public void update() {
        camera.update();
        minefield.update(this);

        if (!isGameFinished() && minefield.isGameWon()) {
            onGameWon();
        }

        if (!isGamePaused() && isGameStarted()) {
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

        bestTime = DataManager.getBestTime(width, height, mines);

        flaggedMines = 0;
        gameDuration = 0;
        gameLost = gameWon = gameStarted = secondLifeUsed = secondLifeDisabled = false;

        camera.reset();
        camera.getPosition().x = width * 0.5f - 0.5f;
        camera.getPosition().y = height * 0.5f - 0.5f;
    }

    public void onGameLost() {
        gameLost = true;

        if (!canUseSecondLife())
            minefield.startLoseAnimation();

        if (gameLostCallback != null)
            gameLostCallback.onGameLost();
    }

    public void onGameWon() {
        gameWon = true;

        minefield.startWinAnimation();

        if (DataManager.checkGameDuration(minefield.getWidth(), minefield.getHeight(), minefield.getMinesCount(), getGameDuration()))
            bestTime = gameDuration;

        if (gameWonCallback != null)
            gameWonCallback.onGameWon();
    }

    public void onGameStarted() {
        gameStarted = true;

        if (gameStartedCallback != null)
            gameStartedCallback.onGameStarted();
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

    public int getBestTime() {
        return bestTime;
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

    public boolean canUseSecondLife() {
        return !secondLifeDisabled && !secondLifeUsed && DataManager.SECOND_LIVES_COUNT > 0;
    }

    public void disableSecondLive() {
        secondLifeDisabled = true;
    }

    public boolean isGamePaused() {
        return isGameFinished();
    }

    public boolean isGameStarted() {
        return gameStarted;
    }


    public RenderView getRenderView() {
        return renderView;
    }

    public void setGameStartedCallback(GameStartedCallback gameStartedCallback) {
        this.gameStartedCallback = gameStartedCallback;
    }

    public void setGameLostCallback(GameLostCallback gameLostCallback) {
        this.gameLostCallback = gameLostCallback;
    }

    public void setGameWonCallback(GameWonCallback gameWonCallback) {
        this.gameWonCallback = gameWonCallback;
    }

    public interface GameWonCallback {
        void onGameWon();
    }

    public interface GameLostCallback {
        void onGameLost();
    }

    public interface GameStartedCallback {
        void onGameStarted();
    }
}
