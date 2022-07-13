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
import com.nelsonaraujo.academicorganizer.Models.Course;
import com.nelsonaraujo.academicorganizer.Models.CourseContract;
import com.nelsonaraujo.academicorganizer.Models.InstructorContract;
import com.nelsonaraujo.academicorganizer.Models.TermContract;
import com.nelsonaraujo.academicorganizer.R;

public class CourseCtrl extends AppCompatActivity {
    private static final String TAG = "CourseCtrl"; // For terminal logging

    Cursor mCursor;

    private TextView mTitleTv;
    private TextView mStartTv;
    private TextView mEndTv;
    private TextView mTermTv;
    private TextView mStatusTv;
    private TextView mInstructorNameTv;
    private TextView mInstructorEmailTv;
    private TextView mInstructorPhoneTv;
    private TextView mNoteTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course);

        // Link variables to layout fields
        mTitleTv = findViewById(R.id.courseTitleTv);
        mStartTv = findViewById(R.id.courseStartTv);
        mEndTv = findViewById(R.id.courseEndTv);
        mTermTv = findViewById(R.id.courseTermTv);
        mStatusTv = findViewById(R.id.courseStatusTv);
        mInstructorNameTv = findViewById(R.id.courseInstructorNameTv);
        mInstructorEmailTv = findViewById(R.id.courseInstructorEmailTv);
        mInstructorPhoneTv = findViewById(R.id.courseInstructorPhoneTv);
        mNoteTv = findViewById(R.id.courseNoteTv);

        // Get the arguments from the bundle.
        Bundle arguments = getIntent().getExtras();

        // Create object
        final Course course;
        course = (Course) arguments.getSerializable(Course.class.getSimpleName());


        // Get Term name
        ContentResolver contentResolver = getContentResolver(); // get content resolver.
        String[] projection;
        projection = new String[]{TermContract.Columns.TITLE}; // setup projection
        mCursor = contentResolver.query(TermContract.buildTermUri(course.getTermId()), projection, null,null,null);
        String termName = "Unknown";
        if(mCursor != null){
            while(mCursor.moveToNext()){ // todo: Why does assigning to selectedTerm return a -1 when outside loop? -1 mean column not found.
                termName = mCursor.getString(mCursor.getColumnIndexOrThrow(TermContract.Columns.TITLE));
            }
        }

        // Get instructor details
        contentResolver = getContentResolver(); // get content resolver.
        projection = new String[]{InstructorContract.Columns.NAME, InstructorContract.Columns.EMAIL, InstructorContract.Columns.PHONE}; // setup projection
        mCursor = contentResolver.query(InstructorContract.buildInstructorUri(course.getInstructorId()), projection, null,null,null);
        String instructorName="Unknown", instructorEmail="Unknown", instructorPhone="Unknown";
        if(mCursor != null){
            while(mCursor.moveToNext()){ // todo: Why does assigning to selectedTerm return a -1 when outside loop? -1 mean column not found.
                instructorName = mCursor.getString(mCursor.getColumnIndexOrThrow(InstructorContract.Columns.NAME));
                instructorEmail = mCursor.getString(mCursor.getColumnIndexOrThrow(InstructorContract.Columns.EMAIL));
                instructorPhone = mCursor.getString(mCursor.getColumnIndexOrThrow(InstructorContract.Columns.PHONE));
            }
        }

        // Set layout text views
        mTitleTv.setText(course.getTitle());
        mStartTv.setText(course.getStart().toString());
        mEndTv.setText(course.getEnd().toString());
        mTermTv.setText(termName);
        mStatusTv.setText(course.getStatus());
        mInstructorNameTv.setText(instructorName);
        mInstructorEmailTv.setText(instructorEmail);
        mInstructorPhoneTv.setText(instructorPhone);
        mNoteTv.setText(course.getNote());

        // Setup edit fab
        FloatingActionButton editFab = findViewById(R.id.courseEditFab);
        editFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onEditFabClick(course);
            }
        });

        // Setup delete fab
        FloatingActionButton deleteFab = findViewById(R.id.courseDeleteFab);
        deleteFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDeleteFabClick(course);
            }
        });
    }

    private void onEditFabClick(Course course){
        // Display
        Intent intent = new Intent(this, CourseAddEditCtrl.class);
        intent.putExtra(Course.class.getSimpleName(), course);
        startActivity(intent);
    }

    private void onDeleteFabClick(Course course){
        Log.d(TAG, "onDeleteFabClick: " + CourseContract.buildCourseUri(course.getId()));
        getContentResolver().delete(CourseContract.buildCourseUri(course.getId()), null, null);
        finish();
    }
}
