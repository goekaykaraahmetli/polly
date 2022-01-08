package com.polly.utils.listener;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

public class PieChartResultsListener implements OnChartValueSelectedListener {
    PollActivity pollActivity;

    public PieChartResultsListener(PollActivity pollActivity){
        this.pollActivity = pollActivity;
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        //TODO
        // show votes for selected poll and other information
    }

    @Override
    public void onNothingSelected() {
        // TODO
    }
}
