package com.github.breskin.minesweeper.game;

public interface GameListener {
    void setLogic(GameLogic logic);

    void onGameStarted();
    void onGameWon();
    void onGameLost();
    void onSecondLiveUsed();

    void onFieldRevealed(Square square);
    void onFieldFlaggedChange(Square square);

    void applyChanges();
}
