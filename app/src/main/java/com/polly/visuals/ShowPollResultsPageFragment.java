package com.polly.visuals;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.polly.utils.communicator.CommunicatorManager;
import com.polly.utils.poll.PollManager;
import com.polly.utils.wrapper.Message;
import com.polly.utils.wrapper.PollResultsWrapper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ShowPollResultsPageFragment extends Fragment {
    private PieChart pieChart;
    private ImageView qrCode;
    static PollResultsWrapper pollResults;
    static Long id;
    private long testDiff;
    private Communicator communicator = initialiseCommunicator();
    private boolean hasRunningPollChangeListener = false;
    private CountDownTimer countDownTimer;

    SavingClass saving;

    public static void open(long id) throws IOException {
        ShowPollResultsPageFragment.id = id;
        pollResults = PollManager.getPollResults(id);
        Navigation.findNavController(MainActivity.mainActivity, R.id.nav_host_fragment).navigate(R.id.showPollResultsPageFragment);
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

        if(pollResults != null) {
            showPoll(root);
        }
        LocalDateTime localDateTime = pollResults.getBasicPollInformation().getExpirationTime();
        testDiff = getDifferenceInMS(convertToDate(LocalDateTime.now(ZoneId.of("Europe/Berlin"))), convertToDate(localDateTime));
        TextView countDownView = (TextView) root.findViewById(R.id.expirationDateTimer);
        String expirationDate = "Expires in: ";

        countDownTimer = new CountDownTimer(testDiff, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                testDiff = millisUntilFinished;
                countDownView.setText(expirationDate + timeDiffInString(testDiff));
            }

            @Override
            public void onFinish() {
                countDownView.setText("Poll is expired");
            }
        }.start();
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
        Communicator communicator = new Communicator() {
            @Override
            public void handleInput(Message message) {
                System.out.println("ShowPollResultsPageFragment received message from type: " + message.getDataType().getName());

                if (message.getDataType().equals(PollResultsWrapper.class)) {
                    PollResultsWrapper updatePoll = (PollResultsWrapper) message.getData();
                    updatePieChart(updatePoll);
                }
            }
        };
        communicator.start();
        return communicator;
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
    public Date convertToDate(LocalDateTime data){
        return Date.from(data.atZone(ZoneId.systemDefault()).toInstant());
    }
    public static long getDifferenceInMS(Date date1, Date date2){
        if(date2.getTime() - date1.getTime() > 0)
            return (date2.getTime() - date1.getTime());
        else
            return 0l;
    }
    public String timeDiffInString(long difference_In_Time){
        long diffMinutes = TimeUnit
                .MILLISECONDS
                .toMinutes(difference_In_Time)
                % 60;
        long diffHours = TimeUnit
                .MILLISECONDS
                .toHours(difference_In_Time)
                % 24;
        long diffDays = TimeUnit
                .MILLISECONDS
                .toDays(difference_In_Time)
                % 365;
        return diffDays + "d " + diffHours + "h : " + diffMinutes + "m";
    }
}