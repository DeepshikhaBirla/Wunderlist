package com.intimetec.wunderlist.data.task;


import android.app.Application;

import com.intimetec.wunderlist.data.Repository;
import com.intimetec.wunderlist.data.WunderListDatabase;

import java.util.List;

public class TaskRepository implements Repository<Task>{

    private TaskDao mTaskDao = null;
    private List<Task> allTasks = null;
    private Repository mRepository;


    public TaskRepository(Application application) {
        WunderListDatabase database = WunderListDatabase.getInstance(application);
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

    public Task fetchTaskByName(String taskName) {
        return mTaskDao.fetchTodoByName(taskName);
    }

    public Task fetchTaskById(int taskId) {
        return mTaskDao.fetchTodoById(taskId);
    }

    @Override
    public List<Task> fetchAll() {
        return mTaskDao.fetchAllToDos();
    }
    }



