package com.example.meowminder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

public class Alarm extends AppCompatActivity {
    private ImageButton completeButton;
    private TextView cancelButton;

    private TextView title;
    private String titleStr;

    private String TAG = "Alarm";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        getSupportActionBar().hide();

        //Initialize text view title
        title = (TextView) findViewById(R.id.title);

        //Get title and set title
        titleStr = getIntent().getStringExtra("title");
        title.setText(titleStr);

        //Mark as complete
        completeButton = (ImageButton) findViewById(R.id.complete_button);
        completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopRingtone();
                loadHome();
            }
        });

        //Stop ringtone but not mark as complete
        cancelButton = (TextView) findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopRingtone();
                loadHome();
            }
        });
    }

    private void stopRingtone() {
        RingtoneControl.getInstance(getApplicationContext()).stopRingtone();
    }

    private void loadHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}