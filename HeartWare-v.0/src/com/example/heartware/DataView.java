/**
 * DataView represents the user's data using graphs.
 * Graph data can be selected and changed (TODO: Add AFreeChart).
 * The day of the data sample can be changed.
 * The DataView is representative of a user's profile.
 * A user can have multiple profiles, and thus
 * multiple data views.
 * 
 * Data includes:
 * exercise plans, weight loss plans, and 
 * other health related information
 */

package com.example.heartware;

import android.app.Activity;
import android.os.Bundle;

public class DataView extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.data_view);
	}
	
	

}
