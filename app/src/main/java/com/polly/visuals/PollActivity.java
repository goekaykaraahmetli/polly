package com.polly.visuals;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
import com.polly.config.Config;
import com.polly.utils.Message;
import com.polly.utils.Organizer;
import com.polly.utils.command.GetParticipatedPollsCommand;
import com.polly.utils.communicator.Communicator;
import com.polly.utils.communicator.ResponseCommunicator;
import com.polly.utils.listener.PieChartResultsListener;
import com.polly.utils.listener.PieChartVoteListener;
import com.polly.utils.poll.Poll;
import com.polly.utils.poll.PollManager;

import java.util.ArrayList;
import java.util.List;

public class PollActivity extends AppCompatActivity {
    private PieChart pieChart;
    private Poll poll;
    private Button voteButton;
    private Communicator communicator = initialiseCommunicator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Thread(() -> {
            while(true){
                try {
                    communicator.handleInput(communicator.getInput());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        setContentView(R.layout.activity_bar);
        pieChart = (PieChart) findViewById(R.id.pieChart);
        pieChart.setVisibility(View.INVISIBLE);
        voteButton = (Button) findViewById(R.id.vote_button);
        voteButton.setVisibility(View.GONE);

        if(getIntent().hasExtra("PollOptions") == true){
            this.poll = (Poll) getIntent().getSerializableExtra("PollOptions");
            showPoll(true);
        } else if (getIntent().hasExtra("PollResult") == true){
            this.poll = (Poll) getIntent().getSerializableExtra("PollResult");
            showPoll(false);
        }
    }

    public void showPoll(boolean voting){
        updatePieChart(voting, poll);

        if(voting){
            pieChart.setOnChartValueSelectedListener(new PieChartVoteListener(this));
        } else {
            communicator.send(Config.getServerCommunicationId(), "send updates!");
            communicator.send(Config.getServerCommunicationId(), 1L);
            pieChart.setOnChartValueSelectedListener(new PieChartResultsListener(this));
        }
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
                    voteButton.setVisibility(View.GONE);
                    try{
                        PollManager.vote(poll.getId(), option);



                        //TODO Zeit mit loading-screen überbrücken:


                        // show poll-results:
                        poll = PollManager.loadPoll(poll.getId());


                        pieChart.setVisibility(View.INVISIBLE);
                        showPoll(false);

                    } catch (InterruptedException e){
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    } catch (IllegalArgumentException e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            });

            voteButton.setVisibility(View.VISIBLE);
        }
    }

    private Context getContext(){
        return this;
    }

    private Communicator initialiseCommunicator(){
        return new Communicator() {
            @Override
            public void handleInput(Message message) {
                System.out.println("PollActivity received message from type: " + message.getDataType().getName());

                if(message.getDataType().equals(Poll.class)){
                    Poll updatePoll = (Poll) message.getData();
                    updatePieChart(false, updatePoll);
                }
            }
        };
    }

    private void updatePieChart(boolean voting, Poll updatePoll){
        this.runOnUiThread(() -> {
            poll = updatePoll;
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

            pieChart.setVisibility(View.INVISIBLE);

            pieChart.setData(pieData);
            Description description = new Description();
            description.setText(poll.getDescription());
            pieChart.setDescription(description);
            pieChart.getDescription().setEnabled(poll.getDescription() != "");
            pieChart.setCenterText(poll.getName());

            pieChart.setUsePercentValues(true);
            pieChart.animate();
            pieChart.setVisibility(View.VISIBLE);
        });
    }
}
