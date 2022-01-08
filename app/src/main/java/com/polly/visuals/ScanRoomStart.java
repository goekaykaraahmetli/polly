package com.polly.visuals;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.polly.R;
import com.polly.utils.QRCode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

public class ScanRoomStart extends Fragment {
    int numberOfParticipants = 4;
    int numberOfOptions = 2;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_roomscanner_start,
                container, false);

        Button createButton = (Button) rootView.findViewById(R.id.createroompoll);
        Button emailButton = (Button) rootView.findViewById(R.id.sendviaemail);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.scanRoomCreate);
            }
        });

        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Uri> uris = new ArrayList<>();
                Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                // set the type to 'email'
                emailIntent.setType("vnd.android.cursor.dir/email");
                String to[] = {"willimowski4@gmail.com"};
                emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
                emailIntent .putExtra(Intent.EXTRA_SUBJECT, "Subject");
                for(int i = 1; i <= numberOfParticipants; i++){
                    for(int j = 97; j <= 96 + numberOfParticipants; j++){
                        Bitmap inImage = QRCode.QRCode("" + i + (char) j);
                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                        String path = MediaStore.Images.Media.insertImage(getContext().getContentResolver(), inImage, "Answer_" + i + "_Participant_"+ (j-96), null);
                        Uri uri = Uri.parse(path);
                        uris.add(uri);

                    }
                }
                // the attachment
                emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
                startActivity(Intent.createChooser(emailIntent , "Send email..."));
            }
        });





        return rootView;
    }
}
