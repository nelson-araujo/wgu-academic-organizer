package com.nelsonaraujo.academicorganizer.Models;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.nelsonaraujo.academicorganizer.R;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * DECOM Service is no longer used.
 * Application service for application-out-of-focus notifications.
 */
public class AppService extends Service {
    private static final String TAG = "AppService";

    public static final String CHN_DUE_TODAY = "notificationChannelDueToday";
    private int messageId = 100;

    public AppService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        notifyStartEndToday();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Notify user of start or ending assessments or courses.
     */
    private void notifyStartEndToday(){
        ArrayList<Assessment> assessments = getAllAssessments();
        ArrayList<Course> courses = getAllCourses();

        // Check for assessments starting or ending today
        for(Assessment assessment : assessments){
            LocalDate currentDate = LocalDate.now();

            // Convert dates to date
            LocalDate start = LocalDate.parse(assessment.getStart());
            LocalDate end = LocalDate.parse(assessment.getStart());

            // Check start
            if(start.isEqual(currentDate)){
                notifyUser(assessment.getTitle() + " starts today.");
            }

            // Check for end
            if(end.isEqual(currentDate)){
                notifyUser(assessment.getTitle() + " ends today.");
            }
        }

        // Check for courses starting or ending today
        for(Course course : courses){
            LocalDate currentDate = LocalDate.now();

            // Convert dates to date
            LocalDate start = LocalDate.parse(course.getStart());
            LocalDate end = LocalDate.parse(course.getStart());

            // Check start
            if(start.isEqual(currentDate)){
                notifyUser(course.getTitle() + " starts today.");
            }

            // Check for end
            if(end.isEqual(currentDate)){
                notifyUser(course.getTitle() + " ends today.");
            }
        }
    }

    /**
     * Get all assessments on the system.
     * @return list of assessments.
     */
    private ArrayList<Assessment> getAllAssessments(){
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

        return assessments;
    }

    /**
     * Get all courses on the system.
     * @return list of courses.
     */
    private ArrayList<Course> getAllCourses(){
        // Get the content resolver
        ContentResolver contentResolver = getContentResolver();

        // Setup projection
        String[] projection = {CourseContract.Columns._ID,
                CourseContract.Columns.TITLE,
                CourseContract.Columns.START,
                CourseContract.Columns.END,
                CourseContract.Columns.STATUS,
                CourseContract.Columns.NOTE,
                CourseContract.Columns.TERM_ID,
                CourseContract.Columns.INSTRUCTOR_ID};

        // Query database
        Cursor cursor = contentResolver.query(CourseContract.CONTENT_URI,projection,null,null);

        // Populate array list
        ArrayList<Course> courses = new ArrayList<Course>();
        if(cursor != null){
            while(cursor.moveToNext()){
                Course course = new Course(cursor.getLong(cursor.getColumnIndexOrThrow(CourseContract.Columns._ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(CourseContract.Columns.TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(CourseContract.Columns.START)),
                        cursor.getString(cursor.getColumnIndexOrThrow(CourseContract.Columns.END)),
                        cursor.getString(cursor.getColumnIndexOrThrow(CourseContract.Columns.STATUS)),
                        cursor.getString(cursor.getColumnIndexOrThrow(CourseContract.Columns.NOTE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(CourseContract.Columns.TERM_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(CourseContract.Columns.INSTRUCTOR_ID)));

                courses.add(course);
            }
        }

        cursor.close();

        return courses;
    }

    /**
     * Notify the user of activity.
     * @param message message to display to the user.
     */
    private void notifyUser(String message){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHN_DUE_TODAY)
                .setSmallIcon(R.drawable.ic_logo)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(messageId++,builder.build());
    }

}