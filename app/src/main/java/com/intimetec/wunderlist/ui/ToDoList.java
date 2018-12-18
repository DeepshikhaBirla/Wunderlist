package com.intimetec.wunderlist.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.intimetec.wunderlist.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ToDoList extends AppCompatActivity implements View.OnClickListener {
    ImageButton btnDatePicker, btnTimePicker;
    EditText txtDate, txtTime;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private Spinner spinner;
    ArrayAdapter<String> adapter;
    List<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list);
        btnDatePicker = (ImageButton) findViewById(R.id.calender_img_btn);
        btnTimePicker = (ImageButton) findViewById(R.id.time_img_btn);
        txtDate = (EditText) findViewById(R.id.due_date_edt);
        txtTime = (EditText) findViewById(R.id.due_time_edt);

        txtDate.setOnClickListener(this);
        btnDatePicker.setOnClickListener(this);

        btnTimePicker.setOnClickListener(this);
        txtTime.setOnClickListener(this);

        spinner = (Spinner) findViewById(R.id.task_spinner);
        list = new ArrayList<String>();
        list.add("Default");
        list.add("Personal");
        list.add("Shopping");
        list.add("Wishlist");
        list.add("Work");
        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
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
        }
    }
}
