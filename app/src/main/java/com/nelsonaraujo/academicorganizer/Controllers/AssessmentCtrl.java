package com.nelsonaraujo.academicorganizer.Controllers;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nelsonaraujo.academicorganizer.Models.Assessment;
import com.nelsonaraujo.academicorganizer.Models.AssessmentContract;
import com.nelsonaraujo.academicorganizer.Models.Term;
import com.nelsonaraujo.academicorganizer.Models.TermContract;
import com.nelsonaraujo.academicorganizer.R;

import java.util.Date;


public class AssessmentCtrl extends AppCompatActivity {
    private static final String TAG = "AssessmentCtrl"; // For terminal logging

    public static final int LOADER_ID = 0; // Loader id to identify the loader if multiple are used.

    Cursor mCursor;
    private Assessment mAssessment = null;

    private TextView mTitleTv;
    private TextView mStartTv;
    private TextView mEndTv;
    private TextView mContentTv;
    private TextView mCourseTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.assessment);

        // Link variables to layout fields
        mTitleTv = findViewById(R.id.assessmentTitleTv);
        mStartTv = findViewById(R.id.assessmentStartTv);
        mEndTv = findViewById(R.id.assessmentEndTv);
        mContentTv = findViewById(R.id.assessmentContentTv);
        mCourseTv = findViewById(R.id.assessmentCourseTv);

        // Get the arguments from the bundle.
        Bundle arguments = getIntent().getExtras();

        // Create object
        mAssessment = (Assessment) arguments.getSerializable(Assessment.class.getSimpleName());


        // Get Term name
//        ContentResolver contentResolver = getContentResolver(); // get content resolver.
//        String[] projection;
//        projection = new String[]{TermContract.Columns.TITLE}; // setup projection
//        mCursor = contentResolver.query(TermContract.buildTermUri(assessment.getTermId()), projection, null,null,null);
//        String termName = "Unknown";
//        if(mCursor != null){
//            while(mCursor.moveToNext()){ // todo: Why does assigning to selectedTerm return a -1 when outside loop? -1 mean column not found.
//                termName = mCursor.getString(mCursor.getColumnIndexOrThrow(TermContract.Columns.TITLE));
//            }
//        }

        // Set layout text views
        mTitleTv.setText(mAssessment.getTitle());
        mContentTv.setText(mAssessment.getContent().toString());
        mStartTv.setText(mAssessment.getStart().toString());
        mEndTv.setText(mAssessment.getEnd().toString());

        // Setup edit fab
        FloatingActionButton editFab = findViewById(R.id.assessmentEditFab);
        editFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onEditFabClick(mAssessment);
            }
        });

        // Setup delete fab
        FloatingActionButton deleteFab = findViewById(R.id.assessmentDeleteFab);
        deleteFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDeleteFabClick(mAssessment);
            }
        });
    }

    private void onEditFabClick(Assessment assessment){
        // Display
        Intent intent = new Intent(this, AssessmentAddEditCtrl.class);
        intent.putExtra(Assessment.class.getSimpleName(), assessment);
        startActivity(intent);
    }

    private void onDeleteFabClick(Assessment assessment){
        Log.d(TAG, "onDeleteFabClick: " + AssessmentContract.buildAssessmentUri(assessment.getId()));
        getContentResolver().delete(AssessmentContract.buildAssessmentUri(assessment.getId()), null, null);
        finish();
    }

    /**
     * On activity restart, when returning from edit screen, update text fields.
     */
    @Override
    public void onRestart() {
        super.onRestart();

        // Get the content resolver
        ContentResolver contentResolver = getContentResolver();

        // Setup projection
        String[] assessmentProjection = {AssessmentContract.Columns._ID,
                AssessmentContract.Columns.TITLE,
                AssessmentContract.Columns.START,
                AssessmentContract.Columns.END,
                AssessmentContract.Columns.CONTENT,
                AssessmentContract.Columns.COURSE_ID};

        // Get record
        mCursor = contentResolver.query(AssessmentContract.buildAssessmentUri(mAssessment.getId()), assessmentProjection, null, null, TermContract.Columns.TITLE);

        // Set assessment
        if(mCursor != null){
            while(mCursor.moveToNext()) { // todo: Why does assigning to selectedTerm return a -1 when outside loop? -1 mean column not found.
                // Populate assessment
                mAssessment = new Assessment(mCursor.getLong(mCursor.getColumnIndexOrThrow(AssessmentContract.Columns._ID)),
                        mCursor.getString(mCursor.getColumnIndexOrThrow(AssessmentContract.Columns.TITLE)),
                        mCursor.getString(mCursor.getColumnIndexOrThrow(AssessmentContract.Columns.START)),
                        mCursor.getString(mCursor.getColumnIndexOrThrow(AssessmentContract.Columns.END)),
                        mCursor.getString(mCursor.getColumnIndexOrThrow(AssessmentContract.Columns.CONTENT)),
                        mCursor.getInt(mCursor.getColumnIndexOrThrow(AssessmentContract.Columns.COURSE_ID)));
            }
        }

        // Set layout text views
        mTitleTv.setText(mAssessment.getTitle());
        mContentTv.setText(mAssessment.getContent());
        mStartTv.setText(mAssessment.getStart());
        mEndTv.setText(mAssessment.getEnd());

    }
}
