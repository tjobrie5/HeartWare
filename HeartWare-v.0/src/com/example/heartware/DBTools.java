/**
 * DBTools handles CRUD operations for the user data.
 * It currently uses Android's SQLite library.
 * TODO: switch from SQLite to MongoDB.
 */

package com.example.heartware;

import java.util.ArrayList;
import java.util.HashMap;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
// Import SQLite to open or create a database 
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// TODO: change SQL data operations to MongoDB 
public class DBTools extends SQLiteOpenHelper {
	
	// Context provides access to application-specific resources and classes
	public DBTools(Context applicationcontext) {
		// super(context, name of data base, version control, version number >= 1) 
	    super(applicationcontext, "profiles.db", null, 1); 
	}
	
	// onCreate is called the first time the database is created
	public void onCreate(SQLiteDatabase database) {
		// Make sure you don't put a ; at the end of the query
		String query = "CREATE TABLE profiles ( profileId INTEGER PRIMARY KEY, firstName TEXT, " +
				"lastName TEXT, phoneNumber TEXT, emailAddress TEXT, homeAddress TEXT)";
		
		// Executes the query provided as long as the query isn't a select or if the query doesn't return any data
		database.execSQL(query);
	}

	// onUpgrade is used to drop tables, add tables, or do anything 
	// else it needs to upgrade
	// This is dropping the table to delete the data and then calling
	// onCreate to make an empty table
	public void onUpgrade(SQLiteDatabase database, int version_old, int current_version) {
		String query = "DROP TABLE IF EXISTS profiles";
		// Executes the query provided as long as the query isn't a select or if the query doesn't return any data
		database.execSQL(query);
    	onCreate(database);
	}
	
	public void insertProfile(HashMap<String, String> queryValues) {
		
		// Open a database for reading and writing
		SQLiteDatabase database = this.getWritableDatabase();
		
		// Stores key value pairs being the column name and the data
		// ContentValues data type is needed because the database
		// requires its data type to be passed
		ContentValues values = new ContentValues();
		
		values.put("firstName", queryValues.get("firstName"));
		values.put("lastName", queryValues.get("lastName"));
		values.put("phoneNumber", queryValues.get("phoneNumber"));
		values.put("emailAddress", queryValues.get("emailAddress"));
		values.put("homeAddress", queryValues.get("homeAddress"));
		
		// Inserts the data in the form of ContentValues into the table name provided
		database.insert("profiles", null, values);
		
		// Release the reference to the SQLiteDatabase object	
		database.close();
	}
	
	public int updateProfile(HashMap<String, String> queryValues) {
		
		// Open a database for reading and writing
		SQLiteDatabase database = this.getWritableDatabase();	
		
		// Stores key value pairs being the column name and the data
	    ContentValues values = new ContentValues();
	    
	    values.put("firstName", queryValues.get("firstName"));
	    values.put("lastName", queryValues.get("lastName"));
		values.put("phoneNumber", queryValues.get("phoneNumber"));
		values.put("emailAddress", queryValues.get("emailAddress"));
		values.put("homeAddress", queryValues.get("homeAddress"));
	    
		// update(TableName, ContentValueForTable, WhereClause, ArgumentForWhereClause)
	    return database.update("profiles", values, 
	    		"profileId" + " = ?", new String[] { queryValues.get("profileId") });
	}
	
	// Used to delete a contact with the matching contactId
	public void deleteProfile(String id) {

		// Open a database for reading and writing
		SQLiteDatabase database = this.getWritableDatabase();
		
		String deleteQuery = "DELETE FROM profiles where profileId='"+ id +"'";
		
		// Executes the query provided as long as the query isn't a select or if the query doesn't return any data
		database.execSQL(deleteQuery);
	}
	
	// get all contacts from the database
	public ArrayList<HashMap<String, String>> getAllProfiles() {
		
		// ArrayList that contains every row in the database and each row key / value stored in a HashMap
		ArrayList<HashMap<String, String>> profileArrayList;
		profileArrayList = new ArrayList<HashMap<String, String>>();
		String selectQuery = "SELECT  * FROM profiles ORDER BY lastName ASC";	
		
		// Open a database for reading and writing
	    SQLiteDatabase database = this.getWritableDatabase();
	    
	    // Cursor provides read and write access for the 
	    // data returned from a database query
	    // rawQuery executes the query and returns the result as a Cursor
	    Cursor cursor = database.rawQuery(selectQuery, null);	
	    
	    // Move to the first row
	    if (cursor.moveToFirst()) {
	        do {
	        	HashMap<String, String> profileMap = new HashMap<String, String>();
	        	
	        	// Store the key / value pairs in a HashMap
	        	// Access the Cursor data by index that is in the same order
	        	// as used when creating the table
	        	profileMap.put("profileId", cursor.getString(0));
	        	profileMap.put("firstName", cursor.getString(1));
	        	profileMap.put("lastName", cursor.getString(2));
	        	profileMap.put("phoneNumber", cursor.getString(3));
	        	profileMap.put("emailAddress", cursor.getString(4));
	        	profileMap.put("homeAddress", cursor.getString(5));
	        	
	        	profileArrayList.add(profileMap);
	        } while (cursor.moveToNext()); // Move Cursor to the next row
	    }
	 
	    // return contact list
	    return profileArrayList;
	}
	
	// get a specific contact from the database
	public HashMap<String, String> getProfileInfo(String id) {
		HashMap<String, String> profileMap = new HashMap<String, String>();
		
		// Open a database for reading only
		SQLiteDatabase database = this.getReadableDatabase();
		
		String selectQuery = "SELECT * FROM profiles where profileId='" + id + "'";
		
		// rawQuery executes the query and returns the result as a Cursor
		Cursor cursor = database.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
	        do {
	        	profileMap.put("profileId", cursor.getString(0));	
	        	profileMap.put("firstName", cursor.getString(1));
	        	profileMap.put("lastName", cursor.getString(2));
	        	profileMap.put("phoneNumber", cursor.getString(3));
	        	profileMap.put("emailAddress", cursor.getString(4));
	        	profileMap.put("homeAddress", cursor.getString(5));
				   
	        } while (cursor.moveToNext());
	    }				    
		
		return profileMap;
	}	

} // class
