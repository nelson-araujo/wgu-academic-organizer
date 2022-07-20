package com.nelsonaraujo.academicorganizer.Models;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

import android.app.ActivityManager;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.util.Log;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Configuration to be applied when the application starts up.
 */
public class AppSetup extends Application {
    private static final String TAG = "AppSetup";

    public static final String CHN_ASSESSMENT = "notificationChannelAssessment";
    public static final String CHN_DUE_TODAY = "notificationChannelDueToday";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannels();

        // Check if AppService is running, if not start it.
        if(!isMyServiceRunning(AppService.class)){
            Log.d(TAG, "AppSetup onCreate :::: AppService is not running, starting.");
            startService(new Intent(this, AppService.class));
        }

    }

    /**
     * Create application's notification channels.
     */
    private void createNotificationChannels(){
        Log.d(TAG, "createNotificationChannels: STARTED"); // todo: remove

        // Confirm Android version is Oreo (25) or higher.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            // Setup the assessments channel
            String assessmentChannelName = "Upcoming Assessments";
            String assessmentChannelDescription = "Displays assessments in the next five(5) days.";

            NotificationChannel assessmentChannel = new NotificationChannel(
                    CHN_ASSESSMENT,
                    assessmentChannelName,
                    NotificationManager.IMPORTANCE_DEFAULT);

            assessmentChannel.setDescription(assessmentChannelDescription);

            // Setup the due today channel
            String dueTodayChannelName = "Due today";
            String dueTodayChannelDescription = "Displays tasks that are due today.";

            NotificationChannel dueTodayChannel = new NotificationChannel(
                    CHN_DUE_TODAY,
                    dueTodayChannelName,
                    NotificationManager.IMPORTANCE_DEFAULT);

            assessmentChannel.setDescription(dueTodayChannelDescription);

            // Create channels
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(assessmentChannel);
            notificationManager.createNotificationChannel(dueTodayChannel);

        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
