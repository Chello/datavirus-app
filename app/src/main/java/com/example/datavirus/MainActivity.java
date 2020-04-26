package com.example.datavirus;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;

public class MainActivity extends AppCompatActivity implements OnDPCDataReady, OnDPCGeoListener {

    DataParser parser;
    DPCData covidData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.parser = new DataParser(getSupportFragmentManager(), this);
        spinnerHandlers();
    }

    @Override
    public void updateData(DPCData data) {
        this.covidData = data;

        DataTilesFragment fragment = (DataTilesFragment) getSupportFragmentManager().findFragmentById(R.id.data_tiles);
        fragment.updateData(data);
        Log.d("Backstack", getSupportFragmentManager().toString());
    }
    /**
     * Manages the handlers for spinners
     * @param v the View where spinners are
     */
    private void spinnerHandlers() {
        Button macrofield = (Button) findViewById(R.id.button_geographic);
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
        DataTilesFragment frg = (DataTilesFragment) getSupportFragmentManager().findFragmentById(R.id.data_tiles);
        frg.updateTiles(this.covidData.getReportFromGeoData(element));
    }
}
