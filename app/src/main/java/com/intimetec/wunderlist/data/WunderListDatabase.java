package com.intimetec.wunderlist.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.intimetec.wunderlist.data.task.Task;
import com.intimetec.wunderlist.data.task.TaskDao;
import com.intimetec.wunderlist.data.user.User;
import com.intimetec.wunderlist.data.user.UserDao;

@Database(entities = {Task.class, User.class}, version = 1, exportSchema = false)
public abstract class WunderListDatabase extends RoomDatabase {

    public abstract TaskDao taskDao();
    public abstract UserDao userDao();

    private static final String APP_DATABASE = "ToDoDatabase";

    private static WunderListDatabase sWunderListDatabaseInstance = null;

    public static WunderListDatabase getInstance(Context context) {
        if (sWunderListDatabaseInstance == null) {
            sWunderListDatabaseInstance = buildDatabase(context);
        }
        return sWunderListDatabaseInstance;
    }

    private synchronized static WunderListDatabase buildDatabase(Context context) {
        sWunderListDatabaseInstance = Room.databaseBuilder(context, WunderListDatabase.class, APP_DATABASE)
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries().build();

        return sWunderListDatabaseInstance;
    }

}
