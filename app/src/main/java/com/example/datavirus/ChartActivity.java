package com.example.datavirus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

public class ChartActivity extends AppCompatActivity {

    public static String DATE = "date";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        LineChart chart = (LineChart) findViewById(R.id.chart);

        Bundle b = getIntent().getExtras();

        ArrayList<Integer> dataObjects = b.getIntegerArrayList(DATE);
        List<Entry> entries = new ArrayList<Entry>();
        int i = 0;
        for (Integer data : dataObjects) {
            // turn your data into Entry objects
            entries.add(new Entry(++i, data));
        }

        LineDataSet dataSet = new LineDataSet(entries, "labella");

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
    }
}
