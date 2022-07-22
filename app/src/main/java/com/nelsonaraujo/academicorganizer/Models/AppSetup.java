package com.nelsonaraujo.academicorganizer.Models;

import android.app.ActivityManager;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

/**
 * Configuration to be applied when the application starts up.
 */
public class AppSetup extends Application {
    private static final String TAG = "AppSetup";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannels();

    }

    /**
     * Create application's notification channels.
     */
    private void createNotificationChannels(){
        // Confirm Android version is Oreo (25) or higher.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            // Setup the assessments channel
            String assessmentChannelName = "Upcoming assessments";
            String assessmentChannelDescription = "Display assessments starting in the next five(5) days.";

            NotificationChannel assessmentChannel = new NotificationChannel(
                    AppNotification.TYPE_UPCOMING_ASSESSMENT,
                    assessmentChannelName,
                    NotificationManager.IMPORTANCE_DEFAULT);

            assessmentChannel.setDescription(assessmentChannelDescription);

            // Setup the due today channel
            String dueTodayChannelName = "Start or end reminders";
            String dueTodayChannelDescription = "Display the start or end reminders set by the user.";

            NotificationChannel dueTodayChannel = new NotificationChannel(
                    AppNotification.CHN_START_END,
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
