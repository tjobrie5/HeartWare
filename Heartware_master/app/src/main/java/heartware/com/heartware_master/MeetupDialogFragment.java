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
// Description: Dialog that pops up with meetup information
///////////////////////////////////////////////////////////////////////////////////////////

package heartware.com.heartware_master;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class MeetupDialogFragment extends DialogFragment
{
    public interface MeetupDialogListener {
        public void onMeetupPositiveClick(DialogFragment dialog,
                                           final String note, final String exercise,
                                           final String location, final String date, final String people);
        public void onMeetupNegativeClick(DialogFragment dialog);
    }

    private static final String TAG = MeetupDialogFragment.class.getSimpleName();
    private MeetupDialogListener mListener;
    private String exercise, location, people, date, note;

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        try {
            mListener = (MeetupDialogListener) activity;
        }
        catch(ClassCastException ex) {
            throw new ClassCastException(activity.toString()
                    + " must implement MeetupDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_meetup, null);
        builder.setView(view);
        // get the view items
        final EditText etNote = (EditText) view.findViewById(R.id.etNote);
        final EditText etExercise = (EditText) view.findViewById(R.id.etExercise);
        final EditText etLocation = (EditText) view.findViewById(R.id.etLocation);
        final EditText etDate = (EditText) view.findViewById(R.id.etDate);
        final EditText etPeople = (EditText) view.findViewById(R.id.etPeople);
        etExercise.setText(exercise);
        etLocation.setText(location);
        etPeople.setText(people);
        etNote.setText(note);
        etDate.setText(date);

        // add the buttons
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id) {
                Log.d(TAG, "Meetup confirmed");
                mListener.onMeetupPositiveClick(MeetupDialogFragment.this,
                        etNote.getText().toString(), etExercise.getText().toString(),
                        etLocation.getText().toString(), etDate.getText().toString(),
                        etPeople.getText().toString());
            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id)
            {
                Log.d(TAG, "Meetup canceled or not changed");
                mListener.onMeetupNegativeClick(MeetupDialogFragment.this);
            }
        });
        return builder.create();
    } // onCreateDialog()

    public void setMeetupText(String exercise, String location, String people)
    {
        this.exercise = exercise;
        this.location = location;
        this.people = people;
    }

    public void setMeetupText(String exercise, String location, String people, String note, String date)
    {
        this.exercise = exercise;
        this.location = location;
        this.people = people;
        this.note = note;
        this.date = date;
    }

} // MeetupDialogFragment class
