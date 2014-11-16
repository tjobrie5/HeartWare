/**
 * The MainActivity is a list view.
 * The lists contain the user's profiles.
 * A single user can have multiple profiles.
 */

package com.example.heartware;

import java.util.ArrayList;
import java.util.HashMap;
import com.example.heartware.DBTools;
import com.example.heartware.NewProfile;
import android.os.Bundle;
import android.app.ListActivity;
import android.content.Intent;
import android.view.View;
//import android.view.Menu;
//import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.ListView;

// whenever we extend *Activity, we set the corresponding .xml as the view in "setContentView"
public class MainActivity extends ListActivity {

	// The Intent is used to issue that an operation should be performed
	Intent intent;
	TextView profileId;

	// The object that allows database access
	DBTools dbTools = new DBTools(this);

	// Called when the Activity is first called
	protected void onCreate(Bundle savedInstanceState) {
		// Get saved data if there is any
		super.onCreate(savedInstanceState);

		// Designate that activity_main.xml is the interface used
		setContentView(R.layout.activity_main);

		// Gets all the data from the database and stores it in an ArrayList
		ArrayList<HashMap<String, String>> profileList =  dbTools.getAllProfiles();
		
		// Check to make sure there are contacts to display
		if(profileList.size() != 0) {
			
			// Get the ListView and assign an event handler to it
			ListView listView = (ListView) findViewById(android.R.id.list);
			listView.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					
					// When an item is clicked get the TextView with the matching ID
					profileId = (TextView) view.findViewById(R.id.profileId);
					
					// Convert that profileId into a String
					String profileIdValue = profileId.getText().toString();	
					
					// getApplication() returns the application that owns this activity
					Intent  theIntent = new Intent(getApplication(), EditProfile.class);
					
					// Put additional data in for EditProfile to use
					theIntent.putExtra("profileId", profileIdValue); 
					
					// Calls for EditProfile activity to start
					startActivity(theIntent); 
				}
			}); 
			
			// A list adapter is used bridge between a ListView and the ListViews data.
			// The SimpleAdapter connects the data in an ArrayList to the XML file.
			// First we pass in a Context to provide information needed about the application.
			// The ArrayList of data is next followed by the xml resource,
			// then we have the names of the data in String format and
			// their specific resource ids
			ListAdapter adapter = new SimpleAdapter( MainActivity.this, profileList, R.layout.profile_entry, 
					new String[] { "profileId","firstName", "lastName"}, 
						new int[] {R.id.profileId, R.id.firstName, R.id.lastName});
			
			// setListAdapter provides the Cursor for the ListView
			// The Cursor provides access to the database data
			setListAdapter(adapter);
		}
	}
	
	// NewProfile activity is called
	public void showAddProfile(View view) {
		Intent theIntent = new Intent(getApplication(), NewProfile.class);
		startActivity(theIntent);
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.main, menu);
//		return true;
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		// Handle action bar item clicks here. The action bar will
//		// automatically handle clicks on the Home/Up button, so long
//		// as you specify a parent activity in AndroidManifest.xml.
//		int id = item.getItemId();
//		if (id == R.id.action_settings) {
//			return true;
//		}
//		return super.onOptionsItemSelected(item);
//	}
} // class
