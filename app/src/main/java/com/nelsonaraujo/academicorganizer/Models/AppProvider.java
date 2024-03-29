package com.nelsonaraujo.academicorganizer.Models;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Provider for the Academic Organizer. This class provides access to the {@link AppDatabase}
 */
public class AppProvider extends ContentProvider {
    private static final String TAG = "AppProvider";

    private AppDatabase mOpenHelper;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    static final String CONTENT_AUTHORITY = "com.nelsonaraujo.academicorganizer.provider";
    public static final Uri CONTENT_AUTHORITY_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private static final int TERM = 100;
    private static final int TERM_ID = 101;
    private static final int INSTRUCTOR = 200;
    private static final int INSTRUCTOR_ID = 201;
    private static final int COURSE = 300;
    private static final int COURSE_ID = 301;
    private static final int ASSESSMENT = 400;
    private static final int ASSESSMENT_ID = 401;

    private static UriMatcher buildUriMatcher(){
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        // Term
        matcher.addURI(CONTENT_AUTHORITY, TermContract.TABLE_NAME, TERM); // No id, return table
        matcher.addURI(CONTENT_AUTHORITY, TermContract.TABLE_NAME + "/#",TERM_ID);

        // Instructor
        matcher.addURI(CONTENT_AUTHORITY, InstructorContract.TABLE_NAME, INSTRUCTOR); // No id, return table
        matcher.addURI(CONTENT_AUTHORITY, InstructorContract.TABLE_NAME + "/#",INSTRUCTOR_ID);

        // Courses
        matcher.addURI(CONTENT_AUTHORITY, CourseContract.TABLE_NAME, COURSE); // No id, return table
        matcher.addURI(CONTENT_AUTHORITY, CourseContract.TABLE_NAME + "/#",COURSE_ID);

        // Assessment
        matcher.addURI(CONTENT_AUTHORITY, AssessmentContract.TABLE_NAME, ASSESSMENT); // No id, return table
        matcher.addURI(CONTENT_AUTHORITY, AssessmentContract.TABLE_NAME + "/#",ASSESSMENT_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = AppDatabase.getInstance(getContext()); // Use the getInstance() as the class is a singleton.
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final int match = sUriMatcher.match(uri);

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        switch(match){
            case TERM:
                queryBuilder.setTables(TermContract.TABLE_NAME);
                break;

            case TERM_ID:
                queryBuilder.setTables(TermContract.TABLE_NAME);
                long termId = TermContract.getTermId(uri);
                queryBuilder.appendWhere(TermContract.Columns._ID + " = " + termId);
                break;

            case INSTRUCTOR:
                queryBuilder.setTables(InstructorContract.TABLE_NAME);
                break;

            case INSTRUCTOR_ID:
                queryBuilder.setTables(InstructorContract.TABLE_NAME);
                long instructorId = InstructorContract.getInstructorId(uri);
                queryBuilder.appendWhere(InstructorContract.Columns._ID + " = " + instructorId);
                break;

            case COURSE:
                queryBuilder.setTables(CourseContract.TABLE_NAME);
                break;

            case COURSE_ID:
                queryBuilder.setTables(CourseContract.TABLE_NAME);
                long courseId = CourseContract.getCourseId(uri);
                queryBuilder.appendWhere(CourseContract.Columns._ID + " = " + courseId);
                break;

            case ASSESSMENT:
                queryBuilder.setTables(AssessmentContract.TABLE_NAME);
                break;

            case ASSESSMENT_ID:
                queryBuilder.setTables(AssessmentContract.TABLE_NAME);
                long assessmentId = AssessmentContract.getAssessmentId(uri);
                queryBuilder.appendWhere(AssessmentContract.Columns._ID + " = " + assessmentId);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null,null, sortOrder);

        cursor.setNotificationUri(getContext().getContentResolver(),uri);

        return cursor;
    }

    /**
     * Get the MIME type the app content provider returns.
     * @param uri Content provider URI.
     * @return MIME type the uri returns.
     */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);

