package com.example.meowminder;

import android.content.Context;
import android.media.MediaPlayer;

public class RingtoneControl {
    private static RingtoneControl instance;
    private Context context;
    private MediaPlayer mediaPlayer;

    public RingtoneControl(Context context) {
        this.context = context;
    }

    public static RingtoneControl getInstance(Context context) {
        if (instance == null)
        {
            instance = new RingtoneControl(context);
        }
        return instance;
    }

    public void playRingtone(String ringtone) {
        int id = getId(ringtone);
        mediaPlayer = MediaPlayer.create(context, id);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    public void stopRingtone() {
        if (mediaPlayer != null)
        {
            mediaPlayer.stop();
        }
    }

    private int getId(String ringtone) {
        switch (ringtone)
        {
            case "Lofi":
                return R.raw.lofi;
            case "Peaceful":
                return R.raw.peaceful;
            case "Relax sound":
                return R.raw.relax_sound;
            case "Relaxing":
                return R.raw.relaxing;
            default:
                return 0;
        }
    }
}
