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

public class ScanRoomStart extends Fragment {
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
                Bitmap inImage = QRCode.QRCode("1A");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                String path = MediaStore.Images.Media.insertImage(getContext().getContentResolver(), inImage, "Answer_" + "1" + "_Participant_"+"1", null);
                Uri uri = Uri.parse(path);
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                // set the type to 'email'
                emailIntent.setType("vnd.android.cursor.dir/email");
                String to[] = {"willimowski4@gmail.com"};
                emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
                // the attachment
                emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
                // the mail subject
                emailIntent .putExtra(Intent.EXTRA_SUBJECT, "Subject");
                startActivity(Intent.createChooser(emailIntent , "Send email..."));
            }
        });





        return rootView;
    }
}
