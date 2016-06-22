package com.byteshaft.a360player.utils;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Helpers {

    private static ProgressDialog progressDialog;

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


    public static void saveDataToSharedPreferences(String key, String value) {
        SharedPreferences sharedPreferences = getPreferenceManager();
        sharedPreferences.edit().putString(key, value).apply();
    }

    public static String getStringFromSharedPreferences(String key) {
        SharedPreferences sharedPreferences = getPreferenceManager();
        return sharedPreferences.getString(key, "");
    }

    public static void saveUserLogin(boolean value) {
        SharedPreferences sharedPreferences = getPreferenceManager();
        sharedPreferences.edit().putBoolean(AppGlobals.KEY_USER_LOGIN, value).apply();
    }

    public static void alertDialog(Activity activity, String title, String msg) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(msg).setCancelable(false).setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public static void showProgressDialog(Activity context, String message) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    public static void dismissProgressDialog() {
        if(progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    public static boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager)
                AppGlobals.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public static boolean isInternetWorking() {
        boolean success = false;

        try {
            URL e = new URL("https://google.com");
            HttpURLConnection connection = (HttpURLConnection)e.openConnection();
            connection.setConnectTimeout(10000);
            connection.connect();
            success = connection.getResponseCode() == 200;
        } catch (IOException var3) {
            var3.printStackTrace();
        }

        return success;
    }


    /**
     * web service helpers start from here  */

    public static HttpURLConnection openConnectionForUrl(String targetUrl, String method)
            throws IOException {
        URL url = new URL(targetUrl);
        System.out.println(targetUrl);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("charset", "utf-8");
        connection.setRequestMethod(method);
        return connection;
    }

    public static String getRegistrationData(String firstName, String lastName, String school,
                                             String email, String password) {
        JSONObject object = new JSONObject();

        try {
            object.put("first_name", firstName);
            object.put("last_name", lastName);
            object.put("school", school);
            object.put("email", email);
            object.put("password", password);
        } catch (JSONException var8) {
            var8.printStackTrace();
        }

        return object.toString();
    }

    private static JSONObject readResponse(HttpURLConnection connection
    ) throws IOException, JSONException {
        InputStream is = connection.getInputStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        StringBuilder response = new StringBuilder();

        String line;
        while((line = rd.readLine()) != null) {
            response.append(line);
            response.append('\r');
        }
        return new JSONObject(response.toString());
    }

    private static void sendRequestData(HttpURLConnection connection, String body)
            throws IOException {
        byte[] outputInBytes = body.getBytes("UTF-8");
        OutputStream os = connection.getOutputStream();
        os.write(outputInBytes);
        os.close();
    }

    public static JSONObject registerUser(
            String firstName, String lastName, String school,
                                          String email, String password)
            throws IOException, JSONException {
        String data = getRegistrationData(firstName,lastName, school ,email, password);
        System.out.println(data);
        String url = AppGlobals.REGISTER_URL;
        HttpURLConnection connection = openConnectionForUrl(url, "POST");
        sendRequestData(connection, data);
        AppGlobals.setResponseCode(connection.getResponseCode());
        System.out.println(connection.getResponseCode());
        return readResponse(connection);
    }

    public static String getUserConfirmationData(String email, String activationKey) {
        JSONObject object = new JSONObject();
        System.out.println(object);

        try {
            object.put("email", email);
            object.put("key", activationKey);
        } catch (JSONException var4) {
            var4.printStackTrace();
        }

        return object.toString();
    }

    public static int ActivationCodeConfirmation(String email, String activationKey) throws IOException, JSONException {
        String data = getUserConfirmationData(email, activationKey);
        System.out.println(data);
        String url = AppGlobals.USER_ACTIVATION_URL;
        HttpURLConnection connection = openConnectionForUrl(url, "POST");
        sendRequestData(connection, data);
        System.out.println(data);
        AppGlobals.setResponseCode(connection.getResponseCode());
        return connection.getResponseCode();
    }

    /**
     *
     * Login starts here
     *
     */

    public static String getLoginData(String email, String password) {
        JSONObject object = new JSONObject();

        try {
            object.put("username", email);
            object.put("password", password);
        } catch (JSONException var4) {
            var4.printStackTrace();
        }

        return object.toString();
    }

    public static String userLogin(String email, String password)
            throws IOException, JSONException {
        String data = getLoginData(email, password);
        System.out.println(data);
        String url = AppGlobals.LOGIN_URL;
        HttpURLConnection connection = openConnectionForUrl(url, "POST");
        sendRequestData(connection, data);
        AppGlobals.setResponseCode(connection.getResponseCode());
        JSONObject jsonObj = readResponse(connection);
        System.out.println(jsonObj);
        return (String)jsonObj.get("token");
    }

    public static JSONObject userData() throws IOException, JSONException {
        String urlME = AppGlobals.USER_DETAILS;
        URL url = new URL(urlME);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("GET");
        connection.setRequestProperty("charset", "utf-8");
        connection.setRequestProperty("Authorization", "Token " + Helpers.getStringFromSharedPreferences("token"));
        AppGlobals.setResponseCode(connection.getResponseCode());
        return readResponse(connection);
    }

    public static String getForgotPassword(String email) {
        JSONObject object = new JSONObject();

        try {
            object.put("email", email);
        } catch (JSONException var8) {
            var8.printStackTrace();
        }
        return object.toString();
    }

    public static Integer forgotPassword(String email) throws IOException, JSONException {
        String data = getForgotPassword(email);
        System.out.println(data);
        String url = AppGlobals.FORGOT_PASSWORD_URL;
        HttpURLConnection connection = openConnectionForUrl(url, "POST");
        sendRequestData(connection, data);
        AppGlobals.setResponseCode(connection.getResponseCode());
        return connection.getResponseCode();
    }

    public static Integer changePassword(String email, String resetkey, String newpassword) throws IOException, JSONException {
        String data = changePasswordData(email, resetkey, newpassword);
        System.out.println(data);
        String url = AppGlobals.CHANGE_PASSWORD_URL;
        HttpURLConnection connection = openConnectionForUrl(url, "POST");
        sendRequestData(connection, data);
        AppGlobals.setResponseCode(connection.getResponseCode());
        return connection.getResponseCode();
    }

    public static String changePasswordData(String email, String resetkey, String newpassword) {
        JSONObject object = new JSONObject();

        try {
            object.put("email", email);
            object.put("reset_key", resetkey);
            object.put("new_password", newpassword);
        } catch (JSONException var8) {
            var8.printStackTrace();
        }

        return object.toString();
    }
}
