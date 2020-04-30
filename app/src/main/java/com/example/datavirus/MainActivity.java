package com.example.datavirus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements OnDPCDataReady, OnDPCGeoListener, OnTileClick {

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
        DataTilesFragment frg = DataTilesFragment.newInstance(this.covidData.getNazionale(), new GeographicElement(DPCData.GeoField.NAZIONALE), getResources());

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.data_tiles, frg)
                .commit();

        this.updateTitle(new GeographicElement(DPCData.GeoField.NAZIONALE));
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


    public void onDPCGeoClick(GeographicElement element) {
        DataTilesFragment newFragment = DataTilesFragment.newInstance(this.covidData.getReportFromGeoData(element), element, getResources());

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.data_tiles, newFragment)
                .commit();

        this.updateTitle(element);
    }

    private void updateTitle(GeographicElement element) {
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

    @Override
    public void onTileClick(GeographicElement geo, String field) {
        Log.d("Tile click", field);
        Intent i = new Intent(this, ChartActivity.class);
        Bundle b = new Bundle();

        b.putIntegerArrayList(ChartActivity.DATE, this.covidData.getValuesFromGeoField(geo, field));

        i.putExtras(b);
        startActivity(i);
    }

}
