package com.nelsonaraujo.academicorganizer.Controllers;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.nelsonaraujo.academicorganizer.R;

public class Courses extends AppCompatActivity {
    private static final String TAG = "AcademicOrganizer"; // For terminal logging

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.courses);
    }
}