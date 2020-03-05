package com.github.breskin.minesweeper;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Window;

public class MainActivity extends AppCompatActivity {

    private RenderView renderView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        renderView = new RenderView(this);

        super.onCreate(savedInstanceState);
        setContentView(renderView);
    }

    @Override
    protected void onPause() {
        super.onPause();

        renderView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        renderView.resume();
    }

    @Override
    public void onBackPressed() {
        if (!renderView.onBackPressed())
            super.onBackPressed();
    }
}
