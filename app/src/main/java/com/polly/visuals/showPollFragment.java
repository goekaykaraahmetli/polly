package com.polly.visuals;

import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.polly.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.PieChart;
import com.polly.config.Config;
import com.polly.utils.QRCode;
import com.polly.utils.command.poll.RegisterPollChangeListenerCommand;
import com.polly.utils.command.poll.RemovePollChangeListenerCommand;
import com.polly.utils.communicator.Communicator;
import com.polly.utils.poll.PollManager;
import com.polly.utils.wrapper.Message;
import com.polly.utils.wrapper.PollOptionsWrapper;
import com.polly.utils.wrapper.PollResultsWrapper;

import java.io.IOException;
import java.util.ArrayList;

public class showPollFragment extends Fragment {
    private PieChart pieChart;
    private ImageView qrCode;
    PollResultsWrapper pollResults;
    PollOptionsWrapper pollOptionsWrapper;
    private Button voteButton;
    private Communicator communicator = initialiseCommunicator();
    private boolean hasRunningPollChangeListener = false;
    private boolean voted = false;

    SavingClass saving;

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(hasRunningPollChangeListener){
            try {
                communicator.send(Config.serverCommunicationId, new RemovePollChangeListenerCommand(pollResults.getBasicPollInformation().getId()));
                hasRunningPollChangeListener = false;
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_showpoll, container, false);
        saving = new ViewModelProvider(getActivity()).get(SavingClass.class);

        new Thread(() -> {
            while(true){
                try {
                    communicator.handleInput(communicator.getInput());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        pieChart = (PieChart) root.findViewById(R.id.pieChart);
        pieChart.setVisibility(View.GONE);
        voteButton = (Button) root.findViewById(R.id.vote_button);
        voteButton.setVisibility(View.GONE);

        try {
            if(PollManager.getPollOptions(pollResults.getBasicPollInformation().getId()) != null){
                pollOptionsWrapper = new PollOptionsWrapper(PollManager.getPollOptions(pollResults.getBasicPollInformation().getId()), pollResults.getBasicPollInformation());
                showPoll(true);
            } else if(PollManager.getPollResults(pollResults.getBasicPollInformation().getId()) != null) {
                showPoll(true);
            }

            return root;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return root;
    }

        public void showPoll(boolean voting){
            updatePieChart(voting, pollResults);
            qrCode = (ImageView) getView().findViewById(R.id.qrCodeImageView);
            qrCode.setImageBitmap(QRCode.QRCode(""+ pollResults.getBasicPollInformation().getId()));
            qrCode.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    MediaStore.Images.Media.insertImage(getContext().getContentResolver(), QRCode.QRCode("" + pollResults.getBasicPollInformation().getId()), "QRCode: " + pollResults.getBasicPollInformation().getName(), pollResults.getBasicPollInformation().getDescription().getDescription());
                    Toast.makeText(getContext(), "The QR-Code has been saved to your camera roll!", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
            if(voting){
                pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                    @Override
                    public void onValueSelected(Entry e, Highlight h) {

                    }

                    @Override
                    public void onNothingSelected() {

                    }
                });
            } else {
                try {
                    communicator.send(Config.serverCommunicationId, new RegisterPollChangeListenerCommand(pollResults.getBasicPollInformation().getId(), voted);
                    hasRunningPollChangeListener = true;
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                    @Override
                    public void onValueSelected(Entry e, Highlight h) {

                    }

                    @Override
                    public void onNothingSelected() {

                    }
                });
            }
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
                        PollManager.vote(pollResults.getBasicPollInformation().getId(), option);



                        //TODO Zeit mit loading-screen überbrücken:


                        // show poll-results:
                        pollResults = PollManager.getPollResults(pollResults.getBasicPollInformation().getId());


                        pieChart.setVisibility(View.INVISIBLE);
                        showPoll(false);

                    } catch (InterruptedException e){
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    } catch (IllegalArgumentException|IOException e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            });

            voteButton.setVisibility(View.VISIBLE);
        }
    }

    private Communicator initialiseCommunicator(){
        return new Communicator() {
            @Override
            public void handleInput(Message message) {
                System.out.println("PollActivity received message from type: " + message.getDataType().getName());
                if(voted){
                    if(message.getDataType().equals(pollResults.getClass())){
                        PollResultsWrapper updatePoll = (PollResultsWrapper) message.getData();

                    }
                } else {
                    if(message.getDataType().equals(pollOptionsWrapper.getClass())){
                        PollOptionsWrapper updatePoll = (PollOptionsWrapper) message.getData();
                    }
                }

            }
        };
    }

    private void updatePieChart(boolean voting, PollResultsWrapper updatePoll){
            pollResults = updatePoll;
            ArrayList<PieEntry> options = new ArrayList<>();
            for(String option : updatePoll.getPollResults().keySet()){
                options.add(new PieEntry(updatePoll.getPollResults().get(option), option));
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
            description.setText(updatePoll.getBasicPollInformation().getDescription().getDescription());
            pieChart.setDescription(description);
            pieChart.getDescription().setEnabled(!updatePoll.getBasicPollInformation().getDescription().getDescription().equals(""));
            pieChart.setCenterText(updatePoll.getBasicPollInformation().getName());

            pieChart.setUsePercentValues(true);
            pieChart.animate();
            pieChart.setVisibility(View.VISIBLE);
    }


}
