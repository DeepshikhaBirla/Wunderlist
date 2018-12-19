package com.intimetec.wunderlist.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "Task")
public class Task {
    @PrimaryKey(autoGenerate = true)
    private int task_id;

    @ColumnInfo(name = "task_name")
    private String taskName;

    @ColumnInfo(name = "task_date")
    private String taskDate;

    @ColumnInfo(name = "task_time")
    private String taskTime;


    @ColumnInfo(name = "category")
    private String category;

    @ColumnInfo(name = "user_id")
    private int userId;

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

    public String getTaskDate() {
        return taskDate;
    }

    public void setTaskDate(String taskDate) {
        this.taskDate = taskDate;
    }

    public String getTaskTime() {
        return taskTime;
    }

    public void setTaskTime(String taskTime) {
        this.taskTime = taskTime;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Task{" +
                "task_id=" + task_id +
                ", taskName='" + taskName + '\'' +
                ", taskDate='" + taskDate + '\'' +
                ", taskTime='" + taskTime + '\'' +
                ", category='" + category + '\'' +
                ", userId=" + userId +
                '}';
    }
}