        // Match the correct uri type
        switch(match){
            case TERM:
                return TermContract.CONTENT_TYPE;

            case TERM_ID:
                return TermContract.CONTENT_ITEM_TYPE;

            case INSTRUCTOR:
                return InstructorContract.CONTENT_TYPE;

            case INSTRUCTOR_ID:
                return InstructorContract.CONTENT_ITEM_TYPE;

            case COURSE:
                return CourseContract.CONTENT_TYPE;

            case COURSE_ID:
                return CourseContract.CONTENT_ITEM_TYPE;

            case ASSESSMENT:
                return AssessmentContract.CONTENT_TYPE;

            case ASSESSMENT_ID:
                return AssessmentContract.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);

        final SQLiteDatabase db;

        Uri returnUri;
        long recordId;

        switch(match){
            case TERM:
                db = mOpenHelper.getWritableDatabase();
                recordId = db.insert(TermContract.TABLE_NAME,null,contentValues);
                if(recordId >= 0){
                    returnUri = TermContract.buildTermUri(recordId);
                } else {
                    throw new android.database.SQLException("Error inserting into: " + uri.toString());
                }
                break;

            case INSTRUCTOR:
                db = mOpenHelper.getWritableDatabase();
                recordId = db.insert(InstructorContract.TABLE_NAME,null,contentValues);
                if(recordId >= 0){
                    returnUri = InstructorContract.buildInstructorUri(recordId);
                } else {
                    throw new android.database.SQLException("Error inserting into: " + uri.toString());
                }
                break;

            case COURSE:
                db = mOpenHelper.getWritableDatabase();
                recordId = db.insert(CourseContract.TABLE_NAME,null,contentValues);
                if(recordId >= 0){
                    returnUri = CourseContract.buildCourseUri(recordId);
                } else {
                    throw new android.database.SQLException("Error inserting into: " + uri.toString());
                }
                break;

            case ASSESSMENT:
                db = mOpenHelper.getWritableDatabase();
                recordId = db.insert(AssessmentContract.TABLE_NAME,null,contentValues);
                if(recordId >= 0){
                    returnUri = AssessmentContract.buildAssessmentUri(recordId);
                } else {
                    throw new android.database.SQLException("Error inserting into: " + uri.toString());
                }
                break;

            default:
                throw new IllegalArgumentException("Unknown uri: " + uri);
        }

