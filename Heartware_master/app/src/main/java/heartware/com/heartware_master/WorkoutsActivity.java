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
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class WorkoutsActivity extends ListActivity
{
    private static final String TAG = WorkoutsActivity.class.getSimpleName();
    private Button bNewWorkout;
    private DBAdapter dbAdapter;
    private String mCurrentProfileId;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workouts);
        dbAdapter = new DBAdapter(this);
        // get the current profile Id from the activity that started this one
        mCurrentProfileId = getIntent().getStringExtra(DBAdapter.PROFILE_ID);

        ListView list = (ListView) findViewById(android.R.id.list);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // send a map of data over to the view workout
                Intent intent = new Intent(getApplication(), GraphsActivity.class);
                //intent.putExtra(DBAdapter.EXERCISE, theExercise);
                startActivity(intent);
                Log.d(TAG, "in onItemSelected listener");
            }
        });

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // @TODO : delete a workout
                Log.d(TAG, "in the onItemLongClick listener");
                return false;
            }
        });

        List<String> graph_List = new ArrayList<String>();

        graph_List.add("steps");
        graph_List.add("calories");
        graph_List.add("placeholder");
        graph_List.add("placeholder");
        graph_List.add("placeholder");
        graph_List.add("placeholder");
        graph_List.add("placeholder");
        graph_List.add("placeholder");
        graph_List.add("placeholder");
        graph_List.add("placeholder");
        graph_List.add("placeholder");
        graph_List.add("placeholder");
        graph_List.add("placeholder");
        graph_List.add("placeholder");
        graph_List.add("placeholder");
        graph_List.add("placeholder");
        graph_List.add("placeholder");
        graph_List.add("placeholder");
        graph_List.add("placeholder");
        graph_List.add("placeholder");
        graph_List.add("placeholder");
        graph_List.add("placeholder");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                graph_List
        );

        setListAdapter(arrayAdapter);

        bNewWorkout = (Button) findViewById(R.id.bNewWorkout);
        bNewWorkout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // @TODO : create a new workout
                startActivity(new Intent(getApplication(), ViewWorkout.class));
                Log.d(TAG, "in the Add Button onClick");
            }
        });
    }
} // GoalsActivity class