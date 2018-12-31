package com.intimetec.wunderlist.data.user;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;


@Dao
public interface UserDao {


    @Insert
    public void addUser(User user);

    @Delete
    public void deleteUser(User user);

    @Update
    public void updateUser(User user);

    @Query("SELECT * FROM user")
    public List<User> fetchAll();

    @Query("SELECT * from user LIMIT 1")
    public User fetchUser();

}
