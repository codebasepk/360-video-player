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

import java.io.IOException;
import java.net.HttpURLConnection;

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

    private class LoginTask extends AsyncTask <String, String, String> {

        private boolean noInternet = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Helpers.showProgressDialog(LoginActivity.this , "Pleas wait");
        }

        @Override
        protected String doInBackground(String... strings) {

            String data = null;
            if (Helpers.isNetworkAvailable() && Helpers.isInternetWorking()) {
                try {
                    data = Helpers.userLogin(mEmail, mPasswordEntry);
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
            Helpers.dismissProgressDialog();
            if (noInternet) {
                Helpers.alertDialog(LoginActivity.this, "Connection error",
                        "Check your internet connection");
            } else if (AppGlobals.getResponseCode() == HttpURLConnection.HTTP_OK) {
                Helpers.saveDataToSharedPreferences(AppGlobals.KEY_USER_TOKEN, s);
                Log.i("Token", " " + Helpers.getStringFromSharedPreferences(AppGlobals.KEY_USER_TOKEN));
                Helpers.saveUserLogin(true);
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            } else {
                Toast.makeText(AppGlobals.getContext(), "Login Failed! Invalid Email or Password",
                        Toast.LENGTH_SHORT).show();
            }

        }
    }
}
