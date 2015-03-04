package com.ryancase.heartware_v2;

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
        builder.setMessage(R.string.login_question)
                .setPositiveButton(R.string.fb_login, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d(TAG, "using Facebook to login");
                        // @TODO : launch FB activity
                    }
                })
                .setNegativeButton(R.string.manual_login, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d(TAG, "entering information manually");
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

} // LoginDialogFragment
