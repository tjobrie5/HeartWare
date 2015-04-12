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
// Description: Selecting a workout lets you edit the details surrounding the workout.
//  A profile (user) can have multiple meetups which are contained in a listview.
///////////////////////////////////////////////////////////////////////////////////////////

package heartware.com.heartware_master;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class MeetupsFragment extends android.support.v4.app.ListFragment
{
    private static final String TAG = MeetupsFragment.class.getSimpleName();
    private MeetupDialogFragment mMeetupDialog;
    private ListView mListView;
    private DBAdapter dbAdapter;
    private TextView tvNote;
    private ArrayAdapter mArrayAdapter;
    private ArrayList<String> mMeetupArray;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_meetups, container, false);
        mMeetupDialog = new MeetupDialogFragment();
        dbAdapter = new DBAdapter(getActivity());

        final HeartwareApplication app = (HeartwareApplication) getActivity().getApplication();

        mListView = (ListView) rootView.findViewById(android.R.id.list);

        final ArrayList<HashMap<String, String>> meetups = dbAdapter.getAllMeetups(app.getCurrentProfileId());
        mMeetupArray = new ArrayList<>(meetups.size());

        setMeetupArray(meetups);

        mArrayAdapter = new ArrayAdapter(getActivity(), R.layout.meetups_entry, R.id.tvNote, mMeetupArray);

        setListAdapter(mArrayAdapter);

        if(meetups.size() != 0) {
//            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
//            {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    Log.d(TAG, "in onItemSelected listener");
//                    tvNote = (TextView) view.findViewById(R.id.tvNote);
//                    HashMap<String, String> meetup = dbAdapter.getMeetupInfo(tvNote.getText().toString(), app.getCurrentProfileId());
//                    mMeetupDialog.setMeetupText(meetup.get(DBAdapter.EXERCISE), meetup.get(DBAdapter.LOCATION),
//                            meetup.get(DBAdapter.PEOPLE), meetup.get(DBAdapter.NOTE), meetup.get(DBAdapter.DATE));
//
//                    mMeetupDialog.show(getActivity().getFragmentManager(), TAG);
//                }
//            });

            mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
            {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, final View view, int position, long id) {
                    Log.d(TAG, "in the onItemLongClick listener");
                    tvNote = (TextView) view.findViewById(R.id.tvNote);
                    final String noteName = tvNote.getText().toString();
//                    dbAdapter.deleteWorkout(exName);
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        view.animate().setDuration(2000).alpha(0)
                                .withEndAction(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        mMeetupArray.remove(noteName);
                                        mArrayAdapter.notifyDataSetChanged();
                                        view.setAlpha(1);
                                    }
                                });
                    }
                    else {
                        mMeetupArray.remove(noteName);
                        mArrayAdapter.notifyDataSetChanged();
                    }

                    // delete from SQL database
                    dbAdapter.deleteMeetup(noteName);
                    Toast.makeText(getActivity(), "Deleting " + noteName, Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }

        return rootView;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        super.onListItemClick(l, v, position, id);
        Log.d(TAG, "in onItemSelected listener");
        tvNote = (TextView) v.findViewById(R.id.tvNote);
        HeartwareApplication app = (HeartwareApplication) getActivity().getApplication();
        HashMap<String, String> meetup = dbAdapter.getMeetupInfo(tvNote.getText().toString(), app.getCurrentProfileId());
        if(meetup.size() != 0) {
            mMeetupDialog.setMeetupText(meetup.get(DBAdapter.EXERCISE), meetup.get(DBAdapter.LOCATION),
                    meetup.get(DBAdapter.PEOPLE), meetup.get(DBAdapter.NOTE), meetup.get(DBAdapter.DATE));
            mMeetupDialog.show(getActivity().getFragmentManager(), TAG);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        HeartwareApplication app = (HeartwareApplication) getActivity().getApplication();
        final ArrayList<HashMap<String, String>> meetups = dbAdapter.getAllMeetups(app.getCurrentProfileId());

        mMeetupArray.clear();
        setMeetupArray(meetups);

        mArrayAdapter.notifyDataSetChanged();
    }

    private void setMeetupArray(ArrayList<HashMap<String, String>> listMap)
    {
        int i = 0;
        for(HashMap<String, String> map : listMap) {
            mMeetupArray.add(i++, map.get(DBAdapter.NOTE));
        }
    }
} // GoalsActivity class