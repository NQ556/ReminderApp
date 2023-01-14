package com.example.meowminder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
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

public class EditNote extends AppCompatActivity implements ItemTouchHelperListener{
    private int id;
    private Note note;

    private AppCompatEditText titleInput;
    private AppCompatEditText dateEdit;
    private AppCompatEditText timeEdit;

    private RecyclerView taskRcv;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;

    private AppCompatEditText taskInput;

    private MaterialButton addButton;

    private Calendar calendar;

    private Switch alarmSwitch;
    private boolean isAlarmOn;
    private LinearLayout ringtoneLayout;
    private AlarmManager alarmManager;

    private AutoCompleteTextView autoCompleteTextView;
    private String[] ringtones = {"Relaxing", "Relax sound", "Peaceful", "Lofi"};
    private ArrayAdapter<String> arrayAdapter;

    private String titleStr;
    private String dateStr;
    private String timeStr;
    private String ringtoneStr = "";

    private ImageButton doneButton;
    private TextView backButton;

    private Date deadlineDate;
    private Date currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        getSupportActionBar().hide();

        //Initialize UI
        initializeUI();

        //Get note from database
        getNoteFromDatabase();

        //Show information
        showNoteInformation();

        //Add new task
        clickAddButton();

        //Remove task by swiping left
        removeTask();

        //Get instance calendar
        calendar = Calendar.getInstance();

        //Select date
        selectDate();

        //Select time
        selectTime();

        //Set drop box
        setDropbox();

        //Choose alarm ringtone
        chooseAlarmRingtone();

        //Done button
        clickDoneButton();

