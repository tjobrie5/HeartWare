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
// Description: Handles Facebook API calls.
///////////////////////////////////////////////////////////////////////////////////////////

package heartware.com.heartware_master;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AppEventsLogger;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.ProfilePictureView;

// @TODO : create a listview of friends and meetups
public class FriendsActivity extends Activity
{
    private static final String TAG = FriendsActivity.class.getSimpleName();
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
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        mUIHelper = new UiLifecycleHelper(this, callback);
        mUIHelper.onCreate(savedInstanceState);

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
} // FriendsActivity class
