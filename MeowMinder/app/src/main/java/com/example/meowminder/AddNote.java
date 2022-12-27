package com.example.meowminder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class AddNote extends AppCompatActivity implements ItemTouchHelperListener{
    private TextView backButton;
    private MaterialButton addButton;
    private ImageButton doneButton;

    private RecyclerView taskRcv;
    private TaskAdapter taskAdapter;
    private ArrayList<Task> taskList;

    private AppCompatEditText titleInput;
    private AppCompatEditText dateEdit;
    private AppCompatEditText timeEdit;
    private AppCompatEditText taskInput;

    private String titleStr;
    private String dateStr;
    private String timeStr;
    private boolean isAlarmOn = false;

    private Switch alarmSwitch;
    private AlarmManager alarmManager;

    private int tmpDay;
    private int tmpMonth;
    private int tmpYear;
    private int tmpHour;
    private int tmpMinute;

    private Calendar calendar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        getSupportActionBar().hide();

        //Back to home page
        backButton = (TextView) findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadHome();
            }
        });

        //Initialize title input edit text
        titleInput = (AppCompatEditText) findViewById(R.id.title_input);

        //Initialize task edit text
        taskInput = (AppCompatEditText) findViewById(R.id.task_input);

        //Initialize recycler view
        taskRcv = (RecyclerView) findViewById(R.id.task_rcv);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        taskRcv.setLayoutManager(gridLayoutManager);

        //Initialize task array list
        taskList = new ArrayList<>();

        //Initialize and set adapter
        taskAdapter = new TaskAdapter(taskList);
        taskRcv.setAdapter(taskAdapter);

        //Add task button
        addButton = (MaterialButton) findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewTask();
            }
        });

        //Remove task by swiping left
        ItemTouchHelper.SimpleCallback simpleCallback = new RecyclerViewItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(taskRcv);

        //Get instance calendar
        calendar = Calendar.getInstance();

        //Set up date picker
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                tmpDay = dayOfMonth;
                tmpMonth = month;
                tmpYear = year;
                updateCalendar();
            }

            private void updateCalendar() {
                String format = "dd/MM/yyyy";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
                dateEdit.setText(simpleDateFormat.format(calendar.getTime()));
            }
        };

        //Set up time picker
        TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                calendar.set(Calendar.HOUR, hour);
                calendar.set(Calendar.MINUTE, minute);
                tmpHour = hour;
                tmpMinute = minute;
                updateCalendar(hour, minute);
            }

            private void updateCalendar(int hour, int minute) {
                String tmp = "";
                String format = "hh:mm";

                if (hour >= 12)
                {
                    tmp = "PM";
                }

                else
                {
                    tmp = "AM";
                }
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
                timeEdit.setText(simpleDateFormat.format(calendar.getTime()) + " " + tmp);
            }
        };

        //Select date
        dateEdit = (AppCompatEditText) findViewById(R.id.date);
        dateEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(AddNote.this, R.style.date_picker_theme, date, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        //Select time
        timeEdit = (AppCompatEditText) findViewById(R.id.time);
        timeEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TimePickerDialog(AddNote.this, R.style.time_picker_theme, time,
                        calendar.get(Calendar.HOUR),calendar.get(Calendar.MINUTE), false).show();
            }
        });

        //Set alarm
        createNotificationChannel();
        alarmSwitch = (Switch) findViewById(R.id.alarm_switch);
        alarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (alarmSwitch.isChecked())
                {
                    isAlarmOn = true;
                }

                else
                {
                    isAlarmOn = false;
                }
            }
        });

        //Done button
        doneButton = (ImageButton) findViewById(R.id.done_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAlarm();
            }
        });
    }

    private void loadHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void addNewTask() {
        String taskName = taskInput.getText().toString().trim();
        boolean isDone = false;

        if (!taskName.isEmpty())
        {
            Task task = new Task(taskName, isDone);
            taskList.add(task);
            taskAdapter.notifyItemInserted(taskList.size()-1);
            taskInput.getText().clear();
        }

        else
        {
            taskInput.setError("Task cannot be empty");
        }
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof TaskAdapter.TaskViewHolder)
        {
            int pos = viewHolder.getBindingAdapterPosition();
            //Delete task
            taskAdapter.deleteItem(pos);
        }
    }

    private void addNewNote() {
        titleStr = titleInput.getText().toString().trim();
        dateStr = dateEdit.getText().toString().trim();
        timeStr = timeEdit.getText().toString().trim();

        if (titleStr.isEmpty())
        {
            titleInput.setError("Title cannot be empty");
        }

        else if (dateStr.isEmpty())
        {
            dateEdit.setError("Date cannot be empty");
        }

        else if (timeStr.isEmpty())
        {
            timeEdit.setError("Time cannot be empty");
        }

        else if (taskList.size() == 0)
        {
            Toast.makeText(AddNote.this, "You need to add at least one task", Toast.LENGTH_SHORT).show();
        }

        else
        {
            Note note = new Note(titleStr, dateStr, timeStr, taskList, isAlarmOn);
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Channel_1";
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("Test", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void setAlarm() {
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(AddNote.this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(AddNote.this, 0, intent, 0);

        Calendar myAlarmDate = Calendar.getInstance();
        myAlarmDate.setTimeInMillis(System.currentTimeMillis());
        myAlarmDate.set(tmpYear, tmpMonth, tmpDay, tmpHour, tmpMinute, 0);

        alarmManager.set(AlarmManager.RTC_WAKEUP, myAlarmDate.getTimeInMillis(), pendingIntent);
        Toast.makeText(AddNote.this, "Set alarm successfully", Toast.LENGTH_SHORT).show();
    }
}