        //Back button
        clickBackButton();
    }

    private void initializeUI() {
        titleInput = (AppCompatEditText) findViewById(R.id.title_input);
        dateEdit = (AppCompatEditText) findViewById(R.id.date);
        timeEdit = (AppCompatEditText) findViewById(R.id.time);

        taskRcv = (RecyclerView) findViewById(R.id.task_rcv);
        taskInput = (AppCompatEditText) findViewById(R.id.task_input);

        addButton = (MaterialButton) findViewById(R.id.add_button);
        doneButton = (ImageButton) findViewById(R.id.done_button);
        backButton = (TextView) findViewById(R.id.back_button);

        alarmSwitch = (Switch) findViewById(R.id.alarm_switch);
        ringtoneLayout = (LinearLayout) findViewById(R.id.ringtone_layout);
        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.auto);
    }

    private void getNoteFromDatabase() {
        id = getIntent().getIntExtra("id", 0);
        note = NoteDatabase.getInstance(this).noteDAO().getNote(id);
    }

    private void showNoteInformation() {
        titleInput.setText(note.getTitle());
        dateEdit.setText(note.getDate());
        timeEdit.setText(note.getTime());

        setUpRecyclerView();

        showAlarm();
    }

    private void setUpRecyclerView() {
        //Set grid layout
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        taskRcv.setLayoutManager(gridLayoutManager);

        //Initialize task array list
        taskList = new ArrayList<>();
        taskList = note.getTaskList();

        //Initialize and set adapter
        taskAdapter = new TaskAdapter(taskList);
        taskRcv.setAdapter(taskAdapter);
    }

    private void showAlarm() {
        if (note.getIsAlarmOn())
        {
            alarmSwitch.setChecked(true);
            ringtoneLayout.setVisibility(View.VISIBLE);
            autoCompleteTextView.setText(note.getRingtone());
            ringtoneStr = note.getRingtone();
        }

        else
        {
            alarmSwitch.setChecked(false);
            ringtoneLayout.setVisibility(View.GONE);
        }
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
        String task = taskInput.getText().toString().trim();
        boolean isDone = false;

        if (task.isEmpty())
        {
            taskInput.setError("Task cannot be empty");
        }

        else
        {
            Task newTask = new Task(task, isDone);
            taskList.add(newTask);
            taskAdapter.notifyItemInserted(taskList.size()-1);
            taskInput.setText("");
            hideSoftKeyboard();
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
                new DatePickerDialog(EditNote.this, R.style.date_picker_theme, date, calendar.get(Calendar.YEAR),
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
                new TimePickerDialog(EditNote.this, R.style.time_picker_theme, time,
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
                ringtoneStr = item;
            }
        });
    }

    private void chooseAlarmRingtone() {
        alarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (alarmSwitch.isChecked())
                {
                    ringtoneLayout.setVisibility(View.VISIBLE);
                }

                else
                {
                    ringtoneLayout.setVisibility(View.GONE);
                }
            }
        });
    }

    private boolean isValid() {
        if (titleStr.isEmpty() || dateStr.isEmpty() || timeStr.isEmpty() || taskList.isEmpty() || (isAlarmOn && ringtoneStr.isEmpty()))
        {
            return false;
        }

        return true;
    }

    private boolean isValidDateTime() {
        String format = "dd/MM/yyyy hh:mm a";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);

        //Get deadline date
        try {
            deadlineDate = simpleDateFormat.parse(dateStr + " " + timeStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //Get current date
        try {
            currentDate = simpleDateFormat.parse(simpleDateFormat.format(new Date()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //Check if deadline date is after than current date or not
        if (deadlineDate.after(currentDate))
        {
            return true;
        }

        else
        {
            return false;
        }
    }

    private void editNote() {
        titleStr = titleInput.getText().toString().trim();
        dateStr = dateEdit.getText().toString().trim();
        timeStr = timeEdit.getText().toString().trim();
        isAlarmOn = alarmSwitch.isChecked();

        if (!isValid())
        {
            Toast.makeText(this, "You need to fill all information", Toast.LENGTH_SHORT).show();
        }

        else if (!isValidDateTime())
        {
            Toast.makeText(this, "The deadline cannot be before today", Toast.LENGTH_SHORT).show();
        }

        else
        {
            //Re-pending intent if user change date, time, alarm or title with alarm on
            if (!timeStr.equals(note.getTime()) || !dateStr.equals(note.getDate())
            || (isAlarmOn && (!ringtoneStr.equals(note.getRingtone())))
            || (isAlarmOn && (!titleStr.equals(note.getTitle()))) || isAlarmOn != note.getIsAlarmOn())
            {
                int intentCode = note.getIntentCode();
                setReminder(intentCode);
            }

            //Update note
            note.setTitle(titleStr);
            note.setDate(dateStr);
            note.setTime(timeStr);
            note.setTaskList(taskList);
            note.setIsAlarmOn(isAlarmOn);
            note.setRingtone(ringtoneStr);

            //Update in database
            NoteDatabase.getInstance(this).noteDAO().updateNote(note);

            //Load note view
            loadViewNote();
        }
    }

    private void setReminder(int intentCode) {
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent;

        //Remind with ringtone
        if (isAlarmOn)
        {
            intent = new Intent(EditNote.this, AlarmReceiver_2.class);
        }

        //Remind without ringtone
        else
        {
            intent = new Intent(EditNote.this, AlarmReceiver.class);
        }

        //Send title name
        intent.putExtra("title", titleStr);
        intent.putExtra("ringtone", ringtoneStr);

        //Create pending intent
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), intentCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Set date and time of the upcoming task
        Calendar myAlarmDate = Calendar.getInstance();
        myAlarmDate.setTime(deadlineDate);
        alarmManager.set(AlarmManager.RTC_WAKEUP, myAlarmDate.getTimeInMillis(), pendingIntent);
    }

    private void clickDoneButton() {
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editNote();
            }
        });
    }

    private void loadViewNote() {
        Intent intent = new Intent(this, ViewNote.class);
        intent.putExtra("id", note.getId());
        startActivity(intent);
    }

    private void clickBackButton() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadViewNote();
            }
        });
    }
}