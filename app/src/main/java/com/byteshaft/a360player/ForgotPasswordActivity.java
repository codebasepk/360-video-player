package com.byteshaft.a360player;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.byteshaft.a360player.utils.AppGlobals;
import com.byteshaft.a360player.utils.Helpers;

import org.json.JSONException;

import java.io.IOException;
import java.net.HttpURLConnection;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout mNewPasswordLayout;
    private Button mRecoverButton;
    private Button mSubmitButton;

    private EditText mEmail;
    private EditText mConfirmationCode;
    private EditText mPassword;
    private EditText mNewPassword;

    private String mEmailAddressString;
    private String mConfirmationCodeString;
    private String mPasswordString;
    private String mNewPasswordString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        mNewPasswordLayout = (LinearLayout) findViewById(R.id.new_password_layout);
        mRecoverButton = (Button) findViewById(R.id.recover);
        mSubmitButton = (Button) findViewById(R.id.btn_submit);
        mEmail = (EditText) findViewById(R.id.email_address);
        mConfirmationCode = (EditText) findViewById(R.id.reset_key);
        mPassword = (EditText) findViewById(R.id.password);
        mNewPassword = (EditText) findViewById(R.id.new_password);

        mRecoverButton.setOnClickListener(this);
        mSubmitButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.recover:
                System.out.println("Button recover");
                if (validateRecoverInfo()) {
                    new RecoverPasswordTask().execute();
                }
                break;

            case R.id.btn_submit:
                if (validateSubmitInfo()) {
                    System.out.println("Button submit");
                    new ChangePasswordTask().execute();
                }
                break;
        }
    }

    public boolean validateRecoverInfo() {
        boolean valid = true;
        mEmailAddressString = mEmail.getText().toString();
        System.out.println(mEmailAddressString);
        if (mEmailAddressString.trim().isEmpty()) {
            mEmail.setError("Empty");
            valid = false;
        } else if (!mEmailAddressString.trim().isEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(mEmailAddressString).matches()) {
            mEmail.setError("Invalid E-Mail");
            valid = false;
        } else {
            mEmail.setError(null);
        }
        return valid;
    }

    public boolean validateSubmitInfo() {
        boolean valid = true;
        mEmailAddressString = mEmail.getText().toString();
        mConfirmationCodeString = mConfirmationCode.getText().toString();
        mPasswordString = mPassword.getText().toString();
        mNewPasswordString = mNewPassword.getText().toString();
        System.out.println(mEmailAddressString);
        System.out.println(mConfirmationCodeString);
        System.out.println(mPasswordString);
        System.out.println(mNewPasswordString);

        if (mConfirmationCodeString.trim().isEmpty() || mConfirmationCodeString.trim().length() != 5) {
            mConfirmationCode.setError("At least 5 characters");
            valid = false;
        } else {
            mConfirmationCode.setError(null);
        }

        if (mNewPasswordString.length() < 4) {
            mNewPassword.setError("At least 4 characters");
            valid = false;
        } else {
            mNewPassword.setError(null);
        }

        if (!mPasswordString.equals(mNewPasswordString)) {
            mNewPassword.setError("Password does not match");
            valid = false;
        } else {
            mNewPassword.setError(null);
        }
        return valid;
    }

    class RecoverPasswordTask extends AsyncTask<Void, Integer, Integer> {

        private boolean noInternet = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Helpers.showProgressDialog(ForgotPasswordActivity.this, "Sending Recovery Mail");
        }

        @Override
        protected Integer doInBackground(Void... params) {
            int  response;

            if (Helpers.isNetworkAvailable() && Helpers.isInternetWorking()) {
                try {
                    response = Helpers.forgotPassword(mEmailAddressString);
                    System.out.println(response);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            } else {
                noInternet = true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            Helpers.dismissProgressDialog();
            if (noInternet) {
                Helpers.alertDialog(ForgotPasswordActivity.this, "Connection error",
                        "Check your internet connection");
            } else if (AppGlobals.getResponseCode() == HttpURLConnection.HTTP_OK) {
                mNewPasswordLayout.setVisibility(View.VISIBLE);
                mRecoverButton.setVisibility(View.GONE);
            } else if (AppGlobals.getResponseCode() == HttpURLConnection.HTTP_FORBIDDEN &&
                    AppGlobals.getResponseCode() == HttpURLConnection.HTTP_BAD_REQUEST) {
                Toast.makeText(AppGlobals.getContext(), "Recovery Failed. User does not exist",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(AppGlobals.getContext(), "Recovery Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    class ChangePasswordTask extends AsyncTask<Void, Integer, Void> {

        private boolean noInternet = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Helpers.showProgressDialog(ForgotPasswordActivity.this, "Generating New Password");
        }

        @Override
        protected Void doInBackground(Void... params) {
            int response;
            if (Helpers.isNetworkAvailable() && Helpers.isInternetWorking()) {
                try {
                    response = Helpers.changePassword(
                            mEmailAddressString,
                            mConfirmationCodeString,
                            mNewPasswordString
                    );
                    System.out.println(response);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            } else {
                noInternet = true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Helpers.dismissProgressDialog();
            if (noInternet) {
                Helpers.alertDialog(ForgotPasswordActivity.this, "Connection error",
                        "Check your internet connection");
            } else if (AppGlobals.getResponseCode() == HttpURLConnection.HTTP_OK) {
                mNewPasswordLayout.setVisibility(View.VISIBLE);
                Toast.makeText(AppGlobals.getContext(), "Password successfully changed",
                        Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            } else {
                Toast.makeText(AppGlobals.getContext(), "Password changed Failed",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
