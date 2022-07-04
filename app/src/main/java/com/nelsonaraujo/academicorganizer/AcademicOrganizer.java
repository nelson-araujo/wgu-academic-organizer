package com.nelsonaraujo.academicorganizer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.nelsonaraujo.academicorganizer.Controllers.Assessments;
import com.nelsonaraujo.academicorganizer.Controllers.Courses;
import com.nelsonaraujo.academicorganizer.Controllers.Terms;

public class AcademicOrganizer extends AppCompatActivity {
    private static final String TAG = "AcademicOrganizer"; // For terminal logging
    Button termsBtn;
    Button coursesBtn;
    Button assessmentsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.academic_organizer);

        // Get database
//        AppDatabase appDatabase = AppDatabase.getInstance(this);
//        final SQLiteDatabase db = appDatabase.getReadableDatabase();

//        Log.d(TAG, "onCreate: ----- START -----"); // todo: remove
//        String [] projection = {TermContract.Columns._ID, TermContract.Columns.TERM_TITLE,
//                                TermContract.Columns.TERM_START, TermContract.Columns.TERM_END};
//        ContentResolver contentResolver = getContentResolver();
//        Cursor cursor = contentResolver.query(TermContract.CONTENT_URI,
////        Cursor cursor = contentResolver.query(TermContract.buildTermUri(3),
//                projection,
//                null,
//                null,
//                TermContract.Columns.TERM_TITLE);
//
//        if(cursor != null){
//            while(cursor.moveToNext()){
//                for( int i=0 ; i < cursor.getColumnCount() ; i++){
//                    Log.d(TAG, "onCreate: " + cursor.getColumnName(i) + ": " + cursor.getString(i)); // todo: remove
//                }
//                Log.d(TAG, "onCreate: ----"); // todo: remove
//            }
//            cursor.close();
//        }


        // Terms
        termsBtn = findViewById(R.id.aoTermsBtn);
        termsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent termsIntent = new Intent(AcademicOrganizer.this, Terms.class);
                startActivity(termsIntent);
            }
        });

        // Courses
        coursesBtn = findViewById(R.id.aoCoursesBtn);
        coursesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent coursesIntent = new Intent(AcademicOrganizer.this, Courses.class);
                startActivity(coursesIntent);
            }
        });

        // Assessments
        assessmentsBtn = findViewById(R.id.termMenuAssessmentsTv);
        assessmentsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent assessmentsIntent = new Intent(AcademicOrganizer.this, Assessments.class);
                startActivity(assessmentsIntent);
            }
        });
    }

}