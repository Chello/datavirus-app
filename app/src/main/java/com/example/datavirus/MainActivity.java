package com.example.datavirus;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.EmptyStackException;
import java.util.Stack;

public class MainActivity extends AppCompatActivity implements OnDPCDataReady, OnDPCGeoListener, OnTileClick {

    private static final String CHANNEL_ID = "DataVirus";
    private Stack<GeographicElement> backStackGeoElements;
    private DataParser parser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.createNotificationChannel();

        this.setAlarm(this);

        this.backStackGeoElements = new Stack<>();
        this.parser = new DataParser(getSupportFragmentManager(), this);

        buttonHandler();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    private void setAlarm(Context context){
        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, DataNotifyService.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 18);

        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY , alarmIntent);
    }

    /**
     * Function called when DataParser object has DPC data ready
     * @param data the COVID-19 report
     */
    @Override
    public void onDPCDataReady(DPCData data) {
        DataTilesFragment frg = (DataTilesFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_tiles_main);

        this.backStackGeoElements.push(new GeographicElement(DPCData.GeoField.NAZIONALE));
        frg.setStaticGeoField(this.backStackGeoElements.peek());
        this.updateTitle();
    }

    /**
     * Manages the handlers for buttons
     */
    private void buttonHandler() {
        final MainActivity activity = this;
        Button macrofield = (Button) findViewById(R.id.button_geographic);
        FloatingActionButton myFab = (FloatingActionButton) findViewById(R.id.fab_saved_list);

        macrofield.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DPCGeoPicker picker = DPCGeoPicker.newInstance();
                //picker.setCovidData(covidData);
                getSupportFragmentManager().beginTransaction()
                        .add(picker, "picker").addToBackStack(null).commit();
            }
        });

        myFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity, SavedTilesActivity.class);
                activity.startActivity(i);
            }
        });
    }

    /**
     * Creates a new DataTilesFragment that overcomes in the existing one with the updated data
     * @param element the element chosen by the user
     */
    public void onDPCGeoClick(GeographicElement element) {
        this.backStackGeoElements.push(element);
        DataTilesFragment frg = DataTilesFragment.newInstance();
        DataTilesFragment oldFrg = (DataTilesFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_tiles_main);

        getSupportFragmentManager().beginTransaction()
                .addToBackStack(null)
                .hide(oldFrg)
                .add(R.id.fragment_tiles_main, frg)
                .commit();

        getSupportFragmentManager().executePendingTransactions();

        frg.setStaticGeoField(element);

        this.updateTitle();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            this.backStackGeoElements.pop();
        } catch (EmptyStackException ex) {
            finish();
        }
        if (!this.backStackGeoElements.empty())
            this.updateTitle();
    }

    private void updateTitle() {
        TextView head = (TextView) findViewById(R.id.data_tiles_head);
        head.setText(String.format(getResources().getString(R.string.data_tiles_head), this.backStackGeoElements.peek().getDenominazione()));

        TextView dateTV = (TextView) findViewById(R.id.head_date);
        Calendar date = DataParser.getDPCDataInstance().getLastDate();
        dateTV.setText(String.format(String.valueOf(getResources().getText(R.string.placeholder_date_ph)),
                date.get(Calendar.DAY_OF_MONTH),
                date.get(Calendar.MONTH) +1,
                date.get(Calendar.YEAR),
                date.get(Calendar.HOUR_OF_DAY)));
    }

    @Override
    public void onTileClick(FieldGeographicElement fieldGeographicElement) {
        Intent i = new Intent(this, ChartActivity.class);
        Bundle b = new Bundle();

        b.putIntegerArrayList(ChartActivity.FIELD_DATA, DataParser.getDPCDataInstance().getValuesFromGeoField(fieldGeographicElement));

        i.putExtras(b);
        i.putExtra(ChartActivity.DATE, DataParser.getDPCDataInstance().getFirstDate());
        i.putExtra(ChartActivity.DENOMINAZIONE, fieldGeographicElement.getDenominazione());
        i.putExtra(ChartActivity.FIELD, fieldGeographicElement.getField());
        startActivity(i);
    }

}
