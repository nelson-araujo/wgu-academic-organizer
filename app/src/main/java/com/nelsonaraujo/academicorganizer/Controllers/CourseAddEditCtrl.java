package com.nelsonaraujo.academicorganizer.Controllers;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
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
import com.nelsonaraujo.academicorganizer.Models.AssessmentContract;
import com.nelsonaraujo.academicorganizer.Models.Course;
import com.nelsonaraujo.academicorganizer.Models.CourseContract;
import com.nelsonaraujo.academicorganizer.Models.DatePickerFragment;
import com.nelsonaraujo.academicorganizer.Models.Instructor;
import com.nelsonaraujo.academicorganizer.Models.InstructorContract;
import com.nelsonaraujo.academicorganizer.Models.Term;
import com.nelsonaraujo.academicorganizer.Models.TermContract;
import com.nelsonaraujo.academicorganizer.R;


import java.text.ParseException;
import java.util.ArrayList;
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

        // Term selection
        mTermEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTermsDialog();
            }
        });

        // Status selection
        mStatusEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showStatusDialog();
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
                        long termId = getTermId(mTermEt.getText().toString());
                        if(termId != course.getTermId()){
                            values.put(CourseContract.Columns.TERM_ID, termId);
                        }

                        // Update instructor if changed.
                        Instructor instructor = getInstructor(course.getId());
                        if(!(mInstructorNameEt.getText().toString().equals(instructor.getName()) ||
                                mInstructorEmailEt.getText().toString().equals(instructor.getEmail()) ||
                                mInstructorPhoneEt.getText().toString().equals(instructor.getPhone()))){

                            ContentValues instructorValues = new ContentValues();
                            instructorValues.put(InstructorContract.Columns.NAME,mInstructorNameEt.getText().toString());
                            instructorValues.put(InstructorContract.Columns.EMAIL,mInstructorEmailEt.getText().toString());
                            instructorValues.put(InstructorContract.Columns.PHONE,mInstructorPhoneEt.getText().toString());

                            contentResolver.update(InstructorContract.buildInstructorUri(course.getId()),instructorValues,null,null);
                        }

                        if(values.size() != 0) {
                            contentResolver.update(CourseContract.buildCourseUri(course.getId()),values,null,null);
                        }

                        finish();
                        break;
                    case ADD:
                        if(mTitleEt.length() > 0){
                            values.put(CourseContract.Columns.TITLE, mTitleEt.getText().toString());
                            values.put(CourseContract.Columns.STATUS, mStatusEt.getText().toString());
                            values.put(CourseContract.Columns.NOTE, mNoteEt.getText().toString());

                            // Add dates
                            try {
                                Date start = dateFormat.parse(mStartEt.getText().toString());
                                values.put(CourseContract.Columns.START, dateFormat.format(start));

                                Date end = dateFormat.parse(mEndEt.getText().toString());
                                values.put(CourseContract.Columns.END, dateFormat.format(end));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            // Add term
                            values.put(CourseContract.Columns.TERM_ID, getTermId(mTermEt.getText().toString()));

                            // Add Instructor
                            // create instructor
                            ContentValues instructorValues = new ContentValues();
                            instructorValues.put(InstructorContract.Columns.NAME,mInstructorNameEt.getText().toString());
                            instructorValues.put(InstructorContract.Columns.EMAIL,mInstructorEmailEt.getText().toString());
                            instructorValues.put(InstructorContract.Columns.PHONE,mInstructorPhoneEt.getText().toString());

                            Uri instructorUri = contentResolver.insert(InstructorContract.CONTENT_URI,instructorValues);
                            long instructorId = Integer.parseInt(instructorUri.getLastPathSegment());

                            // Update
                            values.put(CourseContract.Columns.INSTRUCTOR_ID, instructorId); // instructor id and course id is the same.


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

    /**
     * Populate and display the status selections dialog.
     */
    private void showStatusDialog(){
        String[] status = {"In progress", "Completed", "Dropped", "Plan to take"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Status")
                .setItems(status, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mStatusEt.setText(status[which]);
                    }
                });

        builder.create();
        builder.show();
    }

    /**
     * Populate and display all the available terms on the system as a selection dialog.
     */
    private void showTermsDialog(){
        // Get terms
        ArrayList<Term> terms = getTerms();

        // create terms string array
        ArrayList<String> termNamesList = new ArrayList<String>();
        for(Term term : terms){
            termNamesList.add(term.getTitle());
        }
        String[] termNamesArray = termNamesList.toArray(new String[0]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Terms")
                .setItems(termNamesArray, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mTermEt.setText(termNamesArray[which]);
                    }
                });

        builder.create();
        builder.show();
    }

    /**
     * Get a list of all terms on the system.
     * @return List of terms.
     */
    private ArrayList<Term> getTerms(){
        // Get the content resolver
        ContentResolver contentResolver = getContentResolver();

        // Setup projection
        String[] projection = {TermContract.Columns._ID,
                TermContract.Columns.TITLE,
                TermContract.Columns.START,
                TermContract.Columns.END};

        // Query database
        Cursor cursor = contentResolver.query(TermContract.CONTENT_URI,projection,null,null);

        // Populate array list
        ArrayList<Term> terms = new ArrayList<Term>();
        if(cursor != null){
            while(cursor.moveToNext()){
                Term term = new Term(cursor.getLong(cursor.getColumnIndexOrThrow(TermContract.Columns._ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(TermContract.Columns.TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(TermContract.Columns.START)),
                        cursor.getString(cursor.getColumnIndexOrThrow(TermContract.Columns.END)));

                terms.add(term);
            }
        }

        cursor.close();

        return terms;
    }

    /**
     * Get the term id from a term title.
     * @param termTitle Term title.
     * @return Term id.
     */
    private long getTermId(String termTitle){
        ArrayList<Term> terms = new ArrayList<Term>();
        terms = getTerms();
        long termId;

        for(Term term : terms){
            if(term.getTitle().equals(termTitle)){
                return termId = term.getId();
            }
        }

        return 0;
    }


    private Instructor getInstructor(long courseId){
        // Get the content resolver
        ContentResolver contentResolver = getContentResolver();

        // Setup projection
        String[] projection = {InstructorContract.Columns._ID,
                InstructorContract.Columns.NAME,
                InstructorContract.Columns.EMAIL,
                InstructorContract.Columns.PHONE};

        // Query database
        Log.d(TAG, "getInstructor: courseId: " + courseId); // todo: remove
//        Cursor cursor = contentResolver.query(InstructorContract.CONTENT_URI,projection,null,null);
        Cursor cursor = contentResolver.query(InstructorContract.buildInstructorUri(courseId), projection,null,null);

        // Populate array list
        ArrayList<Instructor> instructors = new ArrayList<Instructor>();
        Instructor instructor = null;
        if(cursor != null){
            while(cursor.moveToNext()){
                instructor = new Instructor(cursor.getLong(cursor.getColumnIndexOrThrow(InstructorContract.Columns._ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(InstructorContract.Columns.NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(InstructorContract.Columns.EMAIL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(InstructorContract.Columns.PHONE)));
            }
        }

        cursor.close();

        Log.d(TAG, "getInstructor: Instructor: " + instructor.toString()); // todo: remove

        return instructor;
    }
}
