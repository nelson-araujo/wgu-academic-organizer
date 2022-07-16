package com.nelsonaraujo.academicorganizer.Controllers;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nelsonaraujo.academicorganizer.Models.Course;
import com.nelsonaraujo.academicorganizer.Models.CourseContract;
import com.nelsonaraujo.academicorganizer.Models.DatePickerFragment;
import com.nelsonaraujo.academicorganizer.Models.InstructorContract;
import com.nelsonaraujo.academicorganizer.Models.TermContract;
import com.nelsonaraujo.academicorganizer.R;


import java.text.ParseException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class CourseAddEditCtrl extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, DatePickerDialog.OnDateSetListener {
    private static final String TAG = "CourseAddEditCtrl"; // For terminal logging
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    // Date picker selection
    public static final int DIALOG_START_DATE = 1;
    public static final int DIALOG_END_DATE = 2;

    public enum EditMode { EDIT, ADD }
    private CourseAddEditCtrl.EditMode mMode;
    private Cursor mCursor;

    private EditText mTitleEt;
    private EditText mStartEt;
    private EditText mEndEt;
    private EditText mTermEt;
    private EditText mStatusEt;
    private EditText mInstructorNameEt;
    private EditText mInstructorEmailEt;
    private EditText mInstructorPhoneEt;
    private EditText mNoteEt;
    private FloatingActionButton mSaveFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_add_edit);

        mTitleEt = findViewById(R.id.courseAddEditTitleEt);
        mStartEt = findViewById(R.id.courseAddEditStartEt);
        mEndEt = findViewById(R.id.courseAddEditEndEt);
        mTermEt = findViewById(R.id.courseAddEditTermEt);
        mStatusEt = findViewById(R.id.courseAddEditStatusEt);
        mInstructorNameEt = findViewById(R.id.courseAddEditInstructorNameEt);
        mInstructorEmailEt = findViewById(R.id.courseAddEditInstructorEmailEt);
        mInstructorPhoneEt = findViewById(R.id.courseAddEditInstructorPhoneEt);
        mNoteEt = findViewById(R.id.courseAddEditNoteEt);
        mSaveFab = findViewById(R.id.courseAddEditSaveFab);

        Bundle arguments = getIntent().getExtras(); // Get the arguments from the bundle.

        final Course course;
        if(arguments != null){
            course = (Course) arguments.getSerializable(Course.class.getSimpleName()); // Get the actual task to confirm it exists
            if(course != null){
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
                mTitleEt.setText(course.getTitle());
                mStartEt.setText(course.getStart().toString());
                mEndEt.setText(course.getEnd().toString());
                mTermEt.setText(termName);
                mStatusEt.setText(course.getStatus());
                mInstructorNameEt.setText(instructorName);
                mInstructorEmailEt.setText(instructorEmail);
                mInstructorPhoneEt.setText(instructorPhone);
                mNoteEt.setText(course.getNote());
                mMode = CourseAddEditCtrl.EditMode.EDIT;
            } else {
                mMode = CourseAddEditCtrl.EditMode.ADD;
            }
        } else {
            course = null;
            mMode = CourseAddEditCtrl.EditMode.ADD;
        }

        // Start date listener
        mStartEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String startDate = null;
                if(course != null){
                    startDate = course.getStart();
                }
                showDatePickerDialog("Start Date", DIALOG_START_DATE, startDate);
            }
        });

        // End date listener
        mEndEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String endDate = null;
                if(course != null){
                    endDate = course.getEnd();
                }
                showDatePickerDialog("Start Date", DIALOG_END_DATE, endDate);
            }
        });

        // Save FAB Action
        mSaveFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentResolver contentResolver = getContentResolver();
                ContentValues values = new ContentValues();

                switch(mMode){
                    case EDIT:
                        // Update tittle if changed.
                        if(!mTitleEt.getText().toString().equals(course.getTitle())){
                            values.put(CourseContract.Columns.TITLE, mTitleEt.getText().toString());
                        }

                        // Update start if changed.
                        if(!mStartEt.getText().toString().equals(course.getStart())){
                            values.put(CourseContract.Columns.START, mStartEt.getText().toString());
                        }

                        // Update end if changed.
                        if(!mEndEt.getText().toString().equals(course.getEnd())){
                            values.put(CourseContract.Columns.END, mEndEt.getText().toString());
                        }

                        // Update status if changed.
                        if(!mStatusEt.getText().toString().equals(course.getStatus())){
                            values.put(CourseContract.Columns.STATUS, mStatusEt.getText().toString());
                        }

                        // Update note if changed.
                        if(!mNoteEt.getText().toString().equals(course.getNote())){
                            values.put(CourseContract.Columns.NOTE, mNoteEt.getText().toString());
                        }

                        // Update term_id if changed.
                        if(!mTermEt.getText().toString().equals(course.getTermId())){
//                            values.put(CourseContract.Columns.TERM_ID, mTermEt.getText().toString()); // todo: update to identify id
                            values.put(CourseContract.Columns.TERM_ID, 1); // todo: remove
                        }

                        // Update instructor_id if changed.
//                        if(!mTermEt.getText().toString().equals(course.getTermId())){
//                            values.put(CourseContract.Columns.INSTRUCTOR_ID, mTermEt.getText().toString()); // todo: update to identify id
                            values.put(CourseContract.Columns.INSTRUCTOR_ID, 1); // todo: remove
//                        }

                        if(values.size() != 0) {
                            contentResolver.update(CourseContract.buildCourseUri(course.getId()),values,null,null);
                            finish();
                            break;
                        }
                    case ADD:
                        if(mTitleEt.length() > 0){
                            values.put(CourseContract.Columns.TITLE, mTitleEt.getText().toString());
                            values.put(CourseContract.Columns.STATUS, mStatusEt.getText().toString());
                            values.put(CourseContract.Columns.NOTE, mNoteEt.getText().toString());

                            // Add dates
                            try {
                                Date start = dateFormat.parse(mStartEt.getText().toString());
                                Log.d(TAG, "onClick: Add start date: " + dateFormat.format(start)); // todo: remove
                                values.put(CourseContract.Columns.START, dateFormat.format(start));

                                Date end = dateFormat.parse(mEndEt.getText().toString());
                                Log.d(TAG, "onClick: Add end date: " + dateFormat.format(end)); // todo: remove
                                values.put(CourseContract.Columns.END, dateFormat.format(end));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            // Add term
                            values.put(CourseContract.Columns.TERM_ID, 1); // todo: remove

                            // Add Instructor
                            values.put(CourseContract.Columns.INSTRUCTOR_ID, 1); // todo: remove

                            // Insert entry to database
                            contentResolver.insert(CourseContract.CONTENT_URI,values);
                        }
                        finish();
                        break;
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
        Log.d(TAG, "onDateSet: STARTED");
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
}
