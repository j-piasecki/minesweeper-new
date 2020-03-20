package com.github.breskin.minesweeper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 370;

    private RenderView renderView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        renderView = new RenderView(this);

        super.onCreate(savedInstanceState);
        setContentView(renderView);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                FirebaseDatabase.getInstance().getReference("user2uid").child(email.substring(0, email.indexOf("@"))).setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                FirebaseDatabase.getInstance().getReference("uid2user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(email.substring(0, email.indexOf("@")));

                DataManager.syncDataWithCloud();
            }
        }
    }

    public void showLoginScreen() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build());

        startActivityForResult(AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setLogo(R.drawable.ic_icon_splash)
                .setTheme(R.style.AppTheme)
                .build(), RC_SIGN_IN);
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
