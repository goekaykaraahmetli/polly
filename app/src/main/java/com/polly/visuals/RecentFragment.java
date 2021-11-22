package com.polly.visuals;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.ActivityNavigator;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.polly.R;
import com.polly.testclasses.ActivityHandler;
import com.polly.utils.poll.Poll;
import com.polly.utils.poll.PollManager;

import java.util.ArrayList;
import java.util.List;

public class RecentFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_recent, container, false);
        LinearLayout layoutParticipatedPolls = (LinearLayout) root.findViewById(R.id.scrollLinearLayoutParticipatedPolls);
        LinearLayout layoutMyPolls = (LinearLayout) root.findViewById(R.id.scrollLinearLayoutMyPolls);

        try {
            List<Poll> participatedPolls = PollManager.getParticipatedPolls();
            for(Poll p : participatedPolls){
                layoutParticipatedPolls.addView(createPieChart(p));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            List<Poll> myPolls = PollManager.getParticipatedPolls();
            for(Poll p : myPolls){
                layoutMyPolls.addView(createPieChart(p));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        return root;
    }

    private PieChart createPieChart(Poll poll){
        PieChart pieChart = new PieChart(getContext());



        ArrayList<PieEntry> options = new ArrayList<>();
        for(String option : poll.getData().keySet()){
            options.add(new PieEntry(poll.getData().get(option), option));
        }

        PieDataSet pieDataSet = new PieDataSet(options, "");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);

        pieDataSet.setValueTextSize(2f);


        PieData pieData = new PieData(pieDataSet);

        pieChart.setData(pieData);
        Description description = new Description();
        description.setText(poll.getDescription());
        pieChart.setDescription(description);
        pieChart.getDescription().setEnabled(poll.getDescription() != "");
        pieChart.setCenterText(poll.getName());

        pieChart.setUsePercentValues(true);
        pieChart.animate();

        pieChart.setMinimumHeight(750);
        pieChart.setMinimumWidth(750);
        return pieChart;
    }
}