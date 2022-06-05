package com.nelsonaraujo.academicorganizer.Controllers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.nelsonaraujo.academicorganizer.Models.TermContract;
import com.nelsonaraujo.academicorganizer.R;

public class Terms extends AppCompatActivity {
    private static final String TAG = "AcademicOrganizer"; // For terminal logging

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.terms);



        // ----------------------------------------------------------------------------------------
        // Term testing todo: remove
        Log.d(TAG, "onCreate: ----- TERM RECORDS -----");

        // Get the content resolver
        ContentResolver contentResolver = getContentResolver();

        // ADD RECORD
//        int termIdToUpdate = 2;
//        String termName = "Term " + termIdToUpdate;
//        Log.d(TAG, "onCreate: Add " + termName);
//        ContentValues values = new ContentValues();
//            values.put(TermContract.Columns.TERM_TITLE, termName);
//            values.put(TermContract.Columns.TERM_START, "200" + termIdToUpdate + "-01-01");
//            values.put(TermContract.Columns.TERM_END,"200" + (termIdToUpdate+1) + "-01-01");
//        Uri uri = contentResolver.insert(TermContract.CONTENT_URI, values);


        // What to show
        String [] projection = {    TermContract.Columns._ID,
                                    TermContract.Columns.TERM_TITLE,
                                    TermContract.Columns.TERM_START,
                                    TermContract.Columns.TERM_END};

        // Query the content resolver
        Cursor cursor = contentResolver.query(TermContract.CONTENT_URI, // Get all records
//        Cursor cursor = contentResolver.query(TermContract.buildTermUri(3), // Get a specific record
                projection,
                null,
                null,
                TermContract.Columns.TERM_TITLE);

        // Display records returned
        if(cursor != null){
            while(cursor.moveToNext()){
                for( int i=0 ; i < cursor.getColumnCount() ; i++){
                    Log.d(TAG, "onCreate: " + cursor.getColumnName(i) + ": " + cursor.getString(i));
                }
                Log.d(TAG, "onCreate: ------------");
            }
            cursor.close();
        }

        // UPDATE RECORD
//        int termIdToUpdate = 2;
//        // String newTermName = "Term 00";
//        String newTermName = "Term " + termIdToUpdate;
//        Log.d(TAG, "onCreate: Update " + newTermName);
//        ContentValues values = new ContentValues();
//            values.put(TermContract.Columns.TERM_TITLE, newTermName);
//            values.put(TermContract.Columns.TERM_START, "200" + termIdToUpdate + "-01-01");
//            values.put(TermContract.Columns.TERM_END,"200" + (termIdToUpdate+1) + "-01-01");
//        int recordsUpdated = contentResolver.update(TermContract.buildTermUri(termIdToUpdate), values,null,null);
//        Log.d(TAG, "onCreate: Total records updated: " + recordsUpdated);
//
//        // Query the content resolver
//        cursor = contentResolver.query(TermContract.buildTermUri(termIdToUpdate), // Get a specific record
//                projection,
//                null,
//                null,
//                TermContract.Columns.TERM_TITLE);
//
//        // Display record returned
//        if(cursor != null){
//            while(cursor.moveToNext()){
//                for( int i=0 ; i < cursor.getColumnCount() ; i++){
//                    Log.d(TAG, "onCreate: " + cursor.getColumnName(i) + ": " + cursor.getString(i));
//                }
//                Log.d(TAG, "onCreate: ------------");
//            }
//            cursor.close();
//        }

        // UPDATE MULTIPLE RECORDS
//         ContentValues values = new ContentValues();
//            values.put(TermContract.Columns.TERM_TITLE, "Term 99");
//        String selection = TermContract.Columns.TERM_TITLE + "=" + "'Term 00'";
//        int recordsUpdated = contentResolver.update(TermContract.CONTENT_URI, values,selection,null);
//        Log.d(TAG, "onCreate: Total records updated: " + recordsUpdated);


        // DELETE RECORD
//        int recordsDeleted = contentResolver.delete(TermContract.buildTermUri(2),null,null);
//        Log.d(TAG, "onCreate: Records deleted: " + recordsDeleted);



        // END Term testing todo: remove
        // ----------------------------------------------------------------------------------------
    }
}