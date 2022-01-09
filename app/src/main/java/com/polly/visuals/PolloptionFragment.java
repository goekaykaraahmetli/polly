package com.polly.visuals;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentController;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.polly.R;
import com.polly.utils.QRCode;
import com.polly.utils.Area;
import com.polly.utils.poll.PollDescription;
import com.polly.utils.poll.PollManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PolloptionFragment extends Fragment {
    public static String name;

    public static String answer1;
    public static String answer2;
    public static String answer3;
    public static String answer4;
    public static int numberOfParticipants;
    LocalTime localTime;
    LocalDate localDate;
    LocalDateTime localDateTime;
    @Override
    public void onResume() {
        super.onResume();
        String[] visibilities = getResources().getStringArray(R.array.visibility);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter(getContext(), R.layout.dropdown_item, visibilities);
        AutoCompleteTextView dropDownMenu = (AutoCompleteTextView) getView().findViewById(R.id.autoCompleteTextView);
        dropDownMenu.setAdapter(arrayAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.startFragment);
        SavingClass saving = new ViewModelProvider(getActivity()).get(SavingClass.class);
        saving.setCalendarText(null);
        saving.setDescription(null);
        saving.setPollname(null);
        saving.reset();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = inflater.inflate(R.layout.activity_polloptions, container, false);
        SavingClass saving = new ViewModelProvider(getActivity()).get(SavingClass.class);
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        if(saving.getNumberOfParticipants() != null){
            ((EditText) root.findViewById(R.id.PollyRoomNumber)).setText(saving.getNumberOfParticipants());
        }
        AutoCompleteTextView test = (AutoCompleteTextView) root.findViewById(R.id.DatePicker);
        AutoCompleteTextView dropDownMenu = (AutoCompleteTextView) root.findViewById(R.id.autoCompleteTextView);

        TextInputLayout datePicker = (TextInputLayout) root.findViewById(R.id.DateLayout);
        TextInputLayout geofence = (TextInputLayout) root.findViewById(R.id.geofencingLayout);
        TextInputLayout userGroup = (TextInputLayout) root.findViewById(R.id.usergroupLayout);
        TextInputLayout votingCandidates = (TextInputLayout) root.findViewById(R.id.votingCandidatesLayout);
        TextInputLayout oberserveCandidates = (TextInputLayout) root.findViewById(R.id.observingCandidatesLayout);
        TextInputLayout pollyRoom = (TextInputLayout) root.findViewById(R.id.PollRoomLayout);
        TextView pollyRoomInfo = (TextView) root.findViewById(R.id.PollyRoomInfo);
        Button createPollBtn = (Button) root.findViewById(R.id.CreatePollOnMenu);
        Button sendQRviaEmail = (Button) root.findViewById(R.id.SendQRviaEmailBtn);
        TextInputEditText geofenceBtn = root.findViewById(R.id.geofencing);

        dropDownMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (dropDownMenu.getText().toString().equals("GEOFENCE")) {
                    createPollBtn.setText("CREATE POLL");
                    geofence.setVisibility(View.VISIBLE);
                    userGroup.setVisibility(View.GONE);
                    votingCandidates.setVisibility(View.GONE);
                    oberserveCandidates.setVisibility(View.GONE);
                    pollyRoom.setVisibility(View.GONE);
                    pollyRoomInfo.setVisibility(View.GONE);
                    datePicker.setVisibility(View.VISIBLE);
                    sendQRviaEmail.setVisibility(View.GONE);
                } else if (dropDownMenu.getText().toString().equals("PRIVATE")) {
                    createPollBtn.setText("CREATE POLL");
                    geofence.setVisibility(View.GONE);
                    userGroup.setVisibility(View.VISIBLE);
                    votingCandidates.setVisibility(View.GONE);
                    oberserveCandidates.setVisibility(View.GONE);
                    pollyRoom.setVisibility(View.GONE);
                    pollyRoomInfo.setVisibility(View.GONE);
                    datePicker.setVisibility(View.VISIBLE);
                    sendQRviaEmail.setVisibility(View.GONE);
                } else if (dropDownMenu.getText().toString().equals("CUSTOM")) {
                    createPollBtn.setText("CREATE POLL");
                    geofence.setVisibility(View.GONE);
                    userGroup.setVisibility(View.GONE);
                    votingCandidates.setVisibility(View.VISIBLE);
                    oberserveCandidates.setVisibility(View.VISIBLE);
                    pollyRoom.setVisibility(View.GONE);
                    pollyRoomInfo.setVisibility(View.GONE);
                    datePicker.setVisibility(View.VISIBLE);
                    sendQRviaEmail.setVisibility(View.GONE);
                } else if (dropDownMenu.getText().toString().equals("PUBLIC")) {
                    createPollBtn.setText("CREATE POLL");
                    geofence.setVisibility(View.GONE);
                    userGroup.setVisibility(View.GONE);
                    votingCandidates.setVisibility(View.GONE);
                    oberserveCandidates.setVisibility(View.GONE);
                    pollyRoom.setVisibility(View.GONE);
                    pollyRoomInfo.setVisibility(View.GONE);
                    datePicker.setVisibility(View.VISIBLE);
                    sendQRviaEmail.setVisibility(View.GONE);
                }else if(dropDownMenu.getText().toString().equals("POLLYROOM")){
                    createPollBtn.setText("SCAN ROOM");
                    geofence.setVisibility(View.GONE);
                    userGroup.setVisibility(View.GONE);
                    votingCandidates.setVisibility(View.GONE);
                    oberserveCandidates.setVisibility(View.GONE);
                    pollyRoomInfo.setVisibility(View.VISIBLE);
                    pollyRoom.setVisibility(View.VISIBLE);
                    saving.setCalendarText(null);
                    datePicker.setVisibility(View.GONE);
                    if(saving.getPollOptions() == null || saving.getPollOptions().size() < 2){
                        sendQRviaEmail.setVisibility(View.GONE);
                    }else{
                        sendQRviaEmail.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        if(saving.getDropDownMenu() != null) {
            dropDownMenu.setText(saving.getDropDownMenu().toString());
            if (dropDownMenu.getText().toString().equals("GEOFENCE")) {
                createPollBtn.setText("CREATE POLL");
                geofence.setVisibility(View.VISIBLE);
                userGroup.setVisibility(View.GONE);
                votingCandidates.setVisibility(View.GONE);
                oberserveCandidates.setVisibility(View.GONE);
                pollyRoom.setVisibility(View.GONE);
                pollyRoomInfo.setVisibility(View.GONE);
                datePicker.setVisibility(View.VISIBLE);
                sendQRviaEmail.setVisibility(View.GONE);
            } else if (dropDownMenu.getText().toString().equals("PRIVATE")) {
                createPollBtn.setText("CREATE POLL");
                geofence.setVisibility(View.GONE);
                userGroup.setVisibility(View.VISIBLE);
                AutoCompleteTextView usergroupText = (AutoCompleteTextView) root.findViewById(R.id.usergroupNumber);
                usergroupText.setText(saving.getUsergroupName());
                votingCandidates.setVisibility(View.GONE);
                oberserveCandidates.setVisibility(View.GONE);
                pollyRoom.setVisibility(View.GONE);
                pollyRoomInfo.setVisibility(View.GONE);
                datePicker.setVisibility(View.VISIBLE);
                sendQRviaEmail.setVisibility(View.GONE);
            } else if (dropDownMenu.getText().toString().equals("CUSTOM")) {
                createPollBtn.setText("CREATE POLL");
                geofence.setVisibility(View.GONE);
                userGroup.setVisibility(View.GONE);
                votingCandidates.setVisibility(View.VISIBLE);
                oberserveCandidates.setVisibility(View.VISIBLE);
                AutoCompleteTextView votingCandidatesList = (AutoCompleteTextView) root.findViewById(R.id.votingCandidates);
                AutoCompleteTextView observingCandidatesList = (AutoCompleteTextView) root.findViewById(R.id.observingCandidates);
                pollyRoom.setVisibility(View.GONE);
                pollyRoomInfo.setVisibility(View.GONE);
                datePicker.setVisibility(View.VISIBLE);
                sendQRviaEmail.setVisibility(View.GONE);
                if(saving.getUserArrayVoting() != null){
                    votingCandidatesList.setText(saving.getUserArrayVoting().get(0).getmText1() + ", ...");
                }
                if(saving.getUserArrayObserving() != null){
                    observingCandidatesList.setText(saving.getUserArrayObserving().get(0).getmText1() + ", ...");
                }
            }else if(dropDownMenu.getText().toString().equals("POLLYROOM")){
                createPollBtn.setText("SCAN ROOM");
                geofence.setVisibility(View.GONE);
                userGroup.setVisibility(View.GONE);
                votingCandidates.setVisibility(View.GONE);
                oberserveCandidates.setVisibility(View.GONE);
                pollyRoomInfo.setVisibility(View.VISIBLE);
                pollyRoom.setVisibility(View.VISIBLE);
                saving.setCalendarText(null);
                datePicker.setVisibility(View.GONE);
                if(saving.getPollOptions() == null){
                    sendQRviaEmail.setVisibility(View.GONE);
                }else{
                    sendQRviaEmail.setVisibility(View.VISIBLE);
                }
            }
        }

        EditText Pollname = (EditText) root.findViewById(R.id.Name);
        TextInputEditText description = (TextInputEditText) root.findViewById(R.id.description);
        Pollname.setText(saving.getPollname());
        description.setText(saving.getDescription());
        if(saving.getCalendarText() != null)
            test.setText(saving.getCalendarText());
        if(saving.getDropDownMenu() != null)
            dropDownMenu.setText(saving.getDropDownMenu());
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        String time = hour + ":" + minute;
                        localTime = LocalTime.parse(time);
                        test.setText(test.getText() + time);
                    }
                }, hour, minute, true);
                timePickerDialog.show();

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        month = month + 1;
                        String date = year+"-"+month+"-"+day + " ";
                        localDate = LocalDate.parse(year +"-"+month+"-"+day);
                        test.setText(date);
                    }
                }, year,month, day);
                datePickerDialog.show();
            }
        });

        root.findViewById(R.id.usergroupNumber).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saving.setDescription(description.getText());
                saving.setPollname(Pollname.getText());
                saving.setCalendarText(test.getText());
                saving.setDropDownMenu(dropDownMenu.getText());
                System.out.println(dropDownMenu.getText());
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.usergroupSearch);
            }
        });
        EditText tmp = root.findViewById(R.id.PollyRoomNumber);
        tmp.setFilters(new InputFilter[]{
                new InputFilterMinMax("1", "26")
        });
        root.findViewById(R.id.votingCandidates).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saving.setDescription(description.getText());
                saving.setPollname(Pollname.getText());
                saving.setCalendarText(test.getText());
                saving.setDropDownMenu(dropDownMenu.getText());
                System.out.println(dropDownMenu.getText());
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.votingCandidates2);
            }
        });
        root.findViewById(R.id.observingCandidates).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saving.setDescription(description.getText());
                saving.setPollname(Pollname.getText());
                saving.setCalendarText(test.getText());
                saving.setDropDownMenu(dropDownMenu.getText());
                saving.setGeofence(geofenceBtn.getText());
                System.out.println(dropDownMenu.getText());
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.observingCandidates2);
            }
        });
        root.findViewById(R.id.EditOptions).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.createPollFragment);
                saving.setDescription(description.getText());
                saving.setPollname(Pollname.getText());
                saving.setCalendarText(test.getText());
                saving.setDropDownMenu(dropDownMenu.getText());
                saving.setGeofence(geofenceBtn.getText());

            }
        });
        root.findViewById(R.id.CreatePollOnMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextInputEditText participants = root.findViewById(R.id.PollyRoomNumber);
                if(!participants.getText().toString().equals(""))
                    numberOfParticipants = Integer.parseInt(participants.getText().toString());
                else
                    numberOfParticipants = 0;
                List<String> pollOptions = saving.getPollOptions();

                name = ((EditText) root.findViewById(R.id.Name)).getText().toString();
                if (dropDownMenu.getText().toString().equals("POLLYROOM")) {
                    if (pollOptions == null) {
                        Toast.makeText(getActivity(), "Please add some Options", Toast.LENGTH_SHORT).show();
                    }
                    else if (Pollname.getText().length() == 0) {
                        Toast.makeText(getActivity(), "Please enter Pollname", Toast.LENGTH_SHORT).show();
                    }
                    else if (numberOfParticipants == 0){
                        Toast.makeText(getActivity(), "Please increase the number of participants", Toast.LENGTH_SHORT).show();
                    }
                    else{
                    List<String> options = saving.getPollOptions();
                    if(options.get(0) != null){
                        answer1 = options.get(0);
                    }
                    if(options.get(1) != null){
                        answer2 = options.get(1);
                    }
                    if(options.size() > 2 && options.get(2) != null){
                        answer3 = options.get(2);
                    }
                    if(options.size() > 3 && options.get(3) != null){
                        answer4 = options.get(3);
                    }
                    Intent intent = new Intent(getActivity(), BarcodeScannerActivity.class);
                    startActivity(intent);}
                } else {
                    //Editable poll = pollName.getText();
                    //CharSequence poll1 = poll.toString();
                    if (pollOptions == null) {
                        Toast.makeText(getActivity(), "Please add some Options", Toast.LENGTH_SHORT).show();
                    }
                    if (Pollname.getText().length() == 0) {
                        Toast.makeText(getActivity(), "Please enter Pollname", Toast.LENGTH_SHORT).show();
                    }

                    if(!(test.getText().toString().contains("-") && test.getText().toString().contains(":"))){
                    Toast.makeText(getActivity(), "Please choose a valid Expiration date", Toast.LENGTH_SHORT).show();
                     }

                    try {
                        long id;
                        localDateTime.of(localDate, localTime);
                        switch(dropDownMenu.getText().toString()) {
                            case "PUBLIC":
                                id = PollManager.createPublicPoll(Pollname.getText().toString(), new PollDescription(description.getText().toString()), localDateTime, pollOptions);
                                break;
                            case "PRIVATE":
                                id = PollManager.createPrivatePoll(Pollname.getText().toString(), new PollDescription(description.getText().toString()), localDateTime, pollOptions, saving.getUsergroupName());
                                break;
                            case "CUSTOM":
                                id = PollManager.createCustomPoll(Pollname.getText().toString(), new PollDescription(description.getText().toString()), localDateTime, pollOptions, saving.getUserArrayVoting(), saving.getUserArrayObserving());
                                break;
                            case "GEOFENCE":
                                long latitude = 0L;     //TODO
                                long longitude = 0L;    //TODO
                                id = PollManager.createGeofencePoll(Pollname.getText().toString(), new PollDescription(description.getText().toString()), localDateTime , pollOptions, new Area(latitude, longitude, Double.parseDouble(geofenceBtn.getText().toString())));
                                break;
                            default:
                                throw new IllegalStateException("Unexpected value: " + "switch statement can't work with the given cases");
                        }
                        Toast.makeText(getActivity(), "Poll ID is: " + id, Toast.LENGTH_SHORT).show();
                    } catch (InterruptedException | IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "No connection to the server!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        Button sendQRViaEmailBtn = root.findViewById(R.id.SendQRviaEmailBtn);
        sendQRViaEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextInputEditText participants = root.findViewById(R.id.PollyRoomNumber);
                numberOfParticipants = Integer.parseInt(participants.getText().toString());
                int numberOfOptions = saving.getOptionCounter();
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle("Enter the E-Mail address you want the QR-Codes sent to");
                alert.setMessage("Please enter an E-Mail");
                EditText usernameInput = new EditText(getContext());
                alert.setView(usernameInput);
                alert.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ArrayList<Uri> uris = new ArrayList<>();
                        Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                        // set the type to 'email'
                        emailIntent.setType("vnd.android.cursor.dir/email");
                        String to[] = {"willimowski4@gmail.com"};
                        emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
                        emailIntent .putExtra(Intent.EXTRA_SUBJECT, "Subject");
                        for(int i = 1; i <= numberOfOptions; i++){
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
                alert.show();
            }
        });
        return root;
    }
}
