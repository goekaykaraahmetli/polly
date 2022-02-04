package com.polly.visuals;

import android.app.ActionBar;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.polly.R;
import com.polly.utils.Organizer;
import com.polly.utils.ShowPollPage;
import com.polly.utils.command.poll.FindPollCommand;
import com.polly.utils.communication.DataStreamManager;
import com.polly.utils.exceptions.CanNotSeePollResultsException;
import com.polly.utils.item.PollItem;
import com.polly.utils.listadapter.ListAdapterPoll;
import com.polly.utils.poll.PollManager;
import com.polly.utils.wrapper.ErrorWrapper;
import com.polly.utils.wrapper.Message;
import com.polly.utils.wrapper.PollOptionListWrapper;
import com.polly.utils.wrapper.PollOptionsWrapper;
import com.polly.utils.wrapper.PollResultsWrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class RecentFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    //private List<PollWrapper> pollItems;
    private List<PollResultsWrapper> participatedPolls;
    private List<PollResultsWrapper> myPolls;
    private LinearLayout layoutMyPolls;
    private LinearLayout layoutParticipatedPolls;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_recent, container, false);
        layoutParticipatedPolls = root.findViewById(R.id.scrollLinearLayoutParticipatedPolls);
        layoutMyPolls = root.findViewById(R.id.scrollLinearLayoutMyPolls);
        setHasOptionsMenu(true);


        participatedPolls = new ArrayList<>();
        try {
            participatedPolls = PollManager.getParticipatedPolls();
        } catch (IOException e) {
            System.err.println("Fehler");
            e.printStackTrace();
        }
        myPolls = new ArrayList<>();
        try {
            myPolls = PollManager.getMyPolls();
        } catch (IOException e) {
            System.err.println("Fehler");
            e.printStackTrace();
        }

        Collections.reverse(participatedPolls);
        Collections.reverse(myPolls);


        addPieChartsToLayout(layoutParticipatedPolls, participatedPolls);
        addPieChartsToLayout(layoutMyPolls, myPolls);
        return root;
    }

    private void addPieChartsToLayout(LinearLayout layout, List<PollResultsWrapper> pollResults) {
        for(PollResultsWrapper p : pollResults) {
            PieChart pieChart = createPieChart(p.getPollResults(), p.getBasicPollInformation().getName(), p.getBasicPollInformation().getDescription().getDescription());

            pieChart.setOnLongClickListener(view -> {
                try {
                    showPollResults(p);
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            });
            layout.addView(pieChart);

        }
    }

    private void showPollResults(PollResultsWrapper p) throws IOException {
        ShowPollPage.showPollResultsPage(p.getBasicPollInformation().getId());
    }

    private PieChart createPieChart(Map<String, Integer> data, String centerText, String descriptionText) {
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

        Description pieChartDescription = new Description();
        pieChartDescription.setText(descriptionText);

        pieChart.setDescription(pieChartDescription);


        pieChart.setMinimumHeight(600);
        pieChart.setMinimumWidth(600);
        pieChart.setCenterTextSize(10f);
        return pieChart;
    }


   @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        System.out.println("HALLOHALLO");
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.testmenu, menu);

        MenuItem searchItem = menu.findItem(R.id.searchMenu);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                performFilteringMyPolls(newText);
                performFilteringParticipatedPolls(newText);
                return false;
            }
        });
    }

    public List<PollResultsWrapper> performFilteringMyPolls(CharSequence charSequence) {
        List<PollResultsWrapper> filteredList = new ArrayList<>();

        if(charSequence == null){
            filteredList.addAll(myPolls);
        }else{
            String filterPattern = charSequence.toString();

            for(PollResultsWrapper pollOption: myPolls){
                if(pollOption.getBasicPollInformation().getName().contains(filterPattern)){
                    filteredList.add(pollOption);
                }
            }
        }
        ((LinearLayout) layoutMyPolls).removeAllViews();
        addPieChartsToLayout(layoutMyPolls, filteredList);
        return filteredList;
    }
    public List<PollResultsWrapper> performFilteringParticipatedPolls(CharSequence charSequence) {
        List<PollResultsWrapper> filteredList = new ArrayList<>();

        if(charSequence == null){
            filteredList.addAll(participatedPolls);
        }else{
            String filterPattern = charSequence.toString();

            for(PollResultsWrapper pollOption: participatedPolls){
                if(pollOption.getBasicPollInformation().getName().contains(filterPattern)){
                    filteredList.add(pollOption);
                }
            }
        }
        ((LinearLayout) layoutParticipatedPolls).removeAllViews();
        addPieChartsToLayout(layoutParticipatedPolls, filteredList);
        return filteredList;
    }
}