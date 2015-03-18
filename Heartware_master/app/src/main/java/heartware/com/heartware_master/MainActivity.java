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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.facebook.AppEventsLogger;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.ProfilePictureView;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends Activity implements View.OnClickListener
{
    private static final String TAG = MainActivity.class.getSimpleName();
    private Button b_friends;
    private Button b_graphs;
    private Button b_home;
    private Button bUpdate;
    private EditText etUserName;
    private EditText etSex;
    private EditText etExercises;
    private EditText etDisabilities;
    private EditText etWorkoutLocations;
    private DBAdapter dbAdapter;
    private LoginDialogFragment mLoginDialog;
    // Facebook stuff
    private LoginButton bAuthButton;
    private GraphUser mUser;
    private ProfilePictureView mProfilePictureView;
    private enum PendingAction { NONE, POST_PHOTO, POST_STATUS_UPDATE };
    private UiLifecycleHelper mUIHelper;
    private PendingAction mPendingAction = PendingAction.NONE;

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // create and show the login pop up as soon as the main activity is created
        mLoginDialog = new LoginDialogFragment();
        mLoginDialog.show(getFragmentManager(), TAG);

        mUIHelper = new UiLifecycleHelper(this, callback);
        mUIHelper.onCreate(savedInstanceState);


        dbAdapter = new DBAdapter(this);

        bAuthButton = (LoginButton) findViewById(R.id.bAuthButton);
        bAuthButton.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {
            @Override
            public void onUserInfoFetched(GraphUser graphUser) {
                Log.d(TAG, "in onUserInfoFetched - bAuthButton");
                // save the user
                mUser = graphUser;
                updateUI();
                handlePendingAction();
            }
        });

        mProfilePictureView = (ProfilePictureView) findViewById(R.id.vProfilePicture);

        b_graphs = (Button) findViewById(R.id.bGoals);
        b_graphs.setOnClickListener(this);

        b_friends = (Button) findViewById(R.id.bFriends);
        b_friends.setOnClickListener(this);

        b_home = (Button) findViewById(R.id.bHome);
        b_home.setOnClickListener(this);

        bUpdate = (Button) findViewById(R.id.bUpdate);
        bUpdate.setOnClickListener(this);

        etUserName = (EditText) findViewById(R.id.etUserName);
        etSex = (EditText) findViewById(R.id.etSex);
        etExercises = (EditText) findViewById(R.id.etExercises);
        etDisabilities = (EditText) findViewById(R.id.etDisabilities);
        etWorkoutLocations = (EditText) findViewById(R.id.etWorkoutLocations);

        ArrayList<HashMap<String, String>> profiles = dbAdapter.getAllProfiles();
        if(profiles.size() == 0) {
            // @NOTE : there is no data in the SQLite on this Android device
        }
        else {
            bUpdate.setText("Update");
            etUserName.setText(profiles.get(0).get(DBAdapter.USER_NAME));
            etSex.setText(profiles.get(0).get(DBAdapter.SEX));
            etExercises.setText(profiles.get(0).get(DBAdapter.FAV_EXERCISE));
            etDisabilities.setText(profiles.get(0).get(DBAdapter.DISABILITIES));
            etWorkoutLocations.setText(profiles.get(0).get(DBAdapter.WORKOUT_LOC));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.bGoals:
                startActivity(new Intent(getApplicationContext(), GoalsActivity.class));
                break;

            case R.id.bFriends:
                // @TODO : verify that the user is logged into Facebook first (mUser != null)
                startActivity(new Intent(getApplicationContext(), FriendsActivity.class));
                break;

            case R.id.bHome:
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                break;

            case R.id.bUpdate:
                Log.d(TAG, "updating " + etUserName.getText().toString());
                if(bUpdate.getText().toString().equals("Create")) {
                    bUpdate.setText("Update");
                    // insert into database
                    HashMap<String, String> queryValues = new HashMap<>();
                    queryValues.put(DBAdapter.USER_NAME, etUserName.getText().toString());
                    queryValues.put(DBAdapter.SEX, etSex.getText().toString());
                    queryValues.put(DBAdapter.FAV_EXERCISE, etExercises.getText().toString());
                    queryValues.put(DBAdapter.DISABILITIES, etDisabilities.getText().toString());
                    queryValues.put(DBAdapter.WORKOUT_LOC, etWorkoutLocations.getText().toString());
                    dbAdapter.createProfile(queryValues);
                }
                else {
                    // perform update to database
                    HashMap<String, String> queryValues = new HashMap<>();
                    // Profile_ID is hardcoded - only 1 profile
                    queryValues.put(DBAdapter.PROFILE_ID, new String("1"));
                    queryValues.put(DBAdapter.USER_NAME, etUserName.getText().toString());
                    queryValues.put(DBAdapter.SEX, etSex.getText().toString());
                    queryValues.put(DBAdapter.FAV_EXERCISE, etExercises.getText().toString());
                    queryValues.put(DBAdapter.DISABILITIES, etDisabilities.getText().toString());
                    queryValues.put(DBAdapter.WORKOUT_LOC, etWorkoutLocations.getText().toString());
                    dbAdapter.updateProfile(queryValues);
                }
                break;
            default:
                break;
        }

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mUIHelper.onResume();
        // Logs 'install' and 'app activate' App Events
        AppEventsLogger.activateApp(this);
        updateUI();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        mUIHelper.onPause();
        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mUIHelper.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        mUIHelper.onSaveInstanceState(outState);

//        outState.putString(PENDING_ACTION_BUNDLE_KEY, pendingAction.name());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
//        uiHelper.onActivityResult(requestCode, resultCode, data, dialogCallback);
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (mPendingAction != PendingAction.NONE &&
                (exception instanceof FacebookOperationCanceledException ||
                        exception instanceof FacebookAuthorizationException)) {
//            new AlertDialog.Builder(HelloFacebookSampleActivity.this)
//                    .setTitle(R.string.cancelled)
//                    .setMessage(R.string.permission_not_granted)
//                    .setPositiveButton(R.string.ok, null)
//                    .show();
            mPendingAction = PendingAction.NONE;
        } else if (state == SessionState.OPENED_TOKEN_UPDATED) {
            handlePendingAction();
        }
        updateUI();
    }

    // UI updates after user logins in
    private void updateUI()
    {
        Session session = Session.getActiveSession();
        boolean enableButtons = (session != null && session.isOpened());

        if(enableButtons && mUser != null) {
            mProfilePictureView.setProfileId(mUser.getId());
        }
        else {
            mProfilePictureView.setProfileId(null);
        }
    }

    @SuppressWarnings("incomplete-switch")
    private void handlePendingAction() {
        PendingAction previouslyPendingAction = mPendingAction;
        // These actions may re-set pendingAction if they are still pending, but we assume they
        // will succeed.
        mPendingAction = PendingAction.NONE;

        switch (previouslyPendingAction) {
            case POST_PHOTO:
//                postPhoto();
                break;
            case POST_STATUS_UPDATE:
//                postStatusUpdate();
                break;
        }
    }

} // MainActivity class
