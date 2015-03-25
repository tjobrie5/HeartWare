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
// Description: The view for either a new workout to be created, or
//  an existing workout that's been launched from the WorkoutsActivity list.
///////////////////////////////////////////////////////////////////////////////////////////

package heartware.com.heartware_master;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class ViewWorkout extends Activity
{
    private Button bConfirm;
    private EditText etExercise;
    private EditText etGoal;
    private RadioGroup rgDifficulty;
    private EditText etExemptions;
    private EditText etData;
    private EditText etPlace, etTime;
    private DBAdapter dbAdapter;
    private TextView tvViewLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_workout);
        dbAdapter = new DBAdapter(this);

        bConfirm = (Button) findViewById(R.id.bConfirm);
        etExercise = (EditText) findViewById(R.id.etExercise);
        etGoal = (EditText) findViewById(R.id.etGoal);
        rgDifficulty = (RadioGroup) findViewById(R.id.rgDifficulty);
        etExemptions = (EditText) findViewById(R.id.etExemptions);
        etData = (EditText) findViewById(R.id.etData);
        etPlace = (EditText) findViewById(R.id.etPlace);
        etTime = (EditText) findViewById(R.id.etTime);
        tvViewLabel = (TextView) findViewById(R.id.tvViewLabel);

        final String theCurrentProfileId = getIntent().getStringExtra(DBAdapter.PROFILE_ID);
        final String theCurrentExercise = getIntent().getStringExtra(DBAdapter.EXERCISE);

        // load workout info based on the exercise
        if(theCurrentExercise != null) {
            tvViewLabel.setText("Workout View");
            HashMap<String, String> workout = dbAdapter.getWorkoutInfo(theCurrentExercise, theCurrentProfileId);
            etExercise.setText(workout.get(DBAdapter.EXERCISE));
            etGoal.setText(workout.get(DBAdapter.GOAL));
            etExemptions.setText(workout.get(DBAdapter.EXEMPTIONS));
            etData.setText(workout.get(DBAdapter.DATA));
            etPlace.setText(workout.get(DBAdapter.PLACE));
            etTime.setText(workout.get(DBAdapter.TIME));
            final String difficulty = workout.get(DBAdapter.DIFFICULTY);
            // @TODO : radio buttons aren't being set properly
            if(difficulty.equals("easy")) {
                rgDifficulty.check(R.id.rbEasy);
            }
            else if(difficulty.equals("medium")) {
                rgDifficulty.check(R.id.rbMedium);
            }
            else {
                rgDifficulty.check(R.id.rbHard);
            }
        }
        else {
            tvViewLabel.setText("Create a new Workout");
        }


        bConfirm.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final int radioId = rgDifficulty.getCheckedRadioButtonId();
                String difficulty = "";
                switch(radioId) {
                    case 1: difficulty = "easy";
                        break;
                    case 2: difficulty = "medium";
                        break;
                    case 3: difficulty = "hard";
                        break;
                }
                HashMap<String, String> queryValues = new HashMap<String, String>();
                queryValues.put(DBAdapter.USER_ID, theCurrentProfileId);
                queryValues.put(DBAdapter.EXERCISE, etExercise.getText().toString());
                queryValues.put(DBAdapter.GOAL, etGoal.getText().toString());
                queryValues.put(DBAdapter.DIFFICULTY, difficulty);
                queryValues.put(DBAdapter.EXEMPTIONS, etExemptions.getText().toString());
                queryValues.put(DBAdapter.DATA, etData.getText().toString());
                queryValues.put(DBAdapter.PLACE, etPlace.getText().toString());
                queryValues.put(DBAdapter.TIME, etPlace.getText().toString());
                if(theCurrentExercise != null) {
                    // update workout
                    dbAdapter.updateWorkout(theCurrentExercise, theCurrentProfileId, queryValues);
                    Toast.makeText(getApplicationContext(), "Updating " + theCurrentExercise, Toast.LENGTH_SHORT).show();
                }
                else {
                    // create a new workout
                    dbAdapter.createWorkout(queryValues);
                    Toast.makeText(getApplicationContext(), "Created new workout " + etExercise.getText().toString(),
                            Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.putExtra(DBAdapter.EXERCISE, etExercise.getText().toString());
                    setResult(RESULT_OK, intent);
                }
                //startActivity(new Intent(getApplication(), WorkoutsActivity.class));
                finish(); // return to WorkoutsActivity
            }
        });
    }
} // ViewWorkout class
