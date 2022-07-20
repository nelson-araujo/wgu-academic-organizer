package com.nelsonaraujo.academicorganizer.Models;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Database query and functionality. Only the {@link AppProvider} class is able to access it.
 */
public class AppDatabase extends SQLiteOpenHelper {
    private static final String TAG = "AppDatabase"; // For terminal logging

    private static final String DATABASE_NAME = "AcademicOrganizer.db";
    private static final int DATABASE_VERSION = 1;

    /**
     * Create the application database and tables if it doesn't exist.
     * @param db Application database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlTermQuery;
        String sqlInstructorQuery;
        String sqlCourseQuery;
        String sqlAssessmentQuery;

        // Create instructor table query
        sqlTermQuery = "CREATE TABLE " + TermContract.TABLE_NAME + "("
                + TermContract.Columns._ID + " INTEGER PRIMARY KEY NOT NULL,"
                + TermContract.Columns.TITLE + " TEXT NOT NULL,"
                + TermContract.Columns.START + " INTEGER,"
                + "'" + TermContract.Columns.END+ "'" + " INTEGER"
                +");";

        // Create instructor table query
        sqlInstructorQuery = "CREATE TABLE " + InstructorContract.TABLE_NAME + "("
                    + InstructorContract.Columns._ID + " INTEGER PRIMARY KEY NOT NULL, "
                    + InstructorContract.Columns.NAME + " TEXT NOT NULL, "
                    + InstructorContract.Columns.PHONE + " INTEGER, "
                    + InstructorContract.Columns.EMAIL + " INTEGER"
                    +");";

        // Create course table query
        sqlCourseQuery = "CREATE TABLE " + CourseContract.TABLE_NAME + "("
                + CourseContract.Columns._ID + " INTEGER PRIMARY KEY NOT NULL, "
                + CourseContract.Columns.TITLE + " TEXT NOT NULL, "
                + CourseContract.Columns.START + " INTEGER, "
                + "'" + CourseContract.Columns.END + "'" + " INTEGER, "
                + CourseContract.Columns.STATUS + " TEXT, "
                + CourseContract.Columns.NOTE + " TEXT, "
                + CourseContract.Columns.TERM_ID + " INTEGER NOT NULL, "
                + CourseContract.Columns.INSTRUCTOR_ID + " INTEGER"
                +");";

        // Create assessment table query
        sqlAssessmentQuery = "CREATE TABLE " + AssessmentContract.TABLE_NAME + "("
                + AssessmentContract.Columns._ID + " INTEGER PRIMARY KEY NOT NULL, "
                + AssessmentContract.Columns.TITLE + " TEXT NOT NULL, "
                + AssessmentContract.Columns.START + " INTEGER, "
                + "'" + AssessmentContract.Columns.END + "'" + " INTEGER, "
                + AssessmentContract.Columns.CONTENT + " TEXT, "
                + AssessmentContract.Columns.COURSE_ID + " INTEGER NOT NULL"
                +");";

        db.execSQL(sqlTermQuery);
        db.execSQL(sqlInstructorQuery);
        db.execSQL(sqlCourseQuery);
        db.execSQL(sqlAssessmentQuery);
    }

    /**
     * SQLite3 database refactoring. To be used to identify if the existing database needs to be upgraded.
     * @param sqLiteDatabase Database to be upgraded.
     * @param oldVersion original database version.
     * @param newVersion new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        switch(oldVersion) {
            case 1:
                // Upgrade logic from version 1
                break;
            default:
                throw new IllegalStateException("onUpgrade() unknown newVersion: " + newVersion);
        }

    }

    // AppDatabase instance
    private static AppDatabase instance = null;

    /**
     * Constructor for the AppDatabase class.
     * @param context Context
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
}
