package com.nelsonaraujo.academicorganizer.Controllers;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nelsonaraujo.academicorganizer.Models.Assessment;
import com.nelsonaraujo.academicorganizer.Models.AssessmentContract;
import com.nelsonaraujo.academicorganizer.Models.Course;
import com.nelsonaraujo.academicorganizer.Models.CourseContract;
import com.nelsonaraujo.academicorganizer.Models.DatePickerFragment;
import com.nelsonaraujo.academicorganizer.R;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class AssessmentAddEditCtrl extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, DatePickerDialog.OnDateSetListener{
    private static final String TAG = "AssessmentAddEditCtrl"; // For terminal logging
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    // Date picker selection
    public static final int DIALOG_START_DATE = 1;
    public static final int DIALOG_END_DATE = 2;
    private final Integer ERROR_BG_COLOR = Color.parseColor("#ffe8e7");

    public enum EditMode { EDIT, ADD }
    private AssessmentAddEditCtrl.EditMode mMode;

    private TextView mTitleEt;
    private TextView mStartEt;
    private TextView mEndEt;
    private TextView mContentEt;
    private TextView mCourseEt;
    private FloatingActionButton mSaveFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.assessment_add_edit);

        mTitleEt = findViewById(R.id.assessmentAddEditTitleEt);
        mStartEt = findViewById(R.id.assessmentAddEditStartEt);
        mEndEt = findViewById(R.id.assessmentAddEditEndEt);
        mContentEt = findViewById(R.id.assessmentAddEditContentEt);
        mCourseEt = findViewById(R.id.assessmentAddEditCourseEt);
        mSaveFab = findViewById(R.id.assessmentAddEditSaveFab);

        Bundle arguments = getIntent().getExtras(); // Get the arguments from the bundle.

        final Assessment assessment;
        if(arguments != null){
            assessment = (Assessment) arguments.getSerializable(Assessment.class.getSimpleName()); // Get the actual task to confirm it exists
            if(assessment != null){
                // Set layout text views
                mTitleEt.setText(assessment.getTitle());
                mStartEt.setText(assessment.getStart().toString());
                mEndEt.setText(assessment.getEnd().toString());
                mCourseEt.setText(getCourseName(assessment.getCourseId()));
                mContentEt.setText(assessment.getContent().toString());
                mMode = AssessmentAddEditCtrl.EditMode.EDIT;

            } else {
                mMode = AssessmentAddEditCtrl.EditMode.ADD;
            }
        } else {
            assessment = null;
            mMode = AssessmentAddEditCtrl.EditMode.ADD;
        }

        // Start date listener
        mStartEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String startDate = null;
                if(assessment != null){
                    startDate = assessment.getStart();
                }
                showDatePickerDialog("Start Date", DIALOG_START_DATE, startDate);
            }
        });

        // End date listener
        mEndEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String endDate = null;
                if(assessment != null){
                    endDate = assessment.getEnd();
                }
                showDatePickerDialog("Start Date", DIALOG_END_DATE, endDate);
            }
        });

        // Course listener
        mCourseEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCoursesDialog();
            }
        });

        // Save FAB Action
        mSaveFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentResolver contentResolver = getContentResolver();
                ContentValues values = new ContentValues();
                Boolean fieldsValid = isFieldsValid();

                if(fieldsValid){
                    switch(mMode) {
                        case EDIT:
                            // Update tittle if changed.
                            if (!mTitleEt.getText().toString().equals(assessment.getTitle())) {
                                values.put(AssessmentContract.Columns.TITLE, mTitleEt.getText().toString());
                            }

                            // Update start if changed.
                            if (!mStartEt.getText().toString().equals(assessment.getStart())) {
                                values.put(AssessmentContract.Columns.START, mStartEt.getText().toString());
                            }

                            // Update end if changed.
                            if (!mEndEt.getText().toString().equals(assessment.getEnd())) {
                                values.put(AssessmentContract.Columns.END, mEndEt.getText().toString());
                            }
                            // Update content if changed.
                            if (!mContentEt.getText().toString().equals(assessment.getContent())) {
                                values.put(AssessmentContract.Columns.CONTENT, mContentEt.getText().toString());
                            }

                            // Update course if changed.
                            long courseId = getCourseId(mCourseEt.getText().toString());
                            if (courseId != assessment.getCourseId()) {
                                values.put(AssessmentContract.Columns.COURSE_ID, courseId);
                            }

                            if (values.size() != 0) {
                                contentResolver.update(AssessmentContract.buildAssessmentUri(assessment.getId()), values, null, null);
                            }

                            finish();
                            break;
                        case ADD:
                            if (mTitleEt.length() > 0) {
                                values.put(AssessmentContract.Columns.TITLE, mTitleEt.getText().toString());
                                values.put(AssessmentContract.Columns.CONTENT, mContentEt.getText().toString());

                                // Add dates
                                try {
                                    Date start = dateFormat.parse(mStartEt.getText().toString());
                                    values.put(AssessmentContract.Columns.START, dateFormat.format(start));

                                    Date end = dateFormat.parse(mEndEt.getText().toString());
                                    values.put(AssessmentContract.Columns.END, dateFormat.format(end));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                // Add course
                                values.put(AssessmentContract.Columns.COURSE_ID, getCourseId(mCourseEt.getText().toString()));

                                // Insert entry to database
                                contentResolver.insert(AssessmentContract.CONTENT_URI, values);
                            }
                            finish();
                            break;
                    }
                }
            }
        });

    }

    private void showDatePickerDialog(String title, int dialogId, String date){
        GregorianCalendar cal = new GregorianCalendar();
        if(date != null){
            try {
                cal.setTime(dateFormat.parse(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        DialogFragment dialogFragment = new DatePickerFragment();

        Bundle args = new Bundle();
        args.putInt(DatePickerFragment.DATE_PICKER_ID, dialogId);
        args.putString(DatePickerFragment.DATE_PICKER_TITLE, title);
        args.putSerializable(DatePickerFragment.DATE_PICKER_DATE, cal.getTime());

        dialogFragment.setArguments(args);
        dialogFragment.show(getSupportFragmentManager(),"datePicker");
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        Bundle args = new Bundle();
        GregorianCalendar cal = new GregorianCalendar();
        int dialogId = (int) datePicker.getTag();
        String dateEntered;
        switch(dialogId) {
            case DIALOG_START_DATE:
                dateEntered = year + "-" + String.format(Locale.US, "%02d", month+1)
                        + "-" + String.format(Locale.US, "%02d", dayOfMonth);
                mStartEt.setText(dateEntered);
                break;
            case DIALOG_END_DATE:
                dateEntered = year + "-" + String.format(Locale.US, "%02d", month+1)
                        + "-" + String.format(Locale.US, "%02d", dayOfMonth);
                mEndEt.setText(dateEntered);
                break;
            default:
                throw new IllegalArgumentException("Invalid mode when receiving DatePickerDialog result");
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        // Empty
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        // Empty
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        // Empty
    }

    /**
     * Populate and display all the available course on the system as a selection dialog.
     */
    private void showCoursesDialog(){
        Log.d(TAG, "showCoursesDialog: COURSES DIALOG STARTED");

        // Get courses
        ArrayList<Course> courses = getCourses();

        // create courses string array
        ArrayList<String> courseNamesList = new ArrayList<String>();
        for(Course course : courses){
            courseNamesList.add(course.getTitle());
        }
        String[] courseNamesArray = courseNamesList.toArray(new String[0]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Courses")
                .setItems(courseNamesArray, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mCourseEt.setText(courseNamesArray[which]);
                    }
                });

        builder.create();
        builder.show();
    }

    /**
     * Get a list of all courses.
     * @return list of all course.
     */
    private ArrayList<Course> getCourses(){
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

        // Query database
        Cursor cursor = contentResolver.query(CourseContract.CONTENT_URI,projection,null,null);

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

    /**
     * Get the course id from a course title.
     * @param courseTitle Course title.
     * @return Course id.
     */
    private long getCourseId(String courseTitle){
        ArrayList<Course> courses = new ArrayList<Course>();
        courses = getCourses();

        for(Course course : courses){
            if(course.getTitle().equals(courseTitle)){
                return course.getId();
            }
        }

        return 0;
    }

    /**
     * Get the name of a course.
     * @param courseId course id.
     * @return Name of course.
     */
    private String getCourseName(long courseId){
        // Get the content resolver
        ContentResolver contentResolver = getContentResolver();

        // Setup projection
        String[] projection = {CourseContract.Columns.TITLE};

        // Query database
        Cursor cursor = contentResolver.query(CourseContract.buildCourseUri(courseId), projection,null,null);

        // Get title
        if(cursor != null){
            while(cursor.moveToNext()){
                return cursor.getString(cursor.getColumnIndexOrThrow(CourseContract.Columns.TITLE));
            }
        }

        cursor.close();

        return null;
    }

    /**
     * Check if required fields are valid.
     * @return true if valid otherwise false.
     */
    private Boolean isFieldsValid(){
        Boolean isValid = true;

        if(mTitleEt.getText().length() == 0){
            mTitleEt.setBackgroundColor(ERROR_BG_COLOR);
            isValid = false;
        } else { mTitleEt.setBackgroundColor(Color.TRANSPARENT); }

        if(mStartEt.getText().length() == 0){
            mStartEt.setBackgroundColor(ERROR_BG_COLOR);
            isValid = false;
        } else { mStartEt.setBackgroundColor(Color.TRANSPARENT); }

        if(mEndEt.getText().length() == 0){
            mEndEt.setBackgroundColor(ERROR_BG_COLOR);
            isValid = false;
        } else { mEndEt.setBackgroundColor(Color.TRANSPARENT); }

        if(mCourseEt.getText().length() == 0){
            mCourseEt.setBackgroundColor(ERROR_BG_COLOR);
            isValid = false;
        } else { mCourseEt.setBackgroundColor(Color.TRANSPARENT); }

        return isValid;
    }
}
