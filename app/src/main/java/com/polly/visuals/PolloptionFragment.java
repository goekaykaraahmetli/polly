package com.polly.visuals;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
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
import com.polly.utils.poll.Poll;
import com.polly.utils.poll.PollManager;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PolloptionFragment extends Fragment {
    @Override
    public void onResume() {
        super.onResume();
        String[] visibilities = getResources().getStringArray(R.array.visibility);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter(getContext(), R.layout.dropdown_item, visibilities);
        AutoCompleteTextView dropDownMenu = (AutoCompleteTextView) getView().findViewById(R.id.autoCompleteTextView);
        dropDownMenu.setAdapter(arrayAdapter);
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

        AutoCompleteTextView test = (AutoCompleteTextView) root.findViewById(R.id.DatePicker);
        AutoCompleteTextView dropDownMenu = (AutoCompleteTextView) root.findViewById(R.id.autoCompleteTextView);
        dropDownMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextInputLayout geofence = (TextInputLayout) root.findViewById(R.id.geofencingLayout);
                TextInputLayout userGroup = (TextInputLayout) root.findViewById(R.id.usergroupLayout);
                TextInputLayout votingCandidates = (TextInputLayout) root.findViewById(R.id.votingCandidatesLayout);
                TextInputLayout oberserveCandidates = (TextInputLayout) root.findViewById(R.id.observingCandidatesLayout);

                if (dropDownMenu.getText().toString().equals("GEOFENCE")) {
                    geofence.setVisibility(View.VISIBLE);
                    userGroup.setVisibility(View.GONE);
                    votingCandidates.setVisibility(View.GONE);
                    oberserveCandidates.setVisibility(View.GONE);
                } else if (dropDownMenu.getText().toString().equals("PRIVATE")) {
                    geofence.setVisibility(View.GONE);
                    userGroup.setVisibility(View.VISIBLE);
                    votingCandidates.setVisibility(View.GONE);
                    oberserveCandidates.setVisibility(View.GONE);
                } else if (dropDownMenu.getText().toString().equals("CUSTOM")) {
                    geofence.setVisibility(View.GONE);
                    userGroup.setVisibility(View.GONE);
                    votingCandidates.setVisibility(View.VISIBLE);
                    oberserveCandidates.setVisibility(View.VISIBLE);
                } else if (dropDownMenu.getText().toString().equals("PUBLIC")) {
                    geofence.setVisibility(View.GONE);
                    userGroup.setVisibility(View.GONE);
                    votingCandidates.setVisibility(View.GONE);
                    oberserveCandidates.setVisibility(View.GONE);
                }
            }
        });
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
                        test.setText(test.getText() + time);
                    }
                }, hour, minute, true);
                timePickerDialog.show();

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        month = month + 1;
                        String date = year+"/"+month+"/"+day + " ";

                        test.setText(date);
                    }
                }, year,month, day);
                datePickerDialog.show();
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

            }
        });
        root.findViewById(R.id.CreatePollOnMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Editable poll = pollName.getText();
                //CharSequence poll1 = poll.toString();
                List<String> pollOptions = saving.getPollOptions();
                if(pollOptions == null){
                    Toast.makeText(getActivity(), "Please add some Options", Toast.LENGTH_SHORT).show();
                }
                if(Pollname.getText().length() == 0){
                    Toast.makeText(getActivity(), "Please enter Pollname", Toast.LENGTH_SHORT).show();
                }

                if(test.getText().length() == 0){
                    Toast.makeText(getActivity(), "Please choose an Expiration date", Toast.LENGTH_SHORT).show();
                }
                try {
                    long id = PollManager.createPoll(Pollname.toString(), pollOptions);
                    Toast.makeText(getActivity(), "Poll ID is: " + id, Toast.LENGTH_SHORT).show();
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "No connection to the server!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return root;
    }
}
