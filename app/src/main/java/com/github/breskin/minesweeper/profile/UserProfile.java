package com.github.breskin.minesweeper.profile;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.github.breskin.minesweeper.DataManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserProfile {

    private static String name;

    public static void load(Context context) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null)
            return;

        String mail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        name = DataManager.getPreferences().getString("user-name", mail.substring(0, mail.indexOf("@")));
    }

    public static String getName() {
        return name;
    }

    public static void syncWithCloud() {
        FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    name = dataSnapshot.getValue().toString();
                    DataManager.getPreferences().edit().putString("user-name", name).apply();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    public static void changeName(String newName) {
        name = newName;

        DataManager.getPreferences().edit().putString("user-name", name).apply();

        FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("name").setValue(name);
    }
}
