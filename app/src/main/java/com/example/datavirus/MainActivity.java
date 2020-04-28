package com.example.datavirus;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements OnDPCDataReady, OnDPCGeoListener {

    DataParser parser;
    DPCData covidData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.parser = new DataParser(getResources(), getSupportFragmentManager(), this);
        buttonHandler();
    }

    @Override
    public void setReport(DPCData data) {
        this.covidData = data;
        //Setting nationals values
        DataTilesFragment frg = DataTilesFragment.newInstance(this.covidData.getNazionale());

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.data_tiles, frg)
                .commit();

        this.updateTitle(new DPCData.GeographicElement(DPCData.GeoField.NAZIONALE));
    }

    /**
     * Manages the handlers for buttons
     */
    private void buttonHandler() {
        Button macrofield = (Button) findViewById(R.id.button_geographic);
        //TODO check lambda
        macrofield.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DPCGeoPicker picker = DPCGeoPicker.newInstance(covidData);
                //picker.setCovidData(covidData);
                getSupportFragmentManager().beginTransaction()
                        .add(picker, "picker").addToBackStack(null).commit();
            }
        });
    }

    @Override
    public void onDPCGeoClick(DPCData.GeographicElement element) {
        DataTilesFragment newFragment = DataTilesFragment.newInstance(this.covidData.getReportFromGeoData(element));;

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.data_tiles, newFragment)
                .commit();

        this.updateTitle(element);
    }

    private void updateTitle(DPCData.GeographicElement element) {
        TextView head = (TextView) findViewById(R.id.data_tiles_head);
        head.setText(String.format(getResources().getString(R.string.data_tiles_head), element.getDenominazione()));

        TextView dateTV = (TextView) findViewById(R.id.head_date);
        Calendar date = this.covidData.getDate();
        dateTV.setText(String.format(String.valueOf(getResources().getText(R.string.date_ph)),
                date.get(Calendar.DAY_OF_MONTH),
                date.get(Calendar.MONTH),
                date.get(Calendar.YEAR),
                date.get(Calendar.HOUR_OF_DAY)));
    }

}
