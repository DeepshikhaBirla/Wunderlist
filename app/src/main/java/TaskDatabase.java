import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.intimetec.wunderlist.ui.Task;


@Database(entities = (Task.class),version = 1)
public abstract class TaskDatabase extends RoomDatabase
{
    public abstract TaskDao taskDao();
}
