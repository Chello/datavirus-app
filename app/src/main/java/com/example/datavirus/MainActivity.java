package com.example.datavirus;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.Calendar;
import java.util.EmptyStackException;
import java.util.Stack;

public class MainActivity extends AppCompatActivity implements OnDPCDataReady, OnDPCGeoListener, OnTileClick {

    private static final String CHANNEL_ID = "DataVirus";
    private static final String PREF_NOTIFICATION = "NOTIFICATIONS";
    private Stack<GeographicElement> backStackGeoElements;
    private DataParser parser;
    private PendingIntent alarmIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.createNotificationChannel();
        this.notificationCheckboxHandler();
        //this.setAlarm();

        this.backStackGeoElements = new Stack<>();
        this.parser = new DataParser(getSupportFragmentManager(), this);
    }

    /**
     * Creates a new notification channel for Datavirus app.
     * Useful for API > 26
     */
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

    /**
     * Triggered when click on refresh button. Refreshes data
     * @param v the view of the element clicked
     */
    public void onRefresh(View v) {
        parser.refreshData();
    }

    /**
     * Sets the alarm for new data incoming
     */
    private void setAlarm(){
        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, DataNotifyReceiver.class);
        this.alarmIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        Calendar calendar = Calendar.getInstance();

        if (calendar.get(Calendar.HOUR_OF_DAY) >= 18)
            //calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) +1);
            calendar.add(Calendar.DATE, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 18);
        calendar.set(Calendar.MINUTE, 00);
        calendar.setTimeInMillis(System.currentTimeMillis());

        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY , alarmIntent);
    }

    /**
     * Function to call for deleting the alarm
     */
    private void unsetAlarm() {
        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (this.alarmIntent != null)
            alarmMgr.cancel(this.alarmIntent);
        this.alarmIntent = null;
    }

    /**
     * Sets or unsets the alarm notifications
     * @param alarmSet true if you want to set alarm, false if you want to disable it
     */
    public void setAlarm(Boolean alarmSet) {
        if (alarmSet)
            this.setAlarm();
        else this.unsetAlarm();
    }

    /**
     * Function called when DataParser object has DPC data ready
     * @param data the COVID-19 report
     */
    @Override
    public void onDPCDataReady(DPCData data) {
        DataTilesFragment frg = (DataTilesFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_tiles_main);

        this.backStackGeoElements.push(new GeographicElement());
        frg.setStaticGeoField(this.backStackGeoElements.peek());

        this.updateTitle(getResources().getString(R.string.national));
    }

    /**
     * Handles notification checkbox.
     * Set default parameter from sharedpreferences and sets and unsets notifications
     */
    public void notificationCheckboxHandler() {
        CheckBox checkBox = (CheckBox) findViewById(R.id.checkbox_notification);

        final SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        Boolean notificationsEnabled = sharedPref.getBoolean(PREF_NOTIFICATION, true);

        checkBox.setChecked(notificationsEnabled);

        this.setAlarm(notificationsEnabled);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPref.edit();

                setAlarm(isChecked);

                editor.putBoolean(MainActivity.PREF_NOTIFICATION, isChecked);
                editor.apply();
            }
        });

    }

    /**
     * Triggered when add button is pressed
     * @param v the button view
     */
    public void onClickAddBtn(View v) {
        DPCGeoPicker picker = DPCGeoPicker.newInstance();
        //picker.setCovidData(covidData);
        getSupportFragmentManager().beginTransaction()
                .add(picker, "picker").commit();
    }

    /**
     * Triggered when FAB button is pressed
     * @param v the FAB view
     */
    public void onClickFAB(View v) {
        Intent i = new Intent(this, StarredActivity.class);
        this.startActivity(i);
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
        this.updateTitle(element.getDenominazione());
    }

    /**
     * When resumes recovers new data about tiles changes.
     * If data are ready, reset all tiles
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (DataParser.getDPCDataInstance() != null) {
            DataTilesFragment frg = (DataTilesFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_tiles_main);
            frg.setStaticGeoField(this.backStackGeoElements.peek());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            this.backStackGeoElements.pop();
            this.updateTitle(this.backStackGeoElements.peek().getDenominazione());
        } catch (EmptyStackException ex) {
            finish();
        }
    }

    private void updateTitle(String title) {
        TextView head = (TextView) findViewById(R.id.data_tiles_head);
        head.setText(String.format(getResources().getString(R.string.data_tiles_head), title));

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
