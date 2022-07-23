package com.nelsonaraujo.academicorganizer.Controllers;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nelsonaraujo.academicorganizer.Models.AppDialog;
import com.nelsonaraujo.academicorganizer.Models.Assessment;
import com.nelsonaraujo.academicorganizer.Models.AssessmentContract;
import com.nelsonaraujo.academicorganizer.Models.Course;
import com.nelsonaraujo.academicorganizer.Models.CourseContract;
import com.nelsonaraujo.academicorganizer.Models.InstructorContract;
import com.nelsonaraujo.academicorganizer.Models.Term;

import com.nelsonaraujo.academicorganizer.Models.TermContract;
import com.nelsonaraujo.academicorganizer.R;

import java.security.InvalidParameterException;
import java.util.ArrayList;

/**
 * Controller for the term layout.
 */
public class TermCtrl extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
                                                            TermCoursesRvClickListener.OnTermCoursesRvClickListener,
                                                            AppDialog.DialogEvents{

    private static final String TAG = "TermCtrl"; // For terminal logging

    public static final int LOADER_ID = 0; // Loader id to identify the loader if multiple are used.
    public static final int DELETE_DIALOG_ID = 1;

    private TextView mTermTv;
    private TextView mStartTv;
    private TextView mEndTv;

    private Cursor mCursor;
    private TermCoursesRvAdapter mAdapter;
    private Term mTerm = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.term);

        // Link fields from layout
        mTermTv = findViewById(R.id.termTermTv);
        mStartTv = findViewById(R.id.termStartTv);
        mEndTv = findViewById(R.id.termEndTv);

        // Get the arguments from the term bundle.
        Bundle arguments = getIntent().getExtras();

        // Create a Term and populate with the actual task to confirm it exists
        mTerm = (Term) arguments.getSerializable(Term.class.getSimpleName());

        // Set TextViews
        mTermTv.setText(mTerm.getTitle());
        mStartTv.setText(mTerm.getStart());
        mEndTv.setText(mTerm.getEnd());

        // Setup edit fab
        FloatingActionButton editFab = findViewById(R.id.termEditFab);
        editFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onEditFabClick(mTerm); // call addTerm and pass term.

            }
        });

        // Setup delete fab
        FloatingActionButton deleteFab = findViewById(R.id.termDeleteFab);
        deleteFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDeleteFabClick(mTerm); // call addTerm and pass null as we want to create a new term.
            }
        });

        // Setup add course button
        ImageView addCourseIv = findViewById(R.id.termAddCourseIv);
        addCourseIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAddCourseClick();
            }
        });

        LoaderManager.getInstance(this).initLoader(LOADER_ID, null, this);

        // Create adapter and pass data
        mAdapter = new TermCoursesRvAdapter(null);
        RecyclerView coursesRv = (RecyclerView) findViewById(R.id.termCoursesRv);
        coursesRv.setHasFixedSize(true);
        coursesRv.setLayoutManager(new LinearLayoutManager(this));
        coursesRv.addOnItemTouchListener(new TermCoursesRvClickListener(this, coursesRv, this));
        coursesRv.setAdapter(mAdapter);
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

        String sortOrder = null; // Sort order
        String whereSelection = CourseContract.Columns.TERM_ID + "=" + mTerm.getId(); // WHERE clause

        switch (id) {
            case LOADER_ID:
                ContentResolver contentResolver = getContentResolver();
                return new CursorLoader(this, CourseContract.CONTENT_URI,projection,whereSelection,null, sortOrder);

            default:
                throw new InvalidParameterException(TAG + ".onCreateLoader called with invalid loader id " + id);
        }
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
        String sortOrder =  null; // Sort order
        String whereSelection = null; // WHERE clause
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
        Intent intent = new Intent(TermCtrl.this, CourseCtrl.class);
        intent.putExtra(Course.class.getSimpleName(), selection);
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
     * Dialog box action when user accepts change.
     * @param dialogId Id of the dialog being displayed.
     * @param args Arguments for the dialog box.
     */
    @Override
    public void onDialogPositiveResponse(int dialogId, Bundle args) {
        long termId = args.getLong("TermId"); // get id from bundle
        ArrayList<Course> courses = new ArrayList<Course>();
        courses = getCourses(termId);

        for(Course course : courses){
            // Delete assessments
            ArrayList<Assessment> assessments = new ArrayList<Assessment>();
            assessments = getAssessments(course.getId());

            for(Assessment assessment : assessments){
                getContentResolver().delete(AssessmentContract.buildAssessmentUri(assessment.getId()), null, null);
            }

            // Delete instructor
            getContentResolver().delete(InstructorContract.buildInstructorUri(course.getInstructorId()), null, null);


            // Delete course
            getContentResolver().delete(CourseContract.buildCourseUri(course.getId()), null, null);
        }

        // Delete term
        getContentResolver().delete(TermContract.buildTermUri(termId), null, null);

        finish(); // close term screen.

    }

    /**
     * Dialog box action when user does not accept change.
     * @param dialogId Id of the dialog being displayed.
     * @param args Arguments for the dialog box.
     */
    @Override
    public void onDialogNegativeResponse(int dialogId, Bundle args) {
        // Empty do nothing.
    }

    /**
     * Dialog box action when user cancels the dialog box.
     * @param dialogId Id of the dialog being displayed.
     */
    @Override
    public void onDialogCancel(int dialogId) {
        // Empty do nothing.
    }

    @Override
    public void onRestart() {
        super.onRestart();

        // Get the content resolver
        ContentResolver contentResolver = getContentResolver();

        // Setup projection
        String[] projection = {TermContract.Columns._ID, TermContract.Columns.TITLE, TermContract.Columns.START, TermContract.Columns.END};

        // Get record
        Cursor cursor = contentResolver.query(TermContract.buildTermUri(mTerm.getId()), projection, null, null, TermContract.Columns.TITLE);

        // Set term
//        mTerm = new Term(0,null,null,null);
        if(cursor != null){
            while(cursor.moveToNext()){
                // Populate term
                mTerm = new Term(cursor.getLong(cursor.getColumnIndexOrThrow(TermContract.Columns._ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(TermContract.Columns.TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(TermContract.Columns.START)),
                        cursor.getString(cursor.getColumnIndexOrThrow(TermContract.Columns.END)));
            }
            cursor.close();
        }

        // Update TextViews
        mTermTv.setText(mTerm.getTitle());
        mStartTv.setText(mTerm.getStart());
        mEndTv.setText(mTerm.getEnd());

    }

    /**
     * Application bar menu.
     * @param menu Menu.
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar, menu);
        getSupportActionBar().setTitle("Term");
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
     * Action to be taken when the edit button is pressed.
     * @param term term to be edited.
     */
    private void onEditFabClick(Term term){
        // Display term
        Intent termIntent = new Intent(this, TermAddEditCtrl.class);
        termIntent.putExtra(Term.class.getSimpleName(), term);
        startActivity(termIntent);
    }

    /**
     * Action to be taken when the add assessment button is pressed.
     */
    private void onAddCourseClick(){
        Intent intent = new Intent(this, CourseAddEditCtrl.class);
        startActivity(intent);
    }

    /**
     * Action when the delete button is pressed.
     * @param term Term selected.
     */
    private void onDeleteFabClick(Term term){
        ArrayList<Course> courses = new ArrayList<Course>();

        courses = getCourses(term.getId());
        Log.d(TAG, "onDeleteFabClick: courses Size: " + courses.size());
        Boolean courseAttached = false;
        if(courses.size() > 0){ courseAttached = true; };

        if(!courseAttached) {
            AppDialog dialog = new AppDialog();
            Bundle args = new Bundle();
            args.putInt(AppDialog.DIALOG_ID, DELETE_DIALOG_ID);
            args.putString(AppDialog.DIALOG_MESSAGE, getString(R.string.deleteTermDialog_message, term.getId(), term.getTitle()));
            args.putInt(AppDialog.DIALOG_POSITIVE_RID, R.string.deleteDialog_positive_caption);

            args.putLong("TermId", term.getId()); // Add id to bundle

            dialog.setArguments(args);
            dialog.show(getSupportFragmentManager(), null);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("A Term cannot be deleted while a course is assigned to it.")
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Empty, do nothing.
                        }
                    });

            // Create and display
            builder.create();
            builder.show();
        }

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
        String sortOrder =  null;
        String whereSelection = CourseContract.Columns.TERM_ID + "=" + mTerm.getId();

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
     * Get assessments attached to a specific course.
     * @param courseId course id.
     * @return array of assessments.
     */
    private ArrayList<Assessment> getAssessments(long courseId){
        // Get the content resolver
        ContentResolver contentResolver = getContentResolver();

        // Setup projection
        String[] assessmentProjection = {AssessmentContract.Columns._ID,
                AssessmentContract.Columns.TITLE,
                AssessmentContract.Columns.START,
                AssessmentContract.Columns.END,
                AssessmentContract.Columns.COURSE_ID,
                AssessmentContract.Columns.CONTENT};

        String sortOrder = AssessmentContract.Columns.TITLE;
        String whereSelection = AssessmentContract.Columns.COURSE_ID + "=" + courseId;

        Cursor cursor = contentResolver.query(AssessmentContract.CONTENT_URI, assessmentProjection, whereSelection, null, sortOrder);

        // Populate array list
        ArrayList<Assessment> assessments = new ArrayList<Assessment>();
        if(cursor != null){
            while(cursor.moveToNext()){
                Assessment assessment = new Assessment(cursor.getLong(cursor.getColumnIndexOrThrow(AssessmentContract.Columns._ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(AssessmentContract.Columns.TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(AssessmentContract.Columns.START)),
                        cursor.getString(cursor.getColumnIndexOrThrow(AssessmentContract.Columns.END)),
                        cursor.getString(cursor.getColumnIndexOrThrow(AssessmentContract.Columns.CONTENT)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(AssessmentContract.Columns.COURSE_ID)));

                assessments.add(assessment);
            }
        }

        cursor.close();

        return assessments;
    }

    /**
     * Get a list of all courses.
     * @return list of all course.
     */
    private ArrayList<Course> getCourses(long termId){
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

        String sortOrder = CourseContract.Columns.TITLE;
        String whereSelection = CourseContract.Columns.TERM_ID + "=" + termId;

        // Query database
        Cursor cursor = contentResolver.query(CourseContract.CONTENT_URI, projection, whereSelection, null, sortOrder);

        // Populate array list
        ArrayList<Course> courses = new ArrayList<Course>();
        if(cursor != null){
            while(cursor.moveToNext()){
                Course course = new Course(cursor.getLong(cursor.getColumnIndexOrThrow(CourseContract.Columns._ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(CourseContract.Columns.TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(CourseContract.Columns.START)),
                        cursor.getString(cursor.getColumnIndexOrThrow(CourseContract.Columns.END)),
                        cursor.getString(cursor.getColumnIndexOrThrow(CourseContract.Columns.STATUS)),
                        cursor.getString(cursor.getColumnIndexOrThrow(CourseContract.Columns.NOTE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(CourseContract.Columns.TERM_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(CourseContract.Columns.INSTRUCTOR_ID)));

                courses.add(course);
            }
        }

        cursor.close();

        return courses;
    }
}