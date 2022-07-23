package com.nelsonaraujo.academicorganizer.Controllers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nelsonaraujo.academicorganizer.Models.AppDialog;
import com.nelsonaraujo.academicorganizer.Models.AppNotification;
import com.nelsonaraujo.academicorganizer.Models.Assessment;
import com.nelsonaraujo.academicorganizer.Models.AssessmentContract;
import com.nelsonaraujo.academicorganizer.Models.Course;
import com.nelsonaraujo.academicorganizer.Models.CourseContract;
import com.nelsonaraujo.academicorganizer.Models.InstructorContract;
import com.nelsonaraujo.academicorganizer.Models.TermContract;
import com.nelsonaraujo.academicorganizer.R;

import java.security.InvalidParameterException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;

/**
 * Controller for the course layout.
 */
public class CourseCtrl extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
                                                                CourseAssessmentsRvClickListener.OnCourseAssessmentsRvClickListener,
                                                                AppDialog.DialogEvents{
    private static final String TAG = "CourseCtrl"; // For terminal logging

    private Integer notificationRequestCode = 100;

    public static final int LOADER_ID = 0; // Loader id to identify the loader if multiple are used.
    public static final int DELETE_DIALOG_ID = 1;

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
    private CourseAssessmentsRvAdapter mAdapter;
    private Course mCourse = null;

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
            while(mCursor.moveToNext()){
                termName = mCursor.getString(mCursor.getColumnIndexOrThrow(TermContract.Columns.TITLE));
            }
        }

        // Get instructor details
        contentResolver = getContentResolver(); // get content resolver.
        projection = new String[]{InstructorContract.Columns.NAME, InstructorContract.Columns.EMAIL, InstructorContract.Columns.PHONE}; // setup projection
        mCursor = contentResolver.query(InstructorContract.buildInstructorUri(mCourse.getInstructorId()), projection, null,null,null);
        String instructorName="Unknown", instructorEmail="Unknown", instructorPhone="Unknown";
        if(mCursor != null){
            while(mCursor.moveToNext()){
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

        // Setup start set reminder
        ImageView startSetReminder = findViewById(R.id.courseStartSetReminder);
        startSetReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSetReminderClick(mCourse.getTitle(), mCourse.getStart(), AppNotification.START_OR_END.start);
            }
        });

        // Setup end set reminder
        ImageView endSetReminder = findViewById(R.id.courseEndSetReminder);
        endSetReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSetReminderClick(mCourse.getTitle(), mCourse.getEnd(), AppNotification.START_OR_END.end);
            }
        });

        // Setup share button
        ImageView shareButton = findViewById(R.id.courseShareIv);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onShareClick(mCourse.getNote());
            }
        });

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

        LoaderManager.getInstance(this).initLoader(LOADER_ID, null, this);

        // Create adapter and pass data
        mAdapter = new CourseAssessmentsRvAdapter(null);
        RecyclerView coursesRv = (RecyclerView) findViewById(R.id.courseAssessmentsRv);
        coursesRv.setHasFixedSize(true);
        coursesRv.setLayoutManager(new LinearLayoutManager(this));
        coursesRv.addOnItemTouchListener(new CourseAssessmentsRvClickListener(this, coursesRv, this));
        coursesRv.setAdapter(mAdapter);
    }

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
            while(mCursor.moveToNext()){
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

    @Override
    public void onDialogPositiveResponse(int dialogId, Bundle args) {
        // set ids
        long courseId = args.getLong("courseId");
        long instructorId = args.getLong("instructorId");


        // Delete assessments
        ArrayList<Assessment> assessments = new ArrayList<Assessment>();
        assessments = getAssessments(mCourse.getId());

        for(Assessment assessment : assessments){
            getContentResolver().delete(AssessmentContract.buildAssessmentUri(assessment.getId()), null, null);
        }

        // Delete instructor
        getContentResolver().delete(InstructorContract.buildInstructorUri(instructorId), null, null);

        // Delete course
        getContentResolver().delete(CourseContract.buildCourseUri(courseId), null, null);

        finish();
    }

    @Override
    public void onDialogNegativeResponse(int dialogId, Bundle args) {
        // Empty, do nothing.
    }

    @Override
    public void onDialogCancel(int dialogId) {
        // Empty, do nothing.
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
            while (mCursor.moveToNext()) {
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
            while(mCursor.moveToNext()){
                termName = mCursor.getString(mCursor.getColumnIndexOrThrow(TermContract.Columns.TITLE));
            }
        }

        // Get instructor details
        ContentResolver instructorContentResolver = getContentResolver(); // get content resolver.
        String[] instructorProjection = new String[]{InstructorContract.Columns.NAME, InstructorContract.Columns.EMAIL, InstructorContract.Columns.PHONE}; // setup projection
        mCursor = instructorContentResolver.query(InstructorContract.buildInstructorUri(mCourse.getInstructorId()), instructorProjection, null,null,null);
        String instructorName="Unknown", instructorEmail="Unknown", instructorPhone="Unknown";
        if(mCursor != null){
            while(mCursor.moveToNext()){
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

    /**
     * Application bar menu.
     * @param menu Menu.
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar, menu);
        getSupportActionBar().setTitle("Course");
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

    /**
     * Action to be taken when the delete button is pressed.
     * @param course course to be deleted.
     */
    private void onDeleteFabClick(Course course){
        AppDialog dialog = new AppDialog();
        Bundle args = new Bundle();
        args.putInt(AppDialog.DIALOG_ID, DELETE_DIALOG_ID);
        args.putString(AppDialog.DIALOG_MESSAGE, getString(R.string.deleteCourseDialog_message, course.getId(), course.getTitle()));
        args.putInt(AppDialog.DIALOG_POSITIVE_RID, R.string.deleteDialog_positive_caption);

        // Add needed id to delete record to bundle.
        args.putLong("courseId", course.getId());
        args.putLong("instructorId", course.getInstructorId());

        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(),null);
    }

    /**
     * Get the courses note and open the share dialog.
     * @param noteToShare note to share
     */
    private void onShareClick(String noteToShare){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");

        intent.putExtra(Intent.EXTRA_TEXT, noteToShare);

        startActivity(Intent.createChooser(intent, "Share using"));
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
     * Action to be taken when the set reminder button is pressed.
     * @param date to set the reminder to.
     */
    private void onSetReminderClick(String title, String date, AppNotification.START_OR_END startOrEnd){
        // Convert date to milliseconds
        LocalDate dateLd = LocalDate.parse(date); // Parse string to LocalDate
        LocalDateTime dateLdt = dateLd.atStartOfDay(); // Convert LocalDate to LocalDateTime using start of day time.
        ZonedDateTime dateZdt = ZonedDateTime.of(dateLdt, ZoneId.systemDefault()); // Convert LocalDateTime to ZonedDateTime.
        long dateMillis =  dateZdt.toEpochSecond() * 1000;

        // Create message to display
        String message = title + " " + startOrEnd + "s today.";

        // Build the intent
        Intent intent = new Intent(this, AppNotification.class);
        intent.putExtra(AppNotification.NOTIFICATION_TYPE, AppNotification.TYPE_START_END);
        intent.putExtra(AppNotification.NOTIFICATION_MESSAGE, message);

        // Setup alarm
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,notificationRequestCode++,intent,PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, dateMillis, pendingIntent);

        // Notify user the alert was set.
        Toast.makeText(this,"Alert set for " + dateLd ,Toast.LENGTH_LONG).show();
    }

}
