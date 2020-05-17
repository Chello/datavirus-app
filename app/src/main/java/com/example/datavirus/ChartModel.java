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


/**
 * This class is used for hold data to plot in a chart.
 * It's a singleton, so access can be made
 */
public class ChartModel {

    private static ChartModel instance;

    private ArrayList<LineDataSet> lineDataSets;

    private ArrayList<String> elementsName;

    private ArrayList<Integer> elementsColor;

    private ArrayList<Boolean> elementsVisible;

    /**
     * Returns the names of the saved items
     * @return the names of the saved items
     */
    public ArrayList<String> getElementsName() {
        return this.elementsName;
    }

    /**
     * Returns the colors of the saved items
     * @return the colors of the saved items
     */
    public ArrayList<Integer> getElementsColor() {
        return this.elementsColor;
    }

    /**
     * Set an element to be visible or not in the chart
     * @param pos the position to hide or show
     * @param visible true if to be shown, false otherwise
     */
    public void setElementVisible(Integer pos, Boolean visible) {
        this.elementsVisible.set(pos, visible);
    }

    /**
     * Constructor of the Chart Model
     */
    public ChartModel() {
        this.lineDataSets = new ArrayList<>();
        this.elementsName = new ArrayList<>();
        this.elementsColor = new ArrayList<>();
        this.elementsVisible = new ArrayList<>();
    }

    /**
     * Removes a item from the chart plot list
     * @param pos the position to remove
     */
    public void deleteItem(int pos) {
        this.elementsVisible.remove(pos);
        this.elementsColor.remove(pos);
        this.lineDataSets.remove(pos);
        this.elementsName.remove(pos);
    }

    /**
     * Returns the number of the chart plot list
     * @return the number of the chart plot list
     */
    public Integer getSize() {
        return this.elementsName.size();
    }

    /**
     * Returns the singleton instance of ChartModel
     * @return the singleton instance of ChartModel
     */
    public static ChartModel getInstance() {
        if (instance == null)
            instance = startFresh();
        return instance;
    }

    /**
     * Recreates a new singleton instance.
     * As consequence, a clean chart model is made.
     * @return the new ChartModel instance
     */
    public static ChartModel startFresh() {
        instance = new ChartModel();
        return instance;
    }

    /**
     * Plots lines added previously to this model to the passed chart
     * @param chart the chart to plot data
     */
    public void setChartData(LineChart chart) {
        LineData ld = new LineData();
        //for (LineDataSet lds : instance.lineDataSets) {
        for (Integer i = 0; i < instance.lineDataSets.size(); i++) {
            LineDataSet lds = instance.lineDataSets.get(i);
            if (instance.elementsVisible.get(i))
                ld.addDataSet(lds);
        }
        chart.setData(ld);
        chart.invalidate();
    }

    /**
     * Adds a dataset to the existing chart
     * @param dataset the dataset to plot
     * @param elementName the title for the new datas
     */
    public void addDataToModel(ArrayList<Integer> dataset, String elementName) {
        List<Entry> entries = new ArrayList<Entry>();
        if (this.elementsName.indexOf(elementName) != -1) {
            return;
        }
        this.elementsName.add(elementName);
        this.elementsVisible.add(true);
        int i = 0;
        for (Integer data : dataset) {
            entries.add(new Entry(i++, data != null ? data : 0));
        }
        LineDataSet lineDataSet = new LineDataSet(entries, elementName);
        Integer color = this.getRandomColor();
        this.elementsColor.add(color);
        lineDataSet.setColor(color);
        lineDataSet.setCircleColor(color);
        instance.lineDataSets.add(lineDataSet);
    }

    /**
     * Returns an integer representing a random color
     * @return a random color
     */
    private Integer getRandomColor() {
        Random rand = new Random();
        Integer r = rand.nextInt(255);
        Integer g = rand.nextInt(255);
        Integer b = rand.nextInt(255);
        return Color.rgb(r, g, b);
    }

}
