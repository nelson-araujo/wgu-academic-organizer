package com.nelsonaraujo.academicorganizer.Controllers;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nelsonaraujo.academicorganizer.Models.Assessment;
import com.nelsonaraujo.academicorganizer.Models.AssessmentContract;
import com.nelsonaraujo.academicorganizer.Models.Course;
import com.nelsonaraujo.academicorganizer.Models.CourseContract;
import com.nelsonaraujo.academicorganizer.Models.InstructorContract;
import com.nelsonaraujo.academicorganizer.Models.Term;
import com.nelsonaraujo.academicorganizer.Models.TermContract;
import com.nelsonaraujo.academicorganizer.R;

import java.security.InvalidParameterException;

public class CourseCtrl extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,CourseAssessmentsRvClickListener.OnCourseAssessmentsRvClickListener {
    private static final String TAG = "CourseCtrl"; // For terminal logging

    public static final int LOADER_ID = 0; // Loader id to identify the loader if multiple are used.

    private TextView mTitleTv;
    private TextView mStartTv;
    private TextView mEndTv;
    private TextView mTermTv;
    private TextView mStatusTv;
    private TextView mInstructorNameTv;
    private TextView mInstructorEmailTv;
    private TextView mInstructorPhoneTv;
    private TextView mNoteTv;

    private Cursor mCursor;
    // ********** Recycle View setup start *****************************************************
    private CourseAssessmentsRvAdapter mAdapter;
    private Course mCourse = null;
    // ********** Recycle View setup end   *****************************************************

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
        mCourse = (Course) arguments.getSerializable(Course.class.getSimpleName());


        // Get Term name
        ContentResolver contentResolver = getContentResolver(); // get content resolver.
        String[] projection;
        projection = new String[]{TermContract.Columns.TITLE}; // setup projection
        mCursor = contentResolver.query(TermContract.buildTermUri(mCourse.getTermId()), projection, null,null,null);
        String termName = "Unknown";
        if(mCursor != null){
            while(mCursor.moveToNext()){ // todo: Why does assigning to selectedTerm return a -1 when outside loop? -1 mean column not found.
                termName = mCursor.getString(mCursor.getColumnIndexOrThrow(TermContract.Columns.TITLE));
            }
        }

        // Get instructor details
        contentResolver = getContentResolver(); // get content resolver.
        projection = new String[]{InstructorContract.Columns.NAME, InstructorContract.Columns.EMAIL, InstructorContract.Columns.PHONE}; // setup projection
        mCursor = contentResolver.query(InstructorContract.buildInstructorUri(mCourse.getInstructorId()), projection, null,null,null);
        String instructorName="Unknown", instructorEmail="Unknown", instructorPhone="Unknown";
        if(mCursor != null){
            while(mCursor.moveToNext()){ // todo: Why does assigning to selectedTerm return a -1 when outside loop? -1 mean column not found.
                instructorName = mCursor.getString(mCursor.getColumnIndexOrThrow(InstructorContract.Columns.NAME));
                instructorEmail = mCursor.getString(mCursor.getColumnIndexOrThrow(InstructorContract.Columns.EMAIL));
                instructorPhone = mCursor.getString(mCursor.getColumnIndexOrThrow(InstructorContract.Columns.PHONE));
            }
        }

        // Set layout text views
        mTitleTv.setText(mCourse.getTitle());
        mStartTv.setText(mCourse.getStart().toString());
        mEndTv.setText(mCourse.getEnd().toString());
        mTermTv.setText(termName);
        mStatusTv.setText(mCourse.getStatus());
        mInstructorNameTv.setText(instructorName);
        mInstructorEmailTv.setText(instructorEmail);
        mInstructorPhoneTv.setText(instructorPhone);
        mNoteTv.setText(mCourse.getNote());

