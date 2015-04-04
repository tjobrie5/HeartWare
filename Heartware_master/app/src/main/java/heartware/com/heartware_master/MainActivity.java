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
// Description: Main entry point for the application, handles user login using Jawbone UP.
//  If the user doesn't log into the app then it means they didn't log into Jawbone UP.
///////////////////////////////////////////////////////////////////////////////////////////

package heartware.com.heartware_master;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jawbone.upplatformsdk.utils.UpPlatformSdkConstants;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class MainActivity extends ActionBarActivity implements LoginDialogFragment.LoginDialogListener
{
    private static final String TAG = MainActivity.class.getSimpleName();

    private EditText etUserName;
    private EditText etSex;
    private Button bAuthButton;
    private Button bUpdate;
    private Button bRecommend;
    private GraphView mGraph;

    private DBAdapter dbAdapter;
    private LoginDialogFragment mLoginDialog;
    private JawboneUpHelper mJboneHelper;
    private String mCurrentProfileId;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // adding invisible worker fragments:
        //  https://developer.android.com/guide/components/fragments.html
        mJboneHelper = new JawboneUpHelper();
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().add(mJboneHelper, JawboneUpHelper.TAG).commit();

        dbAdapter = new DBAdapter(this);
        createLoginDialog();

        bAuthButton = (Button) findViewById(R.id.bAuthButton);
        bAuthButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                clearEditTexts();
                mCurrentProfileId = "0";
                if(bAuthButton.getText().toString().equals(R.string.login))
                    bAuthButton.setText(R.string.logout);
                else
                    bAuthButton.setText(R.string.login);
                mLoginDialog.show(getFragmentManager(), TAG);
            }
        });
        bUpdate = (Button) findViewById(R.id.bUpdate);
        bUpdate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                HashMap<String, String> queryValues = new HashMap<>();
                queryValues.put(DBAdapter.PROFILE_ID, mCurrentProfileId);
                queryValues.put(DBAdapter.USERNAME, etUserName.getText().toString());
                queryValues.put(DBAdapter.SEX, etSex.getText().toString());
                dbAdapter.updateProfile(queryValues);
                Toast.makeText(v.getContext(), "Updating " + etUserName.getText().toString(),
                        Toast.LENGTH_SHORT).show();
            }
        });
        bRecommend = (Button) findViewById(R.id.bRecommend);
        bRecommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LongRunningGetIO().execute();
            }
        });

        etUserName = (EditText) findViewById(R.id.etUserName);
        etSex = (EditText) findViewById(R.id.etSex);

        // Build the graph from user's data
        mGraph = (GraphView) findViewById(R.id.userDataGraph);
        mGraph.setTitle("Your Workouts");
        //mGraph.setTitleTextSize(14.0f);
        mGraph.setTitleColor(Color.YELLOW);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });
        series.setColor(Color.RED);
        mGraph.getGridLabelRenderer().setGridColor(Color.WHITE);
        mGraph.getGridLabelRenderer().setHorizontalAxisTitle("Time");
        mGraph.getGridLabelRenderer().setHorizontalAxisTitleColor(Color.YELLOW);
        mGraph.getGridLabelRenderer().setHorizontalLabelsColor(Color.YELLOW);
        mGraph.getGridLabelRenderer().setVerticalAxisTitle("Space");
        mGraph.getGridLabelRenderer().setVerticalAxisTitleColor(Color.YELLOW);
        mGraph.getGridLabelRenderer().setVerticalLabelsColor(Color.YELLOW);
        mGraph.addSeries(series);
        mGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // @TODO : send user to a DIALOG comprehensive data view
                Log.d(TAG, "The Graph got clicked");
            }
        });
    } // onCreate

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId()) {
            case R.id.action_workouts:
                Intent intent = new Intent(getApplicationContext(), WorkoutsActivity.class);
                intent.putExtra(DBAdapter.PROFILE_ID, mCurrentProfileId);
                startActivity(intent);
                return true;
            case R.id.action_friends:
                startActivity(new Intent(getApplicationContext(), FriendsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * create and show the login dialog
     */
    public void createLoginDialog()
    {
        // create and show the login pop up as soon as the main activity is created
        mLoginDialog = new LoginDialogFragment();
        mLoginDialog.show(getFragmentManager(), TAG);
    }

    /**
     * returning user
     * @param dialog
     */
    @Override
    public void onDialogPositiveClick(DialogFragment dialog, final String user, final String pw)
    {
//        Log.d(TAG, " onDialogPositiveClick " + user + " " + pw);
//        HashMap<String, String> profile = dbAdapter.getProfileByUserAndPass(user, pw);
//        if(profile.size() == 0) {
//            // no profile exist, force the user to enter again
//            Log.d(TAG, user + " does not exist");
//            // not sure the best way to keep the dialog open, but this way works
//            mLoginDialog.dismiss();
//            mLoginDialog.show(getFragmentManager(), TAG);
//            bAuthButton.setText(R.string.login);
//        }
//        else {
//            etUserName.setText(profile.get(DBAdapter.USERNAME));
//            etSex.setText(profile.get(DBAdapter.SEX));
//            mCurrentProfileId = profile.get(DBAdapter.PROFILE_ID);
//            mLoginDialog.dismiss();
//            mJboneHelper.sendToken(); // send the token
//            bAuthButton.setText(R.string.logout);
//        }

        // @NOTICE, this has to be called within the dialog (or anywhere outside onCreate)
        mJboneHelper.sendToken(); // synchronize Android with Jawbone UP
       // now that we're synced with Jawbone UP, set up this user's profile on SQLite in Android


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String username = preferences.getString(UpPlatformSdkConstants.UP_PLATFORM_ACCESS_TOKEN, "NULL");
        String password = preferences.getString(UpPlatformSdkConstants.UP_PLATFORM_REFRESH_TOKEN, "NULL");
        if(!username.equals("NULL") && !password.equals("NULL")) {
            HashMap<String, String> profile = dbAdapter.getProfileByUserAndPass(username, password);
            if(profile.size() == 0) { // new user
                HashMap<String, String> newProfile = new HashMap<>();
                newProfile.put(DBAdapter.USERNAME, username);
                newProfile.put(DBAdapter.PASSWORD, password);
                dbAdapter.createProfile(newProfile);
                // this is sloppy, but once the profile is created a new profileId is made and we need to keep track of it
                mCurrentProfileId = dbAdapter.getProfileByUserAndPass(username, password).get(DBAdapter.PROFILE_ID);
            }
            else { // returning user
                etUserName.setText(profile.get(DBAdapter.USERNAME));
                etSex.setText(profile.get(DBAdapter.SEX));
                mCurrentProfileId = profile.get(DBAdapter.PROFILE_ID);
            }
        }
        else {
            throw new RuntimeException(TAG + " User did not log into Jawbone UP");
        }


    }

    /**
     * new user
     * @param dialog
     */
    @Override
    public void onDialogNegativeClick(DialogFragment dialog, final String user, final String pw)
    {
        Log.d(TAG, " onDialogNegativeClick " + user + " " + pw);
        Log.d(TAG, "creating " + user);
        // insert into database
        HashMap<String, String> newProfile = new HashMap<>();
        newProfile.put(DBAdapter.USERNAME, user);
        newProfile.put(DBAdapter.PASSWORD, pw);
        dbAdapter.createProfile(newProfile);
        // this is sloppy, but once the profile is created a new profileId is made and we need it
        mCurrentProfileId = dbAdapter.getProfileByUserAndPass(user, pw).get(DBAdapter.PROFILE_ID);
        mLoginDialog.dismiss();
        // set the edit text for the user name on the main layout
        etUserName.setText(user);
        mJboneHelper.sendToken();
        bAuthButton.setText(R.string.logout);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        triggerAnimation();
    }

    /**
     * Erase the text fields when user logs out
     */
    private void clearEditTexts()
    {
        etUserName.setText("");
        etSex.setText("");
    }

    /**
     * start the animation on the graph
     */
    private void triggerAnimation()
    {
        // set up animation
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.animated_view);
        animation.setRepeatMode(Animation.REVERSE);
        animation.setRepeatCount(Animation.INFINITE);
        mGraph.startAnimation(animation);
    }

    private class LongRunningGetIO extends AsyncTask<Void, Void, String> {
        String body = "nada";
        protected String getASCIIContentFromEntity(HttpEntity entity) throws IllegalStateException, IOException {
            InputStream in = entity.getContent();


            StringBuffer out = new StringBuffer();
            int n = 1;
            while (n>0) {
                byte[] b = new byte[4096];
                n =  in.read(b);


                if (n>0) out.append(new String(b, 0, n));
            }


            System.out.println("First string: "+out.toString());
            return out.toString();
        }


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
                //Toast.makeText(getApplicationContext(), body, Toast.LENGTH_LONG).show();
                //Toast.makeText(getApplicationContext(),
                        //"You haven't take that many steps today. Why don't you " + body + "?", Toast.LENGTH_LONG)
                        //.show();
            }
            catch(IOException ex) {
                Log.d(TAG, ex.getMessage().toString());
            }
            return "ayooo";
        }

        @Override
        protected void onPostExecute(String results){
            Toast.makeText(getApplicationContext(), "You have not been very active... " + body, Toast.LENGTH_LONG).show();
        }
    } // LongRunningGetIO class
} // MainActivity class
