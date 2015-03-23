///////////////////////////////////////////////////////////////////////////////////////////
// Copyright (c) Heartware Group Fall 2014 - Spring 2015
// @license
// @purpose ASU Computer Science Capstone Project
// @app a smart health application
// @authors Mark Aleheimer, Ryan Case, Tyler O'Brien, Amy Mazzola, Zach Mertens, Sri Somanchi
// @mailto zmertens@asu.edu
// @version 1.0
//
// Source code: github.com/tjobrie5/HeartWare
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
    private static final String DB_Name = "Heartware";
    private static final String PROFILES_TABLE = "profiles";
    private static final String WORKOUTS_TABLE = "workouts";
    // profiles table data
    public static final String PROFILE_ID = "profileId";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String SEX = "sex";
    // workouts table data
    public static final String USER_ID = "userId";
    public static final String EXERCISE = "exercise";
    public static final String GOAL = "goal";
    public static final String DIFFICULTY = "difficulty";
    public static final String EXEMPTIONS = "exemptions";
    public static final String DATA = "data";
    public static final String PLACE = "place";
    public static final String TIME = "time";

    // @NOTE : Make sure you don't put a ; at the end of the SQL schema string
    private final String profileSchema = "CREATE TABLE " + PROFILES_TABLE +
        " ( " + PROFILE_ID + " INTEGER PRIMARY KEY, " + USERNAME + " TEXT NOT NULL, " +
        PASSWORD + " TEXT NOT NULL, " + SEX + " TEXT)";

    private final String workoutSchema = "CREATE TABLE " + WORKOUTS_TABLE +
            " ( " + USER_ID + " INTEGER NOT NULL, " + EXERCISE + " TEXT, " +
            GOAL + " TEXT, " + DIFFICULTY + " TEXT, " + EXEMPTIONS + " TEXT, " +
            DATA + " INTEGER, " + PLACE + " TEXT, " + TIME + " TEXT, " +
            "FOREIGN KEY ( " + USER_ID + " ) REFERENCES " + PROFILES_TABLE + " (" + PROFILE_ID + ") )";

    public DBAdapter(Context appContext)
    {
        // super(context, name of data base, version control, version number >= 1)
        super(appContext, DB_Name + ".db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase database)
    {
        database.execSQL(profileSchema);
        database.execSQL(workoutSchema);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int version_old, int current_version)
    {
        final String queryProfiles = "DROP TABLE IF EXISTS " + PROFILES_TABLE;
        final String queryWorkouts = "DROP TABLE IF EXISTS " + WORKOUTS_TABLE;
        database.execSQL(queryProfiles);
        database.execSQL(queryWorkouts);
        onCreate(database);
    }

    public void createProfile(HashMap<String, String> queryValues)
    {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USERNAME, queryValues.get(USERNAME));
        values.put(PASSWORD, queryValues.get(PASSWORD));
        values.put(SEX, queryValues.get(SEX));
        database.insert(PROFILES_TABLE, null, values);
        database.close();
    }

    public int updateProfile(HashMap<String, String> queryValues)
    {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USERNAME, queryValues.get(USERNAME));
        values.put(PASSWORD, getProfilePassword(queryValues.get(PROFILE_ID)).get(PASSWORD));
        values.put(SEX, queryValues.get(SEX));
        // update(TableName, ContentValueForTable, WhereClause, ArgumentForWhereClause)
        return database.update(PROFILES_TABLE, values,
                PROFILE_ID + " = ?", new String[] { queryValues.get(PROFILE_ID) });
    }

    public void deleteProfile(String id)
    {
        SQLiteDatabase database = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM " + PROFILES_TABLE + " where " + PROFILE_ID + "='" + id + "'";
        database.execSQL(deleteQuery);
    }

    public ArrayList<HashMap<String, String>> getAllProfiles()
    {
        ArrayList<HashMap<String, String>> profileArrayList;
        profileArrayList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT * FROM " + PROFILES_TABLE + " ORDER BY " + USERNAME + " ASC";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> profileMap = new HashMap<String, String>();
                profileMap.put(PROFILE_ID, cursor.getString(0));
                profileMap.put(USERNAME, cursor.getString(1));
                profileMap.put(PASSWORD, cursor.getString(2));
                profileMap.put(SEX, cursor.getString(3));
                profileArrayList.add(profileMap);
            } while (cursor.moveToNext());
        }

        return profileArrayList;
    }

    public HashMap<String, String> getProfileInfo(String id)
    {
        HashMap<String, String> profileMap = new HashMap<String, String>();
        SQLiteDatabase database = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + PROFILES_TABLE + " WHERE " + PROFILE_ID + "='" + id + "'";
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                profileMap.put(PROFILE_ID, cursor.getString(0));
                profileMap.put(USERNAME, cursor.getString(1));
                profileMap.put(PASSWORD, cursor.getString(2));
                profileMap.put(SEX, cursor.getString(3));

            } while (cursor.moveToNext());
        }
        return profileMap;
    }

    public HashMap<String, String> getProfilePassword(String id)
    {
        HashMap<String, String> profileMap = new HashMap<String, String>();
        SQLiteDatabase database = this.getReadableDatabase();
        String selectQuery = "SELECT " + PASSWORD + " FROM " + PROFILES_TABLE + " WHERE " + PROFILE_ID + "='" + id + "'";
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                profileMap.put(PASSWORD, cursor.getString(0));
            } while (cursor.moveToNext());
        }
        return profileMap;
    }

    public HashMap<String, String> getProfileInfo(String user, String pw)
    {
        HashMap<String, String> profileMap = new HashMap<String, String>();
        SQLiteDatabase database = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + PROFILES_TABLE + " WHERE " + USERNAME + "='" + user + "'";
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                profileMap.put(PROFILE_ID, cursor.getString(0));
                profileMap.put(USERNAME, cursor.getString(1));
                profileMap.put(PASSWORD, cursor.getString(2));
                profileMap.put(SEX, cursor.getString(3));

            } while (cursor.moveToNext());
        }
        return profileMap;
    }
} // DBAdapter class
