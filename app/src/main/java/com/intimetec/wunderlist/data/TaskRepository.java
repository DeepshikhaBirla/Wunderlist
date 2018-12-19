package com.intimetec.wunderlist.data;


import android.app.Application;

import java.util.List;

public class TaskRepository implements Repository<Task> {

    private TaskDao mTaskDao = null;
    private List<Task> allTasks = null;

    public TaskRepository(Application application) {
        TaskDatabase database = TaskDatabase.getInstance(application);
        mTaskDao = database.taskDao();
        allTasks = mTaskDao.fetchAllToDos();
    }


    @Override
    public void add(Task task) {
        mTaskDao.addTask(task);
    }

    @Override
    public void update(Task task) {
        mTaskDao.updateTask(task);
    }

    @Override
    public void delete(Task task) {
        mTaskDao.deleteTask(task);
    }

    @Override
    public List<Task> fetchAll() {
        return mTaskDao.fetchAllToDos();
    }
}
