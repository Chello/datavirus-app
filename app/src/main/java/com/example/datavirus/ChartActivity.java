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

import com.github.mikephil.charting.charts.Chart;
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

//    private ArrayList<ArrayList<Integer>> datasets;
//    private ArrayList<Integer> denominazioneList;
    private ChartModel chartModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        this.chartModel = ChartModel.getInstance();

        this.setupChart();
        this.chartModel.addDataToChart(getIntent().getExtras().getIntegerArrayList(FIELD_DATA),
                getIntent().getStringExtra(FIELD) + " " + getIntent().getStringExtra(DENOMINAZIONE));
        this.chartModel.addLinesToChart(this.covidChart);
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
