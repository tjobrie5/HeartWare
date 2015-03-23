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
// Description: Encapsulates all the data surrounding a profile
///////////////////////////////////////////////////////////////////////////////////////////

package heartware.com.heartware_master;

import java.util.ArrayList;

public class Profile
{
    private int mProfileId;
    private String mUsername;
    private String mPassword;
    private String mSex;
    private ArrayList<Workout> mWorkouts;

    public Profile(String mUsername, String mPassword)
    {
        this.mUsername = mUsername;
        this.mPassword = mPassword;
        mWorkouts = new ArrayList<>();
    }

    public Profile(String mUsername, String mPassword, String sex)
    {
        this.mUsername = mUsername;
        this.mPassword = mPassword;
        this.mSex = sex;
        mWorkouts = new ArrayList<>();
    }

    public String getSex()
    {
        return mSex;
    }

    public void setSex(String mSex)
    {
        this.mSex = mSex;
    }

    public int getProfileId()
    {
        return mProfileId;
    }

    public void setProfileId(int mProfileId)
    {
        this.mProfileId = mProfileId;
    }

    public String getUsername()
    {
        return mUsername;
    }

    public void setUsername(String mUsername)
    {
        this.mUsername = mUsername;
    }

    public String getPassword()
    {
        return mPassword;
    }

    public void setPassword(String mPassword)
    {
        this.mPassword = mPassword;
    }

    private class Workout
    {
        private int mUserId;
        private String mExercise;
        private String mGoal;
        private String mDifficulty;
        private String mExemptions;
        private int mData;
        private String mPlace;
        private String mTime;
    }
} // Profile class
