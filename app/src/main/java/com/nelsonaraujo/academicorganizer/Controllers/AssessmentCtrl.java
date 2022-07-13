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
import com.nelsonaraujo.academicorganizer.R;

import java.util.Date;


public class AssessmentCtrl extends AppCompatActivity {
    private static final String TAG = "AssessmentCtrl"; // For terminal logging

    Cursor mCursor;

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
        final Assessment assessment;
        assessment = (Assessment) arguments.getSerializable(Assessment.class.getSimpleName());


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
        mTitleTv.setText(assessment.getTitle());
        mContentTv.setText(assessment.getContent().toString());
//        mTermTv.setText(termName);

        // Set dates
//        Date start = new Date(assessment.getStart()));
        //        new Date(mCursor.getLong(mCursor.getColumnIndexOrThrow(AssessmentContract.Columns.START)))
        Log.d(TAG, "onCreate: start date:" + assessment.getStart()); // todo: remove
        mStartTv.setText(assessment.getStart().toString());
        mEndTv.setText(assessment.getEnd().toString());

        // Setup edit fab
        FloatingActionButton editFab = findViewById(R.id.assessmentEditFab);
        editFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onEditFabClick(assessment);
            }
        });

        // Setup delete fab
        FloatingActionButton deleteFab = findViewById(R.id.assessmentDeleteFab);
        deleteFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDeleteFabClick(assessment);
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
}
