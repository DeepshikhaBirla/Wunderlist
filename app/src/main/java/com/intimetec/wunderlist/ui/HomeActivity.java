package com.intimetec.wunderlist.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.support.v7.widget.helper.ItemTouchHelper.SimpleCallback;
import android.util.Log;
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
import android.widget.ImageButton;
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
import com.intimetec.wunderlist.data.task.TaskDao;
import com.intimetec.wunderlist.data.task.TaskRepository;
import com.intimetec.wunderlist.data.user.User;
import com.intimetec.wunderlist.util.PreferenceManager;

import java.util.List;

import static android.support.v7.widget.helper.ItemTouchHelper.*;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView mTaskRecyclerView;
    private DeleteAsyncTask mDeleteAsyncTask;
    private LoadTasksAsyncTask mLoadAsyncTask;

    private FirebaseFirestore db;
    private TaskAdapter mTaskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ImageButton fab = findViewById(R.id.fab);


        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
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

        mTaskRecyclerView = findViewById(R.id.recycle_list);



        new ItemTouchHelper(new SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                ItemTouchHelper.LEFT) {
            public  boolean onMove(RecyclerView recyclerView,
                                   RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                final int fromPos = viewHolder.getAdapterPosition();
                final int toPos = target.getAdapterPosition();

                return true;
            }

            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                Task task = mTaskAdapter.getItem(viewHolder.getAdapterPosition());
                mTaskAdapter.removeItem(viewHolder.getAdapterPosition());

                Toast.makeText(HomeActivity.this, "Task Deleted : "+task.getTaskName(), Toast.LENGTH_SHORT).show();

                mDeleteAsyncTask = new DeleteAsyncTask(task);
                mDeleteAsyncTask.execute();
            }

        }).attachToRecyclerView(mTaskRecyclerView);

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
        mLoadAsyncTask = new LoadTasksAsyncTask();
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


    /**
     * Class to load All tasks data from database
     */

    private class LoadTasksAsyncTask extends AsyncTask<Void, Void, List<Task>> {

        List<Task> allTask = null;

        @Override
        protected List<Task> doInBackground(Void... voids) {

            allTask = new TaskRepository(getApplication()).fetchAll();
            return allTask;
        }

        @Override
        protected void onPostExecute(List<Task> allTask) {
            super.onPostExecute(allTask);

            mTaskRecyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
            mTaskAdapter = new TaskAdapter(allTask);
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.all_list) {
            // Handle the camera action
        } else if (id == R.id.nav_default) {

        } else if (id == R.id.nav_personal) {

        } else if (id == R.id.nav_shopping) {

        } else if (id == R.id.nav_wishlist) {

        } else if (id == R.id.nav_work) {

        } else if (id == R.id.nav_signout) {
            PreferenceManager.setUserLogin(HomeActivity.this, false);
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
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

