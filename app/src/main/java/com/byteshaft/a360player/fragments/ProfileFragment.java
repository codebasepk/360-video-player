package com.byteshaft.a360player.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.byteshaft.a360player.MainActivity;
import com.byteshaft.a360player.R;
import com.byteshaft.a360player.utils.AppGlobals;
import com.byteshaft.a360player.utils.Helpers;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private View mBaseView;
    private Button mDoneButton;
    private EditText mFirstName;
    private EditText mLastName;
    private EditText mSchool;
    private EditText mEmailAddress;
    private EditText mPassword;
    private EditText mNewPassword;


    private String mFirstNameString;
    private String mLastNameString;
    private String mSchoolString;
    public String mEmailString;
    private String mPasswordString;
    private String mNewPasswordString;
    private boolean isPasswordChaged = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        mBaseView = inflater.inflate(R.layout.fragment_profile, container, false);
        mFirstName = (EditText) mBaseView.findViewById(R.id.first_name);
        mLastName = (EditText) mBaseView.findViewById(R.id.last_name);
        mSchool = (EditText) mBaseView.findViewById(R.id.school);
        mEmailAddress = (EditText) mBaseView.findViewById(R.id.email);
        mPassword = (EditText) mBaseView.findViewById(R.id.password);
        mNewPassword = (EditText) mBaseView.findViewById(R.id.repeat_password);
        mPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                isPasswordChaged = true;
            }
        });
        mNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                isPasswordChaged = true;
            }
        });
        mDoneButton = (Button) mBaseView.findViewById(R.id.done_button);
        mDoneButton.setOnClickListener(this);
        mDoneButton.setVisibility(View.GONE);
        getValuesFromSharedPreference();
        return mBaseView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.done_button:
                mFirstNameString = mFirstName.getText().toString();
                mLastNameString = mLastName.getText().toString();
                mSchoolString = mSchool.getText().toString();
                Log.i("TAG", "" + isPasswordChaged);
                if (isPasswordChaged) {
                    if (validateSubmitInfo()) {
                        new UpdateUserProfileTask().execute();
                    }
                } else {
                    new UpdateUserProfileTask().execute();
                }

        }
    }

    public boolean validateSubmitInfo() {
        boolean valid = true;
        mPasswordString = mPassword.getText().toString();
        mNewPasswordString = mNewPassword.getText().toString();
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

    public void getValuesFromSharedPreference() {
        mFirstName.setText(Helpers.getStringFromSharedPreferences(AppGlobals.KEY_FIRST_NAME));
        mFirstName.setEnabled(false);
        mLastName.setText(Helpers.getStringFromSharedPreferences(AppGlobals.KEY_LAST_NAME));
        mLastName.setEnabled(false);
        mSchool.setText(Helpers.getStringFromSharedPreferences(AppGlobals.KEY_SCHOOL));
        mSchool.setEnabled(false);
        mEmailAddress.setText(Helpers.getStringFromSharedPreferences(AppGlobals.KEY_EMAIL));
        mEmailAddress.setEnabled(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        isPasswordChaged = false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.update_profile, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.user_profile_button:
                mFirstName.setEnabled(true);
                mFirstName.setSelection(mFirstName.getText().length());
                mLastName.setEnabled(true);
                mLastName.setSelection(mLastName.getText().length());
                mSchool.setEnabled(true);
                mSchool.setSelection(mSchool.getText().length());
                mEmailAddress.setEnabled(false);
                mNewPassword.setVisibility(View.VISIBLE);
                mDoneButton.setVisibility(View.VISIBLE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    class UpdateUserProfileTask extends AsyncTask<Void, String, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Helpers.showProgressDialog(getActivity(), "Updating Profile");
        }

        @Override
        protected Integer doInBackground(Void... params) {

            int jsonObject = 0;
            try {
                jsonObject = Helpers.updateUser(
                        mFirstNameString,
                        mLastNameString,
                        mSchoolString,
                        mNewPasswordString);
                if (jsonObject == HttpURLConnection.HTTP_OK) {
                    System.out.println(jsonObject);
                    //saving values
                    Helpers.saveDataToSharedPreferences(AppGlobals.KEY_FIRST_NAME, mFirstNameString);
                    Helpers.saveDataToSharedPreferences(AppGlobals.KEY_LAST_NAME, mLastNameString);
                    Helpers.saveDataToSharedPreferences(AppGlobals.KEY_SCHOOL, mSchoolString);
                    Log.i("First name", " " + Helpers.getStringFromSharedPreferences(
                            AppGlobals.KEY_FIRST_NAME));
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return jsonObject;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            Helpers.dismissProgressDialog();
            if (integer == HttpURLConnection.HTTP_OK) {
                MainActivity.getInstance().openFirstTab();
                mFirstName.setEnabled(false);
                mLastName.setEnabled(false);
                mSchool.setEnabled(false);
                mEmailAddress.setEnabled(false);
                isPasswordChaged = false;
            }
        }
    }
}
