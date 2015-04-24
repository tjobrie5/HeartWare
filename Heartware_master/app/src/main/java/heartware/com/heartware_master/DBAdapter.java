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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

public class DBAdapter extends SQLiteOpenHelper
{
    private static final String TAG = DBAdapter.class.getSimpleName();
    private static final String DB_Name = "Heartware";
    private static final String PROFILES_TABLE = "profiles";
    private static final String MEETUPS_TABLE = "meetups";
    // profiles table data
    public static final String PROFILE_ID = "profileId";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password"; // usually the user's Jawbone UP token
    public static final String DIFFICULTY = "difficulty"; // easy medium hard
    public static final String DISABILITY = "disability";
    // meetups table data
    public static final String USER_ID = "userId";
    public static final String NOTE = "note";
    public static final String EXERCISE = "exercise";
    public static final String LOCATION = "location";
    public static final String DATE = "date";
    public static final String PEOPLE = "people";

    // @NOTE : Make sure you don't put a ; at the end of the SQL schema string
    private final String profileSchema = "CREATE TABLE " + PROFILES_TABLE +
        " ( " + PROFILE_ID + " INTEGER PRIMARY KEY, " + USERNAME + " TEXT NOT NULL, " +
        PASSWORD + " TEXT NOT NULL, " + DIFFICULTY + " TEXT, " + DISABILITY + " TEXT)";

