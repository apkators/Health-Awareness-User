package com.health.threat.awareness.user.fragment;

import static com.health.threat.awareness.user.GoogleLocationActivity.MY_PERMISSIONS_REQUEST_LOCATION;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.health.threat.awareness.user.R;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class GetAppointmentFragment extends Fragment implements android.location.LocationListener {
    Button GetAppointmentBtn;
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    EditText edLatitude, edLongitude, edAltitude;
    EditText Sickness_title, Symptoms_description;
    Spinner HospitalsSpinner;
    TextView DateAndTimeTV;
    String date, month, year, hour, minutes;
    Button GetTime;

    ArrayList<String> HospitalIDs = new ArrayList<>();
    List<String> HospitalsNames = new ArrayList<>();

    public GetAppointmentFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_get_appointment, container, false);

        date = month = year = hour = minutes = null;

        edLatitude = view.findViewById(R.id.edLatitude);
        edLongitude = view.findViewById(R.id.edLongitude);
        edAltitude = view.findViewById(R.id.edAltitude);
        GetTime = view.findViewById(R.id.date_time_set);
        DateAndTimeTV = view.findViewById(R.id.DateAndTimeTV);
        Sickness_title = view.findViewById(R.id.Sickness_title);
        Symptoms_description = view.findViewById(R.id.Symptoms_description);
        HospitalsSpinner = view.findViewById(R.id.HospitalsSpinner);

        GetAppointmentBtn = view.findViewById(R.id.GetAppointmentBtn);

        GetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View dialogView = View.inflate(getActivity(), R.layout.date_time_picker, null);
                final AlertDialog alertDialog = new AlertDialog.Builder(requireActivity()).create();

                dialogView.findViewById(R.id.date_time_set).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        DatePicker datePicker = dialogView.findViewById(R.id.date_picker);
                        TimePicker timePicker = dialogView.findViewById(R.id.time_picker);

                        Calendar calendar = new GregorianCalendar(datePicker.getYear(),
                                datePicker.getMonth(),
                                datePicker.getDayOfMonth(),
                                timePicker.getHour(),
                                timePicker.getMinute());

                        date = String.valueOf(datePicker.getDayOfMonth());
                        month = String.valueOf(datePicker.getMonth());
                        year = String.valueOf(datePicker.getYear());
                        hour = String.valueOf(timePicker.getHour());
                        minutes = String.valueOf(timePicker.getMinute());

                        DateAndTimeTV.setText(date + "-" + month + "-" + year + " " + hour + ":" + minutes);
                        //time = calendar.getTimeInMillis();
                        alertDialog.dismiss();
                    }
                });
                alertDialog.setView(dialogView);
                alertDialog.show();
            }
        });

        GetAppointmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edLatitude.getText() == null || edLatitude.getText().toString().equals("")) {
                    Toast.makeText(requireActivity(), "Latitude is Required", Toast.LENGTH_SHORT).show();
                    edLatitude.requestFocus();
                    edLatitude.setError("Required");
                    return;
                }
                if (edLongitude.getText() == null || edLongitude.getText().toString().equals("")) {
                    Toast.makeText(requireActivity(), "Longitude is Required", Toast.LENGTH_SHORT).show();
                    edLongitude.requestFocus();
                    edLongitude.setError("Required");
                    return;
                }
                if (edAltitude.getText() == null || edAltitude.getText().toString().equals("")) {
                    Toast.makeText(requireActivity(), "edAltitude is Required", Toast.LENGTH_SHORT).show();
                    edAltitude.requestFocus();
                    edAltitude.setError("Required");
                    return;
                }

                if (Sickness_title.getText() == null || Sickness_title.getText().toString().equals("")) {
                    Toast.makeText(requireActivity(), "Sickness Title is Required", Toast.LENGTH_SHORT).show();
                    Sickness_title.requestFocus();
                    Sickness_title.setError("Required");
                    return;
                }

                if (Symptoms_description.getText() == null || Symptoms_description.getText().toString().equals("")) {
                    Toast.makeText(requireActivity(), "Symptoms Description is Required", Toast.LENGTH_SHORT).show();
                    Symptoms_description.requestFocus();
                    Symptoms_description.setError("Required");
                    return;
                }

                if (HospitalsSpinner.getSelectedItem() == null) {
                    Toast.makeText(requireActivity(), "Please Select Hospital from List", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (date == null || month == null || year == null || hour == null || minutes == null) {
                    Toast.makeText(requireActivity(), "Please Select Date and Time", Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(requireActivity(), "Please wait...", Toast.LENGTH_SHORT).show();

                SaveAppointmentToDatabase();
            }
        });

        getHospitals();

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            GetLocationPermission();
        }else {
            locationManager.requestLocationUpdates(LocationManager.FUSED_PROVIDER, 0L, (float) 0, this);
        }

        return view;
    }

    private void GetLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)) {

                // show an explanation to the user
                new AlertDialog.Builder(requireActivity())
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission to function properly. We use your Location to send you nearest Threats")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // try requesting the permission again
                                ActivityCompat.requestPermissions(requireActivity(),
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();

            } else {
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        } else {
            locationManager.requestLocationUpdates(LocationManager.FUSED_PROVIDER, 0L, (float) 0, this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        edLatitude.setText(String.valueOf(location.getLatitude()));
        edLongitude.setText(String.valueOf(location.getLongitude()));
        edAltitude.setText(String.valueOf(location.getAltitude()));
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude", "disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude", "enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude", "status");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted
                // do your location-related task here
                if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    Toast.makeText(requireActivity(), "Location Permission is needed", Toast.LENGTH_SHORT).show();
                    getActivity().onBackPressed();
                }
                locationManager.requestLocationUpdates(LocationManager.FUSED_PROVIDER, 0L, (float) 0, this);
            } else {
                // permission denied
                // disable the functionality that depends on this permission
                Toast.makeText(requireActivity(), "Location is needed to get You Notified about Treats in Your Area", Toast.LENGTH_SHORT).show();

            }
            return;
        }
    }


    private void getHospitals() {
        FirebaseDatabase.getInstance().getReference().child("Hospitals").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HospitalIDs.clear();
                HospitalsNames.clear();

                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                        (requireActivity(), android.R.layout.simple_spinner_item); //selected item will look like a spinner set from XML

                if (dataSnapshot.exists()) {
                    for (DataSnapshot eachAdRecord : dataSnapshot.getChildren()) {
                        {
                            HospitalIDs.add(eachAdRecord.getKey());
                            HospitalsNames.add(eachAdRecord.child("Name").getValue(String.class));

                            spinnerArrayAdapter.add(eachAdRecord.child("Name").getValue(String.class));
                        }
                    }
                    if (!HospitalIDs.isEmpty()) {
                        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                                .simple_spinner_dropdown_item);
                        HospitalsSpinner.setAdapter(spinnerArrayAdapter);

                        HospitalsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                    } else {
                        Toast.makeText(getActivity(), "No Hospitals found, Please wait for Hospital for Sign Up", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "No Hospitals found, Please wait for Hospital for Sign Up", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void SaveAppointmentToDatabase() {
        HashMap<String, Object> data = new HashMap<>();
        data.put("Sickness_title", Sickness_title.getText().toString());
        data.put("Symptoms_description", Symptoms_description.getText().toString());
        data.put("Latitude", edLatitude.getText().toString());
        data.put("CaseStatus","");
        data.put("Longitude", edLongitude.getText().toString());
        data.put("Altitude", edAltitude.getText().toString());
        data.put("HospitalID", HospitalIDs.get(HospitalsSpinner.getSelectedItemPosition()));
        data.put("HospitalName", HospitalsNames.get(HospitalsSpinner.getSelectedItemPosition()));
        data.put("date", date);
        data.put("month", month);
        data.put("year", year);
        data.put("hour", hour);
        data.put("minutes", minutes);
        data.put("By", FirebaseAuth.getInstance().getUid());
        data.put("AppointmentStatus","New");
        data.put("SicknessIdentified",false);
        data.put("Sickness","");

        DatabaseReference ownerRef;
        ownerRef = FirebaseDatabase.getInstance().getReference().child("Appointments");

        ownerRef.child(ownerRef.push().getKey()).updateChildren(data)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(requireActivity(), "Appointment applied successfully", Toast.LENGTH_SHORT).show();
                        getActivity().onBackPressed();
                    } else {
                        String message = task.getException().toString();
                        Toast.makeText(requireActivity(), "Error: " + message, Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(requireActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}