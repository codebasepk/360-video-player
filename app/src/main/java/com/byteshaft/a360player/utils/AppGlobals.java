package com.byteshaft.a360player.utils;

import android.app.Application;
import android.content.Context;


public class AppGlobals extends Application {

    private static Context sContext;
    public static final String INTERVIEW_PREP =
            "http://192.169.235.30/~careeredgeaci/careeredgectl.com/v2/vrtest.mp4";
    public static boolean sVideoPaused = false;
    public static boolean sPausedByHand = false;


    public static final String BASE_URL = "http://46.101.75.194:8000";
    public static final String REGISTER_URL = String.format("%s/api/register", BASE_URL);
    public static final String LOGIN_URL = String.format("%s/api/login/", BASE_URL);

    public static final String user_login_key = "user_login";
    public static final String KEY_FULLNAME = "full_name";
    public static final String KEY_EMAIL = "email";

    public static final String KEY_USER_TOKEN = "token";
    public static final String KEY_USER_LOGIN = "user_login";
    public static int postResponse;
    public static int responseCode = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
    }

    public static Context getContext() {
        return sContext;
    }

    public static void setPostResponse(int value) {
        postResponse = value;
    }

    public static void setResponseCode(int code) {
        responseCode = code;
    }

    public static int getResponseCode() {
        return responseCode;
    }
}
