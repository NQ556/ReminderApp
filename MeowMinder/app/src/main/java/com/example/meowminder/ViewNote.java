package com.example.meowminder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.example.meowminder.database.NoteDatabase;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class ViewNote extends AppCompatActivity {
    private Note note;
    private int id;

    private TextView date;
    private TextView time;
    private TextView title;

    private ImageButton backButton;
    private LinearLayout editButton;
    private ImageView yesButton;

    private RecyclerView taskRcv;
    private List<Task> taskList;
    private TaskViewAdapter taskViewAdapter;

    private TextView onOff;
    private Switch alarmSwitch;
    private AppCompatEditText ringtone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_note);
        getSupportActionBar().hide();

        //Initialize UI
        initializeUI();

        //Get note from database
        getNoteFromDatabase();

        //Show all note information
        showNoteInformation();

        //Back button
        clickBackButton();

        //Edit button
        clickEditButton();
    }

    private void initializeUI() {
        backButton = (ImageButton) findViewById(R.id.back_button);
        yesButton = (ImageButton) findViewById(R.id.yes_button);
        editButton = (LinearLayout) findViewById(R.id.edit_button);

        date = (TextView) findViewById(R.id.date);
        time = (TextView) findViewById(R.id.time);
        title = (TextView) findViewById(R.id.title);

        taskRcv = (RecyclerView) findViewById(R.id.task_rcv);

        onOff = (TextView) findViewById(R.id.on_off);
        alarmSwitch = (Switch) findViewById(R.id.alarm_switch);
        ringtone = (AppCompatEditText) findViewById(R.id.ringtone);
    }

    private void getNoteFromDatabase() {
        //Get note's id and note
        id = getIntent().getIntExtra("id", 0);
        note = NoteDatabase.getInstance(this).noteDAO().getNote(id);
    }

    private void setUpRecyclerView() {
        //Set grid layout
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        taskRcv.setLayoutManager(gridLayoutManager);

        //Initialize task array list
        taskList = new ArrayList<>();
        taskList = note.getTaskList();

        //Initialize and set adapter
        taskViewAdapter = new TaskViewAdapter(taskList);
        taskRcv.setAdapter(taskViewAdapter);
    }

    private void showNoteInformation() {
        //Show date and time
        date.setText(note.getDate());
        time.setText(note.getTime());

        //Show title
        title.setText(note.getTitle());

        //Show all tasks
        setUpRecyclerView();

        //Show alarm
        checkIfAlarmIsOn();
    }

    private void checkIfAlarmIsOn() {
        if (note.getIsAlarmOn())
        {
            alarmSwitch.setChecked(true);
            ringtone.setText(note.getRingtone());
            ringtone.setVisibility(View.VISIBLE);
        }

        else
        {
            alarmSwitch.setChecked(false);
            ringtone.setVisibility(View.INVISIBLE);
        }
    }

    private void loadHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void clickBackButton() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadHome();
            }
        });
    }

    private void loadEditNote() {
        Intent intent = new Intent(this, EditNote.class);
        startActivity(intent);
    }

    private void clickEditButton() {
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadEditNote();
            }
        });
    }
}