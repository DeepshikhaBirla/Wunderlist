package com.intimetec.wunderlist.data.task;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.intimetec.wunderlist.R;
import com.intimetec.wunderlist.WunderListApplication;
import com.intimetec.wunderlist.ui.HomeActivity;

import java.util.List;

public class TaskAlarm extends BroadcastReceiver {

    private String taskNotificationChannelId = TaskAlarm.class.getCanonicalName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Application application = ((WunderListApplication) context.getApplicationContext());
        TaskRepository taskRepository = new TaskRepository(application);
        List<Task> allTasks = taskRepository.fetchAll();

        Log.d("on receive called", allTasks.toString());

        for (Task task : allTasks) {

        }

    }

    private void showNotification(Context context, String taskName, String taskDate) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = TaskAlarm.class.getName();
            String description = TaskAlarm.class.getSimpleName();
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(taskNotificationChannelId, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this

            notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, taskNotificationChannelId)
                        .setSmallIcon(R.drawable.work)
                        .setContentTitle(taskName)
                        .setContentText(taskDate)
                        .setWhen(System.currentTimeMillis())
                        .setDefaults(Notification.DEFAULT_SOUND |
                                Notification.DEFAULT_VIBRATE);


        Intent notificationIntent = new Intent(context, HomeActivity.class);
        Bundle b = new Bundle();
        notificationIntent.putExtras(b);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        notificationManager.notify(0, builder.build());

    }

}