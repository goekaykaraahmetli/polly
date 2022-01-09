package com.polly.visuals;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.polly.R;
import com.polly.utils.ShowPollPage;
import com.polly.utils.wrapper.PollResultsWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RecentFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_recent, container, false);
        LinearLayout layoutParticipatedPolls = (LinearLayout) root.findViewById(R.id.scrollLinearLayoutParticipatedPolls);
        LinearLayout layoutMyPolls = (LinearLayout) root.findViewById(R.id.scrollLinearLayoutMyPolls);

        List<PollResultsWrapper> participatedPolls = new ArrayList<>(); //TODO: insert useful values
        List<PollResultsWrapper> myPolls = new ArrayList<>(); //TODO: insert useful values

        /**try {
            // get participatedPolls
            // or
            // get myPolls
         } catch (InterruptedException | IllegalArgumentException e) {
         Toast.makeText(getContext(), "unable to fetch your participated polls!", Toast.LENGTH_SHORT).show();
         e.printStackTrace();
         } catch (IOException e){
         Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
         e.printStackTrace();
         }*/

        showRecentPolls(layoutParticipatedPolls, participatedPolls, layoutMyPolls, myPolls);
        return root;
    }

    private void showRecentPolls(LinearLayout layoutParticipatedPolls, List<PollResultsWrapper> participatedPolls, LinearLayout layoutMyPolls, List<PollResultsWrapper> myPolls) {
        for(PollResultsWrapper p : participatedPolls) {
            PieChart pieChart = createPieChart(p.getPollResults(), p.getBasicPollInformation().getName());

            pieChart.setOnLongClickListener(view -> {
                showPollResults(p);
                return true;
            });
            layoutParticipatedPolls.addView(pieChart);
        }

        for(PollResultsWrapper p : myPolls) {
            PieChart pieChart = createPieChart(p.getPollResults(), p.getBasicPollInformation().getName());

            pieChart.setOnLongClickListener(view -> {
                showPollResults(p);
                return true;
            });
            layoutMyPolls.addView(pieChart);
        }
    }

    private void showPollResults(PollResultsWrapper p) {
        ShowPollPage.showPollResultsPage(p);
    }

    private PieChart createPieChart(Map<String, Integer> data, String centerText){
        PieChart pieChart = new PieChart(getContext());

        ArrayList<PieEntry> options = new ArrayList<>();
        for(String option : data.keySet()){
            options.add(new PieEntry(data.get(option), option));
        }

        PieDataSet pieDataSet = new PieDataSet(options, "");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);

        pieDataSet.setValueTextSize(2f);


        PieData pieData = new PieData(pieDataSet);

        pieChart.setData(pieData);
        pieChart.setCenterText(centerText);

        pieChart.setOnTouchListener(null);
        pieChart.setClickable(true);

        pieChart.setUsePercentValues(true);
        pieChart.animate();


        pieChart.setMinimumHeight(600);
        pieChart.setMinimumWidth(600);
        return pieChart;
    }
}