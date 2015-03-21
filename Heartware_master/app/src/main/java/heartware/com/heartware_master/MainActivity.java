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
// Description: Main entry point for the application, handles user login
///////////////////////////////////////////////////////////////////////////////////////////

package heartware.com.heartware_master;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jawbone.upplatformsdk.api.ApiManager;
import com.jawbone.upplatformsdk.utils.UpPlatformSdkConstants;
import com.jawbone.upplatformsdk.api.response.OauthAccessTokenResponse;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends ActionBarActivity implements View.OnClickListener,
        LoginDialogFragment.LoginDialogListener
{
    private static final String TAG = MainActivity.class.getSimpleName();
    // Jawbone stuff
    private static final String CLIENT_ID = "7cXqsS_BjH8";
    private static final String CLIENT_SECRET = "eba2d19923c18c57393b289653302ff633817012";

    private Button b_friends;
    private Button b_graphs;
    private Button bUpdate;
    private Button bSync;
    private Button bLogout;
    private EditText etUserName;
    private EditText etSex;
    private EditText etExercises;
    private EditText etDisabilities;
    private EditText etWorkoutLocations;

    private DBAdapter dbAdapter;
    private LoginDialogFragment mLoginDialog;
    private String mCurrentProfileId;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createLoginDialog();

        dbAdapter = new DBAdapter(this);

        b_graphs = (Button) findViewById(R.id.bGoals);
        b_graphs.setOnClickListener(this);

        b_friends = (Button) findViewById(R.id.bFriends);
        b_friends.setOnClickListener(this);

        bSync = (Button) findViewById(R.id.bSync);
        bSync.setOnClickListener(this);

        bUpdate = (Button) findViewById(R.id.bUpdate);
        bUpdate.setOnClickListener(this);

        bLogout = (Button) findViewById(R.id.bLogout);
        bLogout.setOnClickListener(this);

        etUserName = (EditText) findViewById(R.id.etUserName);
        etSex = (EditText) findViewById(R.id.etSex);
        etExercises = (EditText) findViewById(R.id.etExercises);
        etDisabilities = (EditText) findViewById(R.id.etDisabilities);
        etWorkoutLocations = (EditText) findViewById(R.id.etWorkoutLocations);
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
            case R.id.action_search: return true;
            case R.id.action_goals: return true;
            case R.id.action_friends: return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // @TODO : this is the start of Mark's Jawbone Up API changes, but they don't work correctly
//    /**
//     * handles Jawbone authentication
//     * @param requestCode
//     * @param resultCode
//     * @param data
//     */
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data)
//    {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == UpPlatformSdkConstants.JAWBONE_AUTHORIZE_REQUEST_CODE
//            && resultCode == RESULT_OK) {
//                String code = data.getStringExtra(UpPlatformSdkConstants.ACCESS_CODE);
//                if (code != null) {
//                     //first clear older accessToken, if it exists..
//                     ApiManager.getRequestInterceptor().clearAccessToken();
//                     ApiManager.getRestApiInterface().getAccessToken(
//                             CLIENT_ID,
//                             CLIENT_SECRET,
//                             code,
//                             accessTokenRequestListener);
//            }
//         }
//    }
//
//    private Callback accessTokenRequestListener = new Callback<OauthAccessTokenResponse>()
//    {
//        @Override
//        public void success(OauthAccessTokenResponse result, Response response)
//        {
//            if (result.access_token != null) {
////                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(HelloUpActivity.this);
////                SharedPreferences.Editor editor = preferences.edit();
////                editor.putString(UpPlatformSdkConstants.UP_PLATFORM_ACCESS_TOKEN, result.access_token);
////                editor.putString(UpPlatformSdkConstants.UP_PLATFORM_REFRESH_TOKEN, result.refresh_token);
////                editor.commit();
//                // @TODO : clean this up - here's the start of mark's changes
//                TokenToServer example = (TokenToServer) new TokenToServer().execute(
//                        new String(result.access_token));
//                //Posting token to server side
//                //THIS IS THE BEGINNING OF OUR APP. Change Homepage.class to "nameofouractivity.class"
//                //Include name of our activity in android manifest.xml
//                //Intent intent = new Intent(HelloUpActivity.this, Homepage.class);
//                //intent.putExtra(UpPlatformSdkConstants.CLIENT_SECRET, CLIENT_SECRET);
//                //startActivity(intent);
//                Toast.makeText(getApplicationContext(), "Jawbone API working", Toast.LENGTH_SHORT);
//
//                Log.d(TAG, result.access_token + " THIS");
//                Log.d(TAG, "accessToken:" + result.access_token);
//            } else {
//                Log.d(TAG, "accessToken not returned by Oauth call, exiting...");
//            }
//        }
//
//        @Override
//        public void failure(RetrofitError retrofitError)
//        {
//            Log.d(TAG, "failed to get accessToken: " + retrofitError.getMessage());
//        }
//    };
//
//    private class TokenToServer extends AsyncTask<String, Void, String> {
//        private static final String URL = "http://qqroute.com:8080/sendToken";
//        @Override
//        protected String doInBackground(String... params)
//        {
//            try {
//                HttpClient httpClient = new DefaultHttpClient();
//                HttpPost httpPost = new HttpPost(URL);
//                List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
//                nameValuePair.add(new BasicNameValuePair("token", params[0]));
//
//                try {
//                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
//                }
//                catch(UnsupportedEncodingException ex) {
//                    Log.d(TAG, ex.getMessage().toString());
//                }
//
//                try {
//                    HttpResponse res = httpClient.execute(httpPost);
//                    Log.d(TAG, "Http Post Response: " + res.toString());
//                }
//                catch(ClientProtocolException ex) {
//                    Log.d(TAG, ex.getMessage().toString());
//                }
//                catch(IOException ex) {
//                    Log.d(TAG, ex.getMessage().toString());
//                }
//            }
//            catch(Exception e) {
//                Log.d(TAG, e.getMessage().toString());
//                return e.getMessage().toString();
//            }
//
//            return "doInBackground() -- TokenToServer";
//        }
//    }

    /**
     * handle all possible button clicks for this view
     * @param v
     */
    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.bGoals:
                startActivity(new Intent(getApplicationContext(), GoalsActivity.class));
                break;

            case R.id.bFriends:
                startActivity(new Intent(getApplicationContext(), FriendsActivity.class));
                break;

            case R.id.bSync:
                // @TODO : perform some kind of sync between Android and Jawbone UP
                Log.d(TAG, "bSync has been pressed ------");
                Toast.makeText(this, "Syncing Data from Jawbone UP", Toast.LENGTH_LONG).show();
                break;

            case R.id.bUpdate:
                Log.d(TAG, "updating " + etUserName.getText().toString());
                HashMap<String, String> queryValues = new HashMap<>();
                queryValues.put(DBAdapter.PROFILE_ID, mCurrentProfileId);
                queryValues.put(DBAdapter.USER_NAME, etUserName.getText().toString());
                queryValues.put(DBAdapter.SEX, etSex.getText().toString());
                queryValues.put(DBAdapter.FAV_EXERCISE, etExercises.getText().toString());
                queryValues.put(DBAdapter.DISABILITIES, etDisabilities.getText().toString());
                queryValues.put(DBAdapter.WORKOUT_LOC, etWorkoutLocations.getText().toString());
                dbAdapter.updateProfile(queryValues);
                break;

            case R.id.bLogout:
                Log.d(TAG, " logging out");
                clearEditTexts();
                mLoginDialog.show(getFragmentManager(), TAG);
                break;
        }
    } // onClick()

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
        Log.d(TAG, " onDialogPositiveClick " + user + " " + pw);
        HashMap<String, String> profile = dbAdapter.getProfileInfo(user, pw);
        if(profile.size() == 0) {
            // no profile exist, force the user to enter again
            Log.d(TAG, user + " does not exist");
            // not sure the best way to keep the dialog open, but this way works
            mLoginDialog.dismiss();
            mLoginDialog.show(getFragmentManager(), TAG);
        }
        else {
            bUpdate.setText(R.string.update);
            etUserName.setText(profile.get(DBAdapter.USER_NAME));
            etSex.setText(profile.get(DBAdapter.SEX));
            etExercises.setText(profile.get(DBAdapter.FAV_EXERCISE));
            etDisabilities.setText(profile.get(DBAdapter.DISABILITIES));
            etWorkoutLocations.setText(profile.get(DBAdapter.WORKOUT_LOC));
            mCurrentProfileId = profile.get(DBAdapter.PROFILE_ID);
            mLoginDialog.dismiss();
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
        HashMap<String, String> queryValues = new HashMap<>();
        queryValues.put(DBAdapter.USER_NAME, user);
        queryValues.put(DBAdapter.SEX, etSex.getText().toString());
        queryValues.put(DBAdapter.FAV_EXERCISE, etExercises.getText().toString());
        queryValues.put(DBAdapter.DISABILITIES, etDisabilities.getText().toString());
        queryValues.put(DBAdapter.WORKOUT_LOC, etWorkoutLocations.getText().toString());
        dbAdapter.createProfile(queryValues);
        // this is sloppy, but once the profile is created a new profileId is made and we need it
        mCurrentProfileId = dbAdapter.getProfileInfo(user, pw).get(DBAdapter.PROFILE_ID);
        mLoginDialog.dismiss();
        // set the edit text for the user name on the main layout
        etUserName.setText(user);
    }

    public void clearEditTexts()
    {
        etUserName.setText("");
        etSex.setText("");
        etExercises.setText("");
        etDisabilities.setText("");
        etWorkoutLocations.setText("");
    }
} // MainActivity class
