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
// Description: Handles user information input and editing
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

public class ProfileDialogFragment extends DialogFragment
{
    public interface ProfileDialogListener {
        public void onProfilePositiveClick(DialogFragment dialog,
                                          final String user, final String skill,
                                          final String disability);
        public void onProfileNegativeClick(DialogFragment dialog);
    }

    private static final String TAG = ProfileDialogFragment.class.getSimpleName();
    private ProfileDialogListener mListener;
    private int mCurrentDisabilityPosition = 0;

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        try {
            mListener = (ProfileDialogListener) activity;
        }
        catch(ClassCastException ex) {
            throw new ClassCastException(activity.toString()
                    + " must implement ProfileDialogListener");
        }
    }
// @TODO : set the text fields if there is already a profile in the database
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_profile, null);
        builder.setView(view);
        // get the view items
        final EditText username = (EditText) view.findViewById(R.id.etName);
        final RadioGroup diffGroup = (RadioGroup) view.findViewById(R.id.rgDifficulty);
        // set up the spinner adapter and array and whatnot
        final String[] DisabilityArray = getResources().getStringArray(R.array.disabilities_array);
        final Spinner disabilitySpinner = (Spinner) view.findViewById(R.id.spinnerDisabilities);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.disabilities_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        disabilitySpinner.setAdapter(adapter);
        disabilitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                mCurrentDisabilityPosition = position;
                Log.d(TAG, "selected " + DisabilityArray[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        // add the buttons
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id) {
                final int radioId = diffGroup.getCheckedRadioButtonId();
                String difficulty = "";
                switch(radioId) {
                    case 1: difficulty = "beginner";
                        break;
                    case 2: difficulty = "intermediate";
                        break;
                    case 3: difficulty = "expert";
                        break;
                }

                mListener.onProfilePositiveClick(ProfileDialogFragment.this,
                        username.getText().toString(),
                        difficulty, DisabilityArray[mCurrentDisabilityPosition]);
            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id)
            {
                mListener.onProfileNegativeClick(ProfileDialogFragment.this);
            }
        });
        return builder.create();
    } // onCreateDialog()

} // ProfileDialogFragment class
