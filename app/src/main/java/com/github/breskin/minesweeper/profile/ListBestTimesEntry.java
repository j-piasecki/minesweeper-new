package com.github.breskin.minesweeper.profile;

import android.graphics.Canvas;

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

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantLock;

public class ListBestTimesEntry extends ListEntry {

    private String uid;

    private ReentrantLock scoresLock = new ReentrantLock();
    private TreeMap<FieldSize, Integer> scores = new TreeMap<>();

    @Override
    public void render(Canvas canvas) {
        super.render(canvas);

        paint.setTextSize(RenderView.VIEW_WIDTH * 0.04f);
        paint.setColor(Theme.getColor(Theme.ColorType.ListEntryText));

        canvas.drawText(DataManager.PROFILE_BEST_RESULTS, (RenderView.VIEW_WIDTH - paint.measureText(DataManager.PROFILE_BEST_RESULTS)) * 0.5f, translation + paint.getTextSize() * 2, paint);
        scoresLock.lock();

        int marginMultiplier = 5;

        for (TreeMap.Entry<FieldSize, Integer> entry : scores.entrySet()) {
            float margin = translation + paint.getTextSize() * marginMultiplier;

            if (margin > 0 && margin < RenderView.VIEW_HEIGHT + paint.getTextSize() * 2) {
                canvas.drawText(entry.getKey().getVisibleName(), RenderView.VIEW_WIDTH * 0.085f, margin, paint);
                canvas.drawText(Utils.getTimeString(entry.getValue()), RenderView.VIEW_WIDTH * 0.915f - paint.measureText(Utils.getTimeString(entry.getValue())), margin, paint);
            }

            marginMultiplier += 2;
        }

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
            boolean smallAdded = false, mediumAdded = false, largeAdded = false;

            for (Map.Entry<String, ?> entry : DataManager.getPreferences().getAll().entrySet()) {
                FieldSize size = FieldSize.fromString(entry.getKey());

                if (FieldSize.SMALL.equals(size)) smallAdded = true;
                if (FieldSize.MEDIUM.equals(size)) mediumAdded = true;
                if (FieldSize.LARGE.equals(size)) largeAdded = true;

                if (size != null)
                    scores.put(size, (Integer) entry.getValue());
            }

            if (!smallAdded) scores.put(FieldSize.SMALL, -1);
            if (!mediumAdded) scores.put(FieldSize.MEDIUM, -1);
            if (!largeAdded) scores.put(FieldSize.LARGE, -1);
        } else {
            getScores();
        }
    }

    private void getScores() {
        FirebaseDatabase.getInstance().getReference("users").child(uid).child("scores").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    scoresLock.lock();

                    boolean smallAdded = false, mediumAdded = false, largeAdded = false;

                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        if (data.exists()) {
                            FieldSize size = FieldSize.fromString(data.getKey());

                            if (FieldSize.SMALL.equals(size)) smallAdded = true;
                            if (FieldSize.MEDIUM.equals(size)) mediumAdded = true;
                            if (FieldSize.LARGE.equals(size)) largeAdded = true;

                            if (size != null)
                                scores.put(size, ((Number) data.getValue()).intValue());
                        }
                    }

                    if (!smallAdded) scores.put(FieldSize.SMALL, -1);
                    if (!mediumAdded) scores.put(FieldSize.MEDIUM, -1);
                    if (!largeAdded) scores.put(FieldSize.LARGE, -1);

                    scoresLock.unlock();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    @Override
    public float getHeight() {
        return RenderView.VIEW_WIDTH * 0.04f * (5 + scores.size() * 2);
    }
}
