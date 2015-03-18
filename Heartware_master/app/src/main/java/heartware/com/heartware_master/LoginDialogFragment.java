///////////////////////////////////////////////////////////////////////////////////////////
// Copyright (c) HeartWare Group Fall 2014 - Spring 2015
// @license
// @purpose ASU Capstone Project
// @app HeartWare smart health monitoring application
// @authors Mark Aleheimer, Ryan Case, Tyler O'Brien, Amy Mazzola, Zach Mertens, Sri Somanchi
// @mailto zmertens@asu.edu
// @version 1.0
//
// Description: Creates a 2 Button pop-up when the user logs in
///////////////////////////////////////////////////////////////////////////////////////////

package heartware.com.heartware_master;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

public class LoginDialogFragment extends DialogFragment
{
    private static final String TAG = LoginDialogFragment.class.getSimpleName();
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.login_pop_up)
                .setPositiveButton(R.string.returning_user, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d(TAG, "this is a returning user");
                    }
                })
                .setNegativeButton(R.string.new_user, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d(TAG, "this is a new user");
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
} // LoginDialogFragment class
