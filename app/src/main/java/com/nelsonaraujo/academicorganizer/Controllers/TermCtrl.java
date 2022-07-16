package com.nelsonaraujo.academicorganizer.Controllers;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nelsonaraujo.academicorganizer.Models.AppDialog;
import com.nelsonaraujo.academicorganizer.Models.Course;
import com.nelsonaraujo.academicorganizer.Models.CourseContract;
import com.nelsonaraujo.academicorganizer.Models.DatePickerFragment;
import com.nelsonaraujo.academicorganizer.Models.Term;

import com.nelsonaraujo.academicorganizer.Models.TermContract;
import com.nelsonaraujo.academicorganizer.R;

import java.security.InvalidParameterException;
import java.util.GregorianCalendar;

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
    private Term term = null;



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
//        final Term term; // todo update
        term = (Term) arguments.getSerializable(Term.class.getSimpleName());

        // Set TextViews
        mTermTv.setText(term.getTitle());
        mStartTv.setText(term.getStart());
        mEndTv.setText(term.getEnd());

        // Setup edit fab
        FloatingActionButton editFab = findViewById(R.id.termEditFab);
        editFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onEditFabClick(term); // call addTerm and pass term.
            }
        });

        // Setup delete fab
        FloatingActionButton deleteFab = findViewById(R.id.termDeleteFab);
        deleteFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDeleteFabClick(term); // call addTerm and pass null as we want to create a new term.
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

        // ********** Recycle View setup start *****************************************************
        LoaderManager.getInstance(this).initLoader(LOADER_ID, null, this);

        // Create adapter and pass data
        mAdapter = new TermCoursesRvAdapter(null);
        RecyclerView coursesRv = (RecyclerView) findViewById(R.id.termCoursesRv);
        coursesRv.setHasFixedSize(true);
        coursesRv.setLayoutManager(new LinearLayoutManager(this));
        coursesRv.addOnItemTouchListener(new TermCoursesRvClickListener(this, coursesRv, this));
        coursesRv.setAdapter(mAdapter);
        // ********** Recycle View setup end   *****************************************************
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
        AppDialog dialog = new AppDialog();
        Bundle args = new Bundle();
        args.putInt(AppDialog.DIALOG_ID, DELETE_DIALOG_ID);
        args.putString(AppDialog.DIALOG_MESSAGE, getString(R.string.deleteDialog_message, term.getId(), term.getTitle()));
        args.putInt(AppDialog.DIALOG_POSITIVE_RID, R.string.deleteDialog_positive_caption);

        args.putLong("TermId", term.getId()); // Add id to bundle

        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(),null);



//        getContentResolver().delete(TermContract.buildTermUri(term.getId()), null, null);
//        finish();
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
        String whereSelection = CourseContract.Columns.TERM_ID + "=" + term.getId(); // WHERE clause

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
            while(mCursor.moveToNext()){ // todo: Why does assigning to selection return a -1 when outside loop? -1 mean column not found.
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
     * Calculate the actual id of the row selected.
     * @param position RV position tapped.
     * @return id number of entry.
     */
    private long getRecordId(int position){
        // Get the content resolver
        ContentResolver contentResolver = getContentResolver();

        String[] projection = {CourseContract.Columns._ID};
        String sortOrder =  null;
        String whereSelection = CourseContract.Columns.TERM_ID + "=" + term.getId();

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
     * Dialog box action when user accepts change.
     * @param dialogId Id of the dialog being displayed.
     * @param args Arguments for the dialog box.
     */
    @Override
    public void onDialogPositiveResponse(int dialogId, Bundle args) {
        long termId = args.getLong("TermId"); // get id from bundle

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
}