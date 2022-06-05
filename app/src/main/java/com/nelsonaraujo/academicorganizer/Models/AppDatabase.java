package com.nelsonaraujo.academicorganizer.Models;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Database query and functionality. Only the {@link AppProvider} class is able to access it.
 */
public class AppDatabase extends SQLiteOpenHelper {
    private static final String TAG = "AppDatabase"; // For terminal logging

    private static final String DATABASE_NAME = "AcademicOrganizer.db";
    private static final int DATABASE_VERSION = 1;

    // AppDatabase instance
    private static AppDatabase instance = null;

    /**
     * Constructor for the AppDatabase class.
     * @param context
     */
    private AppDatabase(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Get instance of the database helper object.
     * @param context Content provider context.
     * @return SQLite database helper object.
     */
    public static AppDatabase getInstance(Context context){
        if(instance == null){
            instance = new AppDatabase(context);
        }

        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlQuery;
        sqlQuery = "CREATE TABLE " + TermContract.TABLE_NAME + "("
                    + TermContract.Columns._ID + " INTEGER PRIMARY KEY NOT NULL,"
                    + TermContract.Columns.TERM_TITLE + " TEXT NOT NULL,"
                    + TermContract.Columns.TERM_START + " INTEGER,"
                    + TermContract.Columns.TERM_END + " INTEGER"
                +");";

        db.execSQL(sqlQuery);
        Log.d(TAG, "onCreate SQL: " + sqlQuery); // todo: remove
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade: start"); // todo: remove

        switch(oldVersion) {
            case 1:
                // Upgrade logic from version 1
                // todo: develop
                break;
            default:
                throw new IllegalStateException("onUpgrade() unknown newVersion: " + newVersion);
        }

        Log.d(TAG, "onUpgrade: end"); // todo: remove
    }
}
