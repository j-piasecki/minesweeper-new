package com.github.breskin.minesweeper;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Calendar;

public class DataManager {

    private static SharedPreferences preferences;

    public static String FIELD_SIZE_SMALL, FIELD_SIZE_MEDIUM, FIELD_SIZE_LARGE, HUB_BUTTON_OK, HUB_BUTTON_REPLAY, HUB_GAME_LOST, HUB_GAME_WON, HUB_BUTTON_NO, HUB_BUTTON_YES, HUB_SECOND_LIFE_AVAILABLE,
            HUB_SECOND_LIFE_ONCE_REMINDER;

    private static String SECOND_LIFES_STRING = "second-chance-count";

    public static final int SECOND_LIFES_DAILY_BONUS = 3;
    public static int SECOND_LIFES_COUNT = 0;

    public static boolean FIRST_LAUNCH = false, FIRST_LAUNCH_TODAY = false;

    public static void load(Context context) {
        FIELD_SIZE_SMALL = context.getString(R.string.game_size_small);
        FIELD_SIZE_MEDIUM = context.getString(R.string.game_size_medium);
        FIELD_SIZE_LARGE = context.getString(R.string.game_size_large);
        HUB_BUTTON_OK = context.getString(R.string.hub_button_ok);
        HUB_BUTTON_NO = context.getString(R.string.hub_button_no);
        HUB_BUTTON_YES = context.getString(R.string.hub_button_yes);
        HUB_BUTTON_REPLAY = context.getString(R.string.hub_button_replay);
        HUB_GAME_LOST = context.getString(R.string.hub_game_lost_message);
        HUB_GAME_WON = context.getString(R.string.hub_game_won_message);
        HUB_SECOND_LIFE_AVAILABLE = context.getString(R.string.hub_second_life_available);
        HUB_SECOND_LIFE_ONCE_REMINDER = context.getString(R.string.hub_second_life_once_reminder);

        preferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        SECOND_LIFES_COUNT = preferences.getInt(SECOND_LIFES_STRING, 0);

        String lastDayPlayed = preferences.getString("last-day-played", "none");

        if (lastDayPlayed.equals("none"))
            FIRST_LAUNCH = true;

        if (!lastDayPlayed.equals(Calendar.getInstance().get(Calendar.DAY_OF_YEAR)+"")) {
            FIRST_LAUNCH_TODAY = true;

            SECOND_LIFES_COUNT += SECOND_LIFES_DAILY_BONUS;

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("last-day-played", String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_YEAR)));
            editor.putInt(SECOND_LIFES_STRING, SECOND_LIFES_COUNT);
            editor.apply();
        }
    }

    public static void onSecondLifeUsed() {
        SECOND_LIFES_COUNT--;

        preferences.edit().putInt(SECOND_LIFES_STRING, SECOND_LIFES_COUNT).apply();
    }
}
