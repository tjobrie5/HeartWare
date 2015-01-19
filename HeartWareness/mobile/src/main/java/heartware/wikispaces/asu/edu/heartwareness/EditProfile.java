// Copyright (c) HeartWare Group Fall 2014 - Spring 2015
// @purpose ASU Capstone Project
// @app HeartWare smart health monitoring application
// @authors Mark Aleheimer, Ryan Case, Tyler O'Brien, Amy Mazzola, Zach Mertens, New Guy
// @version 1.0
//
// @class

package heartware.wikispaces.asu.edu.heartwareness;

import java.util.HashMap;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.app.Activity;
import android.content.Intent;
import android.view.View;

public class EditProfile extends Activity {

    static final String TAG = "EditProfile";
    EditText firstName;
    EditText lastName;
    EditText phoneNumber;
    EditText emailAddress;
    EditText homeAddress;

    DBTools dbTools = new DBTools(this);

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        // Get the EditText objects
        firstName = (EditText) findViewById(R.id.firstName);
        lastName = (EditText) findViewById(R.id.lastName);
        phoneNumber = (EditText) findViewById(R.id.phoneNumber);
        emailAddress = (EditText) findViewById(R.id.emailAddress);
        homeAddress = (EditText) findViewById(R.id.homeAddress);

        Intent theIntent = getIntent();
        String profileId = theIntent.getStringExtra("profileId");

        // Get the HashMap of data associated with the profileId
        HashMap<String, String> profileList = dbTools.getProfileInfo(profileId);

        if(profileList.size() != 0) {

            // Put the values in the EditText boxes
            firstName.setText(profileList.get("firstName"));
            lastName.setText(profileList.get("lastName"));
            phoneNumber.setText(profileList.get("phoneNumber"));
            emailAddress.setText(profileList.get("emailAddress"));
            homeAddress.setText(profileList.get("homeAddress"));
        }

        Log.d(TAG, " == onCreate() == ");
    }

    public void editProfile(View view) {
        HashMap<String, String> queryValuesMap =  new  HashMap<String, String>();

        // Get the EditText objects
        firstName = (EditText) findViewById(R.id.firstName);
        lastName = (EditText) findViewById(R.id.lastName);
        phoneNumber = (EditText) findViewById(R.id.phoneNumber);
        emailAddress = (EditText) findViewById(R.id.emailAddress);
        homeAddress = (EditText) findViewById(R.id.homeAddress);

        Intent theIntent = getIntent();

        String profileId = theIntent.getStringExtra("profileId");

        queryValuesMap.put("profileId", profileId);
        queryValuesMap.put("firstName", firstName.getText().toString());
        queryValuesMap.put("lastName", lastName.getText().toString());
        queryValuesMap.put("phoneNumber", phoneNumber.getText().toString());
        queryValuesMap.put("emailAddress", emailAddress.getText().toString());
        queryValuesMap.put("homeAddress", homeAddress.getText().toString());

        dbTools.updateProfile(queryValuesMap);

        this.callMainActivity(view);
    }

    public void removeProfile(View view) {
        Intent theIntent = getIntent();
        String profileId = theIntent.getStringExtra("profileId");
        dbTools.deleteProfile(profileId);
        this.callMainActivity(view);
    }

    public void graph(View view) {
        Intent dataIntent = new Intent(getApplication(), DataView.class);
        startActivity(dataIntent);
    }

    public void query(View view) {
        Intent qIntent = new Intent(getApplication(), SmartQuery.class);
        qIntent.putExtra("query", "This is a placeholder - everything looks good");
        startActivity(qIntent);
    }

    // switches the view to the MainActivity
    public void callMainActivity(View view) {
        Intent objIntent = new Intent(getApplication(), MainActivity.class);
        startActivity(objIntent);
    }
} // EditProfile class
