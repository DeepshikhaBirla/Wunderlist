package com.intimetec.wunderlist.data.task;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "Task")
public class Task {
    @PrimaryKey(autoGenerate = true)
    private int taskId;

    @ColumnInfo(name = "task_name")
    private String taskName;

    @ColumnInfo(name = "task_date")
    private String taskDate;

    @ColumnInfo(name = "task_time")
    private String taskTime;


    @ColumnInfo(name = "category")
    private String category;

    @ColumnInfo(name = "user_id")
    private String userId;

    public Task() {
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Task{" +
                "taskId=" + taskId +
                ", taskName='" + taskName + '\'' +
                ", taskDate='" + taskDate + '\'' +
                ", taskTime='" + taskTime + '\'' +
                ", category='" + category + '\'' +
                ", userId=" + userId +
                '}';
    }
}