    private final String meetupsSchema = "CREATE TABLE " + MEETUPS_TABLE +
            " ( " + USER_ID + " INTEGER NOT NULL, " + NOTE + " TEXT, " + EXERCISE + " TEXT, " +
            LOCATION + " TEXT, " + DATE + " TEXT, " + PEOPLE + " TEXT, " +
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
        database.execSQL(meetupsSchema);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int version_old, int current_version)
    {
        final String queryProfiles = "DROP TABLE IF EXISTS " + PROFILES_TABLE;
        final String queryWorkouts = "DROP TABLE IF EXISTS " + MEETUPS_TABLE;
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
        values.put(DIFFICULTY, queryValues.get(DIFFICULTY));
        values.put(DISABILITY, queryValues.get(DISABILITY));
        database.insert(PROFILES_TABLE, null, values);
        database.close();
    }

    public int updateProfile(HashMap<String, String> queryValues)
    {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USERNAME, queryValues.get(USERNAME));
        values.put(PASSWORD, queryValues.get(PROFILE_ID));
        values.put(DIFFICULTY, queryValues.get(DIFFICULTY));
        values.put(DISABILITY, queryValues.get(DISABILITY));
        // update(TableName, ContentValueForTable, WhereClause, ArgumentForWhereClause)
        return database.update(PROFILES_TABLE, values,
                PROFILE_ID + " = ?", new String[] { queryValues.get(PROFILE_ID) });
    }

    public void deleteProfile(String id)
    {
        SQLiteDatabase database = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM " + PROFILES_TABLE + " WHERE " + PROFILE_ID + "='" + id + "'";
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
                profileMap.put(DIFFICULTY, cursor.getString(3));
                profileMap.put(DISABILITY, cursor.getString(4));
                profileArrayList.add(profileMap);
            } while (cursor.moveToNext());
        }

        return profileArrayList;
    }

    public HashMap<String, String> getProfileById(String id)
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
                profileMap.put(DIFFICULTY, cursor.getString(3));
                profileMap.put(DISABILITY, cursor.getString(4));
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

    public HashMap<String, String> getProfileByUserAndToken(String user, String token)
    {
        HashMap<String, String> profileMap = new HashMap<String, String>();
        SQLiteDatabase database = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + PROFILES_TABLE + " WHERE " + USERNAME + "='" + user + "'" + " AND " + PASSWORD + "='" + token + "'";
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                profileMap.put(PROFILE_ID, cursor.getString(0));
                profileMap.put(USERNAME, cursor.getString(1));
                profileMap.put(PASSWORD, cursor.getString(2));
                profileMap.put(DIFFICULTY, cursor.getString(3));
                profileMap.put(DISABILITY, cursor.getString(4));


            } while (cursor.moveToNext());
        }
        return profileMap;
    }

    public void createMeetup(HashMap<String, String> queryValues)
    {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_ID, queryValues.get(USER_ID));
        values.put(NOTE, queryValues.get(NOTE));
        values.put(EXERCISE, queryValues.get(EXERCISE));
        values.put(LOCATION, queryValues.get(LOCATION));
        values.put(DATE, queryValues.get(DATE));
        values.put(PEOPLE, queryValues.get(PEOPLE));
        database.insert(MEETUPS_TABLE, null, values);
        database.close();
    }

    public int updateMeetup(final String note, final String userId, HashMap<String, String> queryValues)
    {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_ID, queryValues.get(USER_ID));
        values.put(NOTE, queryValues.get(NOTE));
        values.put(EXERCISE, queryValues.get(EXERCISE));
        values.put(LOCATION, queryValues.get(LOCATION));
        values.put(DATE, queryValues.get(DATE));
        values.put(PEOPLE, queryValues.get(PEOPLE));
        // update(TableName, ContentValueForTable, WhereClause, ArgumentForWhereClause)
        return database.update(MEETUPS_TABLE, values,
                NOTE + " = '" + note + "' AND " + USER_ID + " = " + userId, null);
    }

    public void deleteMeetup(String note)
    {
        SQLiteDatabase database = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM " + MEETUPS_TABLE + " WHERE " + NOTE + "='" + note + "'";
        database.execSQL(deleteQuery);
    }

    // get every workout in the table
    public ArrayList<HashMap<String, String>> getAllMeetups()
    {
        ArrayList<HashMap<String, String>> workoutList;
        workoutList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT * FROM " + MEETUPS_TABLE + " ORDER BY " + USER_ID + " ASC";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> workoutMap = new HashMap<String, String>();
                workoutMap.put(USER_ID, cursor.getString(0));
                workoutMap.put(NOTE, cursor.getString(1));
                workoutMap.put(EXERCISE, cursor.getString(2));
                workoutMap.put(LOCATION, cursor.getString(3));
                workoutMap.put(DATE, cursor.getString(4));
                workoutMap.put(PEOPLE, cursor.getString(5));
                workoutList.add(workoutMap);
            } while (cursor.moveToNext());
        }
        return workoutList;
    }

    // get every workout for a specific userid
    public ArrayList<HashMap<String, String>> getAllMeetups(final String userId)
    {
        ArrayList<HashMap<String, String>> workoutList;
        workoutList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT * FROM " + MEETUPS_TABLE + " WHERE " + USER_ID + " ='" + userId + "' ORDER BY " + USER_ID + " ASC";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> workoutMap = new HashMap<String, String>();
                workoutMap.put(USER_ID, cursor.getString(0));
                workoutMap.put(NOTE, cursor.getString(1));
                workoutMap.put(EXERCISE, cursor.getString(2));
                workoutMap.put(LOCATION, cursor.getString(3));
                workoutMap.put(DATE, cursor.getString(4));
                workoutMap.put(PEOPLE, cursor.getString(5));
                workoutList.add(workoutMap);
            } while (cursor.moveToNext());
        }
        return workoutList;
    }

    // assumes all meetup notes are uniquely named
    public HashMap<String, String> getMeetupInfo(final String note, final String userId)
    {
        HashMap<String, String> workoutMap = new HashMap<String, String>();
        SQLiteDatabase database = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + MEETUPS_TABLE + " WHERE " + NOTE + "='" + note + "'" + " AND " + USER_ID + " = " + userId;
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                workoutMap.put(USER_ID, cursor.getString(0));
                workoutMap.put(NOTE, cursor.getString(1));
                workoutMap.put(EXERCISE, cursor.getString(2));
                workoutMap.put(LOCATION, cursor.getString(3));
                workoutMap.put(DATE, cursor.getString(4));
                workoutMap.put(PEOPLE, cursor.getString(5));
            } while (cursor.moveToNext());
        }
        return workoutMap;
    }
} // DBAdapter class




