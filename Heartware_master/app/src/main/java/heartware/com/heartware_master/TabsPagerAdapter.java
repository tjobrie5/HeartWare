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
// Description:
///////////////////////////////////////////////////////////////////////////////////////////

package heartware.com.heartware_master;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsPagerAdapter extends FragmentPagerAdapter
{
    private static final int TAB_COUNT = 3;

    public TabsPagerAdapter(android.support.v4.app.FragmentManager fm)
    {
        super(fm);
    }

    /**
     * Consider caching instead of always instantiating;
     * typical use case will require all three
     */
    @Override
    public Fragment getItem(int index)
    {
        System.out.println("MainActivity.TabsPagerAdapter.getItem(): " + index);
        switch (index) {
            case 0:
                // Leftmost tab (and default) is the Profile (graph, user info, etc... )
                return new ProfileFragment();
            case 1:
                return new FriendsFragment();
            case 2:
                return new MeetupsFragment();
        }
        return null;
    }

    @Override
    public int getCount()
    {
        return TAB_COUNT;	// we always have 3 tabs.
    }
} // TabsPagerAdapter class
