/**
 * EditProfile allows the user to edit a 
 * selected profile's information.
 */

package com.example.heartware;

import java.util.HashMap;
import android.os.Bundle;
import android.widget.EditText;
import android.app.Activity;
import android.content.Intent;
import android.view.View;

public class EditProfile extends Activity {
	
	// Allows access to data in the EditTexts
	EditText firstName;
	EditText lastName;
	EditText phoneNumber;
	EditText emailAddress;
	EditText homeAddress;

	// get access to the database
	DBTools dbTools = new DBTools(this);

	// Sets up everything when the Activity is displayed
	public void onCreate(Bundle savedInstanceState) {

		// Get saved data if there is any
		super.onCreate(savedInstanceState);

		// Designate that edit_profile.xml is the interface used
		setContentView(R.layout.edit_profile);

		// Get the EditText objects
		firstName = (EditText) findViewById(R.id.firstName);
		lastName = (EditText) findViewById(R.id.lastName);
		phoneNumber = (EditText) findViewById(R.id.phoneNumber);
		emailAddress = (EditText) findViewById(R.id.emailAddress);
		homeAddress = (EditText) findViewById(R.id.homeAddress);

		// Intent defines that an operation will be performed
		Intent theIntent = getIntent(); // getIntent gets the intent that started this activity

		// Get the extended data provided to this activity
		// putExtra("contactId", contactIdValue); in MainActivity
		// will pass contactId here
		String contactId = theIntent.getStringExtra("contactId");

		// Get the HashMap of data associated with the contactId

		HashMap<String, String> contactList = dbTools.getProfileInfo(contactId);

		if(contactList.size() != 0) {

			// Put the values in the EditText boxes
			firstName.setText(contactList.get("firstName"));
			lastName.setText(contactList.get("lastName"));
			phoneNumber.setText(contactList.get("phoneNumber"));
			emailAddress.setText(contactList.get("emailAddress"));
			homeAddress.setText(contactList.get("homeAddress"));
		}
	}
	
	public void editProfile(View view) {
		HashMap<String, String> queryValuesMap =  new  HashMap<String, String>();

		// Get the EditText objects
		firstName = (EditText) findViewById(R.id.firstName);
		lastName = (EditText) findViewById(R.id.lastName);
		phoneNumber = (EditText) findViewById(R.id.phoneNumber);
		emailAddress = (EditText) findViewById(R.id.emailAddress);
		homeAddress = (EditText) findViewById(R.id.homeAddress);

		// Intent defines that an operation will be performed
		Intent theIntent = getIntent();

		// Get the extended data provided to this activity
		// putExtra("contactId", contactIdValue); in MainActivity
		// will pass contactId here
		String contactId = theIntent.getStringExtra("contactId");

		// Put the values in the EditTexts in the HashMap
		queryValuesMap.put("contactId", contactId);
		queryValuesMap.put("firstName", firstName.getText().toString());
		queryValuesMap.put("lastName", lastName.getText().toString());
		queryValuesMap.put("phoneNumber", phoneNumber.getText().toString());
		queryValuesMap.put("emailAddress", emailAddress.getText().toString());
		queryValuesMap.put("homeAddress", homeAddress.getText().toString());

		// Send the HashMap to update the data in the database
		dbTools.updateProfile(queryValuesMap);
		
		// Call for MainActivity
		this.callMainActivity(view);
	}
	
	public void removeProfile(View view) {
		Intent theIntent = getIntent();
		String contactId = theIntent.getStringExtra("contactId");
		
		// Call for the contact with the contactId provided to be deleted
		dbTools.deleteProfile(contactId);
		
		// Call for MainActivity
		this.callMainActivity(view);

	}
	
	public void cancelEdit(View view) {
		this.callMainActivity(view);
	}
	
	// switches the view to the MainActivity
	public void callMainActivity(View view) {
		
		// getApplication returns an Application object which allows 
		// you to manage your application and respond to different actions.
		// It returns an Application object which extends Context.
		// A Context provides information on the environment your application 
		// is currently running in. It provides services like how to obtain 
		// access to a database and preferences.
		// Google says a Context is an entity that represents various 
		// environment data. It provides access to local files, databases, 
		// class loaders associated to the environment, services including 
		// system-level services, and more.	
		// The following Intent states that you want to switch to a new 
		// Activity being the MainActivity	
		Intent objIntent = new Intent(getApplication(), MainActivity.class);
		startActivity(objIntent);
	}
} // class
