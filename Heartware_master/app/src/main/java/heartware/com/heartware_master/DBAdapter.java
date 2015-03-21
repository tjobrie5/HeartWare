///////////////////////////////////////////////////////////////////////////////////////////
// Copyright (c) HeartWare Group Fall 2014 - Spring 2015
// @license
// @purpose ASU Capstone Project
// @app HeartWare smart health monitoring application
// @authors Mark Aleheimer, Ryan Case, Tyler O'Brien, Amy Mazzola, Zach Mertens, Sri Somanchi
// @mailto zmertens@asu.edu
// @version 1.0
//
// Description: Inherits SQL database. Handles "CRUD" operations.
///////////////////////////////////////////////////////////////////////////////////////////

package heartware.com.heartware_master;

import java.util.ArrayList;
import java.util.HashMap;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBAdapter extends SQLiteOpenHelper
{
    private static final String TAG = DBAdapter.class.getSimpleName();
    private static final String DB_Name = "Profile";
    public static final String PROFILE_ID = "profileID";
    public static final String USER_NAME = "userName";
    public static final String USER_PASSWORD = "userPassword";
    public static final String SEX = "sex";
    public static final String FAV_EXERCISE = "favExercise";
    public static final String DISABILITIES = "disabilities";
    public static final String WORKOUT_LOC = "workoutLocation";
    public static final String WORKOUT_LEVEL = "workoutLevel";
    public static final String GOAL = "goal";
    public static final String STEPS = "steps";

    public DBAdapter(Context appContext)
    {
        // super(context, name of data base, version control, version number >= 1)
        super(appContext, DB_Name + ".db", null, 1);
    }

    public void onCreate(SQLiteDatabase database)
    {
        // @NOTE : Make sure you don't put a ; at the end of the SQL query string
        String query = "CREATE TABLE " + DB_Name +
                " ( " + PROFILE_ID + " INTEGER PRIMARY KEY, " + USER_NAME + " TEXT, " +
                SEX + " TEXT, " + FAV_EXERCISE + " TEXT, " + DISABILITIES + " TEXT, " +
                WORKOUT_LOC + " TEXT)";
        database.execSQL(query);
    }

    public void onUpgrade(SQLiteDatabase database, int version_old, int current_version)
    {
        String query = "DROP TABLE IF EXISTS " + DB_Name;
        database.execSQL(query);
        onCreate(database);
    }

    public void createProfile(HashMap<String, String> queryValues)
    {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_NAME, queryValues.get(USER_NAME));
        values.put(SEX, queryValues.get(SEX));
        values.put(FAV_EXERCISE, queryValues.get(FAV_EXERCISE));
        values.put(DISABILITIES, queryValues.get(DISABILITIES));
        values.put(WORKOUT_LOC, queryValues.get(WORKOUT_LOC));
        database.insert(DB_Name, null, values);
        database.close();
    }

    public int updateProfile(HashMap<String, String> queryValues)
    {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_NAME, queryValues.get(USER_NAME));
        values.put(SEX, queryValues.get(SEX));
        values.put(FAV_EXERCISE, queryValues.get(FAV_EXERCISE));
        values.put(DISABILITIES, queryValues.get(DISABILITIES));
        values.put(WORKOUT_LOC, queryValues.get(WORKOUT_LOC));
        // update(TableName, ContentValueForTable, WhereClause, ArgumentForWhereClause)
        return database.update(DB_Name, values,
                PROFILE_ID + " = ?", new String[] { queryValues.get(PROFILE_ID) });
    }

    public void deleteProfile(String id)
    {
        SQLiteDatabase database = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM " + DB_Name + " where " + PROFILE_ID + "='" + id + "'";
        database.execSQL(deleteQuery);
    }

    // @TODO : refactor into something useful
    public ArrayList<HashMap<String, String>> getAllProfiles()
    {
        ArrayList<HashMap<String, String>> profileArrayList;
        profileArrayList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT  * FROM " + DB_Name + " ORDER BY " + USER_NAME + " ASC";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> profileMap = new HashMap<String, String>();
                profileMap.put(PROFILE_ID, cursor.getString(0));
                profileMap.put(USER_NAME, cursor.getString(1));
                profileMap.put(SEX, cursor.getString(2));
                profileMap.put(FAV_EXERCISE, cursor.getString(3));
                profileMap.put(DISABILITIES, cursor.getString(4));
                profileMap.put(WORKOUT_LOC, cursor.getString(5));
                profileArrayList.add(profileMap);
            } while (cursor.moveToNext());
        }

        return profileArrayList;
    }

    public HashMap<String, String> getProfileInfo(String id)
    {
        HashMap<String, String> profileMap = new HashMap<String, String>();
        SQLiteDatabase database = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM profiles where profileID='" + id + "'";
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                profileMap.put(PROFILE_ID, cursor.getString(0));
                profileMap.put(USER_NAME, cursor.getString(1));
                profileMap.put(SEX, cursor.getString(2));
                profileMap.put(FAV_EXERCISE, cursor.getString(3));
                profileMap.put(DISABILITIES, cursor.getString(4));
                profileMap.put(WORKOUT_LOC, cursor.getString(5));

            } while (cursor.moveToNext());
        }
        return profileMap;
    }
} // DBAdapter class

