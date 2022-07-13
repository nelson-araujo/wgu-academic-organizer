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
import com.nelsonaraujo.academicorganizer.Models.Course;
import com.nelsonaraujo.academicorganizer.Models.CourseContract;
import com.nelsonaraujo.academicorganizer.R;

import java.security.InvalidParameterException;
import java.sql.Date;

public class CoursesCtrl extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,CoursesRvClickListener.OnCoursesRvClickListener {
    private static final String TAG = "CoursesCtrl"; // For terminal logging

    public static final int LOADER_ID = 0; // Loader id to identify the loader if multiple are used.

    private CoursesRvAdapter mAdapter; // adapter reference
    private Cursor mCursor;

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

    public void addCourse(Course course){
        Intent intent = new Intent(CoursesCtrl.this, CourseAddEditCtrl.class);
        if(course != null) { // Edit
            intent.putExtra(Course.class.getSimpleName(), course);
            startActivity(intent);
        } else { // New
            startActivity(intent);
        }
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

        String sortOrder = null; // Set sort order

        switch (id) {
            case LOADER_ID:
                ContentResolver contentResolver = getContentResolver();
                return new CursorLoader(this, CourseContract.CONTENT_URI,projection,null,null, sortOrder);

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
//        String[] projection = {CourseContract.Columns._ID, CourseContract.Columns.TITLE, CourseContract.Columns.START, CourseContract.Columns.END}; // todo: remove
        String[] projection = {CourseContract.Columns._ID,
                CourseContract.Columns.TITLE,
                CourseContract.Columns.START,
                CourseContract.Columns.END,
                CourseContract.Columns.STATUS,
                CourseContract.Columns.NOTE,
                CourseContract.Columns.TERM_ID,
                CourseContract.Columns.INSTRUCTOR_ID};

        // Get a specific record
        mCursor = contentResolver.query(CourseContract.buildCourseUri(position+1), projection, null, null, CourseContract.Columns.TITLE);

        // Get course
        Course selection = new Course(0,null,null,null,null,null,null,null);
        if(mCursor != null){
            while(mCursor.moveToNext()){ // todo: Why does assigning to selection return a -1 when outside loop? -1 mean column not found.
                // Populate selection
                selection = new Course(mCursor.getLong(mCursor.getColumnIndexOrThrow(CourseContract.Columns._ID)),
                        mCursor.getString(mCursor.getColumnIndexOrThrow(CourseContract.Columns.TITLE)),
                        new Date(mCursor.getLong(mCursor.getColumnIndexOrThrow(CourseContract.Columns.START))),
                        new Date(mCursor.getLong(mCursor.getColumnIndexOrThrow(CourseContract.Columns.END))),
                        mCursor.getString(mCursor.getColumnIndexOrThrow(CourseContract.Columns.STATUS)),
                        mCursor.getString(mCursor.getColumnIndexOrThrow(CourseContract.Columns.NOTE)),
                        mCursor.getInt(mCursor.getColumnIndexOrThrow(CourseContract.Columns.TERM_ID)),
                        mCursor.getInt(mCursor.getColumnIndexOrThrow(CourseContract.Columns.INSTRUCTOR_ID)));
            }
//            mCursor.close();
        }

        // Display selection
        Intent intent = new Intent(CoursesCtrl.this, CourseCtrl.class);
        intent.putExtra(Course.class.getSimpleName(), selection);
        startActivity(intent);
    }

}