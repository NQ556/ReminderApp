package com.example.meowminder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.meowminder.database.NoteDatabase;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class Alarm extends AppCompatActivity {
    private Note note;

    private ImageButton completeButton;
    private TextView cancelButton;

    private TextView title;
    private TextView textView;

    private String TAG = "Alarm";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        getSupportActionBar().hide();

        //Initialize UI
        initializeUI();

        //Get note from database
        getNoteFromDatabase();

        //Set title
        title.setText(note.getTitle());

        //Set number of finished tasks
        setFinishedTasks();

        //Mark as complete
        completeButton = (ImageButton) findViewById(R.id.complete_button);
        completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Stop ringtone
                stopRingtone();

                //Delete note from database
                NoteDatabase.getInstance(Alarm.this).noteDAO().deleteNote(note);

                //Load home
                loadHome();
            }
        });

        //Stop ringtone but not mark as complete
        cancelButton = (TextView) findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopRingtone();

                //Mark status as overdue
                note.setStatus(Note.IS_OVERDUE);
                NoteDatabase.getInstance(Alarm.this).noteDAO().updateNote(note);

                loadHome();
            }
        });
    }

    private void initializeUI() {
        title = (TextView) findViewById(R.id.title);
        textView = (TextView) findViewById(R.id.text);
    }

    private void getNoteFromDatabase() {
        int id = getIntent().getIntExtra("id", 0);
        note = NoteDatabase.getInstance(this).noteDAO().getNote(id);
    }

    private void setFinishedTasks() {
        List<Task> taskList = note.getTaskList();
        int allTasksNum = taskList.size();

        int finishedTasksNum = 0;
        for (int i = 0; i < allTasksNum; i++)
        {
            if (taskList.get(i).getIsDone())
            {
                finishedTasksNum++;
            }
        }

        String text = "You have completed " + Integer.toString(finishedTasksNum) + " \nout of "
                + Integer.toString(allTasksNum) + " of your tasks";

        textView.setText(text);
    }

    private void stopRingtone() {
        RingtoneControl.getInstance(getApplicationContext()).stopRingtone();
    }

    private void loadHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}