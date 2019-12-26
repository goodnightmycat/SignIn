package com.example.administrator.bombtest;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.example.administrator.bombtest.Model.Group;


public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //收到广播取出数据
        Bundle bundle = intent.getBundleExtra("data");
        Group mgroup = (Group) bundle.getSerializable("group");
        //发通知
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent1 = new Intent(context, GroupActivity.class);
        intent1.putExtra("data", bundle);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("channel", "签到提醒", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
            Notification notification = new Notification.Builder(context, "channel")
                    .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                    .setContentIntent(pendingIntent)
                    .setContentTitle("“" + mgroup.getName() + "”要签到了")
                    .setContentText("点进来签到")
                    .setWhen(System.currentTimeMillis())
                    .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
                    .setAutoCancel(true)
                    .build();
            notificationManager.notify(1, notification);
        } else {
            Notification notification = new Notification.Builder(context)
                    .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                    .setContentIntent(pendingIntent)
                    .setContentTitle("“" + mgroup.getName() + "”要签到了")
                    .setContentText("点进来签到")
                    .setWhen(System.currentTimeMillis())
                    .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
                    .setAutoCancel(true)
                    .build();
            notificationManager.notify(1, notification);
        }


    }
}