        // Setup edit fab
        FloatingActionButton editFab = findViewById(R.id.courseEditFab);
        editFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onEditFabClick(mCourse);
            }
        });

        // Setup delete fab
        FloatingActionButton deleteFab = findViewById(R.id.courseDeleteFab);
        deleteFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDeleteFabClick(mCourse);
            }
        });

        // Setup add course button
        ImageView addCourseIv = findViewById(R.id.courseAddAssessmentIv);
        addCourseIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAddAssessmentClick();
            }
        });

        // ********** Recycle View setup start *****************************************************
        LoaderManager.getInstance(this).initLoader(LOADER_ID, null, this);

        // Create adapter and pass data
        mAdapter = new CourseAssessmentsRvAdapter(null);
        RecyclerView coursesRv = (RecyclerView) findViewById(R.id.courseAssessmentsRv);
        coursesRv.setHasFixedSize(true);
        coursesRv.setLayoutManager(new LinearLayoutManager(this));
        coursesRv.addOnItemTouchListener(new CourseAssessmentsRvClickListener(this, coursesRv, this));
        coursesRv.setAdapter(mAdapter);
        // ********** Recycle View setup end   *****************************************************
    }

    /**
     * Action to be taken when the add assessment button is pressed.
     */
    private void onAddAssessmentClick(){
        Intent intent = new Intent(this, AssessmentAddEditCtrl.class);
        startActivity(intent);
    }

    /**
     * Action to be taken when the edit button is pressed.
     * @param course Course to be edited.
     */
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

    // ********** Recycle View setup start *****************************************************
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {AssessmentContract.Columns._ID,
                AssessmentContract.Columns.TITLE,
                AssessmentContract.Columns.START,
                AssessmentContract.Columns.END,
                AssessmentContract.Columns.CONTENT,
                AssessmentContract.Columns.COURSE_ID};

        String sortOrder = AssessmentContract.Columns.TITLE;
        String whereSelection = AssessmentContract.Columns.COURSE_ID + "=" + mCourse.getId();

        switch (id) {
            case LOADER_ID:
                ContentResolver contentResolver = getContentResolver();
                return new CursorLoader(this, AssessmentContract.CONTENT_URI,projection,whereSelection,null, sortOrder);

            default:
                throw new InvalidParameterException(TAG + ".onCreateLoader called with invalid loader id " + id);
        }
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
        String sortOrder = null;
        String whereSelection = null;
        mCursor = contentResolver.query(AssessmentContract.buildAssessmentUri(recordId), projection, whereSelection, null, sortOrder);

        // Get assessment
        Assessment selection = new Assessment(0,null,null,null,null,null);
        if(mCursor != null){
            while(mCursor.moveToNext()){ // todo: Why does assigning to selection return a -1 when outside loop? -1 mean column not found.
                // Populate selection
                selection = new Assessment(mCursor.getLong(mCursor.getColumnIndexOrThrow(AssessmentContract.Columns._ID)),
                        mCursor.getString(mCursor.getColumnIndexOrThrow(AssessmentContract.Columns.TITLE)),
                        mCursor.getString(mCursor.getColumnIndexOrThrow(AssessmentContract.Columns.START)),
                        mCursor.getString(mCursor.getColumnIndexOrThrow(AssessmentContract.Columns.END)),
                        mCursor.getString(mCursor.getColumnIndexOrThrow(AssessmentContract.Columns.CONTENT)),
                        mCursor.getInt(mCursor.getColumnIndexOrThrow(AssessmentContract.Columns.COURSE_ID)));
            }
        }

        // Display selection
        Intent intent = new Intent(this, AssessmentCtrl.class);
        intent.putExtra(Assessment.class.getSimpleName(), selection);
        startActivity(intent);
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


    /**
     * Calculate the actual id of the row selected.
     * @param position RV position tapped.
     * @return id number of entry.
     */
    private long getRecordId(int position){
        // Get the content resolver
        ContentResolver contentResolver = getContentResolver();

        String[] projection = {AssessmentContract.Columns._ID};
        String sortOrder = AssessmentContract.Columns.TITLE;
        String whereSelection = AssessmentContract.Columns.COURSE_ID + "=" + mCourse.getId();

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
    // ********** Recycle View setup end   *****************************************************

    /**
     * On activity restart, when returning from edit screen, update text fields.
     */
    @Override
    public void onRestart() {
        super.onRestart();

        // Get the content resolver
        ContentResolver contentResolver = getContentResolver();

        // Setup projection
        String[] courseProjection = {CourseContract.Columns._ID,
                CourseContract.Columns.TITLE,
                CourseContract.Columns.START,
                CourseContract.Columns.END,
                CourseContract.Columns.STATUS,
                CourseContract.Columns.NOTE,
                CourseContract.Columns.TERM_ID,
                CourseContract.Columns.INSTRUCTOR_ID};

        // Get record
        Cursor mCursor = contentResolver.query(CourseContract.buildCourseUri(mCourse.getId()), courseProjection, null, null, TermContract.Columns.TITLE);

        // Set course
        if (mCursor != null) {
            while (mCursor.moveToNext()) { // todo: Why does assigning to selected return a -1 when outside loop? -1 mean column not found.
                // Populate course
                mCourse = new Course(mCursor.getLong(mCursor.getColumnIndexOrThrow(CourseContract.Columns._ID)),
                        mCursor.getString(mCursor.getColumnIndexOrThrow(CourseContract.Columns.TITLE)),
                        mCursor.getString(mCursor.getColumnIndexOrThrow(CourseContract.Columns.START)),
                        mCursor.getString(mCursor.getColumnIndexOrThrow(CourseContract.Columns.END)),
                        mCursor.getString(mCursor.getColumnIndexOrThrow(CourseContract.Columns.STATUS)),
                        mCursor.getString(mCursor.getColumnIndexOrThrow(CourseContract.Columns.NOTE)),
                        mCursor.getInt(mCursor.getColumnIndexOrThrow(CourseContract.Columns.TERM_ID)),
                        mCursor.getInt(mCursor.getColumnIndexOrThrow(CourseContract.Columns.INSTRUCTOR_ID)));
            }
        }

        // Get Term name
        ContentResolver termContentResolver = getContentResolver(); // get content resolver.
        String[] termProjection = new String[]{TermContract.Columns.TITLE}; // setup projection
        mCursor = termContentResolver.query(TermContract.buildTermUri(mCourse.getTermId()), termProjection, null,null,null);
        String termName = "Unknown";
        if(mCursor != null){
            while(mCursor.moveToNext()){ // todo: Why does assigning to selectedTerm return a -1 when outside loop? -1 mean column not found.
                termName = mCursor.getString(mCursor.getColumnIndexOrThrow(TermContract.Columns.TITLE));
            }
        }

        // Get instructor details
        ContentResolver instructorContentResolver = getContentResolver(); // get content resolver.
        String[] instructorProjection = new String[]{InstructorContract.Columns.NAME, InstructorContract.Columns.EMAIL, InstructorContract.Columns.PHONE}; // setup projection
        mCursor = instructorContentResolver.query(InstructorContract.buildInstructorUri(mCourse.getInstructorId()), instructorProjection, null,null,null);
        String instructorName="Unknown", instructorEmail="Unknown", instructorPhone="Unknown";
        if(mCursor != null){
            while(mCursor.moveToNext()){ // todo: Why does assigning to selectedTerm return a -1 when outside loop? -1 mean column not found.
                instructorName = mCursor.getString(mCursor.getColumnIndexOrThrow(InstructorContract.Columns.NAME));
                instructorEmail = mCursor.getString(mCursor.getColumnIndexOrThrow(InstructorContract.Columns.EMAIL));
                instructorPhone = mCursor.getString(mCursor.getColumnIndexOrThrow(InstructorContract.Columns.PHONE));
            }
        }

        // Set layout text views
        mTitleTv.setText(mCourse.getTitle());
        mStartTv.setText(mCourse.getStart());
        mEndTv.setText(mCourse.getEnd());
        mTermTv.setText(termName);
        mStatusTv.setText(mCourse.getStatus());
        mInstructorNameTv.setText(instructorName);
        mInstructorEmailTv.setText(instructorEmail);
        mInstructorPhoneTv.setText(instructorPhone);
        mNoteTv.setText(mCourse.getNote());
    }
}
