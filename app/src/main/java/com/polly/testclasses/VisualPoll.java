package com.polly.testclasses;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.polly.R;

import java.util.ArrayList;

public class VisualPoll extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar);
        PieChart pieChart = (PieChart) findViewById(R.id.pieChart);
        ArrayList<PieEntry> visitors = new ArrayList<>();
        visitors.add(new PieEntry(10, "Option1"));
        visitors.add(new PieEntry(20, "Option1"));
        visitors.add(new PieEntry(30, "Option1"));
        visitors.add(new PieEntry(40, "Option1"));
        visitors.add(new PieEntry(50, "Option1"));

        PieDataSet pieDataSet = new PieDataSet(visitors, "Visitors");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(16f);

        PieData pieData = new PieData(pieDataSet);

        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(true);
        pieChart.setCenterText("Visitors");
        pieChart.animate();
    }
}
