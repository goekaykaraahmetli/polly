package com.polly.visuals;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.polly.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.polly.config.Config;
import com.polly.utils.Area;
import com.polly.utils.Location;
import com.polly.utils.Organizer;
import com.polly.utils.QRCode;
import com.polly.utils.RecyclerItemClickListener;
import com.polly.utils.SavingClass;
import com.polly.utils.ShowPollPage;
import com.polly.utils.command.poll.RegisterPollChangeListenerCommand;
import com.polly.utils.command.poll.RemovePollChangeListenerCommand;
import com.polly.utils.communication.DataStreamManager;
import com.polly.utils.communicator.Communicator;
import com.polly.utils.item.PollResultItem;
import com.polly.utils.item.SearchListItem;
import com.polly.utils.listadapter.ListAdapter;
import com.polly.utils.poll.PollDescription;
import com.polly.utils.poll.PollManager;
import com.polly.utils.wrapper.Message;
import com.polly.utils.wrapper.PollOptionsWrapper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ShowPollVotingPageFragment extends Fragment implements OnMapReadyCallback, OnChartGestureListener {
    private PieChart pieChart;
    private ImageView qrCode;
    static PollOptionsWrapper pollOptions;
    static Long id;
    private Button voteButton;
    private Communicator communicator = initialiseCommunicator();
    private boolean hasRunningPollChangeListener = false;
    long testDiff;
    private CountDownTimer countDownTimer;
    boolean isExpired = false;
    private boolean isGeofencePoll;
    private SwitchCompat toggleView;
    private GoogleMap googleMap;
    private boolean alertActive;
    private boolean locationPermissionGranted;
    private RecyclerView mRecyclerView;
    private ListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<SearchListItem> listOptions;
    private LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationProviderClient;
    Location usersLocation;
    private TextView listPollname;
    public static boolean toggled = false;

    private SavingClass saving;

    public static void open(long id) throws IOException {
        ShowPollVotingPageFragment.id = id;
        pollOptions = PollManager.getPollOptions(id);
        Navigation.findNavController(Organizer.getMainActivity(), R.id.nav_host_fragment).navigate(R.id.showPollVotingPageFragment);
    }

    @Override
    public void onDestroy() {
        if (hasRunningPollChangeListener) {
            try {
                communicator.send(DataStreamManager.PARTNERS_DEFAULT_COMMUNICATION_ID, new RemovePollChangeListenerCommand(id));
                hasRunningPollChangeListener = false;
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        id = null;

        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_showpoll, container, false);
        saving = new ViewModelProvider(getActivity()).get(SavingClass.class);

        pieChart = (PieChart) root.findViewById(R.id.pieChart);
        pieChart.setVisibility(View.GONE);
        voteButton = (Button) root.findViewById(R.id.vote_button);
        voteButton.setVisibility(View.GONE);
        toggleView = (SwitchCompat) root.findViewById(R.id.toggleView);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView = root.findViewById(R.id.PollOptionRecyclerView);
        mRecyclerView.setHasFixedSize(true); //Performance
        listPollname = (TextView) root.findViewById(R.id.listviewName);

        try {
            if (PollManager.isMyPoll(id)) {
                Button editButton = (Button) root.findViewById(R.id.edit_poll_button);
                editButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editPoll();
                    }
                });
                editButton.setVisibility(View.VISIBLE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        showPoll(root);
        LocalDateTime localDateTime = pollOptions.getBasicPollInformation().getExpirationTime();

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
                isExpired = true;
                countDownView.setText("Poll is expired");
            }
        }.start();

        if (id < 0) {
            isGeofencePoll = true;
            root.findViewById(R.id.mapLayout).setVisibility(View.VISIBLE);
            createForGeofencePoll(root);
            updateUsersLocation();
        }
        toggleView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    root.findViewById(R.id.toggleViewLayout).setVisibility(View.VISIBLE);
                    pieChart.setVisibility(View.INVISIBLE);
                    toggled = true;
                }else{
                    root.findViewById(R.id.toggleViewLayout).setVisibility(View.INVISIBLE);
                    pieChart.setVisibility(View.VISIBLE);
                    toggled = false;
                }

            }
        });
        return root;
    }

    public void showPoll(View root) {
        updatePieChart(pollOptions);
        updateListView(pollOptions);
        qrCode = (ImageView) root.findViewById(R.id.qrCodeImageView);
        qrCode.setImageBitmap(QRCode.QRCode("" + pollOptions.getBasicPollInformation().getId()));
        qrCode.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                MediaStore.Images.Media.insertImage(getContext().getContentResolver(), QRCode.QRCode("" + pollOptions.getBasicPollInformation().getId()), "QRCode: " + pollOptions.getBasicPollInformation().getName(), pollOptions.getBasicPollInformation().getDescription().getDescription());
                Toast.makeText(getContext(), "The QR-Code has been saved to your camera roll!", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        try {
            communicator.send(DataStreamManager.PARTNERS_DEFAULT_COMMUNICATION_ID, new RegisterPollChangeListenerCommand(pollOptions.getBasicPollInformation().getId(), false));
            hasRunningPollChangeListener = true;
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                PieEntry entry = (PieEntry) e;
                String selectedOption = entry.getLabel();
                showVoteButton(selectedOption);
            }

            @Override
            public void onNothingSelected() {
                showVoteButton(null);
            }
        });
        //TODO recyclerview onClickListener

      /*  mAdapter.setOnItemClickListener(new ListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                showVoteButton(listOptions.get(position).getmText1());
            }
        });*/
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            View lastView = null;
            @Override
            public void onItemClick(View view, int position) {
                showVoteButton(listOptions.get(position).getmText1());
                if(lastView != null){
                    lastView.setSelected(false);
                }
                lastView = view;
                view.setSelected(true);
            }

            @Override
            public void onLongItemClick(View view, int position) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle("Polldescription");
                alert.setMessage(pollOptions.getBasicPollInformation().getDescription().getDescription());
                alert.setPositiveButton("OK", null);
                alert.create().show();
            }
        }));
        pieChart.setOnChartGestureListener(this);
        pieChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle("Polldescription");
                alert.setMessage(pollOptions.getBasicPollInformation().getDescription().toString());
                alert.setPositiveButton("OK", null);
                alert.create().show();
            }
        });
    }

    public void showVoteButton(String option) {
        if (option == null) {
            // remove existing button:
            voteButton.setVisibility(View.GONE);
        } else {
            // add button which will vote for "option":
            voteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    voteButton.setVisibility(View.GONE);
                    try {
                        boolean voteSuccessful;
                        if (isGeofencePoll) {
                            voteSuccessful = PollManager.vote(pollOptions.getBasicPollInformation().getId(), option, usersLocation);
                        } else {
                            voteSuccessful = PollManager.vote(pollOptions.getBasicPollInformation().getId(), option);
                        }


                        //TODO Zeit mit loading-screen ??berbr??cken:


                        // show poll-results:
                        if (voteSuccessful) {
                            communicator.send(DataStreamManager.PARTNERS_DEFAULT_COMMUNICATION_ID, new RemovePollChangeListenerCommand(id));
                            ShowPollPage.showPollResultsPage(id);
                        } else {
                            Toast.makeText(getContext(), "voting for Poll failed. Please try again!", Toast.LENGTH_SHORT).show();
                        }


                    } catch (IllegalArgumentException | IOException e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            });
            if (isExpired) {
                voteButton.setVisibility(View.GONE);
            } else {
                voteButton.setVisibility(View.VISIBLE);
            }

        }
    }

    private Communicator initialiseCommunicator() {
        Communicator communicator = new Communicator() {
            @Override
            public void handleInput(Message message) {
                System.out.println("ShowPollVotingPageFragment received message from type: " + message.getDataType().getName());
                if (message.getDataType().equals(PollOptionsWrapper.class)) {
                    PollOptionsWrapper updatePoll = (PollOptionsWrapper) message.getData();

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updatePieChart(updatePoll);
                            updateListView(updatePoll);
                        }
                    });
                }
            }
        };
        communicator.start();
        return communicator;
    }

    private void updatePieChart(PollOptionsWrapper updatePoll) {
        pollOptions = updatePoll;
        ArrayList<PieEntry> options = new ArrayList<>();
        for (String option : pollOptions.getPollOptions()) {
            options.add(new PieEntry(Integer.valueOf(1), option));
        }

        PieDataSet pieDataSet = new PieDataSet(options, "");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(0f);
        PieData pieData = new PieData(pieDataSet);

        pieChart.setVisibility(View.INVISIBLE);

        pieChart.setData(pieData);
        /**Description description = new Description();
        description.setText(updatePoll.getBasicPollInformation().getDescription().getDescription());
        pieChart.setDescription(description);
        pieChart.getDescription().setEnabled(!updatePoll.getBasicPollInformation().getDescription().getDescription().equals(""));**/
        pieChart.setCenterText(updatePoll.getBasicPollInformation().getName());

        pieChart.setUsePercentValues(false);
        pieChart.animate();
        pieChart.setVisibility(View.VISIBLE);
    }

    private void updateListView(PollOptionsWrapper updatePoll) {
        pollOptions = updatePoll;
        ArrayList<SearchListItem> options = new ArrayList<>();
        for (String option : pollOptions.getPollOptions()) {
            options.add(new SearchListItem(R.drawable.ic_logo, option));
        }
        mAdapter = new ListAdapter(options);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        listPollname.setText(updatePoll.getBasicPollInformation().getName());
        listOptions = options;
    }

    public Date convertToDate(LocalDateTime data) {
        return Date.from(data.atZone(ZoneId.of("Europe/Berlin")).toInstant());
    }

    public static long getDifferenceInMS(Date date1, Date date2) {
        if (date2.getTime() - date1.getTime() > 0)
            return (date2.getTime() - date1.getTime());
        else
            return 0l;
    }

    public String timeDiffInString(long difference_In_Time) {
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
        if (diffDays == 0l && diffMinutes == 0l && diffHours == 0l) {
            return "less than a minute";
        }
        return diffDays + "d " + diffHours + "h : " + diffMinutes + "m";
    }

    private void createForGeofencePoll(View view) {
        if (!checkGooglePlayServices()) {
            Toast.makeText(getContext(), "No Google Play Services Available!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!locationPermissionGranted)
            checkLocationPermission();


        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragmentMapShowPoll);
        supportMapFragment.getMapAsync(this);

        checkGps();
    }

    private boolean checkGooglePlayServices() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int result = googleApiAvailability.isGooglePlayServicesAvailable(getContext());
        if (result == ConnectionResult.SUCCESS)
            return true;
        else if (googleApiAvailability.isUserResolvableError(result)) {
            Dialog dialog = googleApiAvailability.getErrorDialog(getActivity(), result, 201, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    Toast.makeText(getContext(), "User canceled dialog", Toast.LENGTH_SHORT).show();
                }
            });
            dialog.show();
        }
        return false;
    }

    private void checkLocationPermission() {
        System.out.println("checking location permission");

        Dexter.withContext(getContext()).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                System.out.println("permission granted");
                locationPermissionGranted = true;
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                System.out.println("permission denied");

                onLocationPermissionDenied();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                System.out.println("permission rationale should be shown");
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }

    private void initMap(LatLng center, double radius) {
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getContext(), "Please grant permission to use your locaiton!", Toast.LENGTH_SHORT).show();
            return;
        }
        googleMap.setMyLocationEnabled(true);

        googleMap.clear();

        CircleOptions circle = new CircleOptions();
        circle.center(center);
        circle.radius(radius);
        circle.strokeColor(Color.argb(255, 100, 255, 255));
        circle.fillColor(Color.argb(100, 100, 255, 255));

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title("Poll Area");
        markerOptions.position(center);

        googleMap.addMarker(markerOptions);
        googleMap.addCircle(circle);

        moveCameraToCurrentLocation();
    }

    private void checkGps() {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest).setAlwaysShow(true);

        Task<LocationSettingsResponse> locationSettingsResponseTask = LocationServices.getSettingsClient(getContext()).checkLocationSettings(builder.build());

        locationSettingsResponseTask.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                } catch (ApiException e) {
                    if (e.getStatusCode() == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                        ResolvableApiException resolvableApiException = (ResolvableApiException) e;

                        try {
                            resolvableApiException.startResolutionForResult(getActivity(), 101);
                        } catch (IntentSender.SendIntentException sendIntentException) {
                            sendIntentException.printStackTrace();
                        }
                    }

                    if (e.getStatusCode() == LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE) {
                        Toast.makeText(getContext(), "No Gps Available", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void moveCameraToCurrentLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                usersLocation = new Location(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude());
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(usersLocation.getLatitude(), usersLocation.getLongitude()), 5);
                googleMap.animateCamera(cameraUpdate);
            }
        }, Looper.myLooper());
    }

    private void updateUsersLocation() {
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                usersLocation = new Location(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude());
            }
        }, Looper.myLooper());
    }

    @Override
    public void onResume() {
        super.onResume();

        if (isGeofencePoll) {
            if (!alertActive)
                checkLocationPermission();

            if (locationPermissionGranted) {
                SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragmentMapShowPoll);
                supportMapFragment.getMapAsync(this);

                checkGps();
            }
        }
    }

    private void onLocationPermissionDenied() {
        alertActive = true;

        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("Grant permission");
        alert.setMessage("Do you want to grant LOCATION_ACCESS_PERMISSION?");
        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", Organizer.getMainActivity().getPackageName(), "");
                intent.setData(uri);
                startActivity(intent);
                alertActive = false;
            }
        });
        alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertActive = false;
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.polloptionFragment);
            }
        });
        alert.create().show();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        if (!isGeofencePoll)
            return;


        this.googleMap = googleMap;

        try {
            Area area = PollManager.getGeofencePollArea(id);
            initMap(new LatLng(area.getLatitude(), area.getLongitude()), area.getRadius());
        } catch (IOException e) {
            if (e.getMessage() != null)
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            else
                Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_LONG).show();
        }
    }

    private void editPoll() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("Delete Poll");
        alert.setMessage("Do you want to delete your Poll?");
        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    PollManager.delete(id);
                    Navigation.findNavController(Organizer.getMainActivity(), R.id.nav_host_fragment).navigate(R.id.startFragment);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                editPollName();
            }
        });
        alert.create().show();
    }

    private void editPollName(){
        PollDescription newDescription = new PollDescription("");
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("Edit your Pollname");
        alert.setMessage("Set your new Pollinformation here!");
        final EditText newPollname = new EditText(getContext());
        newPollname.setInputType(InputType.TYPE_CLASS_TEXT);
        newPollname.setHint("new Pollname");
        alert.setView(newPollname);
        alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String newName = newPollname.getText().toString();
                if(!newName.isEmpty()){
                    try {
                        PollManager.editPollName(id, newName);
                    } catch (IOException e) {
                        if (e.getMessage() != null)
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(getContext(), "No changes to Pollname were made", Toast.LENGTH_LONG).show();
                }
                editPollDescription();
            }
        });
        alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                editPollDescription();
            }
        });
        alert.create().show();

    }

    private void editPollDescription() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("Edit your Polldescription");
        alert.setMessage("Set your new Pollinformation here!");
        final EditText newPollname = new EditText(getContext());
        newPollname.setInputType(InputType.TYPE_CLASS_TEXT);
        newPollname.setHint("new Polldescription");
        alert.setView(newPollname);
        alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String newName = newPollname.getText().toString();
                try {
                    PollManager.editPollDescription(id, new PollDescription(newName));
                } catch (IOException e) {
                    if (e.getMessage() != null)
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_LONG).show();
                }
            }
        });
        alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alert.create().show();
    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartLongPressed(MotionEvent me) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("Polldescription");
        alert.setMessage(pollOptions.getBasicPollInformation().getDescription().getDescription());
        alert.setPositiveButton("OK", null);
        alert.create().show();
    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {

    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {

    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {

    }
}
