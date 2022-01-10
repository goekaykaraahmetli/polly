package com.polly.visuals;

import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.polly.R;
import com.polly.config.Config;
import com.polly.utils.QRCode;
import com.polly.utils.command.poll.RegisterPollChangeListenerCommand;
import com.polly.utils.command.poll.RemovePollChangeListenerCommand;
import com.polly.utils.communicator.Communicator;
import com.polly.utils.poll.PollManager;
import com.polly.utils.wrapper.Message;
import com.polly.utils.wrapper.PollResultsWrapper;

import java.io.IOException;
import java.util.ArrayList;

public class ShowPollResultsPageFragment extends Fragment {
    private PieChart pieChart;
    private ImageView qrCode;
    PollResultsWrapper pollResults;
    static Long id;
    private Communicator communicator = initialiseCommunicator();
    private boolean hasRunningPollChangeListener = false;

    SavingClass saving;

    public static void open(long id) {
        Navigation.findNavController(MainActivity.mainActivity, R.id.nav_host_fragment).navigate(R.id.showPollResultsPageFragment);
        ShowPollResultsPageFragment.id = id;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(hasRunningPollChangeListener){
            try {
                communicator.send(Config.serverCommunicationId, new RemovePollChangeListenerCommand(id));
                hasRunningPollChangeListener = false;
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        id = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_showpoll, container, false);
        saving = new ViewModelProvider(getActivity()).get(SavingClass.class);
        pieChart = (PieChart) root.findViewById(R.id.pieChart);
        pieChart.setVisibility(View.GONE);

        try {
            pollResults = PollManager.getPollResults(id);
            showPoll(root);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return root;
    }

        public void showPoll(View root){
            updatePieChart(pollResults);
            qrCode = (ImageView) root.findViewById(R.id.qrCodeImageView);
            qrCode.setImageBitmap(QRCode.QRCode(""+ pollResults.getBasicPollInformation().getId()));
            qrCode.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    MediaStore.Images.Media.insertImage(getContext().getContentResolver(), QRCode.QRCode("" + pollResults.getBasicPollInformation().getId()), "QRCode: " + pollResults.getBasicPollInformation().getName(), pollResults.getBasicPollInformation().getDescription().getDescription());
                    Toast.makeText(getContext(), "The QR-Code has been saved to your camera roll!", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
                try {
                    communicator.send(Config.serverCommunicationId, new RegisterPollChangeListenerCommand(pollResults.getBasicPollInformation().getId(), true));
                    hasRunningPollChangeListener = true;
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

    private Communicator initialiseCommunicator() {
        return new Communicator() {
            @Override
            public void handleInput(Message message) {
                System.out.println("PollActivity received message from type: " + message.getDataType().getName());
                if (message.getDataType().equals(PollResultsWrapper.class)) {
                    PollResultsWrapper updatePoll = (PollResultsWrapper) message.getData();
                    updatePieChart(updatePoll);
                }
            }
        };
    }


    private void updatePieChart(PollResultsWrapper updatePoll){
            pollResults = updatePoll;
            ArrayList<PieEntry> options = new ArrayList<>();
            for(String option : updatePoll.getPollResults().keySet()){
                options.add(new PieEntry(updatePoll.getPollResults().get(option), option));
            }

            PieDataSet pieDataSet = new PieDataSet(options, "");
            pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
            pieDataSet.setValueTextColor(Color.BLACK);
            pieDataSet.setValueTextSize(16f);
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