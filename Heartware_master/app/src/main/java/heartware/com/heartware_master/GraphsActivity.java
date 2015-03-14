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

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class GraphsActivity extends ActionBarActivity
{
    private static final String TAG = GraphsActivity.class.getSimpleName();
    private GraphView mGraph;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphs);

        mGraph = (GraphView) findViewById(R.id.graph);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });

        mGraph.addSeries(series);
    }
} // GraphActivity class
