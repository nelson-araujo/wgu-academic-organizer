package com.nelsonaraujo.academicorganizer.Models;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.nelsonaraujo.academicorganizer.R;

/**
 * In application notifications.
 */
public class AppNotification extends BroadcastReceiver{
    private static final String TAG = "AppNotification";

    public static final String CHN_UPCOMING_ASSESSMENT = "notificationChannelAssessment";
    public static final String CHN_START_END = "notificationChannelStartEnd";

    public enum START_OR_END {start,end};

    public static final String NOTIFICATION_TYPE = "Notification type";
    public static final String NOTIFICATION_MESSAGE = "Notification message";
    public static final String TYPE_UPCOMING_ASSESSMENT = "Upcoming assessment";
    public static final String TYPE_START_END = "Start or end alert";

    private static int notificationId = 200;

    @Override
    public void onReceive(Context context, Intent intent) {
        String notificationType = intent.getStringExtra(NOTIFICATION_TYPE);
        String notificationMessage = intent.getStringExtra(NOTIFICATION_MESSAGE);

        switch(notificationType){
            case TYPE_UPCOMING_ASSESSMENT:
                NotificationCompat.Builder upcomingAssessmentBuilder = new NotificationCompat.Builder(context, CHN_UPCOMING_ASSESSMENT)
                        .setSmallIcon(R.drawable.ic_logo)
                        .setContentTitle(TYPE_UPCOMING_ASSESSMENT)
                        .setContentText(notificationMessage)
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                notificationManager.notify(notificationId++,upcomingAssessmentBuilder.build());
                break;

            case TYPE_START_END:
                Notification notification = new NotificationCompat.Builder(context,CHN_START_END)
                        .setSmallIcon(R.drawable.ic_logo)
                        .setContentText(notificationMessage).build();

                NotificationManager alarmNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                alarmNotificationManager.notify(notificationId++, notification);
                break;

            default:
                throw new IllegalStateException("AppNotification: Unknown notification type: " + notificationType);
        }
    }
}
