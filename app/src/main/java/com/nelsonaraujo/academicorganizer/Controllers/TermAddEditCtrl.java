package com.nelsonaraujo.academicorganizer.Controllers;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nelsonaraujo.academicorganizer.Models.DatePickerFragment;
import com.nelsonaraujo.academicorganizer.Models.Term;
import com.nelsonaraujo.academicorganizer.Models.TermContract;
import com.nelsonaraujo.academicorganizer.R;

import java.text.ParseException;
import java.util.GregorianCalendar;
import java.util.Locale;

public class TermAddEditCtrl extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, DatePickerDialog.OnDateSetListener {
    private static final String TAG = "TermAddEditCtrl"; // For terminal logging

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    // Date picker selection
    public static final int DIALOG_START_DATE = 1;
    public static final int DIALOG_END_DATE = 2;
    private final Integer ERROR_BG_COLOR = Color.parseColor("#ffe8e7");

    // Edit mode type
    public enum EditMode { EDIT, ADD }
    private EditMode mMode;

    private TextView mTermEt;
    private EditText mStartEt;
    private EditText mEndEt;
    private FloatingActionButton mSaveFab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.term_add_edit);

        mTermEt = findViewById(R.id.taeTitleEt);
        mStartEt = findViewById(R.id.taeStartEt);
        mEndEt = findViewById(R.id.taeEndEt);
        mSaveFab = findViewById(R.id.taeSaveFab);

        Bundle arguments = getIntent().getExtras(); // Get the arguments from the term bundle.

        final Term term;
        if(arguments != null){
            term = (Term) arguments.getSerializable(Term.class.getSimpleName()); // Get the actual task to confirm it exists
            if(term != null){
                mTermEt.setText(term.getTitle());
                mStartEt.setText(term.getStart());
                mEndEt.setText(term.getEnd());
                mMode = EditMode.EDIT;
            } else {
                mMode = EditMode.ADD;
            }
        } else {
            term = null;
            mMode = EditMode.ADD;
        }

        // Start date listener
        mStartEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String startDate = null;
                if(term != null){
                    startDate = term.getStart();
                }
                showDatePickerDialog("Start Date", DIALOG_START_DATE, startDate);
            }
        });

        // End date listener
        mEndEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String endDate = null;
                if(term != null){
                    endDate = term.getEnd();
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
                Boolean fieldsValid = isFieldsValid();

                if(fieldsValid) {
                    switch (mMode) {
                        case EDIT:
                            if (!mTermEt.getText().toString().equals(term.getTitle())) { // Check if there was a change
                                values.put(TermContract.Columns.TITLE, mTermEt.getText().toString());
                            }
                            if (!mStartEt.getText().toString().equals(term.getStart())) {
                                values.put(TermContract.Columns.START, mStartEt.getText().toString());
                            }
                            if (!mEndEt.getText().toString().equals(term.getEnd())) {
                                values.put(TermContract.Columns.END, mEndEt.getText().toString());
                            }

                            if (values.size() != 0) {
                                contentResolver.update(TermContract.buildTermUri(term.getId()), values, null, null);
                            }

                            finish();
                            break;
                        case ADD:
                            if (mTermEt.length() > 0) {
                                values.put(TermContract.Columns.TITLE, mTermEt.getText().toString());
                                values.put(TermContract.Columns.START, mStartEt.getText().toString());
                                values.put(TermContract.Columns.END, mEndEt.getText().toString());
                                contentResolver.insert(TermContract.CONTENT_URI, values);
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
     * Check if required fields are valid.
     * @return true if valid otherwise false.
     */
    private Boolean isFieldsValid(){
        Boolean isValid = true;

        if(mTermEt.getText().length() == 0){
            mTermEt.setBackgroundColor(ERROR_BG_COLOR);
            isValid = false;
        } else { mTermEt.setBackgroundColor(Color.TRANSPARENT); }

        if(mStartEt.getText().length() == 0){
            mStartEt.setBackgroundColor(ERROR_BG_COLOR);
            isValid = false;
        } else { mStartEt.setBackgroundColor(Color.TRANSPARENT); }

        if(mEndEt.getText().length() == 0){
            mEndEt.setBackgroundColor(ERROR_BG_COLOR);
            isValid = false;
        } else { mEndEt.setBackgroundColor(Color.TRANSPARENT); }

        return isValid;
    }
}