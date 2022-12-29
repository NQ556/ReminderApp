package com.example.meowminder;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.List;

@Entity(tableName = "note")
public class Note {
    @PrimaryKey(autoGenerate = true)
    private int id;

    public static int IS_NOT_DONE = 1;
    public static int IS_DONE = 2;
    public static int IS_OVERDUE = 3;

    private String title;
    private String date;
    private String time;
    private List<Task> taskList;
    private boolean isAlarmOn;
    private int status;
    private String ringtone;

    public Note() {
        id = 0;
        title = "";
        date = "";
        time = "";
        taskList = null;
        isAlarmOn = false;
        status = IS_NOT_DONE;
        ringtone = "";
    }

    public Note(String title, String date, String time, List<Task> taskList, boolean isAlarmOn, String ringtone, int status) {
        this.title = title;
        this.date = date;
        this.time = time;
        this.taskList = taskList;
        this.isAlarmOn = isAlarmOn;
        this.ringtone = ringtone;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<Task> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
    }

    public String getFirstTask() {
        String taskName = "";

        if (taskList != null)
        {
            Task firstTask = taskList.get(0);
            taskName = firstTask.getTaskName();
        }

        return taskName;
    }

    public boolean getIsAlarmOn() {
        return isAlarmOn;
    }

    public void setIsAlarmOn(boolean isAlarmOn) {
        this.isAlarmOn = isAlarmOn;
    }

    public String getRingtone() {
        return ringtone;
    }

    public void setRingtone(String ringtone) {
        this.ringtone = ringtone;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}