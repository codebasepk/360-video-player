package com.byteshaft.a360player.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

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
                mEmailString = mEmailAddress.getText().toString();
                mSchoolString = mSchool.getText().toString();
                System.out.println(mFirstNameString);
                if (validateSubmitInfo()) {
                    new UpdateUserProfileTask().execute();
                }
        }

    }

    public boolean validateSubmitInfo() {
        boolean valid = true;
        mPasswordString = mPassword.getText().toString();
        mNewPasswordString = mNewPassword.getText().toString();
        System.out.println(mPasswordString + "new password");
        System.out.println(mNewPasswordString + "repeat new password");
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.update_profile, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.user_profile_button:
                System.out.println("working");
                mFirstName.setEnabled(true);
                mFirstName.setSelection(mFirstName.getText().length());
                mLastName.setEnabled(true);
                mLastName.setSelection(mLastName.getText().length());
                mSchool.setEnabled(true);
                mSchool.setSelection(mSchool.getText().length());
                mEmailAddress.setEnabled(false);
                mEmailAddress.setSelection(mEmailAddress.getText().length());
                mNewPassword.setVisibility(View.VISIBLE);
                mDoneButton.setVisibility(View.VISIBLE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    class UpdateUserProfileTask extends AsyncTask<Void, String, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Helpers.showProgressDialog(getActivity(), "Updating Profile");
        }

        @Override
        protected Void doInBackground(Void... params) {

            JSONObject jsonObject;
            try {
                jsonObject = Helpers.updateUser(
                        mFirstNameString,
                        mLastNameString,
                        mSchoolString,
                        mNewPasswordString);
                System.out.println(jsonObject);
                if (AppGlobals.getResponseCode() == HttpURLConnection.HTTP_OK) {
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Helpers.dismissProgressDialog();
        }
    }
}
