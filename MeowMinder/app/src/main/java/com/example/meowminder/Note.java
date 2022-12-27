package com.example.meowminder;

import java.util.ArrayList;

public class Note {
    private String title;
    private String date;
    private String time;
    private ArrayList<Task> taskList;
    private boolean isAlarmOn;

    public Note() {
        title = "";
        date = "";
        time = "";
        taskList = null;
        isAlarmOn = false;
    }

    public Note(String title, String date, String time, ArrayList<Task> taskList, boolean isAlarmOn) {
        this.title = title;
        this.date = date;
        this.time = time;
        this.taskList = taskList;
        this.isAlarmOn = isAlarmOn;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public ArrayList<Task> getTaskList() {
        return taskList;
    }

    public boolean getIsAlarmOn() {
        return isAlarmOn;
    }
}
