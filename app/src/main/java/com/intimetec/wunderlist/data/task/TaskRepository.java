package com.intimetec.wunderlist.data.task;


import android.app.Application;

import com.intimetec.wunderlist.data.Repository;
import com.intimetec.wunderlist.data.WunderListDatabase;

import java.util.List;

public class TaskRepository implements Repository<Task> {

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

    public void addTasks(List<Task> allTasks) {
        mTaskDao.addTasks(allTasks);
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


    public List<Task> fetchAllFinishedTasks() {
        return mTaskDao.fetchAllFinishedToDos();
    }

    public List<Task> fetchTodoListByCategory(String categoryName) {
        return mTaskDao.fetchTodoListByCategory(categoryName);
    }

    public List<Task> fetchAllToDos() {
        return mTaskDao.fetchAllToDos();
    }


    public List<Task> fetchUserOrderByDateInDesc(String categoryType) {
        return mTaskDao.fetchUserOrderByDateInDesc(categoryType);
    }


    public List<Task> fetchUserOrderByDateInDesc() {
        return mTaskDao.fetchUserOrderByDateInDesc();
    }


    public List<Task> fetchUserOrderByDateInAsc(String categoryType) {
        return mTaskDao.fetchUserOrderByDateInAsc(categoryType);


    }

    public List<Task> fetchUserOrderByDateInAsc() {
        return mTaskDao.fetchUserOrderByDateInAsc();


    }

    public List<Task> fetchAllFinishedToDosInAsc() {
        return mTaskDao.fetchAllFinishedToDosInAsc();
    }

    public List<Task> fetchAllFinishedToDosInDesc() {
        return mTaskDao.fetchAllFinishedToDosInDesc();
    }


    public void deleteAllTasks() {
        mTaskDao.deleteAllTasks();
    }


}



