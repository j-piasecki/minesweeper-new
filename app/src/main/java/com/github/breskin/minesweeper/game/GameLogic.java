package com.github.breskin.minesweeper.game;

import com.github.breskin.minesweeper.RenderView;
import com.github.breskin.minesweeper.particles.ParticleSystem;

public class GameLogic {

    private RenderView renderView;

    private Camera camera;
    private Minefield minefield;

    public GameLogic(RenderView renderView) {
        this.renderView = renderView;

        this.camera = new Camera();
        this.minefield = new Minefield();
    }

    public void update() {
        camera.update();
        minefield.update();
    }

    public Camera getCamera() {
        return camera;
    }

    public Minefield getMinefield() {
        return minefield;
    }

    public void init(int width, int height, int mines) {
        minefield.init(width, height, mines);

        camera.reset();
        camera.getPosition().x = width * 0.5f - 0.5f;
        camera.getPosition().y = height * 0.5f - 0.5f;
    }

    public ParticleSystem getParticleSystem() {
        return renderView.getParticleSystem();
    }
}
