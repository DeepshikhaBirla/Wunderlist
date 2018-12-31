package com.intimetec.wunderlist.data.task;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface TaskDao {

    @Insert
    public void addTask(Task task);

    @Delete
    public void deleteTask(Task task);

    @Update
    public void updateTask(Task task);

    @Query("SELECT * FROM task")
    public List<Task> fetchAllToDos();

    @Query("SELECT * FROM task WHERE category = :category")
    public List<Task> fetchTodoListByCategory(String category);

    @Query("SELECT * FROM task WHERE taskId = :toDoId")
    public Task fetchTodoById(int toDoId);

    @Query("SELECT * FROM task WHERE task_name = :taskName")
    public Task fetchTodoByName(String taskName);



    @Query("SELECT * FROM task WHERE category LIKE :search " +
            "OR taskId LIKE :search")
    public List<Task> findCategoryWithTaskName(String search);

    @Query("DELETE FROM task")
    void deleteAll();
}

