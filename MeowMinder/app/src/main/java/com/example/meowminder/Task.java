package com.example.meowminder;

public class Task {
    private String taskName;
    private boolean isDone;

    public Task() {
        taskName = "";
        isDone = false;
    }

    public Task(String taskName, boolean isDone) {
        this.taskName = taskName;
        this.isDone = isDone;
    }

    public String getTaskName() {
        return taskName;
    }

    public boolean getIsDone() {
        return isDone;
    }
}
