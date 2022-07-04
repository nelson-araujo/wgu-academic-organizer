package com.nelsonaraujo.academicorganizer.Controllers;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.nelsonaraujo.academicorganizer.R;

public class TermListCourses extends AppCompatActivity {
    private static final String TAG = "AcademicOrganizer"; // For terminal logging

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.term_add_edit);
    }
}
