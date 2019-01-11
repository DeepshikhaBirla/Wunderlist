package com.intimetec.wunderlist.ui;

import android.app.ActionBar;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.support.v7.widget.helper.ItemTouchHelper.SimpleCallback;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Adapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.intimetec.wunderlist.R;
import com.intimetec.wunderlist.adapter.TaskAdapter;
import com.intimetec.wunderlist.data.Repository;
import com.intimetec.wunderlist.data.task.Task;
import com.intimetec.wunderlist.data.task.TaskCategory;
import com.intimetec.wunderlist.data.task.TaskDao;
import com.intimetec.wunderlist.data.task.TaskRepository;
import com.intimetec.wunderlist.data.user.User;
import com.intimetec.wunderlist.data.user.UserRepository;
import com.intimetec.wunderlist.util.PreferenceManager;

import org.w3c.dom.Text;

import java.util.List;

import static android.support.v7.widget.helper.ItemTouchHelper.*;

public class HomeActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener {

    private RecyclerView mTaskRecyclerView;
    private DeleteAsyncTask mDeleteAsyncTask;
    private LoadTasksAsyncTask mLoadAsyncTask;
    private TaskRepository mTaskRepository;
    private FirebaseFirestore db;
    private SearchView searchView;
    private SignOutAsyncTask mSignOutAsyncTask;
    private TaskAdapter mTaskAdapter;
    private MenuItem ascendingMenuItem, descendingMenuItem;
    private String mCategoryType = TaskCategory.AllList.toString();
    private TextView mUserNameTxtview, mUserEmailTxtView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ImageButton fab = findViewById(R.id.fab);

