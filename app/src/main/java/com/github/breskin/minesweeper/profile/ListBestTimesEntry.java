package com.github.breskin.minesweeper.profile;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;

import androidx.annotation.NonNull;

import com.github.breskin.minesweeper.DataManager;
import com.github.breskin.minesweeper.RenderView;
import com.github.breskin.minesweeper.Theme;
import com.github.breskin.minesweeper.Utils;
import com.github.breskin.minesweeper.game.FieldSize;
import com.github.breskin.minesweeper.generic.ListEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

public class ListBestTimesEntry extends ListEntry {

    private String uid;

    private ReentrantLock scoresLock = new ReentrantLock();
    private HashMap<FieldSize, Integer> scores = new HashMap<>();

    @Override
    public void render(Canvas canvas) {
        super.render(canvas);

        paint.setTextSize(RenderView.VIEW_WIDTH * 0.04f);
        paint.setColor(Theme.getColor(Theme.ColorType.ListEntryText));

        canvas.drawText(DataManager.PROFILE_BEST_RESULTS, (RenderView.VIEW_WIDTH - paint.measureText(DataManager.PROFILE_BEST_RESULTS)) * 0.5f, translation + paint.getTextSize() * 2, paint);
        scoresLock.lock();

        canvas.drawText(FieldSize.SMALL.getVisibleName(), RenderView.VIEW_WIDTH * 0.085f, translation + paint.getTextSize() * 5, paint);
        canvas.drawText(Utils.getTimeString(scores.get(FieldSize.SMALL)), RenderView.VIEW_WIDTH * 0.915f - paint.measureText(Utils.getTimeString(scores.get(FieldSize.SMALL))), translation + paint.getTextSize() * 5, paint);

        canvas.drawText(FieldSize.MEDIUM.getVisibleName(), RenderView.VIEW_WIDTH * 0.085f, translation + paint.getTextSize() * 7, paint);
        canvas.drawText(Utils.getTimeString(scores.get(FieldSize.MEDIUM)), RenderView.VIEW_WIDTH * 0.915f - paint.measureText(Utils.getTimeString(scores.get(FieldSize.MEDIUM))), translation + paint.getTextSize() * 7, paint);

        canvas.drawText(FieldSize.LARGE.getVisibleName(), RenderView.VIEW_WIDTH * 0.085f, translation + paint.getTextSize() * 9, paint);
        canvas.drawText(Utils.getTimeString(scores.get(FieldSize.LARGE)), RenderView.VIEW_WIDTH * 0.915f - paint.measureText(Utils.getTimeString(scores.get(FieldSize.LARGE))), translation + paint.getTextSize() * 9, paint);

        scoresLock.unlock();
    }

    @Override
    public void refresh() {
        super.refresh();

        loadScores();
    }

    public ListBestTimesEntry setUser(String uid) {
        this.uid = uid;

        loadScores();

        return this;
    }

    private void loadScores() {
        scoresLock.lock();
        scores.clear();
        scoresLock.unlock();

        if (uid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            scores.put(FieldSize.SMALL, DataManager.getBestTime(FieldSize.SMALL));
            scores.put(FieldSize.MEDIUM, DataManager.getBestTime(FieldSize.MEDIUM));
            scores.put(FieldSize.LARGE, DataManager.getBestTime(FieldSize.LARGE));
        } else {
            fetchScore(FieldSize.SMALL);
            fetchScore(FieldSize.MEDIUM);
            fetchScore(FieldSize.LARGE);
        }
    }

    private void fetchScore(final FieldSize size) {
        FirebaseDatabase.getInstance().getReference("users").child(uid).child("scores").child(size.toString()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    scoresLock.lock();
                    scores.put(size, ((Number) dataSnapshot.getValue()).intValue());
                    scoresLock.unlock();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    @Override
    public float getHeight() {
        return RenderView.VIEW_WIDTH * 0.04f * 11;
    }
}
