package com.example.datavirus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ChartActivity extends AppCompatActivity {

    public static String DATE = "date";
    public static String FIELD = "field";
    public static String DENOMINAZIONE = "denominazione";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        LineChart chart = (LineChart) findViewById(R.id.chart);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        Calendar date = (Calendar) intent.getSerializableExtra(DATE);

        ArrayList<Integer> dataObjects = b.getIntegerArrayList(FIELD);
        List<Entry> entries = new ArrayList<Entry>();
        int i = 0;
        for (Integer data : dataObjects) {
            // turn your data into Entry objects
            new Entry();
            entries.add(new Entry(++i, data));
        }

        LineDataSet dataSet = new LineDataSet(entries, "labella");

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);

        //TODO Tentone 1
        YAxis y = chart.getAxisLeft();
        LimitLine ll = new LimitLine(0);
        ll.setLineColor(Color.BLACK);
        ll.setLineWidth(3f);

        y.addLimitLine(ll);

        //TODO Tentone 2

        DateXAxisFormatter formatter = new DateXAxisFormatter(date);
        chart.getXAxis().setValueFormatter(formatter);

    }

    class DateXAxisFormatter extends ValueFormatter {

        private Calendar startDate;

        public DateXAxisFormatter(Calendar startDate) {
            this.startDate = startDate;
        }

        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            Calendar toRet = (Calendar) this.startDate.clone();
            toRet.add(Calendar.DAY_OF_MONTH, Math.round(value));
            return String.format(getResources().getString(R.string.placeholder_date_chart), toRet.get(Calendar.DAY_OF_MONTH), toRet.get(Calendar.MONTH));

        }
    }
}
