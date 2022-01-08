package com.polly.visuals;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.interfaces.datasets.IPieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.polly.R;
import com.polly.utils.poll.Poll;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DisplayQRCodePie extends AppCompatActivity {
    private int numberOfParticipants;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrpie);
        setTheme(R.style.Theme_Polly);
        LinearLayout linearLayout = findViewById(R.id.linearLayoutQRPie);
        numberOfParticipants = (int) getIntent().getExtras().get("PARTICIPANTS");
        HashMap<String, Integer> hashMap = (HashMap<String, Integer>) getIntent().getExtras().get("THE_PIE");
        PieChart pieChart = createPieChart(hashMap);
        pieChart.setVisibility(View.VISIBLE);
        Description description = new Description();
        description.setText((String) getIntent().getExtras().get("DESCRIPTION"));
        pieChart.setDescription(description);
        linearLayout.addView(pieChart);

    }

    private PieChart createPieChart(HashMap<String, Integer> hashMap){
        PieChart pieChart = new PieChart(this);
        ArrayList<PieEntry> options = new ArrayList<>();
        for(String option : hashMap.keySet()){
            if(hashMap.get(option) != 0)
                options.add(new PieEntry(hashMap.get(option), option));
        }

        PieDataSet pieDataSet = new PieDataSet(options, "");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(2f);
        pieDataSet.setSliceSpace(0f);
        pieDataSet.setSelectionShift(5f);
        PieData pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new PercentFormatter());
        pieData.setValueTextSize(11f);
        pieData.setValueTextSize(Color.WHITE);
        pieChart.setData(pieData);
        pieChart.setCenterText("Results");
        pieChart.setUsePercentValues(true);
        pieChart.animate();

        pieChart.setMinimumHeight(1000);
        pieChart.setMinimumWidth(1000);
        return pieChart;
    }

}
