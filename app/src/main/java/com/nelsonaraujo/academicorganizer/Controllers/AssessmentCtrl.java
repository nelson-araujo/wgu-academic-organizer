package com.nelsonaraujo.academicorganizer.Controllers;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nelsonaraujo.academicorganizer.Models.AppDialog;
import com.nelsonaraujo.academicorganizer.Models.Assessment;
import com.nelsonaraujo.academicorganizer.Models.AssessmentContract;
import com.nelsonaraujo.academicorganizer.Models.CourseContract;
import com.nelsonaraujo.academicorganizer.Models.TermContract;
import com.nelsonaraujo.academicorganizer.R;

/**
 * Controller for the assessment layout.
 */
public class AssessmentCtrl extends AppCompatActivity implements AppDialog.DialogEvents{
    private static final String TAG = "AssessmentCtrl"; // For terminal logging

    public static final int LOADER_ID = 0; // Loader id to identify the loader if multiple are used.
    public static final int DELETE_DIALOG_ID = 1;

    Cursor mCursor;
    private Assessment mAssessment = null;

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
        mAssessment = (Assessment) arguments.getSerializable(Assessment.class.getSimpleName());

        // Set layout text views
        mTitleTv.setText(mAssessment.getTitle());
        mContentTv.setText(mAssessment.getContent().toString());
        mStartTv.setText(mAssessment.getStart().toString());
        mEndTv.setText(mAssessment.getEnd().toString());
        mCourseTv.setText(getCourseName(mAssessment.getCourseId()));

        // Setup edit fab
        FloatingActionButton editFab = findViewById(R.id.assessmentEditFab);
        editFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onEditFabClick(mAssessment);
            }
        });

        // Setup delete fab
        FloatingActionButton deleteFab = findViewById(R.id.assessmentDeleteFab);
        deleteFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDeleteFabClick(mAssessment);
            }
        });
    }

    @Override
    public void onDialogPositiveResponse(int dialogId, Bundle args) {
        long assessmentId = args.getLong("AssessmentId"); // get the id from the bundle.

        getContentResolver().delete(AssessmentContract.buildAssessmentUri(assessmentId), null, null);

        finish(); // close term screen.
    }

    @Override
    public void onDialogNegativeResponse(int dialogId, Bundle args) {
        // Empty do nothing.
    }

    @Override
    public void onDialogCancel(int dialogId) {
        // Empty do nothing.
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
        String[] assessmentProjection = {AssessmentContract.Columns._ID,
                AssessmentContract.Columns.TITLE,
                AssessmentContract.Columns.START,
                AssessmentContract.Columns.END,
                AssessmentContract.Columns.CONTENT,
                AssessmentContract.Columns.COURSE_ID};

        // Get record
        mCursor = contentResolver.query(AssessmentContract.buildAssessmentUri(mAssessment.getId()), assessmentProjection, null, null, TermContract.Columns.TITLE);

        // Set assessment
        if(mCursor != null){
            while(mCursor.moveToNext()) {
                // Populate assessment
                mAssessment = new Assessment(mCursor.getLong(mCursor.getColumnIndexOrThrow(AssessmentContract.Columns._ID)),
                        mCursor.getString(mCursor.getColumnIndexOrThrow(AssessmentContract.Columns.TITLE)),
                        mCursor.getString(mCursor.getColumnIndexOrThrow(AssessmentContract.Columns.START)),
                        mCursor.getString(mCursor.getColumnIndexOrThrow(AssessmentContract.Columns.END)),
                        mCursor.getString(mCursor.getColumnIndexOrThrow(AssessmentContract.Columns.CONTENT)),
                        mCursor.getInt(mCursor.getColumnIndexOrThrow(AssessmentContract.Columns.COURSE_ID)));
            }
        }

        // Set layout text views
        mTitleTv.setText(mAssessment.getTitle());
        mContentTv.setText(mAssessment.getContent());
        mStartTv.setText(mAssessment.getStart());
        mEndTv.setText(mAssessment.getEnd());
        mCourseTv.setText(getCourseName(mAssessment.getCourseId()));

    }

    /**
     * Action to be taken when the edit button is pressed.
     * @param assessment assessment selected.
     */
    private void onEditFabClick(Assessment assessment){
        // Display
        Intent intent = new Intent(this, AssessmentAddEditCtrl.class);
        intent.putExtra(Assessment.class.getSimpleName(), assessment);
        startActivity(intent);
    }

    /**
     * Action to be taken when the delete button is pressed.
     * @param assessment assessment to be deleted.
     */
    private void onDeleteFabClick(Assessment assessment){
        AppDialog dialog = new AppDialog();
        Bundle args = new Bundle();
        args.putInt(AppDialog.DIALOG_ID, DELETE_DIALOG_ID);
        args.putString(AppDialog.DIALOG_MESSAGE, getString(R.string.deleteAssessmentDialog_message, assessment.getId(), assessment.getTitle()));
        args.putInt(AppDialog.DIALOG_POSITIVE_RID, R.string.deleteDialog_positive_caption);

        args.putLong("AssessmentId", assessment.getId()); // Add id to bundle

        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(),null);


//        getContentResolver().delete(AssessmentContract.buildAssessmentUri(assessment.getId()), null, null);
//        finish();
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
     * Application bar menu.
     * @param menu Menu.
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar, menu);
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
        }

        return super.onOptionsItemSelected(item);
    }
}
