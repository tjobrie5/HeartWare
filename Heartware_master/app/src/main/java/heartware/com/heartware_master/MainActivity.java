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
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import android.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.appevents.AppEventsLogger;
import com.jawbone.upplatformsdk.api.ApiManager;
import com.jawbone.upplatformsdk.utils.UpPlatformSdkConstants;

import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends FragmentActivity implements LoginDialogFragment.LoginDialogListener,
        ProfileDialogFragment.ProfileDialogListener, MeetupDialogFragment.MeetupDialogListener
{
    private static final String TAG = MainActivity.class.getSimpleName();

    private ActionBar mActionBar;
    private TabsPagerAdapter mTabsAdapter;

    private ViewPager mViewPager;
    private DBAdapter mDBAdapter;
    private LoginDialogFragment mLoginDialog;
    private JawboneUpHelper mJboneHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDBAdapter = new DBAdapter(this);

        // adding invisible worker fragments: https://developer.android.com/guide/components/fragments.html
        mJboneHelper = new JawboneUpHelper();
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().add(mJboneHelper, JawboneUpHelper.TAG).commit();
        mLoginDialog = new LoginDialogFragment();
        mLoginDialog.show(getFragmentManager(), TAG);

        setupActionTabs();
    } // onCreate

    @Override
    public void onResume()
    {
        super.onResume();

        // Call the 'activateApp' method to log an app event for use in analytics and advertising
        // reporting.  Do so in the onResume methods of the primary Activities that an app may be
        // launched into.
        AppEventsLogger.activateApp(this);
    }

    @Override
    public void onPause()
    {
        super.onPause();

        // Call the 'deactivateApp' method to log an app event for use in analytics and advertising
        // reporting.  Do so in the onPause methods of the primary Activities that an app may be
        // launched into.
        AppEventsLogger.deactivateApp(this);
    }

    private void setupActionTabs()
    {
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mActionBar = getActionBar();
        mTabsAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mTabsAdapter);
        mViewPager.setOnPageChangeListener(new SwipedListener());
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        mActionBar.setHomeButtonEnabled(false);
        ActionBar.TabListener tabListener = new ActionBar.TabListener()
        {
            @Override
            public void onTabSelected(ActionBar.Tab tab, android.app.FragmentTransaction ft)
            {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, android.app.FragmentTransaction ft)
            {

            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, android.app.FragmentTransaction ft)
            {

            }
        };

        // Now that listeners are in place we can safely add the tabs
        mActionBar.addTab(
                mActionBar.newTab()
                        .setText(getString(R.string.profile_tab))
                        .setIcon(R.drawable.ic_arrow)
                        .setTabListener(tabListener));
        mActionBar.addTab(
                mActionBar.newTab()
                        .setText(getString(R.string.friends_tab))
                        .setIcon(R.drawable.ic_action_friends)
                        .setTabListener(tabListener));
        mActionBar.addTab(
                mActionBar.newTab()
                        .setText(getString(R.string.meetups_tab))
                        .setIcon(R.drawable.ic_action_workout)

                        .setTabListener(tabListener));

    }

    private class SwipedListener implements ViewPager.OnPageChangeListener
    {
        @Override
        public void onPageSelected(int position) {
            // when changing the page make make the respected tab selected
            mActionBar.setSelectedNavigationItem(position);

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // not used
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
            // not used
        }
    }

    /**
     * This dialog pops up when the app starts and the user syncs with Jawbone UP
     * @param dialog
     */
    @Override
    public void onDialogPositiveClick(DialogFragment dialog)
    {
        mJboneHelper.sendToken();
    }

    /**
     * Update user information using the ProfileDialogFragment
     * @param dialog
     * @param user
     * @param disability
     */
    @Override
    public void onProfilePositiveClick(DialogFragment dialog, final String user,
                                       final String skill, final String disability)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String token = preferences.getString(UpPlatformSdkConstants.UP_PLATFORM_REFRESH_TOKEN, "NULL");
        HashMap<String, String> profile = mDBAdapter.getProfileByUserAndToken(user, token);
        if(profile.size() == 0) { // user does not exist in SQL storage
            HashMap<String, String> newProfile = new HashMap<>();
            newProfile.put(DBAdapter.USERNAME, user);
            newProfile.put(DBAdapter.PASSWORD, token);
            newProfile.put(DBAdapter.DIFFICULTY, skill);
            newProfile.put(DBAdapter.DISABILITY, disability);
            mDBAdapter.createProfile(newProfile);
//            // this is sloppy, but once the profile is created a new profileId is made and we need to keep track of it
//            String currentId = mDBAdapter.getProfileByUserAndToken(user, token).get(DBAdapter.PROFILE_ID);
//            HeartwareApplication app = (HeartwareApplication) getApplication();
//            app.setCurrentProfileId(currentId);
            Toast.makeText(this, "Creating " + user, Toast.LENGTH_SHORT).show();
        }
        else { // user is already in database and now we should update their info
            HashMap<String, String> updateProfile = new HashMap<>();
            HeartwareApplication app = (HeartwareApplication) getApplication();
            updateProfile.put(DBAdapter.PROFILE_ID, app.getCurrentProfileId());
            updateProfile.put(DBAdapter.USERNAME, user);
            updateProfile.put(DBAdapter.PASSWORD, token);
            updateProfile.put(DBAdapter.DIFFICULTY, skill);
            updateProfile.put(DBAdapter.DISABILITY, disability);
            mDBAdapter.updateProfile(updateProfile);
            Toast.makeText(this, "Updating " + user, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Using the ProfileDialogFragment
     * Don't do anything
     * @param dialog
     */
    @Override
    public void onProfileNegativeClick(DialogFragment dialog)
    {

    }

    /**
     * Confirmed a new meetup should be created or updated
     * @param dialog
     * @param note
     * @param exercise
     * @param location
     * @param date
     * @param people
     */
    @Override
    public void onMeetupPositiveClick(DialogFragment dialog, String note, String exercise, String location, String date, String people)
    {
        HashMap<String, String> newMeetup = new HashMap<>();
        HeartwareApplication app = (HeartwareApplication) getApplication();
        newMeetup.put(DBAdapter.USER_ID, app.getCurrentProfileId());
        newMeetup.put(DBAdapter.NOTE, note);
        newMeetup.put(DBAdapter.EXERCISE, exercise);
        newMeetup.put(DBAdapter.LOCATION, location);
        newMeetup.put(DBAdapter.DATE, date);
        newMeetup.put(DBAdapter.PEOPLE, people);
        mDBAdapter.createMeetup(newMeetup);
        Toast.makeText(this, "Creating meetup with note: " + note, Toast.LENGTH_SHORT).show();
    }

    /**
     * Meetup will not be created or changed, so do nothing
     * @param dialog
     */
    @Override
    public void onMeetupNegativeClick(DialogFragment dialog)
    {

    }



} // MainActivity class
