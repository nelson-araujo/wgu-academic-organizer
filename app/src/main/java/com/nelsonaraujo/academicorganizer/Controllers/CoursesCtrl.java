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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nelsonaraujo.academicorganizer.Models.Course;
import com.nelsonaraujo.academicorganizer.Models.CourseContract;
import com.nelsonaraujo.academicorganizer.Models.TermContract;
import com.nelsonaraujo.academicorganizer.R;

import java.security.InvalidParameterException;

/**
 * Controller for the courses layout.
 */
public class CoursesCtrl extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,CoursesRvClickListener.OnCoursesRvClickListener {
    private static final String TAG = "CoursesCtrl"; // For terminal logging

    public static final int LOADER_ID = 0; // Loader id to identify the loader if multiple are used.

    private CoursesRvAdapter mAdapter; // adapter reference
    private Cursor mCursor;
    private String sortOrder = TermContract.Columns.TITLE; // DB sort order
    private String whereSelection = null; // DB Where selection

    // Constructor
    public CoursesCtrl(){
        // empty constructor.
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.courses);

        LoaderManager.getInstance(this).initLoader(LOADER_ID, null, this);

        // Create adapter and pass data
        mAdapter = new CoursesRvAdapter(null);
        RecyclerView coursesRv = (RecyclerView) findViewById(R.id.coursesCoursesRv);
        coursesRv.setHasFixedSize(true);
        coursesRv.setLayoutManager(new LinearLayoutManager(this));
        coursesRv.addOnItemTouchListener(new CoursesRvClickListener(this, coursesRv, this));
        coursesRv.setAdapter(mAdapter);

        // Add floating action button
        FloatingActionButton addFab = findViewById(R.id.coursesAddFab);
        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCourse(null); // Pass null to create a new entry.
            }
        });
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {CourseContract.Columns._ID,
                CourseContract.Columns.TITLE,
                CourseContract.Columns.START,
                CourseContract.Columns.END,
                CourseContract.Columns.STATUS,
                CourseContract.Columns.NOTE,
                CourseContract.Columns.TERM_ID,
                CourseContract.Columns.INSTRUCTOR_ID};

        switch (id) {
            case LOADER_ID:
                return new CursorLoader(this, CourseContract.CONTENT_URI,projection,whereSelection,null, sortOrder);

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
    public void onCourseClick(View view, int position) {
        // Get the content resolver
        ContentResolver contentResolver = getContentResolver();

        // Setup projection
        String[] projection = {CourseContract.Columns._ID,
                CourseContract.Columns.TITLE,
                CourseContract.Columns.START,
                CourseContract.Columns.END,
                CourseContract.Columns.STATUS,
                CourseContract.Columns.NOTE,
                CourseContract.Columns.TERM_ID,
                CourseContract.Columns.INSTRUCTOR_ID};

        // Get a specific record
        long recordId = getRecordId(position);
        mCursor = contentResolver.query(CourseContract.buildCourseUri(recordId), projection, whereSelection, null, sortOrder);

        // Get course
        Course selection = new Course(0,null,null,null,null,null,null,null);
        if(mCursor != null){
            while(mCursor.moveToNext()){
                // Populate selection
                selection = new Course(mCursor.getLong(mCursor.getColumnIndexOrThrow(CourseContract.Columns._ID)),
                        mCursor.getString(mCursor.getColumnIndexOrThrow(CourseContract.Columns.TITLE)),
                        mCursor.getString(mCursor.getColumnIndexOrThrow(CourseContract.Columns.START)),
                        mCursor.getString(mCursor.getColumnIndexOrThrow(CourseContract.Columns.END)),
                        mCursor.getString(mCursor.getColumnIndexOrThrow(CourseContract.Columns.STATUS)),
                        mCursor.getString(mCursor.getColumnIndexOrThrow(CourseContract.Columns.NOTE)),
                        mCursor.getInt(mCursor.getColumnIndexOrThrow(CourseContract.Columns.TERM_ID)),
                        mCursor.getInt(mCursor.getColumnIndexOrThrow(CourseContract.Columns.INSTRUCTOR_ID)));
            }
        }

        // Display selection
        Intent intent = new Intent(CoursesCtrl.this, CourseCtrl.class);
        intent.putExtra(Course.class.getSimpleName(), selection);
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

        String[] projection = {CourseContract.Columns._ID};
        Cursor cursor = contentResolver.query(CourseContract.CONTENT_URI, projection, whereSelection, null, sortOrder);

        if( (cursor == null) || (cursor.getCount()==0) ){ // If no records are returned
            return -1; // Unable to determine position
        } else {
            if (!cursor.moveToPosition(position)) {
                throw new IllegalStateException("Unable to move cursor to position " + position);
            }

            // move to position
            cursor.moveToPosition(position);
            return cursor.getLong(cursor.getColumnIndexOrThrow(CourseContract.Columns._ID));
        }
    }

    /**
     * Application bar menu.
     * @param menu Menu.
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar, menu);
        getSupportActionBar().setTitle("Courses");
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Application bar menu item selection.
     * @param item Item selected.
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.appbar_terms:
                Intent termsIntent = new Intent(this, TermsCtrl.class);
                startActivity(termsIntent);
                break;

            case R.id.appbar_courses:
                Intent coursesIntent = new Intent(this, CoursesCtrl.class);
                startActivity(coursesIntent);
                break;

            case R.id.appbar_assessments:
                Intent assessmentsIntent = new Intent(this, AssessmentsCtrl.class);
                startActivity(assessmentsIntent);
                break;

            case R.id.appbar_search:
                Intent searchIntent = new Intent(this, SearchCtrl.class);
                startActivity(searchIntent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Add a course. If a course is passed in it goes into edit mode otherwise it adds.
     * @param course to be added.
     */
    public void addCourse(Course course){
        Intent intent = new Intent(CoursesCtrl.this, CourseAddEditCtrl.class);
        if(course != null) { // Edit
            intent.putExtra(Course.class.getSimpleName(), course);
            startActivity(intent);
        } else { // New
            startActivity(intent);
        }
    }

}