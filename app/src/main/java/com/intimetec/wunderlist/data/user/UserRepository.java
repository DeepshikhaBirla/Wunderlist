package com.intimetec.wunderlist.data.user;

import android.app.Application;

import com.intimetec.wunderlist.data.Repository;
import com.intimetec.wunderlist.data.TaskDatabase;



import java.util.List;

public class UserRepository implements Repository<User> {
    private UserDao mUserDao = null;
    private List<User> allUser = null;

    public UserRepository(Application application) {
        TaskDatabase database = TaskDatabase.getInstance(application);
        mUserDao = database.userDao();
        allUser = mUserDao.fetchAll();

    }

    @Override
    public void add(User user) {
        mUserDao.addUser(user);
    }

    @Override
    public void update(User user) {
        mUserDao.updateUser(user);
    }

    @Override
    public void delete(User user) {
        mUserDao.deleteUser(user);
    }

    @Override
    public List<User> fetchAll() {
        return mUserDao.fetchAll();
    }

    public User fetchUser() {
        return mUserDao.fetchUser();
    }
}
