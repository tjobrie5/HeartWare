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
// Description: Handles user logins using a DialogFragment..
//  https://developer.android.com/guide/topics/ui/dialogs.html
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
import android.view.ViewGroup;
import android.widget.EditText;

public class LoginDialogFragment extends DialogFragment
{
    public interface LoginDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
    }

    private LoginDialogListener mListener;

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        try {
            mListener = (LoginDialogListener) activity;
        }
        catch(ClassCastException ex) {
            throw new ClassCastException(activity.toString()
                    + " must implement LoginDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_login, null);
        builder.setView(view);
        // add the buttons
        builder.setPositiveButton(R.string.jbone_login, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mListener.onDialogPositiveClick(LoginDialogFragment.this);
            }
        }).setIcon(R.drawable.jawbone_button);
        // Create the AlertDialog object and return it
        return builder.create();
    }
} // LoginDialogFragment class
