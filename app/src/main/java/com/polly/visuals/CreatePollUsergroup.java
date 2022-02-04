package com.polly.visuals;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.polly.R;
import com.polly.utils.InputFilterMinMax;
import com.polly.utils.SavingClass;
import com.polly.utils.item.SearchListItemUser;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CreatePollUsergroup extends Fragment {
    public static String name;

    static String userGroupName;
    public static int numberOfParticipants;
    LocalTime localTime;
    LocalDate localDate;

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

        if (saving.getNumberOfParticipants() != 0) {
            ((EditText) root.findViewById(R.id.PollyRoomNumber)).setText(String.valueOf(saving.getNumberOfParticipants()));
        }
        AutoCompleteTextView test = (AutoCompleteTextView) root.findViewById(R.id.DatePicker);
        AutoCompleteTextView dropDownMenu = (AutoCompleteTextView) root.findViewById(R.id.autoCompleteTextView);

        TextInputLayout datePicker = (TextInputLayout) root.findViewById(R.id.DateLayout);
        TextInputLayout votingCandidates = (TextInputLayout) root.findViewById(R.id.votingCandidatesLayout);
        TextInputLayout oberserveCandidates = (TextInputLayout) root.findViewById(R.id.observingCandidatesLayout);
        TextInputLayout pollyRoom = (TextInputLayout) root.findViewById(R.id.PollRoomLayout);
        TextView pollyRoomInfo = (TextView) root.findViewById(R.id.PollyRoomInfo);
        //Button createPollBtn = (Button) root.findViewById(R.id.CreatePollOnMenu);
        dropDownMenu.setVisibility(View.INVISIBLE);
        root.findViewById(R.id.VisibiltyLayout).setVisibility(View.INVISIBLE);
        votingCandidates.setVisibility(View.GONE);
        oberserveCandidates.setVisibility(View.GONE);
        pollyRoom.setVisibility(View.GONE);
        pollyRoomInfo.setVisibility(View.GONE);
        datePicker.setVisibility(View.VISIBLE);



        EditText Pollname = (EditText) root.findViewById(R.id.Name);
        TextInputEditText description = (TextInputEditText) root.findViewById(R.id.description);
        Pollname.setText(saving.getPollname());
        description.setText(saving.getDescription());
        if (saving.getCalendarText() != null)
            test.setText(saving.getCalendarText());
        if (saving.getDropDownMenu() != null)
            dropDownMenu.setText(saving.getDropDownMenu());
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        String time = hour + ":" + minute;
                        localTime = LocalTime.of(hour, minute);
                        saving.setLocalTime(localTime);
                        test.setText(test.getText() + time);
                    }
                }, hour, minute, true);
                timePickerDialog.show();

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        month = month + 1;
                        String date = year + "-" + month + "-" + day + " ";
                        localDate = LocalDate.of(year, month, day);
                        saving.setLocalDate(localDate);
                        test.setText(date);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });




        root.findViewById(R.id.continueBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Pollname.getText().length() == 0) {
                    Toast.makeText(getActivity(), "Please enter Pollname", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!dropDownMenu.getText().toString().equals("POLLYROOM")) {
                    if (saving.getLocalDate() == null || saving.getLocalTime() == null) {
                        Toast.makeText(getActivity(), "Please choose a date and a time!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    LocalDateTime localDateTime = LocalDateTime.of(saving.getLocalDate(), saving.getLocalTime());
                    if (localDateTime.isBefore(LocalDateTime.now(ZoneId.of("Europe/Berlin")))) {
                        Toast.makeText(getActivity(), "Please choose a date in the future!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (timeDiffInString(getDifferenceInMS(convertToDate(LocalDateTime.now(ZoneId.of("Europe/Berlin"))), convertToDate(LocalDateTime.of(saving.getLocalDate(), saving.getLocalTime()))))) {
                        Toast.makeText(getActivity(), "Polls must only last up to 1 year", Toast.LENGTH_SHORT).show();
                        return;
                    }

                }
                dropDownMenu.setText("CUSTOM");
                saving.setDescription(description.getText());
                saving.setPollname(Pollname.getText());
                saving.setCalendarText(test.getText());
                saving.setDropDownMenu(dropDownMenu.getText());
                saving.setNumberOfParticipants(numberOfParticipants);


                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child(userGroupName);
                rootRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        Iterator iter = snapshot.child("Users").getChildren().iterator();
                        List<String> canVoteList = new ArrayList<>();
                        while (iter.hasNext()) {
                            canVoteList.add((String) ((DataSnapshot) iter.next()).getKey());
                        }



                        saving.setCanVoteList(canVoteList);
                        saving.setCanSeeAndVoteList(canVoteList);


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });




                saving.setIsGroupPoll(true);
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.createPollFragment);
            }
        });

        return root;
    }

    public Date convertToDate(LocalDateTime data) {
        return Date.from(data.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static long getDifferenceInMS(Date date1, Date date2) {
        if (date2.getTime() - date1.getTime() > 0)
            return (date2.getTime() - date1.getTime());
        else
            return 0l;
    }

    public boolean timeDiffInString(long difference_In_Time) {
        long diffYears = TimeUnit
                .MILLISECONDS
                .toDays(difference_In_Time)
                / 365l;
        return diffYears > 0;
    }
}
