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
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import android.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends FragmentActivity implements LoginDialogFragment.LoginDialogListener
{
    private static final String TAG = MainActivity.class.getSimpleName();

    private ActionBar mActionBar;
    private TabsPagerAdapter mTabsAdapter;
    private ViewPager mViewPager;

    private LoginDialogFragment mLoginDialog;
    private JawboneUpHelper mJboneHelper;
    private String mCurrentProfileId;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // adding invisible worker fragments: https://developer.android.com/guide/components/fragments.html
        mJboneHelper = new JawboneUpHelper();
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().add(mJboneHelper, JawboneUpHelper.TAG).commit();
        mCurrentProfileId = "0"; // zero means no current profile set
        mLoginDialog = new LoginDialogFragment();
        mLoginDialog.show(getFragmentManager(), TAG);

        setupActionTabs();
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
//            case R.id.action_workouts:
//                Intent intent = new Intent(getApplicationContext(), MeetupsFragment.class);
//                intent.putExtra(DBAdapter.PROFILE_ID, mCurrentProfileId);
//                startActivity(intent);
//                return true;
//            case R.id.action_friends:
//                startActivity(new Intent(getApplicationContext(), FriendsFragment.class));
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
                // @TODO : move view when tab is selected
                mActionBar.setSelectedNavigationItem(tab.getPosition());
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
                        .setIcon(R.drawable.ic_heartware_main_transparent)
                        .setTabListener(tabListener));
        mActionBar.addTab(
                mActionBar.newTab()
                        .setText(getString(R.string.friends_tab))
                        .setIcon(R.drawable.ic_action_friends)
                        .setTabListener(tabListener));
        mActionBar.addTab(
                mActionBar.newTab()
                        .setText(getString(R.string.meetups_tab))
                        .setTabListener(tabListener));
    }

    /**
     * This dialog pops up when the app starts and the user syncs with Jawbone UP
     * @param dialog
     */
    @Override
    public void onDialogPositiveClick(DialogFragment dialog)
    {
        // @TODO : figure out persistent storage
        mJboneHelper.sendToken();
    }

    private class SwipedListener implements ViewPager.OnPageChangeListener
    {
        @Override
        public void onPageSelected(int position) {
            // on changing the page
            // make respected tab selected
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
} // MainActivity class
