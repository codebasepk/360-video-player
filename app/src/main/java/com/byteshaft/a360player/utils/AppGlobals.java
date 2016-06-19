package com.byteshaft.a360player.utils;

import android.app.Application;
import android.content.Context;


public class AppGlobals extends Application {

    private static Context sContext;
    public static final String INTERVIEW_PREP =
            "http://192.169.235.30/~careeredgeaci/careeredgectl.com/v2/vrtest.mp4";
    public static boolean sVideoPaused = false;
    public static boolean sPausedByHand = false;


    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
    }

    public static Context getContext() {
        return sContext;
    }
}
