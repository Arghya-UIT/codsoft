package com.example.alarmclock;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.alarmclock.database.AlarmModel;
import com.example.alarmclock.database.MyDbHelper;


public class AlarmReceiver extends BroadcastReceiver {
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmModel alarmModel;
        MyDbHelper db = new MyDbHelper(context);
        Uri alarmsound = Uri.parse(intent.getStringExtra("sound"));

        alarmModel = db.fetchTaskById(intent.getLongExtra("id", -1));

        Log.d("alarm-status", "" + alarmModel.getStatus());
        if ("1".equals(alarmModel.getStatus())) {
            Log.d("alarm-yes", "alarm will ring");

            Intent intent1 = new Intent(context, MainActivity.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
            taskStackBuilder.addParentStack(CreateAlarm.class);
            taskStackBuilder.addNextIntent(intent1);

            PendingIntent intent2 = taskStackBuilder.getPendingIntent(1, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            Intent snoozeIntent = new Intent(context, AlarmActionReceiver.class);
            snoozeIntent.setAction("SNOOZE_ALARM");
            snoozeIntent.putExtra("id",alarmModel.getId());
            snoozeIntent.putExtra("sound",alarmModel.getRingtone_uri());
            snoozeIntent.putExtra("name",alarmModel.getAlarm_name());
            PendingIntent snoozePendingIntent = PendingIntent.getBroadcast(context, 0, snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

// Create an intent for the dismiss action
            Intent dismissIntent = new Intent(context, AlarmActionReceiver.class);
            dismissIntent.setAction("DISMISS_ALARM");
            dismissIntent.putExtra("notificationId",alarmModel.getId());
            PendingIntent dismissPendingIntent = PendingIntent.getBroadcast(context, 0, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);


            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "default_notification_channel_id")
                    .setSmallIcon(R.drawable.oig_4)
                    .setContentTitle(intent.getStringExtra("Message"))
                    .setContentText(intent.getStringExtra("description"))
                    .setSound(alarmsound)
                    .setContentIntent(intent2)
                    .setPriority(NotificationCompat.PRIORITY_HIGH) // Set priority to HIGH
                    .setDefaults(NotificationCompat.DEFAULT_ALL); // Set default behavior

            mBuilder.addAction(R.drawable.snooze, "Snooze", snoozePendingIntent);
            mBuilder.addAction(R.drawable.dismiss, "Dismiss", dismissPendingIntent);

            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (mNotificationManager == null) {
                return; // Handle error gracefully
            }
            int notificationId = (int) alarmModel.getId();

// Notify and then cancel the notification when "Dismiss" action is clicked
            mNotificationManager.notify(notificationId, mBuilder.build());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build();

                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel notificationChannel = new NotificationChannel("my_channel_01", "My Channel", importance);
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.enableVibration(true);
                notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                notificationChannel.setSound(alarmsound, audioAttributes);

                mBuilder.setChannelId("my_channel_01");
                mNotificationManager.createNotificationChannel(notificationChannel);
            }

            mNotificationManager.notify((int) System.currentTimeMillis(), mBuilder.build());

        } else {
            Log.d("alarm-no", "will not ring");
        }
    }
}