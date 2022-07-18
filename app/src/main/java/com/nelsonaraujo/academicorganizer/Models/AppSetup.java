package com.nelsonaraujo.academicorganizer.Models;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

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
            String dueTodayChannelName = "Tasks due today";
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
}
