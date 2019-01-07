package com.intimetec.wunderlist.data.task;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;
import android.os.Parcel;
import android.os.Parcelable;

import com.intimetec.wunderlist.data.TimestampConverter;

import java.util.Date;

@Entity(tableName = "Task")
public class Task implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private int taskId;

    @ColumnInfo(name = "task_name")
    private String taskName;

    @ColumnInfo(name = "date_time")
    private Date dateTime;

    @ColumnInfo(name = "category")
    private String category;

    @ColumnInfo(name = "user_id")
    private String userId;

    @ColumnInfo(name = "is_finished")
    private int isFinished;


    public Task() {
    }

    protected Task(Parcel in) {
        taskId = in.readInt();
        taskName = in.readString();

        long tmpDate = in.readLong();
        this.dateTime = tmpDate == -1 ? null : new Date(tmpDate);

        category = in.readString();
        userId = in.readString();
        isFinished = in.readInt();
    }

    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel in) {

            return new Task(in);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

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

    public int getIsFinished() {
        return isFinished;
    }

    public void setIsFinished(int isFinished) {
        this.isFinished = isFinished;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public String toString() {
        return "Task{" +
                "taskId=" + taskId +
                ", taskName='" + taskName + '\'' +
                ", dateTime='" + dateTime + '\'' +
                ", category='" + category + '\'' +
                ", userId=" + userId +
                ",isFinished=" + isFinished +

                '}';
    }

    public int getChecked() {
        return isFinished;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(taskId);
        parcel.writeString(taskName);
        parcel.writeLong(dateTime != null ? dateTime.getTime() : -1);
        parcel.writeString(category);
        parcel.writeString(userId);
        parcel.writeInt(isFinished);
    }

}



