package com.nelsonaraujo.academicorganizer.Controllers;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nelsonaraujo.academicorganizer.Models.Term;
import com.nelsonaraujo.academicorganizer.Models.TermContract;
import com.nelsonaraujo.academicorganizer.R;

public class TermAddEditCtrl extends AppCompatActivity {
    private static final String TAG = "TermAddEditCtrl"; // For terminal logging

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

        // Save FAB Action
        mSaveFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentResolver contentResolver = getContentResolver();
                ContentValues values = new ContentValues();

                switch(mMode){
                    case EDIT:
                        if(!mTermEt.getText().toString().equals(term.getTitle())){ // Check if there was a change
                            values.put(TermContract.Columns.TITLE, mTermEt.getText().toString());
                        }
                        if(!mStartEt.getText().toString().equals(term.getStart())){
                            values.put(TermContract.Columns.START, mStartEt.getText().toString());
                        }
                        if(!mEndEt.getText().toString().equals(term.getEnd())){
                            values.put(TermContract.Columns.END, mEndEt.getText().toString());
                        }

                        if(values.size() != 0) {
                            contentResolver.update(TermContract.buildTermUri(term.getId()),values,null,null);
                            finish();
                            break;
                        }
                    case ADD:
                        if(mTermEt.length() > 0){
                            values.put(TermContract.Columns.TITLE, mTermEt.getText().toString());
                            values.put(TermContract.Columns.START, mStartEt.getText().toString());
                            values.put(TermContract.Columns.END, mEndEt.getText().toString());
                            contentResolver.insert(TermContract.CONTENT_URI,values);
                        }
                        finish();
                        break;
                }
            }
        });

    }
}