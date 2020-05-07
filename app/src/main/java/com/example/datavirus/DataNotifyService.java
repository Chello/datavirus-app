package com.example.datavirus;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

public class DataNotifyService extends BroadcastReceiver implements OnDPCDataReady {

    private static final String CHANNEL_ID = "DataVirus";
    private static Integer persist;
    private Context context;

    public DataNotifyService() {
        super();
        persist = 0;

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //android.os.Debug.waitForDebugger();
        this.context = context;
        String message = "Hellooo" + (persist++).toString();
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
//        Intent intent2 = new Intent(context, TripNotification.class);
//        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(intent2);
        DataParser parser = new DataParser(this);
    }

    @Override
    public void onDPCDataReady(DPCData data) {
        //Toast.makeText(this.context, "Data ready", Toast.LENGTH_LONG).show();

        Calendar downloaded = data.getLastDate();
        Calendar today = Calendar.getInstance();

        //If the dowloaded data have the same day of today
        if (today.get(Calendar.DAY_OF_YEAR) == downloaded.get(Calendar.DAY_OF_YEAR)) {
            this.showNotification();
        //Else if today is a day following than the downloaded day update
        } else if (today.get(Calendar.DAY_OF_YEAR) > downloaded.get(Calendar.DAY_OF_YEAR)) {
            this.setNextAlarm();
            Toast.makeText(this.context, downloaded.toString() + " " + today.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void showNotification() {
        Toast.makeText(this.context, "Notif ", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(this.context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this.context, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this.context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(this.context.getString(R.string.notification_title))
                .setContentText(this.context.getString(R.string.notification_description))
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this.context);
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(10, builder.build());
        Toast.makeText(this.context, "Notif shown", Toast.LENGTH_LONG).show();
    }

    private void setNextAlarm() {
        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, DataNotifyService.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() +
                        60 * 1000, alarmIntent);

    }
}

