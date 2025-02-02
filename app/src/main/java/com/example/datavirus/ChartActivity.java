package com.example.datavirus;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.Calendar;

/**
 * Activity that holds and plots the chart, including list of elements plotted
 */
public class ChartActivity extends AppCompatActivity implements OnChartElementActions {

    public static String DATE = "date";
    public static String FIELD_DATA = "field_data";
    public static String FIELD = "field";
    public static String DENOMINAZIONE = "denominazione";

    private LineChart covidChart;

    private ChartModel chartModel;

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        this.chartModel = ChartModel.getInstance();

        this.chartModel.addDataToModel(getIntent().getExtras().getIntegerArrayList(FIELD_DATA),
                getIntent().getStringExtra(FIELD) + " " + getIntent().getStringExtra(DENOMINAZIONE));
        this.initChart();
    }

    /**
     * Updates the chart
     */
    private void initChart() {
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
        //remove legend
        this.covidChart.getLegend().setEnabled(false);

        //Remove description label
        this.covidChart.getDescription().setEnabled(false);

        //Finally set data on the chart
        this.chartModel.setChartData(this.covidChart);
    }

    /**
     * Refreshes data plotted in chart.
     * If nothing to plot, activity calls finish()
     */
    @Override
    public void onChartElementActions() {
        if (this.chartModel.getSize() == 0)
            finish();
        this.chartModel.setChartData(this.covidChart);

    }

    /**
     * Handler for clean button. Clears the chart
     * @param v the button view
     */
    public void onClickClearBtn(View v) {
        ChartModel.startFresh();
        finish();
    }

    /**
     * Triggered when back button is pressed
     */
    @Override
    public void onBackPressed() {
        ChartModel.startFresh();
        super.onBackPressed();
    }

    /**
     * Closes the view leaving user choose another tile for adding at the chart
     * @param view the view of the button
     */
    public void onClickAddBtn(View view) {
        Toast.makeText(this, getResources().getText(R.string.toast_add_to_chart), Toast.LENGTH_LONG).show();
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

        /**
         * Returns the formatted date string from the day number after the start of the pandemic.
         * @param num the number of days after the start of the pandemic
         * @param axis the axis base holder
         * @return a string with the label to insert in the axis
         */
        @Override
        public String getAxisLabel(float num, AxisBase axis) {
            Calendar toRet = (Calendar) this.startDate.clone();
            toRet.add(Calendar.DAY_OF_MONTH, Math.round(num) +1);
            return String.format(getResources().getString(R.string.placeholder_date_chart), toRet.get(Calendar.DAY_OF_MONTH), toRet.get(Calendar.MONTH));

        }
    }
}
