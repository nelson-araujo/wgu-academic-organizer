package com.nelsonaraujo.academicorganizer.Models;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.nelsonaraujo.academicorganizer.R;

public class AssessmentBroadcast extends BroadcastReceiver {
    private static final String TAG = "AssessmentBroadcast";

    @Override
    public void onReceive(Context context, Intent intent) {
        String upcomingAssessments = intent.getStringExtra("assessment");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"upcomingAssessmentNotifier")
                .setSmallIcon(R.drawable.ic_logo)
                .setContentTitle("Upcoming Assessment(s)")
                .setContentText(upcomingAssessments)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager.notify(200,builder.build());
    }
}
