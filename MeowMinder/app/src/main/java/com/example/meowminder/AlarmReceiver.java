package com.example.meowminder;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.meowminder.database.NoteDatabase;

import java.util.Date;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //Get note's id
        int id = intent.getIntExtra("id", 0);

        //Get note from database
        Note note = NoteDatabase.getInstance(context).noteDAO().getNote(id);

        //Check status
        if (note.getStatus() == Note.IS_NOT_DONE)
        {
            note.setStatus(Note.IS_OVERDUE);
            NoteDatabase.getInstance(context).noteDAO().updateNote(note);
        }

        else if (note.getStatus() == Note.IS_DONE)
        {
            NoteDatabase.getInstance(context).noteDAO().deleteNote(note);
        }

        //Create notification
        Notification notification = new NotificationCompat.Builder(context, AddNote.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Notification")
                .setContentText("Your task is due")
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
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
