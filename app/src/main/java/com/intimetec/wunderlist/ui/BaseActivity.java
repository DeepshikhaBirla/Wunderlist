package com.intimetec.wunderlist.ui;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.intimetec.wunderlist.R;
import com.intimetec.wunderlist.data.task.TaskAlarm;

public class BaseActivity extends AppCompatActivity {
    private ProgressDialog mProgressDialog;
    protected FirebaseAuth mFireBaseAuth;
    private FirebaseFirestore db;
    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFireBaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .setPersistenceEnabled(false)
                .build();
        db.setFirestoreSettings(settings);
        mContext = this;

        Intent taskAlarm = new Intent(mContext, TaskAlarm.class);
        boolean taskAlarmIsRunning = (PendingIntent.getBroadcast(mContext, 0 , taskAlarm, PendingIntent.FLAG_NO_CREATE) != null);

        if (taskAlarmIsRunning == false) {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, taskAlarm, 0);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 60000, pendingIntent);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    protected FirebaseFirestore getFireStoreInstance() {
        return db;
    }

    protected void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    protected void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.progress_message));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
