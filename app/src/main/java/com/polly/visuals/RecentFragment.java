package com.polly.visuals;

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
import com.polly.utils.ShowPollPage;
import com.polly.utils.exceptions.CanNotSeePollResultsException;
import com.polly.utils.listadapter.ListAdapterPoll;
import com.polly.utils.poll.PollManager;
import com.polly.utils.wrapper.PollResultsWrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RecentFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private ListAdapterPoll mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    //private List<PollWrapper> pollItems;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_recent, container, false);
        LinearLayout layoutParticipatedPolls = root.findViewById(R.id.scrollLinearLayoutParticipatedPolls);
        LinearLayout layoutMyPolls = root.findViewById(R.id.scrollLinearLayoutMyPolls);

        List<PollResultsWrapper> participatedPolls = new ArrayList<>();
        try {
            participatedPolls = PollManager.getParticipatedPolls();
        } catch (IOException e) {
            System.err.println("Fehler");
            e.printStackTrace();
        }
        List<PollResultsWrapper> myPolls = new ArrayList<>();
        try {
            myPolls = PollManager.getMyPolls();
        } catch (IOException e) {
            System.err.println("Fehler");
            e.printStackTrace();
        }

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
        pieChart.setCenterTextSize(17f);
        return pieChart;
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
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
                if(mAdapter == null)return false;
                //mAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }
}