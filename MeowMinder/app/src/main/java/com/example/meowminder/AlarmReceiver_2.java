package com.example.meowminder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Date;

public class AlarmReceiver_2 extends BroadcastReceiver {
    private String TAG = "Receiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        //Create alarm intent
        Intent intent1 = new Intent(context, Alarm.class);

        //Get title name, ringtone
        String titleStr = intent.getStringExtra("title");
        String ringtoneStr = intent.getStringExtra("ringtone");

        //Send title name, ringtone to Alarm
        intent1.putExtra("title", titleStr);
        intent1.putExtra("ringtone", ringtoneStr);

        //Play ringtone
        RingtoneControl.getInstance(context).playRingtone(ringtoneStr);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, getNotificationId(), intent1, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(context, AddNote.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Notification")
                .setContentText("Your task is due")
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setFullScreenIntent(pendingIntent, true)
                .build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null)
        {
            notificationManager.notify(getNotificationId(), notification);
        }
    }

    private int getNotificationId() {
        return (int) new Date().getTime();
    }
}
