package com.nelsonaraujo.academicorganizer.Controllers;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nelsonaraujo.academicorganizer.Models.Term;

import com.nelsonaraujo.academicorganizer.Models.TermContract;
import com.nelsonaraujo.academicorganizer.R;

public class TermCtrl extends AppCompatActivity {
    private static final String TAG = "TermCtrl"; // For terminal logging

    private TextView mTermTv;
    private TextView mStartTv;
    private TextView mEndTv;

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
        final Term term;
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
    }

    private void onEditFabClick(Term term){
        // Display term
        Intent termIntent = new Intent(this, TermAddEdit.class);
        termIntent.putExtra(Term.class.getSimpleName(), term);
        startActivity(termIntent);
    }

    private void onDeleteFabClick(Term term){
        getContentResolver().delete(TermContract.buildTermUri(term.getId()), null, null);
        finish();
    }
}