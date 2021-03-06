package com.polly.visuals;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;
import com.polly.R;
import com.polly.utils.ShowPollPage;
import com.polly.utils.command.poll.RemovePollChangeListenerCommand;
import com.polly.utils.communication.DataStreamManager;
import com.polly.utils.poll.PollManager;
import com.polly.utils.wrapper.PollOptionsWrapper;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ScanQRFragment extends Fragment {
    private CodeScanner mCodeScanner;
    private final int CAMERA_ACCESS_REQUEST_CODE = 1324;
    private CodeScannerView scannerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_codescanner, container, false);
        scannerView = root.findViewById(R.id.scanner_view);
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startScanner();
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_ACCESS_REQUEST_CODE);
        }
        return root;
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_ACCESS_REQUEST_CODE) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                startScanner();
            } else {
                //TODO verbessern
                Toast.makeText(getActivity(), "Please grant the camera permission to use this option!", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void startScanner(){
        Activity activity = getActivity();
        mCodeScanner = new CodeScanner(activity, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String text = result.getText();
                        String[] textParts = text.split(" ");

                        if(textParts.length < 2) {
                            Toast.makeText(activity, "\"" + result.getText() + "\" is not a valid voting code! - please try again", Toast.LENGTH_SHORT).show();
                            restartScanner(2.5);
                            return;
                        }

                        String pollIdText = textParts[0];
                        String pollOptionText = textParts[1];

                        try{
                            long id = Long.valueOf(pollIdText);

                            if(id < 0) {
                                Toast.makeText(activity, "\"" + "Poll(" + id + ")" + "\" is not a public poll! - please try again", Toast.LENGTH_SHORT).show();
                                restartScanner(2.5);
                                return;
                            }

                            List<String> options = PollManager.getPollOptions(id).getPollOptions();
                            if(!options.contains(pollOptionText)) {
                                Toast.makeText(activity, "\"" + pollOptionText + "\" is not a valid option for this poll! - please try again", Toast.LENGTH_SHORT).show();
                                restartScanner(2.5);
                                return;
                            }

                            if (PollManager.vote(id, pollOptionText)) {
                                ShowPollPage.showPollResultsPage(id);
                            } else {
                                Toast.makeText(getContext(), "Voting for Poll failed. Please try again!", Toast.LENGTH_SHORT).show();
                            }
                        } catch(NumberFormatException e) {
                            Toast.makeText(activity, "\"" + result.getText() + "\" is not a valid poll id", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                            restartScanner(2.5);
                        } catch (IOException e) {
                            Toast.makeText(activity, "\"" + e.getMessage() + "\"", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                            restartScanner(2.5);
                        }
                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
        mCodeScanner.startPreview();
    }

    private void restartScanner(double delay) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                mCodeScanner.startPreview();
            }
        }, (int)(delay*1000));
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mCodeScanner != null){
            mCodeScanner.startPreview();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mCodeScanner != null){
            mCodeScanner.releaseResources();
        }
    }
}
