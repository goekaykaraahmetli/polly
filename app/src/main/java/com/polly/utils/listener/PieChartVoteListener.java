package com.polly.utils.listener;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

public class PieChartVoteListener implements OnChartValueSelectedListener{
    PollActivity pollActivity;

    public PieChartVoteListener(PollActivity pollActivity){
        this.pollActivity = pollActivity;
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        PieEntry entry = (PieEntry) e;
        String selectedOption = entry.getLabel();
        System.out.println(selectedOption);
        pollActivity.showVoteButton(selectedOption);
    }

    @Override
    public void onNothingSelected() {
        pollActivity.showVoteButton(null);
    }
}
