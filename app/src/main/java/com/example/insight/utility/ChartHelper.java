package com.example.insight.utility;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import java.util.Comparator;

import com.example.insight.R;
import com.example.insight.model.VitalRecord;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

public class ChartHelper {

    public static void createLineChart(View view, List<VitalRecord> records, Context context, String measureName1, String measureName2) {

        if (records == null || records.isEmpty()) {
            Log.d("graphs", "Vital records list is null or empty. Cannot create chart.");
            return; // Exit if the list is null or empty.
        }

        // Sort the list by dayOfMonth (ascending)
        records.sort(Comparator.comparingInt(VitalRecord::getDayOfMonth));

        LineChart lineChart = view.findViewById(R.id.lineChart); // Replace with your LineChart ID

        // Retrieve colors from resources
        TypedValue graphLineValue1 = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.graphLine1, graphLineValue1, true);
        int graphLineColor1 = graphLineValue1.data;

        TypedValue graphLineValue2 = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.graphLine2, graphLineValue2, true);
        int graphLineColor2 = graphLineValue2.data;

        TypedValue textColorValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.chartTextColor, textColorValue, true);
        int textColor = textColorValue.data;

        TypedValue axisColorValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.chartAxisColor, axisColorValue, true);
        int lineColor = axisColorValue.data;

        TypedValue gridColorValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.gridColor, gridColorValue, true);
        int gridColor = gridColorValue.data;

        // Set the background to transparent & no grid
        lineChart.setBackgroundColor(Color.TRANSPARENT);
        lineChart.setDrawGridBackground(false);

        // Prepare data for the chart
        List<Entry> measure1Entries = new ArrayList<>();
        List<Entry> measure2Entries = new ArrayList<>();

        // Generate X-axis labels (interval of 1, for all days)
        List<String> xAxisLabelsInterval1 = new ArrayList<>();
        for (int i = 1; i <= 31; i += 1) {
            xAxisLabelsInterval1.add(String.format("%02d", i));
        }

        // Populate entries for all days in the VitalRecord list
        for (VitalRecord record : records) {
            int dayOfMonth = record.getDayOfMonth();
            int xIndex = (dayOfMonth - 1);

            if (record.getMeasure1() != null) {
                measure1Entries.add(new Entry(xIndex, record.getMeasure1()));
                Log.d("ChartDebug", "Measure1 - Day: " + dayOfMonth + ", Value: " + record.getMeasure1());
            }

            if (record.getMeasure2() != null) {
                measure2Entries.add(new Entry(xIndex, record.getMeasure2()));
                Log.d("ChartDebug", "Measure2 - Day: " + dayOfMonth + ", Value: " + record.getMeasure2());
            }
        }

        // Create LineDataSet for measure1
        LineDataSet measure1DataSet = new LineDataSet(measure1Entries, measureName1);
        measure1DataSet.setColor(graphLineColor1);
        measure1DataSet.setLineWidth(4f);
        measure1DataSet.setDrawCircles(false);
        measure1DataSet.setCircleRadius(4f);
        measure1DataSet.setDrawValues(false);
        measure1DataSet.setValueTextSize(10f);
        //measure1DataSet.setMode(LineDataSet.Mode.LINEAR);
        measure1DataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER); // Smooth line
        measure1DataSet.setCubicIntensity(0.1f); // Control the smoothness


        // Create LineDataSet for measure2 (if available)
        LineDataSet measure2DataSet = null;
        if (!measure2Entries.isEmpty()) {
            measure2DataSet = new LineDataSet(measure2Entries, measureName2);
            measure2DataSet.setColor(graphLineColor2);
            measure2DataSet.setLineWidth(4f);
            measure2DataSet.setDrawCircles(false);
            measure2DataSet.setCircleRadius(4f);
            measure2DataSet.setDrawValues(false);
            measure2DataSet.setValueTextSize(10f);
            //measure2DataSet.setMode(LineDataSet.Mode.LINEAR);
            measure2DataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER); // Smooth line
            measure2DataSet.setCubicIntensity(0.1f); // Control the smoothness

        }

        // Combine datasets
        List<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(measure1DataSet);
        if (measure2DataSet != null) {
            dataSets.add(measure2DataSet);
        }
        LineData lineData = new LineData(dataSets);

        // Set data to the chart
        lineChart.setData(lineData);

        // Customize X-axis
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new CustomXAxisValueFormatter(xAxisLabelsInterval1));
        xAxis.setLabelCount(xAxisLabelsInterval1.size());
        xAxis.setAxisMinimum(-0.5f); // offset the start point (day 01) away from y-axis
        xAxis.setAxisMaximum(xAxisLabelsInterval1.size() - 0.5f);//have the same offset at the end pont on x-axis
        xAxis.setCenterAxisLabels(false);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(textColor);
        xAxis.setAxisLineWidth(1f);
        xAxis.setAxisLineColor(lineColor);
        xAxis.setGridColor(gridColor);
        xAxis.setTextSize(11f);


        //Find the Maximum and Minimum Values
        float maxMeasureValue = Float.MIN_VALUE;
        float minMeasureValue = Float.MAX_VALUE;


        for (Entry entry : measure1Entries) {
            if (entry.getY() > maxMeasureValue) {
                maxMeasureValue = entry.getY();
            }
            if (entry.getY() < minMeasureValue) {
                minMeasureValue = entry.getY();
            }
        }

        for (Entry entry : measure2Entries) {
            if (entry.getY() > maxMeasureValue) {
                maxMeasureValue = entry.getY();
            }
            if (entry.getY() < minMeasureValue) {
                minMeasureValue = entry.getY();
            }
        }

        // Adjust the Y-Axis Range - Round up yAxisMax to the nearest integer
        float yAxisMax = (float) Math.ceil(maxMeasureValue + 5f);
        float yAxisMin = (float) Math.floor(minMeasureValue - 5f);


        // Customize Y-axis
        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setAxisMinimum(yAxisMin); // Set minimum Y-axis value
        yAxis.setAxisMaximum(yAxisMax); // Set maximum Y-axis value
        yAxis.setGranularity(2f); // Set interval between Y-axis values
        yAxis.setDrawGridLines(true);
        yAxis.setGridLineWidth(1f);
        yAxis.setTextColor(textColor);
        yAxis.setAxisLineWidth(1f);
        yAxis.setAxisLineColor(lineColor);
        yAxis.setGridColor(gridColor);
        yAxis.setTextSize(11f);
        yAxis.setAxisMaximum(yAxisMax - 1f);
        yAxis.enableGridDashedLine(10f, 10f, 0f);// Set grid lines to be dotted

        // Disable right Y-axis
        lineChart.getAxisRight().setEnabled(false);

        // Customize the legend , will show if we present 2 measurements
        Legend legend = lineChart.getLegend();
        if (!measure2Entries.isEmpty()){
            legend.setEnabled(true);
            legend.setTextColor(textColor);
            legend.setTextSize(12f);
            legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
            legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
            legend.setOrientation(Legend.LegendOrientation.VERTICAL);
            legend.setDrawInside(false);
            legend.setForm(Legend.LegendForm.LINE);
            legend.setFormSize(10f);
            legend.setFormLineWidth(2f);
            //legend.setXOffset(20f); // Add 20dp margin on the X-axis (left and right)
            //legend.setYOffset(10f); // Add 10dp margin on the Y-axis (top and bottom)

        }else{
            legend.setEnabled(false);
        }


        // Customize chart appearance
        lineChart.setBackgroundColor(Color.TRANSPARENT);
        lineChart.getDescription().setEnabled(false);
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setPinchZoom(true);

        // Animation
        lineChart.animateY(1500);

        // Refresh the chart
        lineChart.invalidate();
    }

    // Custom ValueFormatter to display labels every other day
    public static class CustomXAxisValueFormatter extends ValueFormatter {

        private final List<String> labels;

        public CustomXAxisValueFormatter(List<String> labels) {
            this.labels = labels;
        }

        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            int index = (int) value;
            if (index >= 0 && index < labels.size()) {

                //day 02 => index =1 so in order to show even days, will return the odd indexes
                if (index % 2 != 0) {
                    return labels.get(index);
                } else {
                    return "";
                }
            }
            return "";
        }
    }

}
