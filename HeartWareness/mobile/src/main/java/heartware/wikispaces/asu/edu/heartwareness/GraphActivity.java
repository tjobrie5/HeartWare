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

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class GraphActivity extends Activity
{
    private static final String TAG = "GraphActivity";
    private DBAdapter dbAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.graph_view);

        dbAdapter = new DBAdapter(this);

        // @TODO gather data from the database and compute
        GraphView graph = (GraphView) findViewById(R.id.graph);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });
        graph.addSeries(series);

        Log.d(TAG, " == onCreate() == ");
    }

} // DataView class

