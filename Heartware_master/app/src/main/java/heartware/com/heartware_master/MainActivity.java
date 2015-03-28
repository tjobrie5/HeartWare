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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;

public class MainActivity extends ActionBarActivity implements View.OnClickListener,
        LoginDialogFragment.LoginDialogListener
{
    private static final String TAG = MainActivity.class.getSimpleName();

    private Button bUpdate;
    private Button bSync;
    private Button bLogout;
    private EditText etUserName;
    private EditText etSex;

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

        createLoginDialog();

        dbAdapter = new DBAdapter(this);

        bSync = (Button) findViewById(R.id.bSync);
        bSync.setOnClickListener(this);

        bUpdate = (Button) findViewById(R.id.bUpdate);
        bUpdate.setOnClickListener(this);

        bLogout = (Button) findViewById(R.id.bLogout);
        bLogout.setOnClickListener(this);

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
            case R.id.action_search:
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
     * handle all possible button clicks for this view
     * @param v
     */
    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.bSync:
                Log.d(TAG, "bSync has been pressed ------");
//                mJboneHelper.sync();
                break;

            case R.id.bUpdate:
                Log.d(TAG, "updating " + etUserName.getText().toString());
                HashMap<String, String> queryValues = new HashMap<>();
                queryValues.put(DBAdapter.PROFILE_ID, mCurrentProfileId);
                queryValues.put(DBAdapter.USERNAME, etUserName.getText().toString());
                queryValues.put(DBAdapter.SEX, etSex.getText().toString());
                dbAdapter.updateProfile(queryValues);
                Toast.makeText(this, "Updating Profile Information",
                        Toast.LENGTH_SHORT).show();
                break;

            case R.id.bLogout:
                Log.d(TAG, " logging out");
                clearEditTexts();
                mCurrentProfileId = "0";
                mJboneHelper.stop(); // stop syncing data for this user
                mLoginDialog.show(getFragmentManager(), TAG);
                break;
        }
    } // onClick()

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
            bUpdate.setText(R.string.update);
            etUserName.setText(profile.get(DBAdapter.USERNAME));
            etSex.setText(profile.get(DBAdapter.SEX));
            mCurrentProfileId = profile.get(DBAdapter.PROFILE_ID);
            mLoginDialog.dismiss();
            mJboneHelper.sync();
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
        mJboneHelper.sync();
    }

    /**
     * Erase the text fields when user logs out
     */
    public void clearEditTexts()
    {
        etUserName.setText("");
        etSex.setText("");
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        Log.d(TAG, "in on Start");
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        Log.d(TAG, "in on Stop)");
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }
} // MainActivity class
