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
    Button termsBtn;
    Button coursesBtn;
    Button assessmentsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.academic_organizer);

        // Terms
        termsBtn = findViewById(R.id.termsBtn);
        termsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent termsIntent = new Intent(AcademicOrganizer.this, Terms.class);
                startActivity(termsIntent);
            }
        });

        // Courses
        coursesBtn = findViewById(R.id.coursesBtn);
        coursesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent coursesIntent = new Intent(AcademicOrganizer.this, Courses.class);
                startActivity(coursesIntent);
            }
        });

        // Assessments
        assessmentsBtn = findViewById(R.id.assessmentsBtn);
        assessmentsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent assessmentsIntent = new Intent(AcademicOrganizer.this, Assessments.class);
                startActivity(assessmentsIntent);
            }
        });
    }
}