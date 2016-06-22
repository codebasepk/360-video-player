package com.byteshaft.a360player;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.byteshaft.a360player.utils.AppGlobals;
import com.byteshaft.a360player.utils.Helpers;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Created by husnain on 6/7/16.
 */
public class CodeConfirmationActivity extends Activity {

    private Button mSubmitButton;
    private EditText mEmail;
    private EditText mCode;

    private String mConfirmationEmail;
    private String mConformationCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirmation_code_activity);
        mEmail = (EditText) findViewById(R.id.et_confirmation_code_email);
        mCode = (EditText) findViewById(R.id.et_confirmation_code);
        mSubmitButton = (Button) findViewById(R.id.btn_confirmation_code_submit);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mConfirmationEmail = mEmail.getText().toString();
                mConformationCode = mCode.getText().toString();
                System.out.println(mConfirmationEmail);
                System.out.println(mConformationCode);
                if (validateConfirmationCode()) {
                    new UserConfirmationTask().execute();
                }
            }
        });
        String email = Helpers.getStringFromSharedPreferences(AppGlobals.KEY_EMAIL);
        mEmail.setText(email);
        mConfirmationEmail = RegistrationActivity.mEmail;
    }

    public boolean validateConfirmationCode() {
        boolean valid = true;
        if (mConformationCode.isEmpty() || mConformationCode.length() < 4) {
            mCode.setError("Minimum 4 Characters");
            valid = false;
        } else {
            mCode.setError(null);
        }
        return valid;
    }

    private class UserConfirmationTask extends AsyncTask<String, Integer, String> {

        private boolean noInternet = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mSubmitButton.setEnabled(false);
            Helpers.showProgressDialog(CodeConfirmationActivity.this, "Activating User");

        }

        @Override
        protected String doInBackground(String... params) {
            int jsonObject;
            if (Helpers.isNetworkAvailable() && Helpers.isInternetWorking()) {

                try {
                    jsonObject = Helpers.ActivationCodeConfirmation(mConfirmationEmail
                            , mConformationCode);
                    System.out.println(jsonObject + "okay");

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            } else {
                noInternet = true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String aString) {
            super.onPostExecute(aString);
            Helpers.dismissProgressDialog();
            if (noInternet) {
                Helpers.alertDialog(CodeConfirmationActivity.this, "Connection error",
                        "Check your internet connection");
            }
            if (AppGlobals.getResponseCode() == HttpURLConnection.HTTP_OK) {
                Toast.makeText(AppGlobals.getContext(),
                        "Confirmation successful",
                        Toast.LENGTH_LONG).show();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            } else {
                Toast.makeText(AppGlobals.getContext(),
                        "Confirmation failed, check internet and retry", Toast.LENGTH_LONG).show();
            }
        }
    }
}
