import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.google.firebase.firestore.auth.User;
import com.intimetec.wunderlist.ui.Task;

import java.util.List;

@Dao
public interface TaskDao
{

        @Insert
        public void addTask(Task task);

        @Query("select * from task")
        public List<Task> gettask();

        @Delete
        public void deleteTask(Task task);

        @Update
        public void updateTask(Task task );

        @Query("SELECT * FROM task")
        List<Task> fetchAllTodos();

        @Query("SELECT * FROM task WHERE category = :category")
        List<User> fetchTodoListByCategory(String category);

        @Query("SELECT * FROM task WHERE task_id = `Task Name`")
        User fetchTodoListById(int todoId);

        @Query("SELECT * FROM task WHERE category LIKE :search " +
                "OR task_id LIKE :search")
        public List<User> findCategoryWithtaskName(String search);


    }

