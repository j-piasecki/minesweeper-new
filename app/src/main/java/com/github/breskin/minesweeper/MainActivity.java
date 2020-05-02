package com.github.breskin.minesweeper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.github.breskin.minesweeper.profile.friends.FriendManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

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
                final String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                FirebaseDatabase.getInstance().getReference("user2uid").child(email.substring(0, email.indexOf("@"))).setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                FirebaseDatabase.getInstance().getReference("uid2user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(email.substring(0, email.indexOf("@")));

                FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("name").runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                        if (mutableData.getValue() == null)
                            mutableData.setValue(email.substring(0, email.indexOf("@")));

                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

                    }
                });

                DataManager.syncDataWithCloud();
                FriendManager.syncFriendsWithCloud();
                FriendManager.setupRequestsListener();
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

    public void showInviteFriendUI() {
        final View dialogView = getLayoutInflater().inflate(R.layout.dialog_invite_friend, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getString(R.string.invite_friend_dialog_name));
        builder.setView(dialogView);
        builder.setCancelable(true);
        builder.setPositiveButton(getString(R.string.invite_friend_dialog_accept_button), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText textInput = dialogView.findViewById(R.id.invite_friend_input);
                String value = textInput.getText().toString().trim();

                if (value.length() > 0) {
                    if (value.contains("@")) {
                        FriendManager.sendInviteByEmail(value.toLowerCase());
                    } else if (value.startsWith("u:")) {
                        FriendManager.sendInviteByUid(value.substring(2));
                    }
                }
            }
        });

        builder.create().show();
    }

    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
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
