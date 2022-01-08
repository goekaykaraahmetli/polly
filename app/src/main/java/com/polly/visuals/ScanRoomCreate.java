package com.polly.visuals;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;
import com.polly.R;

import java.util.List;

public class ScanRoomCreate extends Fragment {

    public static String question;
    public static String answer1;
    public static String answer2;
    public static String answer3;
    public static String answer4;

    int empty = 0;

    public static int numberOfParticipants;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_roomscanner_create,
                container, false);

        Button scanRoomButton = (Button) rootView.findViewById(R.id.scanRoomBtn);




        if(answer1 == "")
            empty++;
        if (answer2 == "")
            empty++;
        if(answer3 == "")
            empty++;
        if(answer4 == "")
            empty++;
        scanRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText participants = rootView.findViewById(R.id.editTextParticipants);
                EditText questionEdit = rootView.findViewById(R.id.pollEdittext);
                question = questionEdit.getText().toString();
                EditText answer1Edit = rootView.findViewById(R.id.pollEdittextAnswer1);
                answer1 = answer1Edit.getText().toString();
                EditText answer2Edit = rootView.findViewById(R.id.pollEdittextAnswer2);
                answer2 = answer2Edit.getText().toString();
                EditText answer3Edit = rootView.findViewById(R.id.pollEdittextAnswer3);
                answer3 = answer3Edit.getText().toString();
                EditText answer4Edit = rootView.findViewById(R.id.pollEdittextAnswer4);
                answer4 = answer4Edit.getText().toString();
                if(participants.getText().toString().equals(""))
                    Toast.makeText(getContext(), "Please enter the number of participants", Toast.LENGTH_SHORT).show();
                else {
                    numberOfParticipants = Integer.parseInt(participants.getText().toString());
                    if (empty >= 3)
                        Toast.makeText(getContext(), "Please Enter at least two answers", Toast.LENGTH_SHORT).show();
                    else {
                        Intent intent = new Intent(getActivity(), BarcodeScannerActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });
        return rootView;
    }

}
