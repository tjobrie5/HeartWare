// Copyright (c) HeartWare Group Fall 2014 - Spring 2015
// @purpose ASU Capstone Project
// @app HeartWare smart health monitoring application
// @authors Mark Aleheimer, Ryan Case, Tyler O'Brien, Amy Mazzola, Zach Mertens, New Guy
// @version 1.0
//
// @class An active list view displaying user exercise profiles and graph.

package heartware.wikispaces.asu.edu.heartwareness;

import java.util.ArrayList;
import java.util.HashMap;
import android.os.Bundle;
import android.app.ListActivity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.ListView;

// jjoe64 graph libraries
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class MainActivity extends ListActivity {

    static final String TAG = "MainActivity";
    TextView profileId;
    DBTools dbTools = new DBTools(this);

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ArrayList<HashMap<String, String>> profileList =  dbTools.getAllProfiles();

        if(profileList.size() != 0) {
            ListView listView = (ListView) findViewById(android.R.id.list);
            listView.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    profileId = (TextView) view.findViewById(R.id.profileId);
                    String profileIdValue = profileId.getText().toString();
                    Intent  theIntent = new Intent(getApplication(), EditProfile.class);
                    theIntent.putExtra("profileId", profileIdValue);
                    startActivity(theIntent);
                }
            });

            ListAdapter adapter = new SimpleAdapter( MainActivity.this, profileList, R.layout.profile_entry,
                    new String[] { "profileId", "firstName", "lastName"},
                    new int[] {R.id.profileId, R.id.firstName, R.id.lastName});

            setListAdapter(adapter);
        }

        // @TESTING : testing graph capabilities
//        GraphView graph = (GraphView) findViewById(R.id.graph);
//        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
//                new DataPoint(0, 1),
//                new DataPoint(1, 5),
//                new DataPoint(2, 3),
//                new DataPoint(3, 2),
//                new DataPoint(4, 6)
//        });
//        graph.addSeries(series);

        Log.d(TAG, " == onCreate() == ");
    }

    public void showAddProfile(View view) {
        Intent theIntent = new Intent(getApplication(), NewProfile.class);
        startActivity(theIntent);
    }
} // MainActivity class

