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
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.d(TAG, "in onItemSelected listener");
                    tvNote = (TextView) view.findViewById(R.id.tvNote);
                    HashMap<String, String> meetup = dbAdapter.getMeetupInfo(tvNote.getText().toString(), app.getCurrentProfileId());
                    mMeetupDialog.setMeetupText(meetup.get(DBAdapter.EXERCISE), meetup.get(DBAdapter.LOCATION),
                            meetup.get(DBAdapter.PEOPLE), meetup.get(DBAdapter.NOTE), meetup.get(DBAdapter.DATE));

                    mMeetupDialog.show(getActivity().getFragmentManager(), TAG);
                }
            });

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
                    Toast.makeText(getActivity(), "Deleting " + noteName, Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }

        // @TODO : add animation on heartware imageview

        return rootView;
    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState)
//    {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.fragment_meetups);
//        dbAdapter = new DBAdapter(this);
//        // get the current profile Id from the activity that started this one
//        mCurrentProfileId = getIntent().getStringExtra(DBAdapter.PROFILE_ID);
//
//        mListView = (ListView) findViewById(android.R.id.list);
//
//        final ArrayList<HashMap<String, String>> meetups = dbAdapter.getAllmeetups(mCurrentProfileId);
//        mMeetupArray = new ArrayList<>(meetups.size());
//
//        setMeetupArray(meetups);
//
//        mArrayAdapter = new ArrayAdapter(this, R.layout.meetups_entry, R.id.tvNote, mMeetupArray);
//
//        setListAdapter(mArrayAdapter);
//
//        if(meetups.size() != 0) {
//            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
//            {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    Log.d(TAG, "in onItemSelected listener");
//                    tvNote = (TextView) view.findViewById(R.id.tvNote);
//                    String exerciseName = tvNote.getText().toString();
//                    // send a map of data over to the view workout
//                    Intent intent = new Intent(getApplication(), ViewWorkout.class);
//                    intent.putExtra(DBAdapter.PROFILE_ID, mCurrentProfileId);
//                    intent.putExtra(DBAdapter.EXERCISE, exerciseName);
//                    startActivityForResult(intent, 0);
//                }
//            });
//
//            mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
//            {
//                @Override
//                public boolean onItemLongClick(AdapterView<?> parent, final View view, int position, long id) {
//                    Log.d(TAG, "in the onItemLongClick listener");
//                    tvNote = (TextView) view.findViewById(R.id.tvNote);
//                    final String exName = tvNote.getText().toString();
//                    dbAdapter.deleteWorkout(exName);
//                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                        view.animate().setDuration(2000).alpha(0)
//                                .withEndAction(new Runnable()
//                                {
//                                    @Override
//                                    public void run()
//                                    {
//                                        mMeetupArray.remove(exName);
//                                        mArrayAdapter.notifyDataSetChanged();
//                                        view.setAlpha(1);
//                                    }
//                                });
//                    }
//                    else {
//                        mMeetupArray.remove(exName);
//                        mArrayAdapter.notifyDataSetChanged();
//                    }
//                    Toast.makeText(getApplicationContext(), "Deleting " + exName, Toast.LENGTH_SHORT).show();
//                    return true;
//                }
//            });
//        }
//
//        bNewWorkout = (Button) findViewById(R.id.bNewWorkout);
//        bNewWorkout.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                Intent intent = new Intent(getApplication(), ViewWorkout.class);
//                intent.putExtra(DBAdapter.PROFILE_ID, mCurrentProfileId);
//                startActivityForResult(intent, 0);
//                Log.d(TAG, "Creating a New Workout.");
//            }
//        });
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data)
//    {
//        super.onActivityResult(requestCode, resultCode, data);
//        // there should always be a new workout, if there isn't the user just hit back
//        if(resultCode == RESULT_OK) {
//            final String oldWorkout = data.getStringExtra(ViewWorkout.OLD_WORKOUT);
//            final String newWorkout = data.getStringExtra(ViewWorkout.NEW_WORKOUT);
//            if(oldWorkout == null) { // new workout
//                mMeetupArray.add(newWorkout);
//                mArrayAdapter.notifyDataSetChanged();
//            }
//            else { // updating current workout
//                for(int i = 0; i < mMeetupArray.size(); ++i) {
//                    if(mMeetupArray.get(i).equals(oldWorkout)) {
//                        mMeetupArray.set(i, newWorkout);
//                        mArrayAdapter.notifyDataSetChanged();
//                    }
//                }
//            }
//        }
//        // else, do nothing the user just hit back
//    }

    private void setMeetupArray(ArrayList<HashMap<String, String>> listMap)
    {
        int i = 0;
        for(HashMap<String, String> map : listMap) {
            mMeetupArray.add(i++, map.get(DBAdapter.NOTE));
        }
    }
} // GoalsActivity class