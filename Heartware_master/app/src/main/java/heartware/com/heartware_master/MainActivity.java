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
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.HashMap;

public class MainActivity extends FragmentActivity implements View.OnClickListener,
        LoginDialogFragment.LoginDialogListener
{
    private static final String TAG = MainActivity.class.getSimpleName();

    private Button b_friends;
    private Button b_graphs;
    private Button bUpdate;
    private Button bSync;
    private Button bLogout;
    private EditText etUserName;
    private EditText etSex;
    private EditText etExercises;
    private EditText etDisabilities;
    private EditText etWorkoutLocations;

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

        b_graphs = (Button) findViewById(R.id.bGoals);
        b_graphs.setOnClickListener(this);

        b_friends = (Button) findViewById(R.id.bFriends);
        b_friends.setOnClickListener(this);

        bSync = (Button) findViewById(R.id.bSync);
        bSync.setOnClickListener(this);

        bUpdate = (Button) findViewById(R.id.bUpdate);
        bUpdate.setOnClickListener(this);

        bLogout = (Button) findViewById(R.id.bLogout);
        bLogout.setOnClickListener(this);

        etUserName = (EditText) findViewById(R.id.etUserName);
        etSex = (EditText) findViewById(R.id.etSex);
        etExercises = (EditText) findViewById(R.id.etExercises);
        etDisabilities = (EditText) findViewById(R.id.etDisabilities);
        etWorkoutLocations = (EditText) findViewById(R.id.etWorkoutLocations);
    } // onCreate

// @TODO : add action bar activity ?
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
            case R.id.action_search: return true;
            case R.id.action_goals: return true;
            case R.id.action_friends: return true;
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
            case R.id.bGoals:
                startActivity(new Intent(getApplicationContext(), GoalsActivity.class));
                break;

            case R.id.bFriends:
                startActivity(new Intent(getApplicationContext(), FriendsActivity.class));
                break;

            case R.id.bSync:
                // @TODO : perform some kind of sync between Android and Jawbone UP
                Log.d(TAG, "bSync has been pressed ------");
                mJboneHelper.sync();
                break;

            case R.id.bUpdate:
                Log.d(TAG, "updating " + etUserName.getText().toString());
                HashMap<String, String> queryValues = new HashMap<>();
                queryValues.put(DBAdapter.PROFILE_ID, mCurrentProfileId);
                queryValues.put(DBAdapter.USER_NAME, etUserName.getText().toString());
                queryValues.put(DBAdapter.SEX, etSex.getText().toString());
                queryValues.put(DBAdapter.FAV_EXERCISE, etExercises.getText().toString());
                queryValues.put(DBAdapter.DISABILITIES, etDisabilities.getText().toString());
                queryValues.put(DBAdapter.WORKOUT_LOC, etWorkoutLocations.getText().toString());
                dbAdapter.updateProfile(queryValues);
                break;

            case R.id.bLogout:
                Log.d(TAG, " logging out");
                clearEditTexts();
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
        HashMap<String, String> profile = dbAdapter.getProfileInfo(user, pw);
        if(profile.size() == 0) {
            // no profile exist, force the user to enter again
            Log.d(TAG, user + " does not exist");
            // not sure the best way to keep the dialog open, but this way works
            mLoginDialog.dismiss();
            mLoginDialog.show(getFragmentManager(), TAG);
        }
        else {
            bUpdate.setText(R.string.update);
            etUserName.setText(profile.get(DBAdapter.USER_NAME));
            etSex.setText(profile.get(DBAdapter.SEX));
            etExercises.setText(profile.get(DBAdapter.FAV_EXERCISE));
            etDisabilities.setText(profile.get(DBAdapter.DISABILITIES));
            etWorkoutLocations.setText(profile.get(DBAdapter.WORKOUT_LOC));
            mCurrentProfileId = profile.get(DBAdapter.PROFILE_ID);
            mLoginDialog.dismiss();
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
        HashMap<String, String> queryValues = new HashMap<>();
        queryValues.put(DBAdapter.USER_NAME, user);
        queryValues.put(DBAdapter.SEX, etSex.getText().toString());
        queryValues.put(DBAdapter.FAV_EXERCISE, etExercises.getText().toString());
        queryValues.put(DBAdapter.DISABILITIES, etDisabilities.getText().toString());
        queryValues.put(DBAdapter.WORKOUT_LOC, etWorkoutLocations.getText().toString());
        dbAdapter.createProfile(queryValues);
        // this is sloppy, but once the profile is created a new profileId is made and we need it
        mCurrentProfileId = dbAdapter.getProfileInfo(user, pw).get(DBAdapter.PROFILE_ID);
        mLoginDialog.dismiss();
        // set the edit text for the user name on the main layout
        etUserName.setText(user);
    }

    /**
     * Erase the text fields when user logs out
     */
    public void clearEditTexts()
    {
        etUserName.setText("");
        etSex.setText("");
        etExercises.setText("");
        etDisabilities.setText("");
        etWorkoutLocations.setText("");
    }
} // MainActivity class
