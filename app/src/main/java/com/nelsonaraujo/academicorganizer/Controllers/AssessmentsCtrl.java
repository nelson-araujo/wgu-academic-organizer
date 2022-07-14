package com.nelsonaraujo.academicorganizer.Controllers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nelsonaraujo.academicorganizer.Models.Assessment;
import com.nelsonaraujo.academicorganizer.Models.AssessmentContract;
import com.nelsonaraujo.academicorganizer.Models.TermContract;
import com.nelsonaraujo.academicorganizer.R;

import java.security.InvalidParameterException;
import java.sql.Date;

public class AssessmentsCtrl extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, AssessmentsRvClickListener.OnAssessmentsRvClickListener{
    private static final String TAG = "AssessmentsCtrl"; // For terminal logging

    public static final int LOADER_ID = 0; // Loader id to identify the loader if multiple are used.

    private AssessmentsRvAdapter mAdapter; // adapter reference
    private Cursor mCursor;
    private String sortOrder = AssessmentContract.Columns.TITLE; // DB sort order
    private String whereSelection = null; // DB Where selection

    // Constructor
    public AssessmentsCtrl(){
        // empty constructor.
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.assessments);

        LoaderManager.getInstance(this).initLoader(LOADER_ID, null, this);

        // Create adapter and pass data
        mAdapter = new AssessmentsRvAdapter(null);
        RecyclerView assessmentsRv = (RecyclerView) findViewById(R.id.assessmentsAssessmentsRv);
        assessmentsRv.setHasFixedSize(true);
        assessmentsRv.setLayoutManager(new LinearLayoutManager(this));
        assessmentsRv.addOnItemTouchListener(new AssessmentsRvClickListener(this, assessmentsRv, this));
        assessmentsRv.setAdapter(mAdapter);

        // Add floating action button
        FloatingActionButton addFab = findViewById(R.id.assessmentsAddFab);
        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addAssessment(null); // Pass null to create a new entry.
            }
        });
    }

    public void addAssessment(Assessment assessment){
        Intent intent = new Intent(AssessmentsCtrl.this, AssessmentAddEditCtrl.class);
        if(assessment != null) { // Edit
            intent.putExtra(Assessment.class.getSimpleName(), assessment);
            startActivity(intent);
        } else { // New
            startActivity(intent);
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {AssessmentContract.Columns._ID,
                AssessmentContract.Columns.TITLE,
                AssessmentContract.Columns.START,
                AssessmentContract.Columns.END,
                AssessmentContract.Columns.CONTENT,
                AssessmentContract.Columns.COURSE_ID};

        switch (id) {
            case LOADER_ID:
                ContentResolver contentResolver = getContentResolver();
                return new CursorLoader(this, AssessmentContract.CONTENT_URI,projection,whereSelection,null, sortOrder);

            default:
                throw new InvalidParameterException(TAG + ".onCreateLoader called with invalid loader id " + id);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
        int count = mAdapter.getItemCount();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onAssessmentClick(View view, int position) {
        // Get the content resolver
        ContentResolver contentResolver = getContentResolver();

        // Setup projection
        String[] projection = {AssessmentContract.Columns._ID,
                AssessmentContract.Columns.TITLE,
                AssessmentContract.Columns.START,
                AssessmentContract.Columns.END,
                AssessmentContract.Columns.CONTENT,
                AssessmentContract.Columns.COURSE_ID};

        // Get a specific record
        long recordId = getRecordId(position);
        mCursor = contentResolver.query(AssessmentContract.buildAssessmentUri(recordId), projection, whereSelection, null, sortOrder);

        // Get Assessment
        Assessment selection = new Assessment(0,null,null,null,null,null);
        if(mCursor != null){
            while(mCursor.moveToNext()){ // todo: Why does assigning to selection return a -1 when outside loop? -1 mean column not found.
                // Populate selection
                selection = new Assessment(mCursor.getLong(mCursor.getColumnIndexOrThrow(AssessmentContract.Columns._ID)),
                        mCursor.getString(mCursor.getColumnIndexOrThrow(AssessmentContract.Columns.TITLE)),
                        mCursor.getString(mCursor.getColumnIndexOrThrow(AssessmentContract.Columns.START)),
                        mCursor.getString(mCursor.getColumnIndexOrThrow(AssessmentContract.Columns.END)),
//                        mCursor.getLong(mCursor.getColumnIndexOrThrow(AssessmentContract.Columns.START)), // todo: remove
//                        mCursor.getLong(mCursor.getColumnIndexOrThrow(AssessmentContract.Columns.END)), // todo: remove
                        mCursor.getString(mCursor.getColumnIndexOrThrow(AssessmentContract.Columns.CONTENT)),
                        mCursor.getInt(mCursor.getColumnIndexOrThrow(AssessmentContract.Columns.COURSE_ID)));
            }
        }

        // Display selection
        Intent intent = new Intent(AssessmentsCtrl.this, AssessmentCtrl.class);
        intent.putExtra(Assessment.class.getSimpleName(), selection);
        startActivity(intent);
    }

    /**
     * Calculate the actual id of the row selected.
     * @param position RV position tapped.
     * @return id number of entry.
     */
    private long getRecordId(int position){
        // Get the content resolver
        ContentResolver contentResolver = getContentResolver();

        String[] projection = {AssessmentContract.Columns._ID};
        Cursor cursor = contentResolver.query(AssessmentContract.CONTENT_URI, projection, whereSelection, null, sortOrder);

        if( (cursor == null) || (cursor.getCount()==0) ){ // If no records are returned
            return -1; // Unable to determine position
        } else {
            if (!cursor.moveToPosition(position)) {
                throw new IllegalStateException("Unable to move cursor to position " + position);
            }

            // move to position
            cursor.moveToPosition(position);
            return cursor.getLong(cursor.getColumnIndexOrThrow(AssessmentContract.Columns._ID));
        }
    }
}