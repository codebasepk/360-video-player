package com.byteshaft.a360player.utils;


import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Helpers {

    // get default sharedPreferences.
    private static SharedPreferences getPreferenceManager() {
        return PreferenceManager.getDefaultSharedPreferences(AppGlobals.getContext());
    }

    // save boolean value for login status of user , takes boolean value as parameter
    public static void videoPlayer(String videoName, Integer value) {
        SharedPreferences sharedPreferences = getPreferenceManager();
        sharedPreferences.edit().putInt(videoName, value).apply();
    }

    // get user login status and manipulate app functions by its returned boolean value
    public static Integer isUserLoggedIn(String key) {
        SharedPreferences sharedPreferences = getPreferenceManager();
        return sharedPreferences.getInt(key, 0);
    }
}
