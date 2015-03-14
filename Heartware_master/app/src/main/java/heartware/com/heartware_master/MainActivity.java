///////////////////////////////////////////////////////////////////////////////////////////
// Copyright (c) HeartWare Group Fall 2014 - Spring 2015
// @license
// @purpose ASU Capstone Project
// @app HeartWare smart health monitoring application
// @authors Mark Aleheimer, Ryan Case, Tyler O'Brien, Amy Mazzola, Zach Mertens, Sri Somanchi
// @mailto zmertens@asu.edu
// @version 1.0
//
// Description: Inherits SQL database. Handles "CRUD" operations.
///////////////////////////////////////////////////////////////////////////////////////////

package heartware.com.heartware_master;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.facebook.AppEventsLogger;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends Activity implements View.OnClickListener
{
    private static final String TAG = MainActivity.class.getSimpleName();
    private Button b_friends;
    private Button b_graphs;
    private Button b_home;
    private Button bUpdate;
    private LoginDialogFragment mLoginDialog;
    private EditText etUserName;
    private EditText etSex;
    private EditText etExercises;
    private EditText etDisabilities;
    private EditText etWorkoutLocations;
    private DBAdapter dbAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbAdapter = new DBAdapter(this);

        b_graphs = (Button) findViewById(R.id.bGoals);
        b_graphs.setOnClickListener(this);

        b_friends = (Button) findViewById(R.id.bFriends);
        b_friends.setOnClickListener(this);

        b_home = (Button) findViewById(R.id.bHome);
        b_home.setOnClickListener(this);

        bUpdate = (Button) findViewById(R.id.bUpdate);
        bUpdate.setOnClickListener(this);

        etUserName = (EditText) findViewById(R.id.etUserName);
        etSex = (EditText) findViewById(R.id.etSex);
        etExercises = (EditText) findViewById(R.id.etExercises);
        etDisabilities = (EditText) findViewById(R.id.etDisabilities);
        etWorkoutLocations = (EditText) findViewById(R.id.etWorkoutLocations);

        mLoginDialog = new LoginDialogFragment();

        ArrayList<HashMap<String, String>> profiles = dbAdapter.getAllProfiles();
        if(profiles.size() == 0) {
            mLoginDialog.show(getFragmentManager(), TAG); // @TODO : change min API ?
            // @TODO : verify if user is using facebook data or manual data
        }
        else {
            bUpdate.setText("Update");
            etUserName.setText(profiles.get(0).get(DBAdapter.USER_NAME));
            etSex.setText(profiles.get(0).get(DBAdapter.SEX));
            etExercises.setText(profiles.get(0).get(DBAdapter.FAV_EXERCISE));
            etDisabilities.setText(profiles.get(0).get(DBAdapter.DISABILITIES));
            etWorkoutLocations.setText(profiles.get(0).get(DBAdapter.WORKOUT_LOC));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.bGoals:
                startActivity(new Intent(getApplicationContext(), GoalsActivity.class));
                break;

            case R.id.bFriends:
                startActivity(new Intent(getApplicationContext(), FriendsActivity.class));
                break;

            case R.id.bHome:
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
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

                }
                break;

            default:
                break;
        }

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        // Logs 'install' and 'app activate' App Events
//        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        // Logs 'app deactivate' App Event.
//        AppEventsLogger.deactivateApp(this);
    }
} // MainActivity class
