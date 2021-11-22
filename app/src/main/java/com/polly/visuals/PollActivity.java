package com.polly.visuals;

import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.polly.R;
import com.polly.utils.listener.PieChartResultsListener;
import com.polly.utils.listener.PieChartVoteListener;
import com.polly.utils.poll.Poll;
import com.polly.utils.poll.PollManager;

import java.util.ArrayList;

public class PollActivity extends AppCompatActivity {
    private PieChart pieChart;
    private Poll poll;
    private Button voteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar);
        pieChart = (PieChart) findViewById(R.id.pieChart);
        pieChart.setVisibility(View.INVISIBLE);
        voteButton = (Button) findViewById(R.id.vote_button);
        voteButton.setVisibility(View.GONE);

        if(getIntent().hasExtra("Poll") == true){
            this.poll = (Poll) getIntent().getSerializableExtra("Poll");
            showPoll(true);
        }
    }

    public void showPoll(boolean voting){
        ArrayList<PieEntry> options = new ArrayList<>();
        for(String option : poll.getData().keySet()){
            options.add(new PieEntry(poll.getData().get(option), option));
        }

        PieDataSet pieDataSet = new PieDataSet(options, "");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);

        if(voting){
            pieDataSet.setValueTextSize(0f);
        } else {
            pieDataSet.setValueTextSize(16f);
        }


        PieData pieData = new PieData(pieDataSet);

        pieChart.setData(pieData);
        Description description = new Description();
        description.setText(poll.getDescription());
        pieChart.setDescription(description);
        pieChart.getDescription().setEnabled(poll.getDescription() != "");
        pieChart.setCenterText(poll.getName());

        pieChart.setUsePercentValues(true);
        pieChart.animate();

        if(voting){
            pieChart.setOnChartValueSelectedListener(new PieChartVoteListener(this));
        } else {
            pieChart.setOnChartValueSelectedListener(new PieChartResultsListener(this));
        }
        pieChart.setVisibility(View.VISIBLE);
    }

    public PieChart getPieChart(){
        return pieChart;
    }


    public void showVoteButton(String option){
        if(option == null){
            // remove existing button:
            voteButton.setVisibility(View.GONE);
        } else {
            // add button which will vote for "option":
            voteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        voteButton.setVisibility(View.GONE);
                        PollManager.vote(poll.getId(), option);
                        //TODO Zeit mit loading-screen überbrücken:


                        // show poll-results:
                        poll = PollManager.loadPoll(poll.getId());
                        pieChart.setVisibility(View.INVISIBLE);
                        showPoll(false);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        //TODO smth went wrong
                    }
                }
            });

            voteButton.setVisibility(View.VISIBLE);
        }
    }
}
