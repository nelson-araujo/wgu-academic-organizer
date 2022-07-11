package com.nelsonaraujo.academicorganizer.Controllers;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.nelsonaraujo.academicorganizer.Models.Term;

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
    }
}
