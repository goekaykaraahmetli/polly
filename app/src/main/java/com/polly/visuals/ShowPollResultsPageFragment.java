package com.polly.visuals;

import android.Manifest;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
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
import com.polly.config.Config;
import com.polly.utils.Area;
import com.polly.utils.Location;
import com.polly.utils.Organizer;
import com.polly.utils.QRCode;
import com.polly.utils.SavingClass;
import com.polly.utils.command.poll.RegisterPollChangeListenerCommand;
import com.polly.utils.command.poll.RemovePollChangeListenerCommand;
import com.polly.utils.communicator.Communicator;
import com.polly.utils.item.PollResultItem;
import com.polly.utils.listadapter.ListAdapterPollResult;
import com.polly.utils.poll.PollDescription;
import com.polly.utils.poll.PollManager;
import com.polly.utils.wrapper.Message;
import com.polly.utils.wrapper.PollResultsWrapper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ShowPollResultsPageFragment extends Fragment implements OnMapReadyCallback {
    private PieChart pieChart;
    private ImageView qrCode;
    static PollResultsWrapper pollResults;
    static Long id;
    private long testDiff;
    private Communicator communicator = initialiseCommunicator();
    private boolean hasRunningPollChangeListener = false;
    private CountDownTimer countDownTimer;
    private boolean isGeofencePoll;

    private GoogleMap googleMap;
    private boolean alertActive;
    private boolean locationPermissionGranted;
    private SwitchCompat toggleView;
    private RecyclerView mRecyclerView;
    private ListAdapterPollResult mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<PollResultItem> listOptions;
    private TextView listPollname;

    private LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationProviderClient;

    SavingClass saving;

    public static void open(long id) throws IOException {
        ShowPollResultsPageFragment.id = id;
        pollResults = PollManager.getPollResults(id);
        Navigation.findNavController(Organizer.getMainActivity(), R.id.nav_host_fragment).navigate(R.id.showPollResultsPageFragment);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (hasRunningPollChangeListener) {
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

        if (id < 0) {
            isGeofencePoll = true;
            root.findViewById(R.id.mapLayout).setVisibility(View.VISIBLE);
            createForGeofencePoll(root);
        }

        toggleView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    root.findViewById(R.id.toggleViewLayout).setVisibility(View.VISIBLE);
                    pieChart.setVisibility(View.INVISIBLE);
                }else{
                    root.findViewById(R.id.toggleViewLayout).setVisibility(View.INVISIBLE);
                    pieChart.setVisibility(View.VISIBLE);
                }

            }
        });

        return root;
    }

    public void showPoll(View root) {
        updatePieChart(pollResults);
        updateListView(pollResults);
        qrCode = (ImageView) root.findViewById(R.id.qrCodeImageView);
        qrCode.setImageBitmap(QRCode.QRCode("" + pollResults.getBasicPollInformation().getId()));
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


    private void updatePieChart(PollResultsWrapper updatePoll) {
        createNotificationChannel();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), "channel_id")
                .setSmallIcon(R.drawable.ic_logo).setContentTitle("Polly Notification")
                .setContentText("Someone has voted for your poll")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());
        notificationManager.notify(100, builder.build());
        
        pollResults = updatePoll;
        ArrayList<PieEntry> options = new ArrayList<>();
        for (String option : updatePoll.getPollResults().keySet()) {
            options.add(new PieEntry(updatePoll.getPollResults().get(option), option));
        }

        PieDataSet pieDataSet = new PieDataSet(options, "");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(17f);
        PieData pieData = new PieData(pieDataSet);
        pieData.setValueTextSize(17f);
        pieChart.setVisibility(View.INVISIBLE);

        pieChart.setData(pieData);
        /**Description description = new Description();
        description.setText(updatePoll.getBasicPollInformation().getDescription().getDescription());
        pieChart.setDescription(description);
        pieChart.getDescription().setEnabled(!updatePoll.getBasicPollInformation().getDescription().getDescription().equals(""));**/
        pieChart.setCenterText(updatePoll.getBasicPollInformation().getName());

        pieChart.setUsePercentValues(true);
        pieChart.animate();
        pieChart.setVisibility(View.VISIBLE);
    }

    private void updateListView(PollResultsWrapper updatePoll) {
        pollResults = updatePoll;
        ArrayList<PollResultItem> options = new ArrayList<>();
        int sum = pollResults.getPollResults().values().stream().mapToInt(Integer::intValue).sum();
        LinkedHashMap<String, Integer> descendingOrder = new LinkedHashMap<>();

        pollResults.getPollResults().entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).forEachOrdered(x -> descendingOrder.put(x.getKey(), x.getValue()));

        for (Map.Entry<String, Integer> option : descendingOrder.entrySet()) {
            options.add(new PollResultItem(R.drawable.ic_logo, option.getKey(), (option.getValue()/sum)*100, String.valueOf((option.getValue()/sum)*100)));
        }
        mAdapter = new ListAdapterPollResult(options);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        listPollname.setText(updatePoll.getBasicPollInformation().getName());
        listOptions = options;
    }

    public void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "polly_Channel";
            String description = "Channel for Push Notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("channel_id", name,importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
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

                Location usersLocation = new Location(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude());
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(usersLocation.getLatitude(), usersLocation.getLongitude()), 5);
                googleMap.animateCamera(cameraUpdate);
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
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void editPoll() {
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
                try {
                    PollManager.editPollName(id, newName);
                } catch (IOException e) {
                    if (e.getMessage() != null)
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_LONG).show();
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
}