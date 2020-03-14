package com.github.breskin.minesweeper;

import android.content.Context;

public class DataManager {

    public static String FIELD_SIZE_SMALL, FIELD_SIZE_MEDIUM, FIELD_SIZE_LARGE, HUB_BUTTON_OK, HUB_BUTTON_REPLAY, HUB_GAME_LOST, HUB_GAME_WON;

    public static void load(Context context) {
        FIELD_SIZE_SMALL = context.getString(R.string.game_size_small);
        FIELD_SIZE_MEDIUM = context.getString(R.string.game_size_medium);
        FIELD_SIZE_LARGE = context.getString(R.string.game_size_large);
        HUB_BUTTON_OK = context.getString(R.string.hub_button_ok);
        HUB_BUTTON_REPLAY = context.getString(R.string.hub_button_replay);
        HUB_GAME_LOST = context.getString(R.string.hub_game_lost_message);
        HUB_GAME_WON = context.getString(R.string.hub_game_won_message);
    }
}
