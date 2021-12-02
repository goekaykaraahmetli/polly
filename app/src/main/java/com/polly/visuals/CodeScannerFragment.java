package com.polly.visuals;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;
import com.polly.R;
import com.budiyev.android.codescanner.CodeScanner;
import com.polly.utils.EnterPoll;

import java.io.IOException;

public class CodeScannerFragment extends Fragment {
    private CodeScanner mCodeScanner;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final Activity activity = getActivity();
        View root = inflater.inflate(R.layout.activity_codescanner, container, false);
        CodeScannerView scannerView = root.findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(activity, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                startScanner(result);
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
        return root;
    }

    private void startScanner(Result result){
        Activity activity = getActivity();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    long id = Long.valueOf(result.getText());
                    EnterPoll.enterPoll(getContext(), id);
                }catch (NumberFormatException e){
                    Toast.makeText(activity, "\"" + result.getText() + "\" is not a valid poll id", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    mCodeScanner.startPreview();
                } catch (InterruptedException e) {
                    Toast.makeText(getContext(), "Something went wrong, please try again!", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    mCodeScanner.startPreview();
                } catch (IllegalStateException | IllegalArgumentException | IOException e){
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    mCodeScanner.startPreview();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    public void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }


}
