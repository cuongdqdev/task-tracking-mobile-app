package com.cuongdang.trackingtask.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.cuongdang.trackingtask.R;
import com.cuongdang.trackingtask.activity.HomeActivity;

public class ReminderBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notifyId")
                .setSmallIcon(R.drawable.ic_work_black_24dp)
                .setContentTitle("Công việc reminder")
                .setContentText("Bạn có " + HomeActivity.todo + " nhiệm vụ khởi tạo và " + HomeActivity.doing + " nhiệm vụ đang làm! Cố gắng lên!")
                .setSound(alarmSound)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager.notify(200, builder.build());
    }
}
