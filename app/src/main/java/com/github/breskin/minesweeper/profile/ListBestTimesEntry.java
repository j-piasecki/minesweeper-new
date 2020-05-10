package com.github.breskin.minesweeper.profile;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;

import com.github.breskin.minesweeper.DataManager;
import com.github.breskin.minesweeper.RenderView;
import com.github.breskin.minesweeper.Theme;
import com.github.breskin.minesweeper.Utils;
import com.github.breskin.minesweeper.game.FieldSize;
import com.github.breskin.minesweeper.generic.ListEntry;
import com.google.firebase.auth.FirebaseAuth;

public class ListBestTimesEntry extends ListEntry {

    private String uid;

    private int smallTime, mediumTime, largeTime;

    @Override
    public void render(Canvas canvas) {
        super.render(canvas);

        paint.setTextSize(RenderView.VIEW_WIDTH * 0.04f);
        paint.setColor(Theme.getColor(Theme.ColorType.ListEntryText));

        canvas.drawText(DataManager.PROFILE_BEST_RESULTS, (RenderView.VIEW_WIDTH - paint.measureText(DataManager.PROFILE_BEST_RESULTS)) * 0.5f, translation + paint.getTextSize() * 2, paint);

        canvas.drawText(FieldSize.SMALL.getVisibleName(), RenderView.VIEW_WIDTH * 0.085f, translation + paint.getTextSize() * 5, paint);
        canvas.drawText(Utils.getTimeString(smallTime), RenderView.VIEW_WIDTH * 0.915f - paint.measureText(Utils.getTimeString(smallTime)), translation + paint.getTextSize() * 5, paint);

        canvas.drawText(FieldSize.MEDIUM.getVisibleName(), RenderView.VIEW_WIDTH * 0.085f, translation + paint.getTextSize() * 7, paint);
        canvas.drawText(Utils.getTimeString(mediumTime), RenderView.VIEW_WIDTH * 0.915f - paint.measureText(Utils.getTimeString(mediumTime)), translation + paint.getTextSize() * 7, paint);

        canvas.drawText(FieldSize.LARGE.getVisibleName(), RenderView.VIEW_WIDTH * 0.085f, translation + paint.getTextSize() * 9, paint);
        canvas.drawText(Utils.getTimeString(largeTime), RenderView.VIEW_WIDTH * 0.915f - paint.measureText(Utils.getTimeString(largeTime)), translation + paint.getTextSize() * 9, paint);
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
        if (uid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            smallTime = DataManager.getBestTime(FieldSize.SMALL);
            mediumTime = DataManager.getBestTime(FieldSize.MEDIUM);
            largeTime = DataManager.getBestTime(FieldSize.LARGE);
        }
    }

    @Override
    public float getHeight() {
        return RenderView.VIEW_WIDTH * 0.04f * 11;
    }
}
