package com.example.meowminder;

import android.content.Context;
import android.media.MediaPlayer;
import android.provider.Settings;

public class RingtoneControl {
    private static RingtoneControl ringtoneControl;
    private Context context;
    private MediaPlayer mediaPlayer;

    public RingtoneControl(Context context) {
        this.context = context;
    }

    public static RingtoneControl getInstance(Context context) {
        if (ringtoneControl == null)
        {
            ringtoneControl = new RingtoneControl(context);
        }
        return ringtoneControl;
    }

    public void playRingtone() {
        mediaPlayer = MediaPlayer.create(context, Settings.System.DEFAULT_RINGTONE_URI);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    public void stopRingtone() {
        if (mediaPlayer != null)
        {
            mediaPlayer.stop();
        }
    }
}
