/**
 * Perform a "smart" query about user
 * data using data retreived from wearable 
 * device. Data from this device is stored
 * on a server with MongoDB / DLVHEX.
 * 
 * TODO: Figure out how to pass / handle data
 */

package com.example.heartware;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class SmartQuery extends Activity {

	TextView queryText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.query_view);
		
		Intent qIntent = getIntent();
		String qText = qIntent.getStringExtra("query");
		
		queryText = (TextView) findViewById(R.id.queryText1);
		
		//queryText.setText(qText);
	}

	
	
} // class
