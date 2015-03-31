///////////////////////////////////////////////////////////////////////////////////////////
// Copyright (c) Heartware Group Fall 2014 - Spring 2015
// @license
// @purpose ASU Computer Science Capstone Project
// @app a smart health application
// @authors Mark Aleheimer, Ryan Case, Tyler O'Brien, Amy Mazzola, Zach Mertens, Sri Somanchi
// @mailto zmertens@asu.edu
// @version 1.0
//
// Source code: github.com/tjobrie5/HeartWare
//
// Description: Main entry point for the application, handles user login
///////////////////////////////////////////////////////////////////////////////////////////

package heartware.com.heartware_master;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;

public class MainActivity extends ActionBarActivity implements LoginDialogFragment.LoginDialogListener
{
    private static final String TAG = MainActivity.class.getSimpleName();

    private EditText etUserName;
    private EditText etSex;
    private boolean mLoggedIn;

    private DBAdapter dbAdapter;
    private LoginDialogFragment mLoginDialog;
    private JawboneUpHelper mJboneHelper;
    private String mCurrentProfileId;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // adding invisible worker fragments:
        //  https://developer.android.com/guide/components/fragments.html
        mJboneHelper = new JawboneUpHelper();
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().add(mJboneHelper, JawboneUpHelper.TAG).commit();
        mLoggedIn = false;
        createLoginDialog();

        dbAdapter = new DBAdapter(this);

        etUserName = (EditText) findViewById(R.id.etUserName);
        etSex = (EditText) findViewById(R.id.etSex);
    } // onCreate

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId()) {
            case R.id.action_auth:
                mLoggedIn = !mLoggedIn;
                if(!mLoggedIn)
                    item.setIcon(R.drawable.ic_action_logout);
                else
                    item.setIcon(R.drawable.ic_action_login);
                clearEditTexts();
                mCurrentProfileId = "0";
                mLoginDialog.show(getFragmentManager(), TAG);
                return true;
            case R.id.action_update:
                HashMap<String, String> queryValues = new HashMap<>();
                queryValues.put(DBAdapter.PROFILE_ID, mCurrentProfileId);
                queryValues.put(DBAdapter.USERNAME, etUserName.getText().toString());
                queryValues.put(DBAdapter.SEX, etSex.getText().toString());
                dbAdapter.updateProfile(queryValues);
                Toast.makeText(this, "Updating " + etUserName.getText().toString(),
                        Toast.LENGTH_SHORT).show();

                return true;
            case R.id.action_workouts:
                Intent intent = new Intent(getApplicationContext(), WorkoutsActivity.class);
                intent.putExtra(DBAdapter.PROFILE_ID, mCurrentProfileId);
                startActivity(intent);
                return true;
            case R.id.action_friends:
                startActivity(new Intent(getApplicationContext(), FriendsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * create and show the login dialog
     */
    public void createLoginDialog()
    {
        // create and show the login pop up as soon as the main activity is created
        mLoginDialog = new LoginDialogFragment();
        mLoginDialog.show(getFragmentManager(), TAG);
    }

    /**
     * returning user
     * @param dialog
     */
    @Override
    public void onDialogPositiveClick(DialogFragment dialog, final String user, final String pw)
    {
        Log.d(TAG, " onDialogPositiveClick " + user + " " + pw);
        HashMap<String, String> profile = dbAdapter.getProfileByUserAndPass(user, pw);
        if(profile.size() == 0) {
            // no profile exist, force the user to enter again
            Log.d(TAG, user + " does not exist");
            // not sure the best way to keep the dialog open, but this way works
            mLoginDialog.dismiss();
            mLoginDialog.show(getFragmentManager(), TAG);
        }
        else {
            etUserName.setText(profile.get(DBAdapter.USERNAME));
            etSex.setText(profile.get(DBAdapter.SEX));
            mCurrentProfileId = profile.get(DBAdapter.PROFILE_ID);
            mLoginDialog.dismiss();
            mJboneHelper.sendToken(); // send the token
        }
    }

    /**
     * new user
     * @param dialog
     */
    @Override
    public void onDialogNegativeClick(DialogFragment dialog, final String user, final String pw)
    {
        Log.d(TAG, " onDialogNegativeClick " + user + " " + pw);
        Log.d(TAG, "creating " + user);
        // insert into database
        HashMap<String, String> newProfile = new HashMap<>();
        newProfile.put(DBAdapter.USERNAME, user);
        newProfile.put(DBAdapter.PASSWORD, pw);
        dbAdapter.createProfile(newProfile);
        // this is sloppy, but once the profile is created a new profileId is made and we need it
        mCurrentProfileId = dbAdapter.getProfileByUserAndPass(user, pw).get(DBAdapter.PROFILE_ID);
        mLoginDialog.dismiss();
        // set the edit text for the user name on the main layout
        etUserName.setText(user);
        mJboneHelper.sendToken();
    }

    /**
     * Erase the text fields when user logs out
     */
    public void clearEditTexts()
    {
        etUserName.setText("");
        etSex.setText("");
    }
} // MainActivity class
