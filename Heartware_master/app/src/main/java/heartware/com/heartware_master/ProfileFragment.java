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
// Description: Represents the a user and common profile information they can modify.
//  Additionally, the user can recommendations from the server regarding their Jawbone UP
//  data. There is a graph view that allows the user to see their data.
///////////////////////////////////////////////////////////////////////////////////////////

package heartware.com.heartware_master;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jawbone.upplatformsdk.utils.UpPlatformSdkConstants;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

public class ProfileFragment extends Fragment
{
    private static final String TAG = ProfileFragment.class.getSimpleName();
    private Button bRecommend;
    private Button bUserInfo;
    private GraphView mGraph;
    private ProfileDialogFragment mProfileDialog;
    private DBAdapter dbAdapter;
    private JSONObject mUserData;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        mProfileDialog = new ProfileDialogFragment();

        dbAdapter = new DBAdapter(getActivity());

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String token = preferences.getString(UpPlatformSdkConstants.UP_PLATFORM_ACCESS_TOKEN, "NULL");

        JawboneAPI_Caller api_caller = (JawboneAPI_Caller) new JawboneAPI_Caller().execute(
                new String(token)
        );

        createButtons(rootView);
        createGraph(rootView);

        return rootView;
    }

    private void createButtons(View view)
    {
        bRecommend = (Button) view.findViewById(R.id.bRecommend);
        bRecommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LongRunningGetIO().execute();
            }
        });
        bUserInfo = (Button) view.findViewById(R.id.bUserInfo);
        bUserInfo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                HashMap<String, String> profileMap = dbAdapter.getProfileById("1");

                if(profileMap.size() != 0) {
                    mProfileDialog.setProfileText(profileMap.get(DBAdapter.USERNAME), profileMap.get(DBAdapter.DIFFICULTY), profileMap.get(DBAdapter.DISABILITY));
                }
                mProfileDialog.show(getActivity().getFragmentManager(), TAG);
            }
        });
    }

    private void createGraph(View view)
    {
        // Build the graph from user's data
        mGraph = (GraphView) view.findViewById(R.id.userDataGraph);
        setGraphSettings();

//        mGraph.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // @TODO : pop up a DIALOG with comprehensive data view
//                Log.d(TAG, "The Graph got clicked");
//            }
//        });
    }

    private void setGraphSettings()
    {
        mGraph.setTitleColor(Color.WHITE);
        mGraph.getGridLabelRenderer().setGridColor(Color.GRAY);
        mGraph.getGridLabelRenderer().setHorizontalAxisTitle("Day");
        mGraph.getGridLabelRenderer().setHorizontalAxisTitleColor(Color.LTGRAY);
        mGraph.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
        mGraph.getGridLabelRenderer().setVerticalAxisTitle("Steps");
        mGraph.getGridLabelRenderer().setVerticalAxisTitleColor(Color.LTGRAY);
        mGraph.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        triggerAnimation();
    }

    /**
     * start the animation on the graph
     */
    private void triggerAnimation()
    {
        // set up animation
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.animated_view);
        animation.setRepeatMode(Animation.REVERSE);
        animation.setRepeatCount(Animation.INFINITE);
        mGraph.startAnimation(animation);
    }

    private class LongRunningGetIO extends AsyncTask<Void, Void, String>
    {
        String body = "BODY_PLACEHOLDER";

        @Override
        protected String doInBackground(Void... params) {
            HttpClient client= new DefaultHttpClient();
            HttpGet httpGet = new HttpGet("http://qqroute.com:8080/getWorkout");
            ResponseHandler<String> handler = new BasicResponseHandler();
            Log.d(TAG," inside do in background");
            try {
                HttpResponse response = client.execute(httpGet);
                body = handler.handleResponse(response);
                Log.d(TAG, body + " got to try");
            }
            catch(IOException ex) {
                Log.d(TAG, ex.getMessage().toString());
            }
            return body;
        }

        @Override
        protected void onPostExecute(String results){
            Toast.makeText(getActivity(), body, Toast.LENGTH_LONG).show();
        }
    } // LongRunningGetIO class

    private class JawboneAPI_Caller extends AsyncTask<String, Void, String>
    {
        private static final String URL = "https://jawbone.com/nudge/api/v.1.1/users/@me/moves";
        private static final String HeaderName = "Authorization";
        String data;
        JSONObject dataObj;
        private LineGraphSeries<DataPoint> series = new LineGraphSeries<>(); // graph data points

        @Override
        protected String doInBackground(String... params) {
            HttpClient client= new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(URL);
            httpGet.addHeader(HeaderName, "Bearer " + params[0]);
            ResponseHandler<String> handler = new BasicResponseHandler();
            Log.d(TAG," JawboneAPI_Caller -- inside do in background");
            try {
                HttpResponse response = client.execute(httpGet);
                data = handler.handleResponse(response);
                dataObj = new JSONObject(data);
                Log.d(TAG, "data: " + data);
            }
            catch(IOException ex) {
                Log.d(TAG, ex.getMessage().toString());
            }
            catch(JSONException excp) {
                Log.d(TAG, excp.getMessage().toString());
            }

            if(dataObj != null) {
                // now parse the dataObj
                try {
                    String step = "";
                    JSONObject jsonData = dataObj.getJSONObject("data");
                    JSONArray jsonItems = jsonData.getJSONArray("items");
                    for (int i = 0; i != jsonItems.length(); ++i) {
                        JSONObject item = jsonItems.getJSONObject(i);
                        JSONObject detail = item.getJSONObject("details");
                        JSONObject hourly_totals = detail.getJSONObject("hourly_totals");
                        Iterator<String> iter = hourly_totals.keys();
                        if (iter.hasNext()) {
                            String key = iter.next(); // the date that starts with 2015
                            JSONObject date_data = hourly_totals.getJSONObject(key);
                            step = date_data.getString("steps");
                        }

                        series.appendData(new DataPoint(i, Double.parseDouble(step)), false, 1000);
                    }
                } catch (JSONException ex) {
                    Log.d(TAG, ex.getMessage().toString());
                }
            }


            return data;
        }

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            // initialize the graph data points
            if(series != null) {
                mGraph.setTitle("Steps per day");
                mGraph.removeAllSeries();
                //setGraphSettings();
                series.setColor(Color.RED);
                mGraph.addSeries(series);
            }
        }
    } // JawboneAPI_Caller class

} // ProfileFragment class
