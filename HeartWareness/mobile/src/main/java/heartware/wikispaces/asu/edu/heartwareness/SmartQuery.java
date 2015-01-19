// Copyright (c) HeartWare Group Fall 2014 - Spring 2015
// @purpose ASU Capstone Project
// @app HeartWare smart health monitoring application
// @authors Mark Aleheimer, Ryan Case, Tyler O'Brien, Amy Mazzola, Zach Mertens, New Guy
// @version 1.0
//
// @class

package heartware.wikispaces.asu.edu.heartwareness;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class SmartQuery extends Activity {

    static final String TAG = "SmartQuery";
    TextView queryText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.query_view);

        Intent qIntent = getIntent();
        String qText = qIntent.getStringExtra("query");

        queryText = (TextView) findViewById(R.id.queryText1);

        //queryText.setText(qText);

        Log.d(TAG, " == onCreate() == ");
    }
} // SmartQuery class