package com.github.breskin.minesweeper;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Objects;

public class DataManager {

    private static SharedPreferences preferences;

    public static String FIELD_SIZE_SMALL, FIELD_SIZE_MEDIUM, FIELD_SIZE_LARGE, FIELD_SIZE_CUSTOM, HUB_BUTTON_OK, HUB_BUTTON_REPLAY, HUB_GAME_LOST, HUB_GAME_WON, HUB_BUTTON_NO, HUB_BUTTON_YES,
            HUB_SECOND_LIFE_AVAILABLE, HUB_SECOND_LIFE_ONCE_REMINDER, HUB_BEST_TIME, CUSTOM_VIEW_WIDTH, CUSTOM_VIEW_HEIGHT, CUSTOM_VIEW_MINES, CUSTOM_VIEW_HEADER, CUSTOM_VIEW_START,
            HOME_VIEW_LOGO_FIRST, HOME_VIEW_LOGO_SECOND;

    private static String SECOND_LIVES_STRING = "second-chance-count";

    public static final int SECOND_LIVES_DAILY_BONUS = 3;
    public static int SECOND_LIVES_COUNT = 0;

    public static boolean FIRST_LAUNCH = false, FIRST_LAUNCH_TODAY = false;

    public static void load(Context context) {
        FIELD_SIZE_SMALL = context.getString(R.string.game_size_small);
        FIELD_SIZE_MEDIUM = context.getString(R.string.game_size_medium);
        FIELD_SIZE_LARGE = context.getString(R.string.game_size_large);
        FIELD_SIZE_CUSTOM = context.getString(R.string.game_size_custom);
        HUB_BUTTON_OK = context.getString(R.string.hub_button_ok);
        HUB_BUTTON_NO = context.getString(R.string.hub_button_no);
        HUB_BUTTON_YES = context.getString(R.string.hub_button_yes);
        HUB_BUTTON_REPLAY = context.getString(R.string.hub_button_replay);
        HUB_GAME_LOST = context.getString(R.string.hub_game_lost_message);
        HUB_GAME_WON = context.getString(R.string.hub_game_won_message);
        HUB_SECOND_LIFE_AVAILABLE = context.getString(R.string.hub_second_life_available);
        HUB_SECOND_LIFE_ONCE_REMINDER = context.getString(R.string.hub_second_life_once_reminder);
        HUB_BEST_TIME = context.getString(R.string.hub_best_time);
        CUSTOM_VIEW_WIDTH = context.getString(R.string.custom_view_width);
        CUSTOM_VIEW_HEIGHT = context.getString(R.string.custom_view_height);
        CUSTOM_VIEW_MINES = context.getString(R.string.custom_view_mines);
        CUSTOM_VIEW_HEADER = context.getString(R.string.custom_view_header);
        CUSTOM_VIEW_START = context.getString(R.string.custom_view_start);
        HOME_VIEW_LOGO_FIRST = context.getString(R.string.home_view_logo_first);
        HOME_VIEW_LOGO_SECOND = context.getString(R.string.home_view_logo_second);

        preferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        SECOND_LIVES_COUNT = preferences.getInt(SECOND_LIVES_STRING, 0);

        String lastDayPlayed = preferences.getString("last-day-played", "none");

        if (lastDayPlayed.equals("none"))
            FIRST_LAUNCH = true;

        if (!lastDayPlayed.equals(Calendar.getInstance().get(Calendar.DAY_OF_YEAR)+"")) {
            FIRST_LAUNCH_TODAY = true;

            SECOND_LIVES_COUNT += SECOND_LIVES_DAILY_BONUS;

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("last-day-played", String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_YEAR)));
            editor.putInt(SECOND_LIVES_STRING, SECOND_LIVES_COUNT);
            editor.apply();

            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("sec-lives");
                reference.setValue(SECOND_LIVES_COUNT);
            }
        }
    }

    public static void onSecondLifeUsed() {
        SECOND_LIVES_COUNT--;

        preferences.edit().putInt(SECOND_LIVES_STRING, SECOND_LIVES_COUNT).apply();

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("sec-lives");
            reference.setValue(SECOND_LIVES_COUNT);
        }
    }

    public static boolean checkGameDuration(int width, int height, int mines, int duration) {
        int bestTime = preferences.getInt(width + "x" + height + "x" + mines, -1);

        if (bestTime == -1 || duration < bestTime) {
            preferences.edit().putInt(width + "x" + height + "x" + mines, duration).apply();

            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("scores").child(width + "x" + height + "x" + mines);
                reference.setValue(duration);
            }

            return true;
        }

        return false;
    }

    public static int getBestTime(int width, int height, int mines) {
        return preferences.getInt(width + "x" + height + "x" + mines, -1);
    }

    public static void syncDataWithCloud() {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("sec-lives").getValue() != null) {
                    int secondLives = ((Number) Objects.requireNonNull(dataSnapshot.child("sec-lives").getValue())).intValue();

                    if (secondLives > SECOND_LIVES_COUNT) {
                        SECOND_LIVES_COUNT = secondLives;
                        preferences.edit().putInt(SECOND_LIVES_STRING, SECOND_LIVES_COUNT).apply();
                    } else {
                        reference.child("sec-lives").setValue(SECOND_LIVES_COUNT);
                    }
                } else {
                    reference.child("sec-lives").setValue(SECOND_LIVES_COUNT);
                }

                SharedPreferences.Editor editor = preferences.edit();

                for (DataSnapshot snapshot : dataSnapshot.child("scores").getChildren()) {
                    int currentTime = preferences.getInt(snapshot.getKey(), -1);
                    int cloudTime = ((Number) Objects.requireNonNull(snapshot.getValue())).intValue();

                    if (currentTime == -1 || cloudTime < currentTime) {
                        editor.putInt(snapshot.getKey(), cloudTime);
                    } else if (currentTime < cloudTime) {
                        reference.child("scores").child(Objects.requireNonNull(snapshot.getKey())).setValue(currentTime);
                    }
                }

                editor.apply();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
