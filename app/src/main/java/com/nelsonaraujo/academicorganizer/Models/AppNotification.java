package com.nelsonaraujo.academicorganizer.Models;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.nelsonaraujo.academicorganizer.R;

public class AppNotification extends BroadcastReceiver{
//public class AppNotification extends BroadcastReceiver{
    private static final String TAG = "AppNotification";

    public static final String CHN_UPCOMING_ASSESSMENT = "notificationChannelAssessment";
    public static final String CHN_DUE_TODAY = "notificationChannelDueToday";

    public static final String TYPE = "Notification type";
    public static final String MESSAGE = "Notification message";
    public static final String TYPE_UPCOMING_ASSESSMENT = "Upcoming assessment";
    public static final String TYPE_COURSE = "Course due today";
    public static final String TYPE_ASSESSMENT = "Assessment due today";


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "AppNotification onReceive:::::::::: START"); // todo: remove

        String notificationType = intent.getStringExtra(TYPE);
        String notificationMessage = intent.getStringExtra(MESSAGE);

        switch(notificationType){
            case TYPE_UPCOMING_ASSESSMENT:
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHN_UPCOMING_ASSESSMENT)
                        .setSmallIcon(R.drawable.ic_logo)
                        .setContentTitle(TYPE_UPCOMING_ASSESSMENT)
                        .setContentText(notificationMessage)
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                notificationManager.notify(201,builder.build());
                break;

            case TYPE_COURSE:
                // todo: update
                break;

            case TYPE_ASSESSMENT:
                // todo: update
                break;

            default:
                throw new IllegalStateException("AppNotification: Unknown notification type: " + notificationType);
        }
    }
}
