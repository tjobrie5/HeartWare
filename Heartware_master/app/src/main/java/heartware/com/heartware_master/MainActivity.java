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
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
// @TODO : use M/F sex radio buttons
public class MainActivity extends ActionBarActivity implements View.OnClickListener,
        LoginDialogFragment.LoginDialogListener
{
    private static final String TAG = MainActivity.class.getSimpleName();
    private Button b_friends;
    private Button b_graphs;
    private Button bUpdate;
    private Button bSync;
    private EditText etUserName;
    private EditText etSex;
    private EditText etExercises;
    private EditText etDisabilities;
    private EditText etWorkoutLocations;
    private DBAdapter dbAdapter;
    private LoginDialogFragment mLoginDialog;
    private static int ProfileCounter = 1; // count the number of profiles on this device

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showLoginDialog();

        dbAdapter = new DBAdapter(this);

        b_graphs = (Button) findViewById(R.id.bGoals);
        b_graphs.setOnClickListener(this);

        b_friends = (Button) findViewById(R.id.bFriends);
        b_friends.setOnClickListener(this);

        bSync = (Button) findViewById(R.id.bSync);
        bSync.setOnClickListener(this);

        bUpdate = (Button) findViewById(R.id.bUpdate);
        bUpdate.setOnClickListener(this);

        etUserName = (EditText) findViewById(R.id.etUserName);
        etSex = (EditText) findViewById(R.id.etSex);
        etExercises = (EditText) findViewById(R.id.etExercises);
        etDisabilities = (EditText) findViewById(R.id.etDisabilities);
        etWorkoutLocations = (EditText) findViewById(R.id.etWorkoutLocations);

        ArrayList<HashMap<String, String>> profiles = dbAdapter.getAllProfiles();
        if(profiles.size() == 0) {
            // no data in the SQLite on this Android device
        }
        else {
            bUpdate.setText("Update");
            etUserName.setText(profiles.get(0).get(DBAdapter.USER_NAME));
            etSex.setText(profiles.get(0).get(DBAdapter.SEX));
            etExercises.setText(profiles.get(0).get(DBAdapter.FAV_EXERCISE));
            etDisabilities.setText(profiles.get(0).get(DBAdapter.DISABILITIES));
            etWorkoutLocations.setText(profiles.get(0).get(DBAdapter.WORKOUT_LOC));
        }
    } // onCreate

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.bGoals:
                startActivity(new Intent(getApplicationContext(), GoalsActivity.class));
                break;

            case R.id.bFriends:
                // @TODO : verify that the user is logged into Facebook first (mUser != null)
                startActivity(new Intent(getApplicationContext(), FriendsActivity.class));
                break;

            case R.id.bSync:
                // @TODO : perform some kind of sync between Android and Jawbone UP
                Log.d(TAG, "bSync has been pressed ------");
                Toast.makeText(this, "Syncing Data from Jawbone UP", Toast.LENGTH_LONG).show();
                break;

            case R.id.bUpdate:
                Log.d(TAG, "updating " + etUserName.getText().toString());
                if(bUpdate.getText().toString().equals("Create")) {
                    bUpdate.setText("Update");
                    // insert into database
                    HashMap<String, String> queryValues = new HashMap<>();
                    queryValues.put(DBAdapter.USER_NAME, etUserName.getText().toString());
                    queryValues.put(DBAdapter.SEX, etSex.getText().toString());
                    queryValues.put(DBAdapter.FAV_EXERCISE, etExercises.getText().toString());
                    queryValues.put(DBAdapter.DISABILITIES, etDisabilities.getText().toString());
                    queryValues.put(DBAdapter.WORKOUT_LOC, etWorkoutLocations.getText().toString());
                    dbAdapter.createProfile(queryValues);
                }
                else {
                    // perform update to database
                    HashMap<String, String> queryValues = new HashMap<>();
                    // Profile_ID is hardcoded - only 1 profile
                    queryValues.put(DBAdapter.PROFILE_ID, new String(String.valueOf(ProfileCounter)));
                    queryValues.put(DBAdapter.USER_NAME, etUserName.getText().toString());
                    queryValues.put(DBAdapter.SEX, etSex.getText().toString());
                    queryValues.put(DBAdapter.FAV_EXERCISE, etExercises.getText().toString());
                    queryValues.put(DBAdapter.DISABILITIES, etDisabilities.getText().toString());
                    queryValues.put(DBAdapter.WORKOUT_LOC, etWorkoutLocations.getText().toString());
                    dbAdapter.updateProfile(queryValues);
                }
                break;
            default:
                break;
        }
    }

    public void showLoginDialog()
    {
        // create and show the login pop up as soon as the main activity is created
        mLoginDialog = new LoginDialogFragment();
        mLoginDialog.show(getFragmentManager(), TAG);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog)
    {
        Log.d(TAG, " onDialogPositiveClick");
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog)
    {
        Log.d(TAG, " onDialogNegativeClick");
    }
} // MainActivity class
