package com.polly.visuals;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.polly.R;
import com.polly.utils.poll.Poll;

import java.util.ArrayList;

public class PollActivity extends AppCompatActivity {
    private PieChart pieChart;
    private Poll poll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar);
        pieChart = (PieChart) findViewById(R.id.pieChart);

        if(getIntent().hasExtra("Poll") == true){
            this.poll = (Poll) getIntent().getSerializableExtra("Poll");
            showPoll();
        }
    }

    public void showPoll(){
        ArrayList<PieEntry> options = new ArrayList<>();
        for(String option : poll.getData().keySet()){
            options.add(new PieEntry(poll.getData().get(option), option));
        }

        PieDataSet pieDataSet = new PieDataSet(options, "");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(16f);

        PieData pieData = new PieData(pieDataSet);

        pieChart.setData(pieData);
        Description description = new Description();
        description.setText(poll.getDescription());
        pieChart.setDescription(description);
        pieChart.getDescription().setEnabled(poll.getDescription() != "");
        pieChart.setCenterText(poll.getName());

        pieChart.setUsePercentValues(true);
        pieChart.animate();
    }
}