        db = getFireStoreInstance();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, ToDoListActivity.class));
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mUserNameTxtview = navigationView.getHeaderView(0).findViewById(R.id.user_name);
        mUserEmailTxtView = navigationView.getHeaderView(0).findViewById(R.id.user_email);

        mTaskRecyclerView = findViewById(R.id.recycle_list);

        new ItemTouchHelper(new SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                ItemTouchHelper.LEFT) {

            @Override
            public void onMoved(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, int fromPos, RecyclerView.ViewHolder target, int toPos, int x, int y) {
                super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y);
                Toast.makeText(getApplicationContext(), " On Move pos : " + fromPos, Toast.LENGTH_SHORT).show();
            }

            public boolean onMove(RecyclerView recyclerView,
                                  RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                final int fromPos = viewHolder.getAdapterPosition();
                final int toPos = target.getAdapterPosition();

                return true;
            }
            //task is not visible according to their category

            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HomeActivity.this);
                alertDialogBuilder.setMessage("Are you sure, You wanted to Delete Task");
                alertDialogBuilder.setPositiveButton("yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                Task task = mTaskAdapter.getItem(viewHolder.getAdapterPosition());
                                mDeleteAsyncTask = new DeleteAsyncTask(task);
                                Toast.makeText(HomeActivity.this, "Task Deleted : " + task.getTaskName(), Toast.LENGTH_SHORT).show();
                                mTaskAdapter.removeItem(viewHolder.getAdapterPosition());
                                mDeleteAsyncTask.execute();
                            }
                        });

                alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mTaskAdapter.notifyDataSetChanged();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }


        }).attachToRecyclerView(mTaskRecyclerView);
        searchView = findViewById(R.id.action_search);
        searchView.setOnQueryTextListener(this);
        UserRepository userRepository = new UserRepository(getApplication());
        User user = userRepository.fetchUser();
        mUserNameTxtview.setText(user.getUserName());
        mUserEmailTxtView.setText(user.getUserEmail());

    }

    @Override
    public boolean onQueryTextSubmit(String newText) {

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mTaskAdapter.filter(newText);
        return false;
    }


    private class DeleteAsyncTask extends AsyncTask<Void, Void, Void> {
        private TaskRepository taskRepository;
        private Task task;


        DeleteAsyncTask(Task task) {
            this.task = task;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            taskRepository = new TaskRepository(getApplication());
            taskRepository.delete(task);
            return null;
        }

        @Override
        protected void onPostExecute(Void Void) {
            super.onPostExecute(Void);
            mTaskAdapter.notifyDataSetChanged();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        mLoadAsyncTask = new LoadTasksAsyncTask(mCategoryType);
        mLoadAsyncTask.execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mLoadAsyncTask != null) {
            mLoadAsyncTask.cancel(true);
            mLoadAsyncTask = null;
        }
    }

    private class SignOutAsyncTask extends AsyncTask<Void, Void, Void> {
        private TaskRepository taskRepository;
        private UserRepository userRepository;

        @Override
        protected Void doInBackground(Void... voids) {
            taskRepository = new TaskRepository(getApplication());
            userRepository = new UserRepository(getApplication());
            taskRepository.deleteAllTasks();
            userRepository.deleteUser();
            return null;
            }

        @Override
        protected void onPostExecute(Void Void) {
            super.onPostExecute(Void);
            PreferenceManager.setUserLogin(HomeActivity.this, false);
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
        }
    }

    /**
     * Class to load All tasks data from database
     */

    private class LoadTasksAsyncTask extends AsyncTask<Void, Void, List<Task>> {

        List<Task> allTask = null;
        String param = TaskCategory.AllList.toString();

        public LoadTasksAsyncTask(String param) {
            this.param = param;
        }

        @Override
        protected List<Task> doInBackground(Void... voids) {

            if (param.equals(TaskCategory.AllList.toString())) {
                allTask = new TaskRepository(getApplication()).fetchAll();
            } else if (param.equals(TaskCategory.IsFinished.toString())) {
                allTask = new TaskRepository(getApplication()).fetchAllFinishedTasks();
            } else {
                allTask = new TaskRepository(getApplication()).fetchTodoListByCategory(param);
            }
            return allTask;
        }

        @Override
        protected void onPostExecute(List<Task> allTask) {
            super.onPostExecute(allTask);

            mTaskRecyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
            mTaskAdapter = new TaskAdapter(allTask, getApplication(), getApplicationContext());
            mTaskRecyclerView.setAdapter(mTaskAdapter);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.home, menu);
        ascendingMenuItem = menu.findItem(R.id.ascending_list_menu);
        descendingMenuItem = menu.findItem(R.id.descending_list_menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        mTaskRepository = new TaskRepository(getApplication());
        //noinspection SimplifiableIfStatement
        if (id == R.id.ascending_list_menu) {
            descendingMenuItem.setChecked(true);
            ascendingMenuItem.setChecked(false);
            List<Task> allTasks = mTaskRepository.fetchUserOrderByDateInAsc();
            mTaskAdapter = new TaskAdapter(allTasks, getApplication(), getApplicationContext());
            mTaskRecyclerView.setAdapter(mTaskAdapter);

        } else if (id == R.id.descending_list_menu) {
            ascendingMenuItem.setChecked(true);
            descendingMenuItem.setChecked(false);
            List<Task> allTasks = mTaskRepository.fetchUserOrderByDateInDesc();
            mTaskAdapter = new TaskAdapter(allTasks, getApplication(), getApplicationContext());
            mTaskRecyclerView.setAdapter(mTaskAdapter);


        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        mTaskRepository = new TaskRepository(getApplication());

        if (id == R.id.all_list) {
            mCategoryType = TaskCategory.AllList.toString();
            mLoadAsyncTask = new LoadTasksAsyncTask(mCategoryType);
            mLoadAsyncTask.execute();

            // Handle the camera action
        } else if (id == R.id.nav_default) {
            mCategoryType = TaskCategory.Default.toString();
            mLoadAsyncTask = new LoadTasksAsyncTask(mCategoryType);
            mLoadAsyncTask.execute();
        } else if (id == R.id.nav_personal) {
            mCategoryType = TaskCategory.Personal.toString();
            mLoadAsyncTask = new LoadTasksAsyncTask(mCategoryType);
            mLoadAsyncTask.execute();

        } else if (id == R.id.nav_shopping) {
            mCategoryType = TaskCategory.Shopping.toString();
            mLoadAsyncTask = new LoadTasksAsyncTask(mCategoryType);
            mLoadAsyncTask.execute();

        } else if (id == R.id.nav_wishlist) {
            mCategoryType = TaskCategory.WishList.toString();
            mLoadAsyncTask = new LoadTasksAsyncTask(mCategoryType);
            mLoadAsyncTask.execute();

        } else if (id == R.id.nav_work) {
            mCategoryType = TaskCategory.Work.toString();
            mLoadAsyncTask = new LoadTasksAsyncTask(mCategoryType);
            mLoadAsyncTask.execute();

        } else if (id == R.id.nav_finished) {
            mCategoryType = TaskCategory.IsFinished.toString();
            mLoadAsyncTask = new LoadTasksAsyncTask(mCategoryType);
            mLoadAsyncTask.execute();

        } else if (id == R.id.nav_signout) {
            mSignOutAsyncTask = new SignOutAsyncTask();
            mSignOutAsyncTask.execute();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void getData() {
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("HomeActivity", document.getId() + " => " + document.getData());
                                User user = document.toObject(User.class);
                                if (user != null) {
                                    System.out.println(user.toString());

                                }
                            }

                        } else {
                            Log.w("HomeActivity", "Error Getting Data", task.getException());
                        }
                    }
                });


    }
}

