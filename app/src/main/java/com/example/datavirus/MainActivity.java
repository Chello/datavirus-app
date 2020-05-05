package com.example.datavirus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.Stack;

public class MainActivity extends AppCompatActivity implements OnDPCDataReady, OnDPCGeoListener, OnTileClick {

    private Stack<GeographicElement> backStackGeoElements;
    private DPCData covidData;
    private DataParser parser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.backStackGeoElements = new Stack<>();
        this.parser = new DataParser(getResources(), getSupportFragmentManager(), this);
        buttonHandler();
    }

    /**
     * Function called when DataParser object has DPC data ready
     * @param data
     */
    @Override
    public void setDPCData(DPCData data) {
        this.covidData = data;
        //Setting nationals values

//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.data_tiles, frg)
//                .commit();
        DataTilesFragment frg = (DataTilesFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_tiles_main);

        this.backStackGeoElements.push(new GeographicElement(DPCData.GeoField.NAZIONALE));
        frg.setStaticGeoField(this.backStackGeoElements.peek(), this.covidData);
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
                DPCGeoPicker picker = DPCGeoPicker.newInstance(covidData);
                //picker.setCovidData(covidData);
                getSupportFragmentManager().beginTransaction()
                        .add(picker, "picker").addToBackStack(null).commit();
            }
        });

        myFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity, SavedChartActivity.class);
                activity.startActivity(i);
            }
        });
    }


    public void onDPCGeoClick(GeographicElement element) {
        this.backStackGeoElements.push(element);
        DataTilesFragment frg = DataTilesFragment.newInstance();

        getSupportFragmentManager().beginTransaction()
                .addToBackStack(null)
                .add(R.id.fragment_tiles_main, frg)
                .commit();

        getSupportFragmentManager().executePendingTransactions();

        frg.setStaticGeoField(element, this.covidData);

        this.updateTitle();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.backStackGeoElements.pop();
        if (!this.backStackGeoElements.empty())
            this.updateTitle();
    }

    private void updateTitle() {
        TextView head = (TextView) findViewById(R.id.data_tiles_head);
        head.setText(String.format(getResources().getString(R.string.data_tiles_head), this.backStackGeoElements.peek().getDenominazione()));

        TextView dateTV = (TextView) findViewById(R.id.head_date);
        Calendar date = this.covidData.getLastDate();
        dateTV.setText(String.format(String.valueOf(getResources().getText(R.string.placeholder_date_ph)),
                date.get(Calendar.DAY_OF_MONTH),
                date.get(Calendar.MONTH),
                date.get(Calendar.YEAR),
                date.get(Calendar.HOUR_OF_DAY)));
    }

    @Override
    public void onTileClick(FieldGeographicElement fieldGeographicElement) {
        Intent i = new Intent(this, ChartActivity.class);
        Bundle b = new Bundle();

        b.putIntegerArrayList(ChartActivity.FIELD_DATA, this.covidData.getValuesFromGeoField(fieldGeographicElement));

        i.putExtras(b);
        i.putExtra(ChartActivity.DATE, this.covidData.getFirstDate());
        i.putExtra(ChartActivity.DENOMINAZIONE, fieldGeographicElement.getDenominazione());
        i.putExtra(ChartActivity.FIELD, fieldGeographicElement.getField());
        startActivity(i);
    }

}
