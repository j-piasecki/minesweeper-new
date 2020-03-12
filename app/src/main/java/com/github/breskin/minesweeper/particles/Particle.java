package com.github.breskin.minesweeper.particles;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;

import com.github.breskin.minesweeper.RenderView;
import com.github.breskin.minesweeper.game.GameLogic;

import java.util.Random;

public class Particle {
    private static Paint paint = new Paint();
    private static Random random = new Random();

    private PointF position, velocity, target;
    private float size, rotation, rotationSpeed, a;
    private int r, g, b;

    public boolean toDelete = false;

    public Particle(GameLogic logic, PointF pos, float size, int cr, int cg, int cb, PointF target) {
        this.position = pos;
        this.size = size;
        this.target = target;
        this.rotation = random.nextInt(360);
        this.rotationSpeed = random.nextInt(20) - 10;
        this.a = 192 + random.nextInt(64);

        int change = random.nextInt(32);

        this.r = cr - change; if (this.r < 0) this.r = 0; if (this.r > 255) this.r = 255;
        this.g = cg - change; if (this.g < 0) this.g = 0; if (this.g > 255) this.g = 255;
        this.b = cb - change; if (this.b < 0) this.b = 0; if (this.b > 255) this.b = 255;

        velocity = new PointF(RenderView.VIEW_WIDTH * 0.007f * (random.nextFloat() - 0.5f) / logic.getCamera().getBlockSize(), RenderView.VIEW_HEIGHT * 0.007f * (random.nextFloat() - 0.5f) / logic.getCamera().getBlockSize());

        if (target != null) {
            velocity.x *= 3;
            velocity.y *= 3;
        }
    }

    public void update(GameLogic logic) {
        float speed = RenderView.FRAME_TIME / 16f;
        if (speed < 0.25) speed = 0.25f;

        position.x += velocity.x * speed;
        position.y += velocity.y * speed;

        if (target != null) {
            velocity.x -= (position.x - target.x - 0.5f) * 0.0075f * speed;
            velocity.y -= (position.y - target.y - 0.5f) * 0.0075f * speed;

            position.x -= (position.x - target.x - 0.5f) * 0.05f * speed;
            position.y -= (position.y - target.y - 0.5f) * 0.05f * speed;
        } else if (velocity.y < RenderView.VIEW_HEIGHT / 100) {
            velocity.y += 0.35f * speed / logic.getCamera().getBlockSize();
        }

        rotation += rotationSpeed * speed;

        if (a > 92)
            a -= 1.25f * speed;

        if (size > RenderView.VIEW_WIDTH / 200)
            size -= size * 0.035f * speed;
        else
            toDelete = true;
    }

    public void render(GameLogic logic, Canvas canvas) {
        PointF pos = logic.getCamera().calculateOnScreenPosition(position);

        if (pos.x < -RenderView.VIEW_WIDTH * 0.1 || pos.y < RenderView.VIEW_WIDTH * 0.1 || pos.x > RenderView.VIEW_WIDTH * 1.1 || pos.y > RenderView.VIEW_HEIGHT * 1.05)
            return;

        paint.setColor(Color.argb((int)(a), r, g, b));
        canvas.save();

        canvas.translate(pos.x, pos.y);
        canvas.rotate(rotation);
        canvas.drawRoundRect(new RectF(-size / 2, -size / 2, size / 2, size / 2), 5, 5, paint);

        canvas.restore();
    }
}
