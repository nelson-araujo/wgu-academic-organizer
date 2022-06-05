package com.nelsonaraujo.academicorganizer.Controllers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.database.Cursor;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;

import com.nelsonaraujo.academicorganizer.Models.TermContract;
import com.nelsonaraujo.academicorganizer.R;

public class Assessments extends AppCompatActivity {
    private static final String TAG = "AcademicOrganizer"; // For terminal logging

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.assessments);
    }
}