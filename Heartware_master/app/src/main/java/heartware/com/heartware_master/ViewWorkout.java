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
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

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

        final String mCurrentProfileId = getIntent().getStringExtra(DBAdapter.PROFILE_ID);
        final String mExercise = getIntent().getStringExtra(DBAdapter.EXERCISE);

        // load workout info based on the exercise
        if(mExercise != null) {
            tvViewLabel.setText("Workout View");
        }
        else {
            tvViewLabel.setText("Create a new Workout");
        }


        bConfirm.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

            }
        });
    }
} // ViewWorkout class
