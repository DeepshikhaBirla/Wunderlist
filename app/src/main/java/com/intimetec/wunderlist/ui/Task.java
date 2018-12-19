package com.intimetec.wunderlist.ui;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "task")
public class Task {
    @PrimaryKey
    private int task_id;

    @ColumnInfo(name = "Task Name")
    private String taskName;

    @ColumnInfo(name = "Task Date")
    private int taskDate;

    @ColumnInfo(name = "Task Time")
    private int taskTime;

    @ColumnInfo(name = "Category")
    private String category;

    @PrimaryKey(autoGenerate = true)
    private int UserId;

    public int getTask_id() {
        return task_id;
    }

    public void setTask_id(int task_id) {
        this.task_id = task_id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public int getTaskDate() {
        return taskDate;
    }

    public void setTaskDate(int taskDate) {
        this.taskDate = taskDate;
    }

    public int getTaskTime() {
        return taskTime;
    }

    public void setTaskTime(int taskTime) {
        this.taskTime = taskTime;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getUserId() {
        return UserId;
    }

    public void setUserId(int userId) {
        UserId = userId;
    }
}







