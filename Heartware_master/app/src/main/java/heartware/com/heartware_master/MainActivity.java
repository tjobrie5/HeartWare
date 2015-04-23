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

import android.app.ActionBar;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.Toast;

import com.facebook.appevents.AppEventsLogger;
import com.jawbone.upplatformsdk.utils.UpPlatformSdkConstants;

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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

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

    private boolean notification = false;
    private NotificationManager notificationManager;
    private int noteID = 100;
    private String avail;

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

        //get user calendar availability
        avail = checkEvents();
        Toast.makeText(this, "Available: " + avail, Toast.LENGTH_LONG).show();

        //send user availability to server
        //CalToServer calToServer = (CalToServer) new CalToServer().execute(new String(avail));

    } // onCreate

    @Override
    public void onResume()
    {
        super.onResume();

        if(!notification)
        {
            //prepare intent that is triggered when notification is selected
            Intent intent = new Intent(this, MeetupsFragment.class);
            PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

            //build notification
            Notification n = new Notification.Builder(this).setContentTitle("Heartware").setContentText("Reminder: You have a Meetup today!").setContentIntent(pIntent).setSmallIcon(R.mipmap.ic_launcher).build();

            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            notificationManager.notify(noteID,n);


            //HeartwareApplication app = (HeartwareApplication) this.getApplication();
            //final ArrayList<HashMap<String, String>> meetups = mDBAdapter.getAllMeetups(app.getCurrentProfileId());

        }

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
            SendProfileData sp = (SendProfileData) new SendProfileData().execute(user, newProfile.get(DBAdapter.PASSWORD), skill, disability);
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
            SendProfileData sp = (SendProfileData) new SendProfileData().execute(user, updateProfile.get(DBAdapter.PASSWORD), skill, disability);
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

    public String checkEvents(){
        //boolean variables for morning, afternoon, and evening
        boolean mB = true, aB = true, eB = true;

        //string for result of calendar query available time
        String result;

        String[] INSTANCE_PROJECTION = new String[]{
                CalendarContract.Events.CALENDAR_ID,
                CalendarContract.Events.TITLE,
                CalendarContract.Events.DESCRIPTION,
                CalendarContract.Events.DTSTART,
                CalendarContract.Events.DTEND,
                CalendarContract.Events.ALL_DAY,
                CalendarContract.Events.EVENT_LOCATION
        };

        //get current time and date
        Calendar c = Calendar.getInstance();
        int second = c.get(Calendar.SECOND);
        int minute = c.get(Calendar.MINUTE);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);

        long startMillis, endMillis;

        //variables for if time of day is being evaluated
        boolean morningE = false, afternoonE = false, eveningE = false;

        //only evaluate times if they are in the future
        if(hour < 12) {
            morningE = true;
            afternoonE = true;
            eveningE = true;
        }
        else if ( hour >= 12 && hour < 17)
        {
            morningE = false;
            afternoonE = true;
            eveningE = true;
        }
        else
        {
            morningE = false;
            afternoonE = false;
            eveningE = true;
        }

        //check if times of day are free and set their flags to true or false
        if(morningE)
        {
            c.set(year, month, day, hour, minute, second);
            startMillis = c.getTimeInMillis();

            //set end time to noon
            Calendar mC = Calendar.getInstance();
            mC.set(year, month, day, 11, 59, 59);
            endMillis = mC.getTimeInMillis();

            String selection = "(( " + CalendarContract.Events.DTSTART + " >= " + startMillis + ") AND ( " + CalendarContract.Events.DTSTART + " <= " + endMillis + " ))";
            Cursor cursor = this.getBaseContext().getContentResolver().query( CalendarContract.Events.CONTENT_URI, INSTANCE_PROJECTION, selection, null, null );

            if(cursor.moveToFirst()) {
                mB = false;
            }
            else
                mB = true;
        }

        if(afternoonE)
        {
            //set begin time to noon
            c.set(year, month, day, 12, 0, 0);
            startMillis = c.getTimeInMillis();

            //set end time to 5pm
            Calendar aC = Calendar.getInstance();
            aC.set(year, month, day, 16, 59, 59);
            endMillis = aC.getTimeInMillis();

            String selection = "(( " + CalendarContract.Events.DTSTART + " >= " + startMillis + ") AND ( " + CalendarContract.Events.DTSTART + " <= " + endMillis + " ))";
            Cursor cursor = this.getBaseContext().getContentResolver().query( CalendarContract.Events.CONTENT_URI, INSTANCE_PROJECTION, selection, null, null );

            if(cursor.moveToFirst()) {
                aB = false;
            }
            else
                aB = true;
        }

        if(eveningE)
        {
            //set begin time to 5pm
            c.set(year, month, day, 17, 0, 0);
            startMillis = c.getTimeInMillis();

            //set end time to 11pm
            Calendar eC = Calendar.getInstance();
            eC.set(year, month, day, 22, 59, 59);
            endMillis = eC.getTimeInMillis();

            String selection = "(( " + CalendarContract.Events.DTSTART + " >= " + startMillis + ") AND ( " + CalendarContract.Events.DTSTART + " <= " + endMillis + " ))";
            Cursor cursor = this.getBaseContext().getContentResolver().query( CalendarContract.Events.CONTENT_URI, INSTANCE_PROJECTION, selection, null, null );

            if(cursor.moveToFirst()) {
                eB = false;
            }
            else
                eB = true;
        }

        //return either morning, afternoon, evening, or none for time free on calendar for the day
        if(morningE && mB)
            result = "morning";
        else if(afternoonE && aB)
            result = "afternoon";
        else if(eveningE && eB)
            result = "evening";
        else
            result = "none";

        return result;
    }

    private class CalToServer extends AsyncTask<String, Void, String>
    {
        private static final String URL = "http://qqroute.com:8080/sendCal";
        @Override
        protected String doInBackground(String... params)
        {
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(URL);
                List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
                nameValuePair.add(new BasicNameValuePair("cal", avail));
                try {
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
                }
                catch(UnsupportedEncodingException ex) {
                    Log.d(TAG, ex.getMessage().toString());
                }

                try {
                    HttpResponse res = httpClient.execute(httpPost);
                    Log.d(TAG, "Http Post Response: " + res.toString());
                }
                catch(ClientProtocolException ex) {
                    Log.d(TAG, ex.getMessage().toString());
                }
                catch(IOException ex) {
                    Log.d(TAG, ex.getMessage().toString());
                }
            }
            catch(Exception e) {
                Log.d(TAG, e.getMessage().toString());
                return e.getMessage().toString();
            }

            return "doInBackground() -- TokenToServer";
        }
    } // CalToServer class

    private class SendProfileData extends AsyncTask<String, Void, String>
    {
        private static final String URL = "http://qqroute.com:8080/profileData";
        @Override
        protected String doInBackground(String... params)
        {
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(URL);
                List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);

                nameValuePair.add(new BasicNameValuePair("user", params[0]));
                nameValuePair.add(new BasicNameValuePair("password", params[1]));
                nameValuePair.add(new BasicNameValuePair("difficulty", params[2]));
                nameValuePair.add(new BasicNameValuePair("disability", params[3]));
                try {
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
                }
                catch(UnsupportedEncodingException ex) {
                    Log.d("", ex.getMessage().toString());
                }

                try {
                    HttpResponse res = httpClient.execute(httpPost);
                    Log.d("", "Http Post Response: " + res.toString());
                }
                catch(ClientProtocolException ex) {
                    Log.d("", ex.getMessage().toString());
                }
                catch(IOException ex) {
                    Log.d("", ex.getMessage().toString());
                }
            }
            catch(Exception e) {
                Log.d("", e.getMessage().toString());
                return e.getMessage().toString();
            }

            return "doInBackground() -- TokenToServer";
        }
    } // SendProfileData class

} // MainActivity class
