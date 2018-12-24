package com.intimetec.wunderlist.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.intimetec.wunderlist.data.user.UserDao;

@Database(entities = {Task.class}, version = 1, exportSchema = false)
public abstract class TaskDatabase extends RoomDatabase {

    public abstract TaskDao taskDao();
    public abstract UserDao userDao();

    private static final String APP_DATABASE = "ToDoDatabase";

    private static TaskDatabase sTaskDatabaseInstance = null;

    public static TaskDatabase getInstance(Context context) {
        if (sTaskDatabaseInstance == null) {
            sTaskDatabaseInstance = buildDatabase(context);
        }
        return sTaskDatabaseInstance;
    }

    private synchronized static TaskDatabase buildDatabase(Context context) {
        sTaskDatabaseInstance = Room.databaseBuilder(context, TaskDatabase.class, APP_DATABASE)
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries().build();

        return sTaskDatabaseInstance;
    }

}
