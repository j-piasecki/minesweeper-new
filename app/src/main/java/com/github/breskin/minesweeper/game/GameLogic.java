package com.github.breskin.minesweeper.game;

import com.github.breskin.minesweeper.DataManager;
import com.github.breskin.minesweeper.RenderView;
import com.github.breskin.minesweeper.game.hub.InfoHub;
import com.github.breskin.minesweeper.particles.ParticleSystem;

public class GameLogic {

    private RenderView renderView;

    private GameStartedCallback gameStartedCallback;
    private GameLostCallback gameLostCallback;
    private GameWonCallback gameWonCallback;

    private GameListener gameListener;

    private Camera camera;
    private Minefield minefield;

    private InfoHub infoHub;

    private int flaggedMines;
    private boolean gameWon;
    private boolean gameLost;
    private boolean gameStarted;
    private boolean gamePaused;
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

        if (gameListener != null)
            gameListener.applyChanges();

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

    public void init(FieldSize fieldSize) {
        minefield.init(fieldSize);

        bestTime = DataManager.getBestTime(fieldSize);

        flaggedMines = 0;
        gameDuration = 0;
        gameLost = gameWon = gameStarted = gamePaused = secondLifeUsed = secondLifeDisabled = false;

        camera.reset();
        camera.getPosition().x = fieldSize.getWidth() * 0.5f - 0.5f;
        camera.getPosition().y = fieldSize.getHeight() * 0.5f - 0.5f;
    }

    public void onMineRevealed() {
        if (canUseSecondLife()) {
            gamePaused = true;

            infoHub.askForSecondLife();
        } else {
            infoHub.onGameLost();
            onGameLost();
        }
    }

    public void onGameLost() {
        gameLost = true;

        minefield.startLoseAnimation();

        if (gameLostCallback != null)
            gameLostCallback.onGameLost();

        if (gameListener != null)
            gameListener.onGameLost();
    }

    public void onGameWon() {
        gameWon = true;

        minefield.startWinAnimation();

        DataManager.incrementGamesWonCounter(minefield.getFieldSize());
        if (DataManager.checkGameDuration(minefield.getFieldSize(), getGameDuration()))
            bestTime = gameDuration;

        flaggedMines = minefield.getFieldSize().getMinesCount();

        infoHub.onGameWon();

        if (gameWonCallback != null)
            gameWonCallback.onGameWon();

        if (gameListener != null)
            gameListener.onGameWon();
    }

    public void onGameStarted() {
        gameStarted = true;

        DataManager.incrementGameCounter(minefield.getFieldSize());

        if (gameStartedCallback != null)
            gameStartedCallback.onGameStarted();

        if (gameListener != null)
            gameListener.onGameStarted();
    }

    public void useSecondLife() {
        gamePaused = false;
        secondLifeUsed = true;

        DataManager.onSecondLifeUsed();

        if (gameListener != null)
            gameListener.onSecondLiveUsed();
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
        return isGameFinished() || gamePaused;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }


    public void setInfoHub(InfoHub infoHub) {
        this.infoHub = infoHub;
    }

    public RenderView getRenderView() {
        return renderView;
    }

    public void setGameListener(GameListener gameListener) {
        this.gameListener = gameListener;
        this.gameListener.setLogic(this);
    }

    GameListener getGameListener() {
        return gameListener;
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
