package com.byteshaft.a360player.utils;

import android.app.Application;
import android.content.Context;


public class AppGlobals extends Application {

    private static Context sContext;
    public static final String INTERVIEW_PREP =
            "http://192.169.235.30/~careeredgeaci/careeredgectl.com/v2/vrtest.mp4";
    public static final String ONE_ON_ONE =
            "http://192.169.235.30/~careeredgeaci/careeredgectl.com/v2/2.mp4";
    public static final String POST_INTERVIEW =
            "http://192.169.235.30/~careeredgeaci/careeredgectl.com/v2/3.mp4";
    public static boolean sPausedByHand = false;


    public static final String BASE_URL = "http://178.62.121.209:8000";
    public static final String REGISTER_URL = String.format("%s/api/users/register", BASE_URL);
    public static final String LOGIN_URL = String.format("%s/api/users/login", BASE_URL);
    public static final String USER_ACTIVATION_URL = String.format("%s/api/users/activate", BASE_URL);
    public static final String USER_DETAILS = String.format("%s/api/users/me", BASE_URL);
    public static final String FORGOT_PASSWORD_URL = String.format("%s/api/password/forgot", BASE_URL);
    public static final String CHANGE_PASSWORD_URL = String.format("%s/api/password/change", BASE_URL);
    public static final String ACCOUNT_STAUS_URL = String.format("%s/api/users/status", BASE_URL);

    public static final String user_login_key = "user_login";
    public static final String KEY_FIRST_NAME = "first_name";
    public static final String KEY_LAST_NAME = "last_name";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_SCHOOL = "school";

    public static final String KEY_USER_TOKEN = "token";
    public static final String KEY_USER_LOGIN = "user_login";
    public static int postResponse;
    public static int responseCode = 0;
    public static boolean sVideoPaused = false;

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
