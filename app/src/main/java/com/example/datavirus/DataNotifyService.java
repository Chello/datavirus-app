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
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Calendar;

public class DataNotifyService extends BroadcastReceiver implements OnDPCDataReady {

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
        Log.d("AAA", "bbb");
        String message = "Hellooo" + (persist++).toString();
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
//        Intent intent2 = new Intent(context, TripNotification.class);
//        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(intent2);
        DataParser parser = new DataParser(this);

        Intent intent1 = new Intent(context, MyNewIntentService.class);
        context.startService(intent1);
    }

    @Override
    public void onDPCDataReady(DPCData data) {
        Toast.makeText(this.context, "Data ready", Toast.LENGTH_LONG).show();

        Notification.Builder builder = new Notification.Builder(this.context);
        builder.setContentTitle("My Title");
        builder.setContentText("This is the Body");
        builder.setSmallIcon(R.drawable.ic_launcher_background);
        Intent notifyIntent = new Intent(this.context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this.context, 3, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //to be able to launch your activity from the notification
        builder.setContentIntent(pendingIntent);
        Notification notificationCompat = builder.build();
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this.context);
        managerCompat.notify(3, notificationCompat);

    }
}

