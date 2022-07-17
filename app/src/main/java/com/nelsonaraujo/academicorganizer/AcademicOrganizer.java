package com.nelsonaraujo.academicorganizer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.nelsonaraujo.academicorganizer.Controllers.AssessmentCtrl;
import com.nelsonaraujo.academicorganizer.Controllers.AssessmentsCtrl;
import com.nelsonaraujo.academicorganizer.Controllers.CoursesCtrl;
import com.nelsonaraujo.academicorganizer.Controllers.TermsCtrl;
import com.nelsonaraujo.academicorganizer.Models.Assessment;
import com.nelsonaraujo.academicorganizer.Models.AssessmentBroadcast;
import com.nelsonaraujo.academicorganizer.Models.AssessmentContract;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AcademicOrganizer extends AppCompatActivity {
    private static final String TAG = "AcademicOrganizer"; // For terminal logging

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    Button termsBtn;
    Button coursesBtn;
    Button assessmentsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.academic_organizer);

        // Setup startup notifications
        startUpNotification();

        // Terms
        termsBtn = findViewById(R.id.aoTermsBtn);
        termsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent termsIntent = new Intent(AcademicOrganizer.this, TermsCtrl.class);
                startActivity(termsIntent);
            }
        });

        // Courses
        coursesBtn = findViewById(R.id.aoCoursesBtn);
        coursesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent coursesIntent = new Intent(AcademicOrganizer.this, CoursesCtrl.class);
                startActivity(coursesIntent);
            }
        });

        // Assessments
        assessmentsBtn = findViewById(R.id.termMenuAssessmentsTv);
        assessmentsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent assessmentsIntent = new Intent(AcademicOrganizer.this, AssessmentsCtrl.class);
                startActivity(assessmentsIntent);
            }
        });
    }

    /**
     * Notify user if there are upcoming assessments starting in the next five days.
     */
    private void startUpNotification(){
        // Setup startup notifications
        ArrayList<Assessment> assessments = getAssessments();
        ArrayList<Assessment> upcomingAssessments = new ArrayList<Assessment>();
        String upcomingAssessmentsString = null;

        // Get upcoming assessments
        for(Assessment assessment : assessments){
            LocalDate currentDate = LocalDate.now();
            LocalDate maxDate = currentDate.plusDays(5);

            // Convert dates to date
            LocalDate start = LocalDate.parse(assessment.getStart());
            LocalDate end = LocalDate.parse(assessment.getStart());

            // Check for upcoming assessments
            if(start.isAfter(currentDate.minusDays(1)) && start.isBefore(maxDate)){
                upcomingAssessments.add(assessment);
                if(upcomingAssessmentsString == null){
                    upcomingAssessmentsString = assessment.getTitle() + "(" + assessment.getId() + "), ";
                } else {
                    upcomingAssessmentsString = upcomingAssessmentsString + assessment.getTitle()
                            + "(" + assessment.getId() + "), ";
                }
            }
        }

        // Broadcast if there are upcoming assessments
        if(upcomingAssessments.size() != 0) {
            createAssessmentNotificationChannel();

            Intent intent = new Intent(this, AssessmentBroadcast.class);

            intent.putExtra("assessment", upcomingAssessmentsString);


            PendingIntent pendingIntent = PendingIntent.getBroadcast(AcademicOrganizer.this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, 0, pendingIntent);
        }
    }

    /**
     * Create the assessment notification channel.
     */
    private void createAssessmentNotificationChannel(){
        CharSequence name = "upcomingAssessmentNotifier";
        String description = "Channel for upcoming assessments";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel("upcomingAssessmentNotifier", name, importance);
        channel.setDescription(description);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    /**
     * Get all assessments.
     * @return list of assessments.
     */
    private ArrayList<Assessment> getAssessments(){
        // Get the content resolver
        ContentResolver contentResolver = getContentResolver();

        // Setup projection
        String[] projection = {AssessmentContract.Columns._ID,
                AssessmentContract.Columns.TITLE,
                AssessmentContract.Columns.START,
                AssessmentContract.Columns.END,
                AssessmentContract.Columns.CONTENT,
                AssessmentContract.Columns.COURSE_ID};

        String whereSelection = null;
        String sortOrder = null;

        Cursor cursor = contentResolver.query(AssessmentContract.CONTENT_URI, projection, whereSelection, null, sortOrder);

        // Get Assessment
        ArrayList<Assessment> assessments = new ArrayList<Assessment>();
        if(cursor != null){
            while(cursor.moveToNext()){
                Assessment assessment = new Assessment(cursor.getLong(cursor.getColumnIndexOrThrow(AssessmentContract.Columns._ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(AssessmentContract.Columns.TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(AssessmentContract.Columns.START)),
                        cursor.getString(cursor.getColumnIndexOrThrow(AssessmentContract.Columns.END)),
                        cursor.getString(cursor.getColumnIndexOrThrow(AssessmentContract.Columns.CONTENT)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(AssessmentContract.Columns.COURSE_ID)));

                assessments.add(assessment);
            }
        }

//        Intent intent = new Intent(AssessmentsCtrl.this, AssessmentCtrl.class);
//        intent.putExtra(Assessment.class.getSimpleName(), selection);

        return assessments;
    }

}