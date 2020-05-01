package com.example.datavirus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;

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
    public static String FIELD_DATA = "field_data";
    public static String FIELD = "field";
    public static String DENOMINAZIONE = "denominazione";

    private LineChart covidChart;

    private ArrayList<ArrayList<Integer>> datasets;
    private ArrayList<Integer> denominazioneList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = pref.edit();
//        if (pref.getString("Chiavesp") == null) {
//            editor.putString("Chiavesp", "valoresp");
//            editor.commit();
//        }

        Log.d("Chiave", pref.getString("Chiavesp", "Non c'e niente di salvato cazzo"));

        this.setupChart();
        this.addDataToChart(getIntent().getExtras().getIntegerArrayList(FIELD_DATA),
                getIntent().getStringExtra(FIELD) + " " + getIntent().getStringExtra(DENOMINAZIONE));
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("Chiave", "Loooool");
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Adds a dataset to the existing chart
     * @param dataset the dataset to plot
     * @param title the title for the new datas
     */
    public void addDataToChart(ArrayList<Integer> dataset, String title) {
        List<Entry> entries = new ArrayList<Entry>();
        int i = 0;
        for (Integer data : dataset) {
            entries.add(new Entry(i++, data));
        }
        LineDataSet dataSet = new LineDataSet(entries, title);
        LineData lineData = new LineData(dataSet);
        this.covidChart.setData(lineData);
    }

    /**
     * Setups the chart
     */
    private void setupChart() {
        this.covidChart = (LineChart) findViewById(R.id.chart);

        //Add formatter to X axis, so will plotted dates instead of increment number
        Calendar date = (Calendar) getIntent().getSerializableExtra(DATE);
        DateXAxisFormatter formatter = new DateXAxisFormatter(date);
        this.covidChart.getXAxis().setValueFormatter(formatter);

        //Make y=0 black and bold
        YAxis y = this.covidChart.getAxisLeft();
        LimitLine ll = new LimitLine(0);
        ll.setLineColor(Color.BLACK);
        ll.setLineWidth(3f);
        y.addLimitLine(ll);
    }

    public void onClickAddBtn(View v) {
        //moveTaskToBack(false);
        SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("Chiavesp", "valoresp");
        editor.apply();
        finish();
    }

    /**
     * Class for implement date in the X axis in current chart
     */
    class DateXAxisFormatter extends ValueFormatter {
        /**
         * The date of the first day of outbreak start
         */
        private Calendar startDate;

        /**
         * Constructor. Requires the date of the first outbreack analysis
         * @param startDate the date of the first day of outbreak
         */
        public DateXAxisFormatter(Calendar startDate) {
            this.startDate = startDate;
        }

        //TODO scrivi qualcosa qua
        /**
         *
         * @param value
         * @param axis
         * @return
         */
        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            Calendar toRet = (Calendar) this.startDate.clone();
            toRet.add(Calendar.DAY_OF_MONTH, Math.round(value));
            return String.format(getResources().getString(R.string.placeholder_date_chart), toRet.get(Calendar.DAY_OF_MONTH), toRet.get(Calendar.MONTH));

        }
    }
}
