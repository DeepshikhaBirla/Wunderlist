package com.intimetec.wunderlist.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.intimetec.wunderlist.R;
import com.intimetec.wunderlist.data.task.Task;
import com.intimetec.wunderlist.data.task.TaskCategory;
import com.intimetec.wunderlist.data.task.TaskRepository;
import com.intimetec.wunderlist.data.user.User;
import com.intimetec.wunderlist.data.user.UserRepository;
import com.intimetec.wunderlist.util.DateUtil;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ToDoListActivity extends BaseActivity implements View.OnClickListener {
    ImageButton btnDatePicker, btnTimePicker;
    EditText txtName, txtDate, txtTime;
    private int mYear, mMonth, mDay, mHour, mMinute, mSecond;
    private Spinner categorySpinner;
    private Button saveBtn;
    private FirebaseFirestore db;
    ArrayAdapter<String> categoryAdapter;
    List<String> list;

    private TaskRepository mTaskRepository;
    private UserRepository mUserRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list);

        db = getFireStoreInstance();

        btnDatePicker = findViewById(R.id.calender_img_btn);
        btnTimePicker = findViewById(R.id.time_img_btn);

        txtName = findViewById(R.id.add_item_edt);
        txtDate = findViewById(R.id.due_date_edt);
        txtTime = findViewById(R.id.due_time_edt);
        saveBtn = findViewById(R.id.task_save_btn);

        mTaskRepository = new TaskRepository(getApplication());
        mUserRepository = new UserRepository(getApplication());

        txtDate.setOnClickListener(this);
        btnDatePicker.setOnClickListener(this);

        btnTimePicker.setOnClickListener(this);
        txtTime.setOnClickListener(this);

        categorySpinner = findViewById(R.id.task_spinner);
        list = getTaskCategories();

        categoryAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, list);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        saveBtn.setOnClickListener(this);


        Bundle data = getIntent().getExtras();
        if (data != null) {
            Task task = (Task) data.getParcelable("task");
            if (task != null) {
                txtName.setText(task.getTaskName());
                txtDate.setText(DateUtil.getDateValue(task.getDateTime()));
                txtTime.setText(DateUtil.getTimeValue(task.getDateTime()));
                TaskCategory taskCategory = TaskCategory.valueOf(task.getCategory());
                categorySpinner.setSelection(taskCategory.getPosition());

                saveBtn.setText("Update");
            }

        }
    }


    private List<String> getTaskCategories() {
        List<String> categories = new ArrayList<>();
        for (TaskCategory category : TaskCategory.values()) {
            categories.add(category.name());
        }

        return categories;
    }


    @Override
    public void onClick(View view) {
        if (view == btnDatePicker || view == txtDate) {

            // Get Current Date


            Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);
            mSecond = c.get(Calendar.SECOND);
            Date date = new Date();

            DateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy hh:mm:ss");
            String strdate = dateFormat.format(date);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {


                            txtDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();
        } else if (view == btnTimePicker || view == txtTime) {

            // Get Current Time

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

                            txtTime.setText(hourOfDay + ":" + minute);
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        } else if (view == saveBtn) {
            Bundle data = getIntent().getExtras();
            if (data != null) {
                Task task = (Task) data.getParcelable("task");
                if (task != null) {
                    attemptUpdate(task);
                }

            } else if (view == saveBtn) {
                attemptSave();
            }
        }
    }

    private void attemptSave() {
        String taskName = txtName.getText().toString().trim();
        String dateTime = txtDate.getText().toString().trim() + " " + txtTime.getText().toString() + ":00";

        txtName.setError(null);
        txtDate.setError(null);
        txtTime.setError(null);

        DateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        Date date = null;
        try {
            date = df.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(taskName)) {
            txtName.setError(getString(R.string.empty_field_error));
        } else if (TextUtils.isEmpty(dateTime)) {
            txtDate.setError(getString(R.string.empty_field_error));
        } else if (TextUtils.isEmpty(dateTime)) {
            txtTime.setError(getString(R.string.empty_field_error));
        } else {

            showProgressDialog();

            User user = mUserRepository.fetchUser();

            Task task = new Task();
            task.setTaskName(taskName);
            task.setDateTime(date);

            task.setUserId(user.getUserId());
            task.setCategory(categorySpinner.getSelectedItem().toString());
            task.setIsFinished(0);
            mTaskRepository.add(task);

            task = mTaskRepository.fetchTaskByName(taskName);

            db.collection("users").document(user.getUserEmail())
                    .collection("tasks")
                    .document(String.valueOf(task.getTaskId()))
                    .set(task)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            hideProgressDialog();
                            Toast.makeText(ToDoListActivity.this, "Task Updated Successfully", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            hideProgressDialog();
                            Toast.makeText(ToDoListActivity.this, "Failed to set data : " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

        }


    }


    private void attemptUpdate(final Task task) {
        String taskName = txtName.getText().toString().trim();
        String dateTime = txtDate.getText().toString().trim() + " " + txtTime.getText().toString() + ":00";

        txtName.setError(null);
        txtDate.setError(null);
        txtTime.setError(null);

        DateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        Date date = null;
        try {
            date = df.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(taskName)) {
            txtName.setError(getString(R.string.empty_field_error));
        } else if (TextUtils.isEmpty(dateTime)) {
            txtDate.setError(getString(R.string.empty_field_error));
        } else if (TextUtils.isEmpty(dateTime)) {
            txtTime.setError(getString(R.string.empty_field_error));
        } else {
            showProgressDialog();

            User user = mUserRepository.fetchUser();

            task.setTaskName(taskName);
            task.setDateTime(date);

            task.setCategory(categorySpinner.getSelectedItem().toString());

            db.collection("users").document(user.getUserEmail())
                    .collection("tasks")
                    .document(String.valueOf(task.getTaskId()))
                    .set(task)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            hideProgressDialog();
                            mTaskRepository.update(task);
                            Toast.makeText(ToDoListActivity.this, "Task Updated Successfully", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            hideProgressDialog();
                            Toast.makeText(ToDoListActivity.this, "Failed to set data : " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

        }

    }
}




