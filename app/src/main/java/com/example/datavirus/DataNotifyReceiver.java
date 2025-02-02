package com.example.datavirus;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Calendar;

/**
 * Broadcast Receiver that checks if exists a new daily update in COVID data in DPC.
 * If that exists, a notification will be reported to user.
 * Otherwise calls an Android Alarm to itself after 10 minutes
 */
public class DataNotifyReceiver extends BroadcastReceiver implements OnDPCDataReady {

    private static final String CHANNEL_ID = "DataVirus";
    private Context context;

    /**
     * When the BroadcastReciever is called, starts download of DPC Data.
     * If updated data incoming, show notification
     * @param context the context used for handle notification
     * @param intent the intent triggered
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        //Log.d("Receiver", "Ore " + Calendar.getInstance().toString() + " OnRecieve chiamato");
        DataParser parser = new DataParser(this);
    }

    /**
     * Called when DPCData is ready.
     * Triggers notification if data are updated
     * Else sets new timer.
     * @param data the DPCData obtained by the parser
     */
    @Override
    public void onDPCDataReady(DPCData data) {

        Calendar downloaded = data.getLastDate();
        Calendar today = Calendar.getInstance();

        //If the dowloaded data have the same day of today
        if (today.get(Calendar.DAY_OF_YEAR) == downloaded.get(Calendar.DAY_OF_YEAR)) {
            this.showNotification();
            Log.d("Receiver", "Ore " + today.toString() + "Triggerato");
            //Log.d("Receiver", "Ore " + Calendar.getInstance().toString() + " Notifica mostrata");
        //Else if today is a day following than the downloaded day update
        } else /*if (today.get(Calendar.DAY_OF_YEAR) > downloaded.get(Calendar.DAY_OF_YEAR)) */{
            this.setNextIntent(); //set a new alarm
            Log.d("Receiver", "Ore " + today.toString() + " Nuovo alarm " + downloaded.toString());
        }
    }

    /**
     * Show notification displaying that data are updated
     */
    private void showNotification() {
        Intent intent = new Intent(this.context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this.context, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this.context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(this.context.getString(R.string.notification_title))
                .setContentText(this.context.getString(R.string.notification_description))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this.context);
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(10, builder.build());
    }

    /**
     * Set a new timer in 10 minutes
     */
    private void setNextIntent() {
        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, DataNotifyReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + 60 * 10 * 1000,
                    alarmIntent);

    }
}

