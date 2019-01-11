package com.intimetec.wunderlist.data.task;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.intimetec.wunderlist.data.user.User;

import java.util.List;

@Dao
public interface TaskDao {

    @Insert
    public void addTask(Task task);

    @Delete
    public void deleteTask(Task task);

    @Update
    public void updateTask(Task task);


    @Query("SELECT * FROM task WHERE is_finished = 0")
    public List<Task> fetchAllToDos();

    @Query("SELECT * FROM task WHERE is_finished = 1")
    public List<Task> fetchAllFinishedToDos();

    @Query("SELECT * FROM task WHERE category = :category AND is_finished = 0")
    public List<Task> fetchTodoListByCategory(String category);

    @Query("SELECT * FROM task WHERE taskId = :toDoId")
    public Task fetchTodoById(int toDoId);

    @Query("SELECT * FROM task WHERE task_name = :taskName")
    public Task fetchTodoByName(String taskName);



    @Query("SELECT * FROM task WHERE category LIKE :search & is_finished = 0")
    public List<Task> fetchTasksByCategoryName(String search);

    @Query("DELETE FROM task")
    void deleteAll();

    @Query("SELECT * FROM Task WHERE is_finished = 0 ORDER BY date_time ASC")
    List<Task> fetchUserOrderByDateInAsc();

    @Query("SELECT * FROM Task WHERE is_finished = 0 ORDER BY date_time DESC")
    List<Task> fetchUserOrderByDateInDesc();

    @Query("DELETE  FROM task")
    public void deleteAllTasks();


}

