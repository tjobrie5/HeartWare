// Copyright (c) HeartWare Group Fall 2014 - Spring 2015
// @purpose ASU Capstone Project
// @app HeartWare smart health monitoring application
// @authors Mark Aleheimer, Ryan Case, Tyler O'Brien, Amy Mazzola, Zach Mertens, New Guy
// @version 1.0
//
// @class

package heartware.wikispaces.asu.edu.heartwareness;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class DataView extends Activity {

    static final String TAG = "DataView";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_view);
        Log.d(TAG, " == onCreate() == ");
    }

} // DataView class

