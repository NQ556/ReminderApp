package com.example.meowminder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.meowminder.database.NoteDatabase;
import com.google.android.material.button.MaterialButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
    private LinearLayout deleteButton;

    private RecyclerView taskRcv;
    private List<Task> taskList;
    private TaskViewAdapter taskViewAdapter;

    private TextView onOff;
    private Switch alarmSwitch;
    private AppCompatEditText ringtone;

    private TextView resetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_note);
        getSupportActionBar().hide();

        //Initialize UI
        initializeUI();

        //Show correct layout for different status
        showLayout();

        //Get note from database
        getNoteFromDatabase();

        //Show all note information
        showNoteInformation();

        //Back button
        clickBackButton();

        //Edit button
        clickEditButton();

        //Delete button
        clickDeleteButton();

        //Click yes button
        clickYesButton();

        //Click reset button for overdue note
        clickResetButton();
    }

    private void initializeUI() {
        backButton = (ImageButton) findViewById(R.id.back_button);
        yesButton = (ImageButton) findViewById(R.id.yes_button);
        editButton = (LinearLayout) findViewById(R.id.edit_button);
        deleteButton = (LinearLayout) findViewById(R.id.delete_button);

        date = (TextView) findViewById(R.id.date);
        time = (TextView) findViewById(R.id.time);
        title = (TextView) findViewById(R.id.title);

        taskRcv = (RecyclerView) findViewById(R.id.task_rcv);

        onOff = (TextView) findViewById(R.id.on_off);
        alarmSwitch = (Switch) findViewById(R.id.alarm_switch);
        ringtone = (AppCompatEditText) findViewById(R.id.ringtone);

        resetButton = (TextView) findViewById(R.id.reset_button);
    }

    private void showLayout() {
        int type = getIntent().getIntExtra("status", 0);

        if (type == Note.IS_NOT_DONE)
        {
            editButton.setVisibility(View.VISIBLE);
            resetButton.setVisibility(View.GONE);
        }

        else if (type == Note.IS_OVERDUE)
        {
            editButton.setVisibility(View.GONE);
            resetButton.setVisibility(View.VISIBLE);
        }
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
                //Update task's status and load homepage
                updateTaskStatus(false);
            }
        });
    }

    private void loadEditNote() {
        Intent intent = new Intent(this, EditNote.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    private void clickEditButton() {
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Update task's status
                updateTaskStatus(true);
                //Load edit note
                loadEditNote();
            }
        });
    }

    private void clickDeleteButton() {
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteNote();
            }
        });
    }

    private void deleteNote() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm delete note")
                .setMessage("Do you want to delete this note?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        cancelReminder();
                        NoteDatabase.getInstance(ViewNote.this).noteDAO().deleteNote(note);
                        Toast.makeText(ViewNote.this, "Delete note successfully", Toast.LENGTH_SHORT).show();
                        loadHome();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void clickYesButton() {
        String msg = "Do you want to mark this note as done?";

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                markAsDone(msg, false);
            }
        });
    }

    private void markAsDone(String msg, boolean isBackHome) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm mark as done")
                .setMessage(msg)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        cancelReminder();
                        NoteDatabase.getInstance(ViewNote.this).noteDAO().deleteNote(note);
                        Toast.makeText(ViewNote.this, "Congratulate on finishing all of your tasks", Toast.LENGTH_SHORT).show();
                        loadHome();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (isBackHome)
                        {
                            loadHome();
                        }
                    }
                })
                .show();
    }

    private void cancelReminder() {
        //Recreate pending intent
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent;

        //Remind with ringtone
        if (note.getIsAlarmOn())
        {
            intent = new Intent(ViewNote.this, AlarmReceiver_2.class);
        }

        //Remind without ringtone
        else
        {
            intent = new Intent(ViewNote.this, AlarmReceiver.class);
        }

        //Send title name
        intent.putExtra("title", note.getTitle());
        intent.putExtra("ringtone", note.getRingtone());

        //Create pending intent
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), note.getIntentCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Set date and time of the upcoming task
        String format = "dd/MM/yyyy hh:mm a";
        Calendar myAlarmDate = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        try {
            myAlarmDate.setTime(simpleDateFormat.parse(note.getDate() + " " + note.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        alarmManager.set(AlarmManager.RTC_WAKEUP, myAlarmDate.getTimeInMillis(), pendingIntent);

        //Cancel pending intent
        alarmManager.cancel(pendingIntent);
    }

    private void clickResetButton() {
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateTaskStatus(true);
                loadEditNote();
            }
        });
    }

    private boolean checkAllTasks(List<Task> list) {
        for (int i = 0; i < list.size(); i++)
        {
            if (!list.get(i).getIsDone())
            {
                return false;
            }
        }

        return true;
    }

    private void updateTaskStatus(boolean isEdit) {
        note.setTaskList(taskList);
        NoteDatabase.getInstance(ViewNote.this).noteDAO().updateNote(note);

        if (!isEdit)
        {
            if (checkAllTasks(taskList))
            {
                String msg = "You have completed all tasks. Do you want to mark this note as done?";
                markAsDone(msg, true);
            }

            else
            {
                loadHome();
            }
        }
    }

    //Override back button on phone
    @Override
    public void onBackPressed() {
        updateTaskStatus(false);
    }
}