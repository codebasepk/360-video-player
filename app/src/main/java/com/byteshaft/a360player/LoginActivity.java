package com.byteshaft.a360player;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.byteshaft.a360player.utils.AppGlobals;
import com.byteshaft.a360player.utils.Helpers;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {


    private Button mLoginButton;
    private EditText mEmailAddress;
    private EditText mPassword;
    private TextView mSignUpText;
    private TextView mForgotPasswordTextView;


    private String mEmail;
    private String mPasswordEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmailAddress = (EditText) findViewById(R.id.email_address);
        mPassword = (EditText) findViewById(R.id.password_entry);
        mLoginButton = (Button) findViewById(R.id.login);
        mSignUpText = (TextView) findViewById(R.id.signup_text);
        mForgotPasswordTextView = (TextView) findViewById(R.id.tv_login_forgot_password);
        mLoginButton.setOnClickListener(this);
        mSignUpText.setOnClickListener(this);
        mForgotPasswordTextView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login:
                // TODO: 09/06/2016
                System.out.println("login");
                System.out.println(validateEditText());
                if (!validateEditText()) {
                    Toast.makeText(getApplicationContext(), "invalid credentials",
                            Toast.LENGTH_SHORT).show();
                } else {
                    new LoginTask().execute();
                }
                break;
            case R.id.signup_text:
                // TODO: 09/06/2016
                System.out.println("sign up");
                startActivity(new Intent(AppGlobals.getContext(), RegistrationActivity.class));
                break;
            case R.id.tv_login_forgot_password:
                System.out.println("forgot password");
                startActivity(new Intent(getApplicationContext(), ForgotPasswordActivity.class));
                break;
        }
    }

    private boolean validateEditText() {

        boolean valid = true;
        mPasswordEntry = mPassword.getText().toString();
        mEmail = mEmailAddress.getText().toString();

        if (mPasswordEntry.trim().isEmpty() || mPasswordEntry.length() < 4) {
            mPassword.setError("must contain 4 character");
            valid = false;
        } else {
            mPassword.setError(null);
        }

        if (mEmail.trim().isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(mEmail).matches()) {
            mEmailAddress.setError("invalid email");
            valid = false;
        } else {
            mEmailAddress.setError(null);
        }
        return valid;
    }

    private class LoginTask extends AsyncTask<String, String, String> {

        private boolean noInternet = false;
        private int accountStatus;
        private int loginState = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Helpers.showProgressDialog(LoginActivity.this, "LoggingIn");
        }

        @Override
        protected String doInBackground(String... strings) {

            String data = null;
            if (Helpers.isNetworkAvailable() && Helpers.isInternetWorking()) {
                try {
                    accountStatus = Helpers.accountStatus(mEmail);
                    System.out.println(accountStatus + " status");
                    data = Helpers.userLogin(mEmail, mPasswordEntry);
                    loginState = AppGlobals.getResponseCode();
                    System.out.println(data + "working");
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            } else {
                noInternet = true;
            }
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e("TAG", ""  + accountStatus);

            Helpers.dismissProgressDialog();
            if (noInternet) {
                Helpers.alertDialog(LoginActivity.this, "Connection error",
                        "Check your internet connection");
            }
            if (loginState == HttpURLConnection.HTTP_OK && !s.trim().isEmpty()) {
                Helpers.saveDataToSharedPreferences(AppGlobals.KEY_USER_TOKEN, s);
                Log.i("Token", " " + Helpers.getStringFromSharedPreferences(AppGlobals.KEY_USER_TOKEN));
                Helpers.saveUserLogin(true);
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                new GetUserDataTask().execute();
                Helpers.saveUserLogin(true);
                finish();
            } else if (loginState == HttpURLConnection.HTTP_UNAUTHORIZED) {
                Toast.makeText(AppGlobals.getContext(), "Login Failed! Invalid Email or Password",
                        Toast.LENGTH_SHORT).show();
            } else {
                if (accountStatus == HttpURLConnection.HTTP_FORBIDDEN) {
                    System.out.println(accountStatus + "working");
                    Toast.makeText(AppGlobals.getContext(), "Login Failed! Account not activated",
                            Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), CodeConfirmationActivity.class));
                } else if (accountStatus == HttpURLConnection.HTTP_NOT_FOUND) {
                    Toast.makeText(AppGlobals.getContext(), "Login Failed! Account not found",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AppGlobals.getContext(), "Login Failed! Invalid Email or Password",
                            Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    class GetUserDataTask extends AsyncTask<Void, String, Void> {

        private boolean unAuthorized = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            JSONObject jsonObject = null;

            try {
                String urlME = AppGlobals.USER_DETAILS;
                URL url = new URL(urlME);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestMethod("GET");
                connection.setRequestProperty("charset", "utf-8");
                connection.setRequestProperty("Authorization", "Token " + Helpers.getStringFromSharedPreferences("token"));
                AppGlobals.setResponseCode(connection.getResponseCode());
                if (connection.getResponseCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    unAuthorized = true;
                } else {
                    jsonObject = Helpers.readResponse(connection);
                }
                if (AppGlobals.getResponseCode() == HttpURLConnection.HTTP_OK) {

                    String firstName = jsonObject.getString(AppGlobals.KEY_FIRST_NAME);
                    String lastName = jsonObject.getString(AppGlobals.KEY_LAST_NAME);
                    String school = jsonObject.getString(AppGlobals.KEY_SCHOOL);
                    String email = jsonObject.getString(AppGlobals.KEY_EMAIL);

                    //saving values
                    Helpers.saveDataToSharedPreferences(AppGlobals.KEY_FIRST_NAME, firstName);
                    Helpers.saveDataToSharedPreferences(AppGlobals.KEY_LAST_NAME, lastName);
                    Helpers.saveDataToSharedPreferences(AppGlobals.KEY_SCHOOL, school);
                    Helpers.saveDataToSharedPreferences(AppGlobals.KEY_EMAIL, email);
                    Log.i("First name", " " + Helpers.getStringFromSharedPreferences(AppGlobals.KEY_FIRST_NAME));
                    Helpers.saveUserLogin(true);
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (unAuthorized) {
                Helpers.alertDialog(LoginActivity.this, "UnAuthorized",
                        "Username or password is incorrect");
            }

        }
    }
}