        // Check if a record was inserted, if so notify of change.
        if(recordId >= 0){
            getContext().getContentResolver().notifyChange(uri,null);
        }

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);

        final SQLiteDatabase db;
        int totalRecordsDeleted;

        String selectionCriteria;

        switch(match){
            case TERM:
                db = mOpenHelper.getWritableDatabase();
                totalRecordsDeleted = db.delete(TermContract.TABLE_NAME,selection,selectionArgs);
                break;

            case TERM_ID:
                db = mOpenHelper.getWritableDatabase();
                long termId = TermContract.getTermId(uri);

                // WHERE statement
                selectionCriteria = TermContract.Columns._ID + "=" + termId;
                if((selection != null) && (selection.length() > 0)){
                    selectionCriteria += " AND (" + selection + ")";
                }

                totalRecordsDeleted = db.delete(TermContract.TABLE_NAME,selectionCriteria,selectionArgs);
                break;

            case INSTRUCTOR:
                db = mOpenHelper.getWritableDatabase();
                totalRecordsDeleted = db.delete(InstructorContract.TABLE_NAME,selection,selectionArgs);
                break;

            case INSTRUCTOR_ID:
                db = mOpenHelper.getWritableDatabase();
                long instructorId = InstructorContract.getInstructorId(uri);

                // WHERE statement
                selectionCriteria = InstructorContract.Columns._ID + "=" + instructorId;
                if((selection != null) && (selection.length() > 0)){
                    selectionCriteria += " AND (" + selection + ")";
                }

                totalRecordsDeleted = db.delete(InstructorContract.TABLE_NAME,selectionCriteria,selectionArgs);
                break;

            case COURSE:
                db = mOpenHelper.getWritableDatabase();
                totalRecordsDeleted = db.delete(CourseContract.TABLE_NAME,selection,selectionArgs);
                break;

            case COURSE_ID:
                db = mOpenHelper.getWritableDatabase();
                long courseId = CourseContract.getCourseId(uri);

                // WHERE statement
                selectionCriteria = CourseContract.Columns._ID + "=" + courseId;
                if((selection != null) && (selection.length() > 0)){
                    selectionCriteria += " AND (" + selection + ")";
                }

                totalRecordsDeleted = db.delete(CourseContract.TABLE_NAME,selectionCriteria,selectionArgs);
                break;

            case ASSESSMENT:
                db = mOpenHelper.getWritableDatabase();
                totalRecordsDeleted = db.delete(AssessmentContract.TABLE_NAME,selection,selectionArgs);
                break;

            case ASSESSMENT_ID:
                db = mOpenHelper.getWritableDatabase();
                long assessmentId = AssessmentContract.getAssessmentId(uri);

                // WHERE statement
                selectionCriteria = AssessmentContract.Columns._ID + "=" + assessmentId;
                if((selection != null) && (selection.length() > 0)){
                    selectionCriteria += " AND (" + selection + ")";
                }

                totalRecordsDeleted = db.delete(AssessmentContract.TABLE_NAME,selectionCriteria,selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown uri: " + uri);
        }

        // Check if a record was deleted, if so notify of change.
        if(totalRecordsDeleted > 0){
            getContext().getContentResolver().notifyChange(uri,null);
        }

        return totalRecordsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);

        final SQLiteDatabase db;
        int totalRecordsUpdated;

        String selectionCriteria;

        switch(match){
            case TERM:
                db = mOpenHelper.getWritableDatabase();
                totalRecordsUpdated = db.update(TermContract.TABLE_NAME,contentValues,selection,selectionArgs);
                break;

            case TERM_ID:
                db = mOpenHelper.getWritableDatabase();
                long termId = TermContract.getTermId(uri);

                // WHERE statement
                selectionCriteria = TermContract.Columns._ID + "=" + termId;
                if((selection != null) && (selection.length() > 0)){
                    selectionCriteria += " AND (" + selection + ")";
                }

                totalRecordsUpdated = db.update(TermContract.TABLE_NAME,contentValues,selectionCriteria,selectionArgs);
                break;

            case INSTRUCTOR:
                db = mOpenHelper.getWritableDatabase();
                totalRecordsUpdated = db.update(InstructorContract.TABLE_NAME,contentValues,selection,selectionArgs);
                break;

            case INSTRUCTOR_ID:
                db = mOpenHelper.getWritableDatabase();
                long instructorId = InstructorContract.getInstructorId(uri);

                // WHERE statement
                selectionCriteria = InstructorContract.Columns._ID + "=" + instructorId;
                if((selection != null) && (selection.length() > 0)){
                    selectionCriteria += " AND (" + selection + ")";
                }

                totalRecordsUpdated = db.update(InstructorContract.TABLE_NAME,contentValues,selectionCriteria,selectionArgs);
                break;

            case COURSE:
                db = mOpenHelper.getWritableDatabase();
                totalRecordsUpdated = db.update(CourseContract.TABLE_NAME,contentValues,selection,selectionArgs);
                break;

            case COURSE_ID:
                db = mOpenHelper.getWritableDatabase();
                long courseId = CourseContract.getCourseId(uri);

                // WHERE statement
                selectionCriteria = CourseContract.Columns._ID + "=" + courseId;
                if((selection != null) && (selection.length() > 0)){
                    selectionCriteria += " AND (" + selection + ")";
                }

                totalRecordsUpdated = db.update(CourseContract.TABLE_NAME,contentValues,selectionCriteria,selectionArgs);
                break;

            case ASSESSMENT:
                db = mOpenHelper.getWritableDatabase();
                totalRecordsUpdated = db.update(AssessmentContract.TABLE_NAME,contentValues,selection,selectionArgs);
                break;

            case ASSESSMENT_ID:
                db = mOpenHelper.getWritableDatabase();
                long assessmentId = AssessmentContract.getAssessmentId(uri);

                // WHERE statement
                selectionCriteria = AssessmentContract.Columns._ID + "=" + assessmentId;
                if((selection != null) && (selection.length() > 0)){
                    selectionCriteria += " AND (" + selection + ")";
                }

                totalRecordsUpdated = db.update(AssessmentContract.TABLE_NAME,contentValues,selectionCriteria,selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown uri: " + uri);
        }

        // Check if a record was updated, if so notify of change.
        if(totalRecordsUpdated > 0){
            getContext().getContentResolver().notifyChange(uri,null);
        }

        return totalRecordsUpdated;
    }
}