package com.example.meowminder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.meowminder.database.NoteDatabase;
import com.google.android.material.button.MaterialButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddNote extends AppCompatActivity implements ItemTouchHelperListener{
    private TextView backButton;
    private MaterialButton addButton;
    private ImageButton doneButton;

    private RecyclerView taskRcv;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;

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

    private LinearLayout ringtoneLayout;
    private String[] ringtones = {"Relaxing", "Relax sound", "Peaceful", "Lofi"};
    private AutoCompleteTextView autoCompleteTextView;
    private ArrayAdapter<String> arrayAdapter;
    private String ringtone = "";

    private Calendar calendar;
    public static final String CHANNEL_ID = "Test";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        getSupportActionBar().hide();

        //Initialize UI
        initializeUI();

        //Set up recycler view
        setUpRecyclerView();

        //Back to home page
        clickBackButton();

        //Add task button
        clickAddButton();

        //Remove task by swiping left
        removeTask();

        //Get instance calendar
        calendar = Calendar.getInstance();

        //Select date
        selectDate();

        //Select time
        selectTime();

        //Drop down box to select ringtone
        setDropbox();

        //Check if the user want to be reminded with alarm
        switchAlarmOnOff();

        //Done button
        clickDoneButton();
    }

    private void initializeUI() {
        backButton = (TextView) findViewById(R.id.back_button);
        addButton = (MaterialButton) findViewById(R.id.add_button);

        titleInput = (AppCompatEditText) findViewById(R.id.title_input);
        taskInput = (AppCompatEditText) findViewById(R.id.task_input);

        dateEdit = (AppCompatEditText) findViewById(R.id.date);
        timeEdit = (AppCompatEditText) findViewById(R.id.time);

        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.auto);

        ringtoneLayout = (LinearLayout) findViewById(R.id.ringtone_layout);
        alarmSwitch = (Switch) findViewById(R.id.alarm_switch);

        doneButton = (ImageButton) findViewById(R.id.done_button);

        taskRcv = (RecyclerView) findViewById(R.id.task_rcv);
    }

    private void setUpRecyclerView() {
        //Set grid layout
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        taskRcv.setLayoutManager(gridLayoutManager);

        //Initialize task array list
        taskList = new ArrayList<>();

        //Initialize and set adapter
        taskAdapter = new TaskAdapter(taskList);
        taskRcv.setAdapter(taskAdapter);
    }

    private void clickBackButton() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadHome();
            }
        });
    }

    private void loadHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void clickAddButton() {
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewTask();
            }
        });
    }

    private void addNewTask() {
        String taskName = taskInput.getText().toString().trim();
        boolean isDone = false;

        if (!taskName.isEmpty())
        {
            Task task = new Task(taskName, isDone);
            taskList.add(task);
            taskAdapter.notifyItemInserted(taskList.size()-1);
            taskInput.setText("");
            hideSoftKeyboard();
        }

        else
        {
            taskInput.setError("Task cannot be empty");
        }
    }

    private void hideSoftKeyboard() {
        try
        {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }

        catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    private void removeTask() {
        ItemTouchHelper.SimpleCallback simpleCallback = new RecyclerViewItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(taskRcv);
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

    private void selectDate() {
        //Set up date picker
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateCalendar();
            }

            private void updateCalendar() {
                String format = "dd/MM/yyyy";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
                dateEdit.setText(simpleDateFormat.format(calendar.getTime()));
            }
        };

        //Select date
        dateEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(AddNote.this, R.style.date_picker_theme, date, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void selectTime() {
        //Set up time picker
        TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                calendar.set(Calendar.HOUR, hour);
                calendar.set(Calendar.MINUTE, minute);
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

        //Select time
        timeEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TimePickerDialog(AddNote.this, R.style.time_picker_theme, time,
                        calendar.get(Calendar.HOUR),calendar.get(Calendar.MINUTE), false).show();
            }
        });
    }

    private void setDropbox() {
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.list_ringtone, ringtones);
        autoCompleteTextView.setAdapter(arrayAdapter);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
                ringtone = item;
            }
        });
    }

    private void switchAlarmOnOff() {
        alarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (alarmSwitch.isChecked())
                {
                    isAlarmOn = true;
                    ringtoneLayout.setVisibility(View.VISIBLE);
                }

                else
                {
                    isAlarmOn = false;
                    ringtoneLayout.setVisibility(View.GONE);
                }
            }
        });
    }

    private void clickDoneButton() {
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewNote();
            }
        });
    }

    private void addNewNote() {
        titleStr = titleInput.getText().toString().trim();
        dateStr = dateEdit.getText().toString().trim();
        timeStr = timeEdit.getText().toString().trim();

        if (titleStr.isEmpty() || dateStr.isEmpty() || timeStr.isEmpty() || taskList.size() == 0 || (isAlarmOn && ringtone.isEmpty()))
        {
            Toast.makeText(AddNote.this, "You need to fill all information", Toast.LENGTH_SHORT).show();
        }

        else
        {
            int intentCode = getNotificationId();
            setReminder(intentCode);
            Note note = new Note(titleStr, dateStr, timeStr, taskList, isAlarmOn, ringtone, Note.IS_NOT_DONE, intentCode);
            NoteDatabase.getInstance(this).noteDAO().insertNote(note);
            Toast.makeText(AddNote.this, "Add new note successfully", Toast.LENGTH_SHORT).show();
            loadHome();
        }
    }

    private void setReminder(int intentCode) {
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent;

        //Remind with ringtone
        if (isAlarmOn)
        {
            intent = new Intent(AddNote.this, AlarmReceiver_2.class);
        }

        //Remind without ringtone
        else
        {
            intent = new Intent(AddNote.this, AlarmReceiver.class);
        }

        //Send title name
        intent.putExtra("title", titleStr);
        intent.putExtra("ringtone", ringtone);

        //Create pending intent
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), intentCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Set date and time of the upcoming task
        String format = "dd/MM/yyyy hh:mm a";
        Calendar myAlarmDate = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        try {
            myAlarmDate.setTime(simpleDateFormat.parse(dateStr + " " + timeStr));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        alarmManager.set(AlarmManager.RTC_WAKEUP, myAlarmDate.getTimeInMillis(), pendingIntent);
    }

    private int getNotificationId() {
        return (int) new Date().getTime();
    }
}