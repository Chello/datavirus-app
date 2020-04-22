package com.example.datavirus;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;

public class MainActivity extends AppCompatActivity implements UpdateUI<DPCData> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Bla", "Bleble1");
        setContentView(R.layout.activity_main);
        Log.d("Bla", "Bleble2");
        DataParser parser = new DataParser(this);
        parser.refreshData();
    }

    @Override
    public void updateData(DPCData data) {
        DPCData.DailyReport[] report = data.get(DPCData.Field.NAZIONALE);

        //Set total value of last day
        TextView total = (TextView) findViewById(R.id.tile_total);
        total.setText(report[report.length -1].getTotale_casi().toString());
        //Set total delta of last day
        TextView totalDelta = (TextView) findViewById(R.id.tile_total_delta);
        totalDelta.setText("Δ" + (report[report.length -1].getTotale_casi() - report[report.length-2].getTotale_casi()));

        //Set active value of last day
        TextView active = (TextView) findViewById(R.id.tile_active);
        active.setText(report[report.length -1].getTotale_positivi().toString());
        //Set active delta of last day
        TextView activeDelta = (TextView) findViewById(R.id.tile_active_delta);
        activeDelta.setText("Δ" + (report[report.length -1].getTotale_positivi() - report[report.length-2].getTotale_positivi()));

        //Set healed value of last day
        TextView healed = (TextView) findViewById(R.id.tile_healed);
        healed.setText(report[report.length -1].getDimessi_guariti().toString());
        //Set healed delta of last day
        TextView healedDelta = (TextView) findViewById(R.id.tile_healed_delta);
        healedDelta.setText("Δ" + (report[report.length -1].getDimessi_guariti() - report[report.length-2].getDimessi_guariti()));

        //Set deaths value of last day
        TextView deaths = (TextView) findViewById(R.id.tile_deaths);
        deaths.setText(report[report.length -1].getDeceduti().toString());
        //Set deaths delta of last day
        TextView deathsDelta = (TextView) findViewById(R.id.tile_deaths_delta);
        deathsDelta.setText("Δ" + (report[report.length -1].getDeceduti() - report[report.length-2].getDeceduti()));
        
        
    }
}
