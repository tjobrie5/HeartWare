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
// Description: Handles Facebook API calls and social networking functionality.
//  Example: http://javatechig.com/android/using-facebook-sdk-in-android-example
///////////////////////////////////////////////////////////////////////////////////////////

package heartware.com.heartware_master;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AppEventsLogger;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.ProfilePictureView;

import java.util.Arrays;
import java.util.List;

public class FriendsActivity extends Activity
{
    private static final String TAG = FriendsActivity.class.getSimpleName();
    private TextView tvUsername;
    private Button bPostImage;
    private Button bUpdateStatus;
    // Facebook stuff
    private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
    private static String TEST_MESSAGE = "Sample status posted from android app";
    private LoginButton bAuthButton;
    private GraphUser mUser;
    private ProfilePictureView mProfilePictureView;
    private UiLifecycleHelper mUIHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        mProfilePictureView = (ProfilePictureView) findViewById(R.id.vProfilePicture);

        mUIHelper = new UiLifecycleHelper(this, statusCallback);
        mUIHelper.onCreate(savedInstanceState);

        tvUsername = (TextView) findViewById(R.id.tvUserName);
        bAuthButton = (LoginButton) findViewById(R.id.bAuthButton);
        bAuthButton.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {
            @Override
            public void onUserInfoFetched(GraphUser graphUser) {
                if(graphUser != null) {
                    tvUsername.setText(graphUser.getName());
                    mUser = graphUser;
                    updateUI();
                }
                else {
                    tvUsername.setText("You are not logged into Facebook");
                    mUser = null;
                    updateUI();
                }
            }
        });

        bPostImage = (Button) findViewById(R.id.bPostImage);
        bPostImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                postImage();
            }
        });

        bUpdateStatus = (Button) findViewById(R.id.bUpdateStatus);
        bUpdateStatus.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                postStatusMessage();
            }
        });

        buttonsEnabled(false);
    } // onCreate

    /**
     * Callback is invoked after user logs in and logs out.
     */
    private Session.StatusCallback statusCallback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state,
                         Exception exception) {
            if (state.isOpened()) {
                buttonsEnabled(true);
                Log.d(TAG, "Facebook session opened");
            } else if (state.isClosed()) {
                buttonsEnabled(false);
                Log.d(TAG, "Facebook session closed");
            }
        }
    };

    /**
     * Uploaded an image to the user's facebook page
     */
    public void postImage()
    {
        if(checkPermissions()) {
            Bitmap img = BitmapFactory.decodeResource(getResources(),
                    R.mipmap.ic_launcher);
            Request uploadRequest = Request.newUploadPhotoRequest(
                    Session.getActiveSession(), img, new Request.Callback() {
                        @Override
                        public void onCompleted(Response response) {
                            Toast.makeText(FriendsActivity.this,
                                    "Photo uploaded successfully",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
            uploadRequest.executeAsync();
        }
        else {
            requestPermissions();
        }
    }

    /**
     * Post an update status to the user's Facebook page.
     */
    public void postStatusMessage()
    {
        if(checkPermissions()) {
            Request request = Request.newStatusUpdateRequest(
                    Session.getActiveSession(), TEST_MESSAGE,
                    new Request.Callback() {
                        @Override
                        public void onCompleted(Response response) {
                            if(response.getError() == null)
                                Toast.makeText(FriendsActivity.this,
                                        "Status updated successfully",
                                        Toast.LENGTH_LONG).show();
                        }
                    });
            request.executeAsync();
        }
        else {
            requestPermissions();
        }
    }

    public boolean checkPermissions()
    {
        Session s = Session.getActiveSession();
        if(s != null)
            return s.getPermissions().contains("publish_actions");
        else
            return false;
    }

    public void requestPermissions()
    {
        Session s = Session.getActiveSession();
        if(s != null)
            s.requestNewPublishPermissions(new Session.NewPermissionsRequest(this, PERMISSIONS));
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        mUIHelper.onActivityResult(requestCode, resultCode, data);
    }

    public void buttonsEnabled(boolean isEnabled)
    {
        bPostImage.setEnabled(isEnabled);
        bUpdateStatus.setEnabled(isEnabled);
    }

    // UI updates after user logs in
    private void updateUI()
    {
        Session session = Session.getActiveSession();
        boolean enableButtons = (session != null && session.isOpened());

        if(enableButtons && mUser != null) {
            mProfilePictureView.setProfileId(mUser.getId());
            buttonsEnabled(true);
        }
        else {
            mProfilePictureView.setProfileId(null);
            buttonsEnabled(false);
        }
    }
} // FriendsActivity class
