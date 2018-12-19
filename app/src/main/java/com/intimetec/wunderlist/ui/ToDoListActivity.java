package com.intimetec.wunderlist.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.intimetec.wunderlist.R;
import com.intimetec.wunderlist.data.Task;
import com.intimetec.wunderlist.data.TaskCategory;
import com.intimetec.wunderlist.data.TaskRepository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ToDoListActivity extends AppCompatActivity implements View.OnClickListener {
    ImageButton btnDatePicker, btnTimePicker;
    EditText txtName, txtDate, txtTime;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private Spinner categorySpinner;
    private Button saveBtn;
    ArrayAdapter<String> categoryAdapter;
    List<String> list;

    private TaskRepository mTaskRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list);
        btnDatePicker = findViewById(R.id.calender_img_btn);
        btnTimePicker = findViewById(R.id.time_img_btn);

        txtName = findViewById(R.id.add_item_edt);
        txtDate = findViewById(R.id.due_date_edt);
        txtTime = findViewById(R.id.due_time_edt);
        saveBtn = findViewById(R.id.task_save_btn);

        mTaskRepository = new TaskRepository(getApplication());

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
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            txtDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        } else if (view == btnTimePicker || view == txtTime) {

            // Get Current Time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

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
            attemptSave();
        }
    }

    private void attemptSave() {
        String taskName = txtName.getText().toString().trim();
        String taskDate = txtDate.getText().toString().trim();
        String taskTime = txtTime.getText().toString().trim();

        txtName.setError(null);
        txtDate.setError(null);
        txtTime.setError(null);

        if (TextUtils.isEmpty(taskName)) {
            txtName.setError(getString(R.string.empty_field_error));
        } else if (TextUtils.isEmpty(taskDate)) {
            txtDate.setError(getString(R.string.empty_field_error));
        } else if (TextUtils.isEmpty(taskTime)) {
            txtTime.setError(getString(R.string.empty_field_error));
        } else {
            Task task = new Task();
            task.setTaskName(taskName);
            task.setTaskDate(taskDate);
            task.setTaskTime(taskTime);
            task.setCategory(categorySpinner.getSelectedItem().toString());

            mTaskRepository.add(task);
            Toast.makeText(getApplicationContext(), "Successfully Task Saved", Toast.LENGTH_LONG).show();

            finish();

        }
    }

}


