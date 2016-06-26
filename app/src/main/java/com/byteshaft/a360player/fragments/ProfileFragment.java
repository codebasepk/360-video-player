package com.byteshaft.a360player.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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

public class ProfileFragment extends Fragment implements View.OnClickListener{

    private View mBaseView;
    private Button mDoneButton;
    private EditText mFirstName;
    private EditText mLastName;
    private EditText mSchool;
    private EditText mEmailAddress;
    private EditText mCurrentPassword;
    private EditText mNewPassword;


    private String mFirstNameString;
    private String mLastNameString;
    private String mSchoolString;
    public String mEmailString;
    private String mCurrentPasswordString;
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
        mCurrentPassword = (EditText) mBaseView.findViewById(R.id.current_password);
        mNewPassword = (EditText) mBaseView.findViewById(R.id.new_password);
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
                mCurrentPasswordString = mCurrentPassword.getText().toString();
                mNewPasswordString = mNewPassword.getText().toString();
                System.out.println(mFirstNameString);
                break;
        }

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
                mEmailAddress.setEnabled(true);
                mEmailAddress.setSelection(mEmailAddress.getText().length());
                mNewPassword.setVisibility(View.VISIBLE);
                mDoneButton.setVisibility(View.VISIBLE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
