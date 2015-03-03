// Copyright (c) HeartWare Group Fall 2014 - Spring 2015
// @purpose ASU Capstone Project
// @app HeartWare smart health monitoring application
// @authors Mark Aleheimer, Ryan Case, Tyler O'Brien, Amy Mazzola, Zach Mertens, New Guy
// @version 1.0
//
// @class Inherits SQL database. Performs CRUD operations.

package heartware.wikispaces.asu.edu.heartwareness;

import java.util.ArrayList;
import java.util.HashMap;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBAdapter extends SQLiteOpenHelper
{
    private static final String dbName = "profiles";

    public DBAdapter(Context appContext)
    {
        // super(context, name of data base, version control, version number >= 1)
        super(appContext, dbName + ".db", null, 1);
    }

    public void onCreate(SQLiteDatabase database)
    {
        // @NOTE : Make sure you don't put a ; at the end of the SQL query string
        String query = "CREATE TABLE profiles ( profileId INTEGER PRIMARY KEY, firstName TEXT, " +
                "lastName TEXT, phoneNumber TEXT, emailAddress TEXT, homeAddress TEXT)";
        database.execSQL(query);
    }

    public void onUpgrade(SQLiteDatabase database, int version_old, int current_version)
    {
        String query = "DROP TABLE IF EXISTS profiles";
        database.execSQL(query);
        onCreate(database);
    }

    public void insertProfile(HashMap<String, String> queryValues)
    {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("firstName", queryValues.get("firstName"));
        values.put("lastName", queryValues.get("lastName"));
        values.put("phoneNumber", queryValues.get("phoneNumber"));
        values.put("emailAddress", queryValues.get("emailAddress"));
        values.put("homeAddress", queryValues.get("homeAddress"));
        database.insert("profiles", null, values);
        database.close();
    }

    public int updateProfile(HashMap<String, String> queryValues)
    {
        SQLiteDatabase database = this.getWritableDatabase();
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

    public void deleteProfile(String id)
    {
        SQLiteDatabase database = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM profiles where profileId='"+ id +"'";
        database.execSQL(deleteQuery);
    }

    public ArrayList<HashMap<String, String>> getAllProfiles()
    {
        ArrayList<HashMap<String, String>> profileArrayList;
        profileArrayList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT  * FROM profiles ORDER BY lastName ASC";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> profileMap = new HashMap<String, String>();
                profileMap.put("profileId", cursor.getString(0));
                profileMap.put("firstName", cursor.getString(1));
                profileMap.put("lastName", cursor.getString(2));
                profileMap.put("phoneNumber", cursor.getString(3));
                profileMap.put("emailAddress", cursor.getString(4));
                profileMap.put("homeAddress", cursor.getString(5));
                profileArrayList.add(profileMap);
            } while (cursor.moveToNext());
        }

        return profileArrayList;
    }

    public HashMap<String, String> getProfileInfo(String id)
    {
        HashMap<String, String> profileMap = new HashMap<String, String>();
        SQLiteDatabase database = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM profiles where profileId='" + id + "'";
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
} // DBTools class