package com.github.breskin.minesweeper.particles;

import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;

import com.github.breskin.minesweeper.RenderView;
import com.github.breskin.minesweeper.game.GameLogic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ParticleSystem {
    private Random random;
    private List<Particle> particles;
    private List<Particle> newParticles;

    private int maximumAllowed = 0;

    public ParticleSystem() {
        random = new Random();
        particles = new ArrayList<>();
        newParticles = new ArrayList<>();

        maximumAllowed = 150;
    }

    public void render(GameLogic logic, Canvas canvas) {
        for (int i = 0; i < particles.size(); i++) {
            if (particles.get(i) == null)
                continue;

            particles.get(i).render(logic, canvas);
        }
    }

    public void update(GameLogic logic) {
        if (!newParticles.isEmpty()) {
            int skipFactor = newParticles.size() / maximumAllowed;

            for (int i = 0; i < newParticles.size(); i++) {
                if (skipFactor < 2 || i % skipFactor == 0)
                    particles.add(newParticles.get(i));
            }

            newParticles.clear();
        }

        boolean purge = particles.size() > maximumAllowed || RenderView.FRAME_TIME > 25;
        int purgeCounter = 0;

        for (int i = 0; i < particles.size(); i++) {
            purgeCounter++;

            if (particles.get(i) == null ||(purge && purgeCounter % 6 == 0)) {
                particles.remove(i);
                i--;

                continue;
            }

            particles.get(i).update(logic);

            if (particles.get(i).toDelete) {
                particles.remove(i);
                i--;
            }
        }
    }

    public void createInPoint(GameLogic logic, PointF position, float size, int count, int r, int g, int b) {
        createInPoint(logic, position, size, count, r, g, b, null);
    }

    public void createInPoint(GameLogic logic, PointF position, float size, int count, int r, int g, int b, PointF target) {
        for (int i=0; i<count; i++) {
            newParticles.add(new Particle(logic, new PointF(position.x, position.y), size, r, g, b, target));
        }
    }

    public void createInArea(GameLogic logic, RectF area, float size, int count, int r, int g, int b) {
        createInArea(logic, area, size, count, r, g, b, null);
    }

    public void createInArea(GameLogic logic, RectF area, float size, int count, int r, int g, int b, PointF target) {
        for (int i=0; i<count; i++) {
            newParticles.add(new Particle(logic, new PointF(area.left + area.width() * random.nextFloat(), area.top + area.height() * random.nextFloat()), size, r, g, b, target));
        }
    }

    public void clear() {
        newParticles.clear();
        particles.clear();
    }
}
