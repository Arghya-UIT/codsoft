package com.example.alarmclock;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class AlarmActionReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if ("SNOOZE_ALARM".equals(action)) {
            String uriString = intent.getStringExtra("sound");

            if (uriString != null) {
                Uri alarmsound = Uri.parse(uriString);

                // Now you can use the 'alarmsound' Uri
                long id = intent.getLongExtra("id", -1);
                long snoozeTimeMillis = System.currentTimeMillis() + (1 * 60 * 1000); // 1 minute

                // Rest of your snooze logic here...
                Intent snoozeIntent = new Intent(context, AlarmReceiver.class);
                snoozeIntent.putExtra("id", id);
                snoozeIntent.putExtra("sound", alarmsound.toString());

                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                if (alarmManager != null) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, snoozeTimeMillis, pendingIntent);
                    Log.d("executeeeeeee", " ++++");
                } else {
                    // Handle the case when 'uriString' is null
                    // This could be due to missing data in the intent
                    Log.d("this is", "---------");
                }
            } else if ("DISMISS_ALARM".equals(action)) {
                long notificationId = intent.getLongExtra("notificationId", -1);
                Log.d("DismissAction", "Cancelling notification with ID: " + notificationId);

                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                if (notificationManager != null) {
//                long notificationId = intent.getLongExtra("notificationId", -1);
                    notificationManager.cancel((int) notificationId);
                }
            }
        }

    }
}