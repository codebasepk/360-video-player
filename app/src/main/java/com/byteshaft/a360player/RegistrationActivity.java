package com.byteshaft.a360player;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

public class RegistrationActivity extends AppCompatActivity {

    private Button mRegisterButton;
    private EditText mFirstName;
    private EditText mLastName;
    private EditText mSchool;
    private EditText mEmailAddress;
    private EditText mPassword;


    private String mFirstNameString;
    private String mLastNameString;
    private String mSchoolString;
    private String mEmail;
    private String mPasswordEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mFirstName = (EditText) findViewById(R.id.first_name);
        mLastName = (EditText) findViewById(R.id.last_name);
        mSchool = (EditText) findViewById(R.id.school);
        mEmailAddress = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);
        mRegisterButton = (Button) findViewById(R.id.register_button);

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateEditText()) {
                    Toast.makeText(getApplicationContext(), "try again", Toast.LENGTH_SHORT).show();
                } else {
                    // TODO: 18/06/2016 execute task
                    new RegistrationTask().execute();
                }
            }
        });
    }

    private boolean validateEditText() {

        boolean valid = true;
        mFirstNameString = mFirstName.getText().toString();
        mPasswordEntry = mPassword.getText().toString();
        mEmail = mEmailAddress.getText().toString();

        if (mFirstNameString.trim().isEmpty() || mFirstNameString.length() < 3) {
            mFirstName.setError("enter at least 3 characters");
            valid = false;
        } else {
            mFirstName.setError(null);
        }

        if (mPasswordEntry.trim().isEmpty() || mPasswordEntry.length() < 4) {
            mPassword.setError("enter at least 4 characters");
            valid = false;
        } else {
            mPassword.setError(null);
        }

        if (mEmail.trim().isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(mEmail).matches()) {
            mEmailAddress.setError("please provide a valid email");
            valid = false;
        } else {
            mEmailAddress.setError(null);
        }
        return valid;
    }

    class RegistrationTask extends AsyncTask<String, String, String> {

        private boolean noInternet = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Helpers.showProgressDialog(RegistrationActivity.this , "Registration");
        }

        @Override
        protected String doInBackground(String... strings) {
            JSONObject jsonObject;
            if (Helpers.isNetworkAvailable() && Helpers.isInternetWorking()) {
                try {
                    jsonObject = Helpers.registerUser(mFirstNameString, mLastNameString, mSchoolString, mEmail,mPasswordEntry);
                    if (AppGlobals.getResponseCode() == HttpURLConnection.HTTP_CREATED) {
                        System.out.println(jsonObject + "working");
                        String fullName = jsonObject.getString(AppGlobals.KEY_FULLNAME);
                        String email = jsonObject.getString(AppGlobals.KEY_EMAIL);


                        //saving values
                        Helpers.saveDataToSharedPreferences(AppGlobals.KEY_FULLNAME, fullName);
                        Log.i("Full name", " " + Helpers.getStringFromSharedPreferences(AppGlobals.KEY_FULLNAME));
                        Helpers.saveDataToSharedPreferences(AppGlobals.KEY_EMAIL, email);
                        Helpers.saveUserLogin(true);

                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            } else {
                noInternet = true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Helpers.dismissProgressDialog();
            if (noInternet) {
                Helpers.alertDialog(RegistrationActivity.this, "Connection error",
                        "Check your internet connection");
            }
            if (AppGlobals.getResponseCode() == HttpURLConnection.HTTP_CREATED) {
                Toast.makeText(AppGlobals.getContext(),
                        "Account Created Successfully",
                        Toast.LENGTH_LONG).show();
                finish();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            } else if (AppGlobals.getResponseCode() == HttpURLConnection.HTTP_BAD_REQUEST) {
                Toast.makeText(AppGlobals.getContext(), "Registration failed. Email already in use",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
