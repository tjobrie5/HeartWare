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
// Description: Selecting a workout lets you edit the details surrounding the workout.
//  A profile (user) can have multiple workouts which are contained in a listview.
///////////////////////////////////////////////////////////////////////////////////////////

package heartware.com.heartware_master;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class WorkoutsActivity extends ListActivity
{
    private static final String TAG = WorkoutsActivity.class.getSimpleName();
    private ListView mListView;
    private Button bNewWorkout;
    private DBAdapter dbAdapter;
    private String mCurrentProfileId;
    private TextView tvExercise;
    private ArrayAdapter mArrayAdapter;
    private ArrayList<String> mWorkoutArray;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workouts);
        dbAdapter = new DBAdapter(this);
        // get the current profile Id from the activity that started this one
        mCurrentProfileId = getIntent().getStringExtra(DBAdapter.PROFILE_ID);

        mListView = (ListView) findViewById(android.R.id.list);

        final ArrayList<HashMap<String, String>> workouts = dbAdapter.getAllWorkouts(mCurrentProfileId);
        mWorkoutArray = new ArrayList<>(workouts.size());

        setWorkoutArray(workouts);

        mArrayAdapter = new ArrayAdapter(this, R.layout.workout_entry, R.id.tvExercise, mWorkoutArray);

        setListAdapter(mArrayAdapter);

        if(workouts.size() != 0) {
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.d(TAG, "in onItemSelected listener");
                    tvExercise = (TextView) view.findViewById(R.id.tvExercise);
                    String exerciseName = tvExercise.getText().toString();
                    // send a map of data over to the view workout
                    Intent intent = new Intent(getApplication(), ViewWorkout.class);
                    intent.putExtra(DBAdapter.PROFILE_ID, mCurrentProfileId);
                    intent.putExtra(DBAdapter.EXERCISE, exerciseName);
                    startActivityForResult(intent, 0);
                }
            });

            mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
            {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, final View view, int position, long id) {
                    Log.d(TAG, "in the onItemLongClick listener");
                    tvExercise = (TextView) view.findViewById(R.id.tvExercise);
                    final String exName = tvExercise.getText().toString();
                    dbAdapter.deleteWorkout(exName);
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        view.animate().setDuration(2000).alpha(0)
                                .withEndAction(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        mWorkoutArray.remove(exName);
                                        mArrayAdapter.notifyDataSetChanged();
                                        view.setAlpha(1);
                                    }
                                });
                    }
                    else {
                        mWorkoutArray.remove(exName);
                        mArrayAdapter.notifyDataSetChanged();
                    }
                    Toast.makeText(getApplicationContext(), "Deleting " + exName, Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }

        bNewWorkout = (Button) findViewById(R.id.bNewWorkout);
        bNewWorkout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getApplication(), ViewWorkout.class);
                intent.putExtra(DBAdapter.PROFILE_ID, mCurrentProfileId);
                startActivityForResult(intent, 0);
                Log.d(TAG, "Creating a New Workout.");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // there should always be a new workout, if there isn't the user just hit back
        if(resultCode == RESULT_OK) {
            final String oldWorkout = data.getStringExtra(ViewWorkout.OLD_WORKOUT);
            final String newWorkout = data.getStringExtra(ViewWorkout.NEW_WORKOUT);
            if(oldWorkout == null) { // new workout
                mWorkoutArray.add(newWorkout);
                mArrayAdapter.notifyDataSetChanged();
            }
            else { // updating current workout
                for(int i = 0; i < mWorkoutArray.size(); ++i) {
                    if(mWorkoutArray.get(i).equals(oldWorkout)) {
                        mWorkoutArray.set(i, newWorkout);
                        mArrayAdapter.notifyDataSetChanged();
                    }
                }
            }
        }
        // else, do nothing the user just hit back
    }

    private void setWorkoutArray(ArrayList<HashMap<String, String>> listMap)
    {
        int i = 0;
        for(HashMap<String, String> map : listMap) {
            mWorkoutArray.add(i++, map.get(DBAdapter.EXERCISE));
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }
} // GoalsActivity class