package com.intimetec.wunderlist.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.intimetec.wunderlist.R;

public class AboutUs extends AppCompatActivity {
private TextView aboutUs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        aboutUs=(TextView)findViewById(R.id.about_us);
    }
}
