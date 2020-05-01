package com.example.datavirus;

import android.content.Intent;
import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ChartModel {

    private static ChartModel instance;

    private ArrayList<LineDataSet> lineDataSets;

    public ChartModel() {
        this.lineDataSets = new ArrayList<>();
    }

    public static ChartModel getInstance() {
        if (instance == null)
            instance = new ChartModel();
        return instance;
    }

    public void addLinesToChart(LineChart chart) {
        LineData ld = new LineData();
        for (LineDataSet lds : instance.lineDataSets) {
            ld.addDataSet(lds);
        }
        chart.setData(ld);

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
        LineDataSet lineDataSet = new LineDataSet(entries, title);
        Integer color = this.getRandomColor();
        lineDataSet.setColor(color);
        lineDataSet.setCircleColor(color);
        instance.lineDataSets.add(lineDataSet);
    }

    private Integer getRandomColor() {
        Random rand = new Random();
        Integer r = rand.nextInt(255);
        Integer g = rand.nextInt(255);
        Integer b = rand.nextInt(255);
        return Color.rgb(r, g, b);
    }
}
