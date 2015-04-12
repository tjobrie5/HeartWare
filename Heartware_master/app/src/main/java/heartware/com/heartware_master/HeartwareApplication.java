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
// Description: Singleton class that handles mostly Facebook operations.
// Resource: Scrumptious example app from Facebook SDK 4.0
///////////////////////////////////////////////////////////////////////////////////////////

package heartware.com.heartware_master;

import android.app.Application;

import com.facebook.FacebookSdk;

import org.json.JSONObject;

import java.util.List;

public class HeartwareApplication extends Application
{
    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());
    }

    private List<JSONObject> selectedUsers;
    private JSONObject selectedPlace;
    private String sCurrentProfileId = "1"; // SQL database user ID

    public List<JSONObject> getSelectedUsers() {
        return selectedUsers;
    }

    public void setSelectedUsers(List<JSONObject> users) {
        selectedUsers = users;
    }

    public JSONObject getSelectedPlace() {
        return selectedPlace;
    }

    public void setSelectedPlace(JSONObject place) {
        this.selectedPlace = place;
    }

    public void setCurrentProfileId(String id) {
        sCurrentProfileId = id;
    }

    public String getCurrentProfileId() {
        return sCurrentProfileId;
    }
} // HeartwareApplication
