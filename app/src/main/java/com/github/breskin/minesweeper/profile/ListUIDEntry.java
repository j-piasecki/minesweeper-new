package com.github.breskin.minesweeper.profile;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.view.MotionEvent;

import com.github.breskin.minesweeper.MainActivity;
import com.github.breskin.minesweeper.R;
import com.github.breskin.minesweeper.RenderView;
import com.github.breskin.minesweeper.Theme;
import com.github.breskin.minesweeper.generic.ListEntry;
import com.github.breskin.minesweeper.generic.buttons.Button;
import com.github.breskin.minesweeper.generic.buttons.ImageButton;
import com.github.breskin.minesweeper.profile.UserProfile;
import com.google.firebase.auth.FirebaseAuth;

public class ListUIDEntry extends ListEntry {

    @Override
    public void render(Canvas canvas) {
        super.render(canvas);

        paint.setTextSize(getHeight() * 0.2f);
        paint.setColor(Theme.getColor(Theme.ColorType.ListEntryText));

        canvas.drawText(RenderView.CONTEXT.getString(R.string.profile_uid) + FirebaseAuth.getInstance().getCurrentUser().getUid(), RenderView.VIEW_WIDTH * 0.085f, translation + (getHeight() - paint.getTextSize()) * 0.475f + paint.getTextSize(), paint);
    }

    @Override
    protected boolean onTouch(float x, float y) {
        ClipboardManager clipboard = (ClipboardManager) RenderView.CONTEXT.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Minesweeper uid", "u:" + FirebaseAuth.getInstance().getCurrentUser().getUid());

        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);

            ((MainActivity)RenderView.CONTEXT).showToast(RenderView.CONTEXT.getString(R.string.message_copied_to_clipboard));
        }

        return true;
    }

    @Override
    public float getHeight() {
        return RenderView.VIEW_WIDTH * 0.2f;
    }
}
