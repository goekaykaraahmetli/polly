package com.polly.visuals;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.polly.R;
import com.polly.utils.poll.PollManager;

import java.io.IOException;
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
                PieChart pieChart = createPieChart(p);

                pieChart.setOnLongClickListener(view -> {
                    Intent intent = new Intent(getContext(), PollActivity.class);
                    intent.putExtra("PollResult", p);
                    getContext().startActivity(intent);
                    return true;
                });

                layoutParticipatedPolls.addView(pieChart);
            }
        } catch (InterruptedException | IllegalArgumentException e) {
            Toast.makeText(getContext(), "unable to fetch your participated polls!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (IOException e){
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        try {
            List<Poll> myPolls = PollManager.getMyPolls();
            for(Poll p : myPolls){
                PieChart pieChart = createPieChart(p);

                pieChart.setOnLongClickListener(view -> {
                    Intent intent = new Intent(getContext(), PollActivity.class);
                    intent.putExtra("PollResult", p);
                    getContext().startActivity(intent);
                    return true;
                });
                layoutMyPolls.addView(pieChart);
            }
        } catch (InterruptedException | IllegalArgumentException e) {
            Toast.makeText(getContext(), "unable to fetch your participated polls!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (IOException e){
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
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
        pieChart.setCenterText(poll.getName());

        pieChart.setOnTouchListener(null);
        pieChart.setClickable(true);

        pieChart.setUsePercentValues(true);
        pieChart.animate();


        pieChart.setMinimumHeight(600);
        pieChart.setMinimumWidth(600);
        return pieChart;
    }
}