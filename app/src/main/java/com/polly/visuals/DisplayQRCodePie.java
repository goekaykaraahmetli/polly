package com.polly.visuals;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.polly.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;

import java.util.ArrayList;
import java.util.HashMap;

public class DisplayQRCodePie extends AppCompatActivity {
    private int numberOfParticipants;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.Theme_Polly);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrpie);
        PieChart layoutPie = findViewById(R.id.pieChartQr);
        numberOfParticipants = (int) getIntent().getExtras().get("PARTICIPANTS");
        HashMap<String, Integer> hashMap = (HashMap<String, Integer>) getIntent().getExtras().get("THE_PIE");
        PieChart pieChart = createPieChart(hashMap, layoutPie);
        pieChart.setVisibility(View.VISIBLE);
        Description description = new Description();
        description.setText((String) getIntent().getExtras().get("DESCRIPTION"));
        pieChart.setCenterText((String) getIntent().getExtras().get("NAME"));
        pieChart.setCenterTextSize(17f);
        pieChart.setDescription(description);
        pieChart.setUsePercentValues(true);


    }


    private PieChart createPieChart(HashMap<String, Integer> hashMap, PieChart pieChart){
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
        pieData.setValueTextSize(17f);
        pieData.setValueTextColor(Color.WHITE);
        pieChart.setData(pieData);
        pieChart.setUsePercentValues(true);
        pieChart.animate();

        pieChart.setMinimumHeight(1000);
        pieChart.setMinimumWidth(1000);
        return pieChart;
    }

}
