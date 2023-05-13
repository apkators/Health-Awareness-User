package com.health.threat.awareness.user;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.health.threat.awareness.user.adapter.CustomDrawerAdapter;
import com.health.threat.awareness.user.app.AppPreferenceManager;
import com.health.threat.awareness.user.dialog_fragment.VirusDetailDialogFragment;
import com.health.threat.awareness.user.fragment.AllVirusCasesFragment;
import com.health.threat.awareness.user.model.DrawerItem;
import com.health.threat.awareness.user.model.VirusModel;
import com.health.threat.awareness.user.services.LocationTrackerService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class GoogleLocationActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final String TAG = "OrderPlaceNotification";
    // as Google documentation
    GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    LocationCallback mLocationCallback;
    Marker mCurrLocationMarker;
    FusedLocationProviderClient mFusedLocationClient;
    //int maxId;
    Boolean running, isFirstTimeAlert = true;
    Boolean isCalled = false, refreshRequest = true;
    private final int RC_LOCATION_ON_REQUEST = 1235;
    private static final int RC_LOCATION_REQUEST = 1234;

    FirebaseDatabase firebaseDatabase;
    ValueEventListener seeker_valueEventListener = null, seeker2_valueEventListener = null,
            order_valueEventListener = null, exp_ValueListener = null, avg_Rating_ValueListener = null, RatingAndReview_ValueListener = null;
    Location location;
    String MyName = "user";
    AlertDialog alertDialog;
    Button LogoutBtn;
    ProgressBar ShowOnLoadingLocation;
    ValueEventListener myVal = null;
    private Boolean isFirstTimeZoom = true;
    private ArrayList<VirusModel> virusList;
    private DatabaseReference databaseReference, ordersRef, ordersRefChecking;
    //private View loadingView;
    private SweetAlertDialog pDialog;
    private DrawerLayout drawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    CustomDrawerAdapter adapter;
    List<DrawerItem> dataList;

    @Override
    public void finish() {
        if (seeker_valueEventListener != null)
            databaseReference.removeEventListener(seeker_valueEventListener);
        if (seeker2_valueEventListener != null)
            databaseReference.removeEventListener(seeker2_valueEventListener);
        if (order_valueEventListener != null)
            databaseReference.removeEventListener(order_valueEventListener);
        if (exp_ValueListener != null)
            databaseReference.removeEventListener(exp_ValueListener);
        if (avg_Rating_ValueListener != null)
            databaseReference.removeEventListener(avg_Rating_ValueListener);
        if (RatingAndReview_ValueListener != null)
            databaseReference.removeEventListener(RatingAndReview_ValueListener);
        super.finish();
    }

    String UID = "";
    private LocationRequest locationRequest;
    public ProgressBar locationOnLoading;
    SwitchCompat showLocationOnMap_Switch;
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (context != null && intent != null) {
                if (intent.getStringExtra("isConnected") != null) {
                    if (intent.getStringExtra("isConnected").equals("true")) {
                        if (showLocationOnMap_Switch != null)
                            if (!showLocationOnMap_Switch.isChecked())
                                showLocationOnMap_Switch.setChecked(true);

                    } else {
                        if (showLocationOnMap_Switch != null)
                            if (showLocationOnMap_Switch.isChecked())
                                showLocationOnMap_Switch.setChecked(false);

                    }
                    if (locationOnLoading != null)
                        if (locationOnLoading.getVisibility() == View.VISIBLE)
                            locationOnLoading.setVisibility(View.GONE);

                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_location);

        UploadDeviceToken();

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("Connection"));

        setUpLocationRequest();

        showLocationOnMap_Switch = findViewById(R.id.show_location_on_map_btn);
        locationOnLoading = findViewById(R.id.locationOnLoading);
        locationOnLoading.setVisibility(View.GONE);

        showLocationOnMap_Switch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                locationOnLoading.setVisibility(View.GONE);
                checkLocationSettingsRequest();
                //startService(new Intent(SeekerActivity.this, LocationTrackerService.class).putExtra("key","start"));
            } else {
                locationOnLoading.setVisibility(View.GONE);
                stopService(new Intent(GoogleLocationActivity.this, LocationTrackerService.class).putExtra("key", "stop"));
            }
        });

        // Initializing
        dataList = new ArrayList<DrawerItem>();
        mTitle = mDrawerTitle = getTitle();
        drawerLayout = findViewById(R.id.my_drawer_layout);
        mDrawerList = findViewById(R.id.left_drawer);

        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        // Add Drawer Item to dataList
        dataList.add(new DrawerItem("All Virus Cases", R.drawable.img));
        //dataList.add(new DrawerItem("Appointment History", R.drawable.appointment));
        //dataList.add(new DrawerItem("Show Google Map", R.drawable.outline_location_on_24));
        dataList.add(new DrawerItem("Logout", R.drawable.outline_directions_walk_24));

        //dataList.add(new DrawerItem("Privacy Policy", R.drawable.outline_privacy_tip_24));
        //dataList.add(new DrawerItem("Disclaimer", R.drawable.outline_back_hand_24));

        adapter = new CustomDrawerAdapter(this, R.layout.custom_drawer_item, dataList);
        mDrawerList.setAdapter(adapter);

        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        Toolbar toolbar = findViewById(R.id.default_toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));

        setSupportActionBar(toolbar);

        try {
            // to make the Navigation drawer icon always appear on the action bar
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            Log.e("Error", e.getMessage() + "");
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to
                // onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to
                // onPrepareOptionsMenu()
            }
        };

        drawerLayout.addDrawerListener(mDrawerToggle);
        GetLocationAndNotificationPermission();

        UID = getIntent().getStringExtra("UID");

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        //loadingView=findViewById(R.id.loadingView);
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading ...");
        pDialog.setCancelable(false);

        ShowOnLoadingLocation = findViewById(R.id.loadingOnLocation);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                List<Location> locationList = locationResult.getLocations();
                if (locationList.size() > 0) {
                    location = locationList.get(locationList.size() - 1);
                    mLastLocation = location;
                    if (mCurrLocationMarker != null) {
                        mCurrLocationMarker.remove();
                    }

                    addYouOnMap(location);
                    if (!isCalled || refreshRequest)
                        getVirusCases();
                }
            }
        };

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        FragmentManager fm = getSupportFragmentManager();
        mapFrag = (SupportMapFragment) fm.findFragmentById(R.id.map);
        if (mapFrag == null) {
            mapFrag = new SupportMapFragment();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.map, mapFrag, "mapFragment");
            ft.commit();
            fm.executePendingTransactions();
        }
        mapFrag.getMapAsync(this);
    }

    private void setUpLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    private void checkLocationSettingsRequest() {

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient locationClient = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> locationSettings = locationClient.checkLocationSettings(builder.build());

        locationSettings.addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("tag", "Exception occur");
                if (task.getException() instanceof ResolvableApiException) {
                    // show permission request to user
                    try {
                        ResolvableApiException resolvable = (ResolvableApiException) task.getException();
                        resolvable.startResolutionForResult(GoogleLocationActivity.this,
                                RC_LOCATION_ON_REQUEST);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            } else {
                startLocationUpdates();
            }
        });
    }


    private void GetLocationAndNotificationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {

                // show an explanation to the user
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission to function properly. We use your Location to send you nearest Threats")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // try requesting the permission again
                                ActivityCompat.requestPermissions(GoogleLocationActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.POST_NOTIFICATIONS},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    private void UploadDeviceToken() {
        String token = new AppPreferenceManager(GoogleLocationActivity.this).getDeviceToken();
        DatabaseReference deviceTokenRef = FirebaseDatabase.getInstance().getReference().child("UsersDeviceTokens").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        deviceTokenRef.child("DeviceToken").setValue(token);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return false;
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public void SelectItem(int position) {
        FragmentTransaction fragmentTransaction;

        switch (position) {
            case 0:
                if (drawerLayout.isOpen())
                    drawerLayout.close();

                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, new AllVirusCasesFragment());
                fragmentTransaction.addToBackStack("HomeFragment");
                fragmentTransaction.commit();

                findViewById(R.id.map).setVisibility(View.GONE);
                return;

           /* case 1:
                if (drawerLayout.isOpen())
                    drawerLayout.close();

                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, new AppointmentHistoryFragment());
                fragmentTransaction.addToBackStack("HomeFragment");
                fragmentTransaction.commit();

                return;*/

            /*case 1:
                if (drawerLayout.isOpen())
                    drawerLayout.close();

                //startActivity(new Intent(this, LocationActivity.class));

                return;*/

            case 1:
                if (drawerLayout.isOpen())
                    drawerLayout.close();

                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, LoginActivity.class));
                this.finish();

                return;
        }

        mDrawerList.setItemChecked(position, false);
        mDrawerList.setCacheColorHint(getColor(R.color.transparent));
        mDrawerList.getSelectedView().setBackgroundColor(getColor(R.color.transparent));
        mDrawerList.setSelector(android.R.color.transparent);
        drawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void onBackPressed(){
        FragmentManager fm = getSupportFragmentManager();
        int count = fm.getBackStackEntryCount();
        if (count > 0) {
            Log.i("MainActivity", "popping backstack");
            fm.popBackStack();

            count = fm.getBackStackEntryCount();
            if (count==1){
                findViewById(R.id.map).setVisibility(View.VISIBLE);
            }
        } else {
            Log.i("MainActivity", "nothing on backstack, calling super");
            super.onBackPressed();
        }
    }

    public class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            SelectItem(position);
            mDrawerList.setItemChecked(position, false);
        }
    }

    private void getVirusCases() {
        if (!isCalled || refreshRequest) {
            refreshRequest = false;
            databaseReference.child("Virus").addValueEventListener(seeker_valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        isCalled = true;

                        String ID;
                        Double Latitude, Longitude, Altitude;
                        String By;
                        String CaseCategory;
                        String CaseStatus;
                        String HospitalID;
                        String HospitalName;
                        String Case_title;
                        String Case_description;
                        String Name;
                        String Remarks;
                        String Sickness;
                        boolean SicknessIdentified;
                        String Hour;
                        String Minutes;
                        String Month;
                        String Year;
                        String AffectedUserID;
                        String Date;

                        if (virusList != null)
                            virusList.clear();
                        if (mGoogleMap != null)
                            mGoogleMap.clear();
                        virusList = new ArrayList<>();
                        for (DataSnapshot eachUserRecord : dataSnapshot.getChildren()) {
                            ID = "";
                            AffectedUserID = "";
                            Date = "";
                            By= "";
                            CaseCategory= "";
                            CaseStatus= "";
                            HospitalID= "";
                            HospitalName= "";
                            Case_title= "";
                            Case_description= "";
                            Name= "";
                            Remarks= "";
                            Sickness= "";
                            SicknessIdentified = true;
                            Hour= "";
                            Minutes= "";
                            Month= "";
                            Year= "";

                            Latitude = null;
                            Longitude = null;
                            Altitude = null;

                            /*if (eachUserRecord.hasChild("ID")) {
                                ID = Objects.requireNonNull(eachUserRecord.child("ID").getValue()).toString().trim();
                            }*/
                            ID = eachUserRecord.getKey();
                            if (eachUserRecord.hasChild("AffectedUserID")) {
                                AffectedUserID = Objects.requireNonNull(eachUserRecord.child("AffectedUserID").getValue()).toString().trim();
                            }
                            if (eachUserRecord.hasChild("date")) {
                                Date = Objects.requireNonNull(eachUserRecord.child("date").getValue()).toString().trim();
                            }
                            if (eachUserRecord.hasChild("month")) {
                                Month = Objects.requireNonNull(eachUserRecord.child("month").getValue()).toString().trim();
                            }
                            if (eachUserRecord.hasChild("year")) {
                                Year = Objects.requireNonNull(eachUserRecord.child("year").getValue()).toString().trim();
                            }
                            if (eachUserRecord.hasChild("By")) {
                                By = Objects.requireNonNull(eachUserRecord.child("By").getValue()).toString().trim();
                            }
                            if (eachUserRecord.hasChild("CaseCategory")) {
                                CaseCategory = Objects.requireNonNull(eachUserRecord.child("CaseCategory").getValue()).toString().trim();
                            }
                            if (eachUserRecord.hasChild("CaseStatus")) {
                                CaseStatus = Objects.requireNonNull(eachUserRecord.child("CaseStatus").getValue()).toString().trim();
                            }
                            if (eachUserRecord.hasChild("HospitalID")) {
                                HospitalID = Objects.requireNonNull(eachUserRecord.child("HospitalID").getValue()).toString().trim();
                            }
                            if (eachUserRecord.hasChild("HospitalName")) {
                                HospitalName = Objects.requireNonNull(eachUserRecord.child("HospitalName").getValue()).toString().trim();
                            }
                            if (eachUserRecord.hasChild("Case_title")) {
                                Case_title = Objects.requireNonNull(eachUserRecord.child("Case_title").getValue()).toString().trim();
                            }
                            if (eachUserRecord.hasChild("Case_description")) {
                                Case_description = Objects.requireNonNull(eachUserRecord.child("Case_description").getValue()).toString().trim();
                            }
                            if (eachUserRecord.hasChild("Name")) {
                                Name = Objects.requireNonNull(eachUserRecord.child("Name").getValue()).toString().trim();
                            }
                            if (eachUserRecord.hasChild("Remarks")) {
                                Remarks = Objects.requireNonNull(eachUserRecord.child("Remarks").getValue()).toString().trim();
                            }
                            if (eachUserRecord.hasChild("Sickness")) {
                                Sickness = Objects.requireNonNull(eachUserRecord.child("Sickness").getValue()).toString().trim();
                            }
                            if (eachUserRecord.hasChild("SicknessIdentified")) {
                                SicknessIdentified = Objects.requireNonNull(eachUserRecord.child("SicknessIdentified").getValue(Boolean.class));
                            }
                            if (eachUserRecord.hasChild("hour")) {
                                Hour = Objects.requireNonNull(eachUserRecord.child("hour").getValue()).toString().trim();
                            }
                            if (eachUserRecord.hasChild("minutes")) {
                                Minutes = Objects.requireNonNull(eachUserRecord.child("minutes").getValue()).toString().trim();
                            }

                            if (eachUserRecord.hasChild("Latitude")) {
                                Latitude = Double.valueOf(Objects.requireNonNull(eachUserRecord.child("Latitude").getValue(String.class)));
                            } else
                                Latitude = 0.0;

                            if (eachUserRecord.hasChild("Longitude")) {
                                Longitude = Double.valueOf(Objects.requireNonNull(eachUserRecord.child("Longitude").getValue(String.class)));
                            } else
                                Longitude = 0.0;
                            if (eachUserRecord.hasChild("Altitude")) {
                                String altitude = String.valueOf(eachUserRecord.child("Altitude").getValue(String.class));
                                if (!altitude.equals("0"))
                                    Altitude = Double.valueOf(Objects.requireNonNull(eachUserRecord.child("Altitude").getValue(String.class)));
                                else
                                    Altitude = 0.0;
                            } else
                                Altitude = 0.0;

                            //SharedPreferences sharedPreferences = getSharedPreferences("Categories", Context.MODE_PRIVATE);
                            //String category = sharedPreferences.getString("category", "");

                            if (Latitude == null || Longitude == null)
                                continue;

                            //float[] results = new float[1];
                            //Location.distanceBetween(location.getLatitude(), location.getLongitude(),
                            //        Latitude, Longitude, results);

                            //if (!(results[0] / 1000 <= SearchingRange)) {
                            //    continue;
                            //}

                            VirusModel virusModel = new VirusModel();
                            ArrayList<String> Ids = new ArrayList<>();
                            Ids.add(AffectedUserID);

                            virusModel.setAffectedUserID(AffectedUserID);
                            virusModel.setID(ID);
                            virusModel.setAffectedUsersID(Ids);
                            virusModel.setDate(Date);
                            virusModel.setLatitude(String.valueOf(Latitude));
                            virusModel.setLongitude(String.valueOf(Longitude));
                            virusModel.setAltitude(String.valueOf(Altitude));
                            virusModel.setBy(By);
                            virusModel.setCaseCategory(CaseCategory);
                            virusModel.setCaseStatus(CaseStatus);
                            virusModel.setHospitalID(HospitalID);
                            virusModel.setHospitalName(HospitalName);
                            virusModel.setCase_title(Case_title);
                            virusModel.setCase_description(Case_description);
                            virusModel.setName(Name);
                            virusModel.setRemarks(Remarks);
                            virusModel.setSickness(Sickness);
                            virusModel.setSicknessIdentified(SicknessIdentified);
                            virusModel.setHour(Hour);
                            virusModel.setMinutes(Minutes);
                            virusModel.setMonth(Month);
                            virusModel.setYear(Year);

                            virusList.add(virusModel);
                        }

                        if (!virusList.isEmpty()) {
                            for (VirusModel p : virusList) {
                                //Place current location marker
                                LatLng latLng = new LatLng(Double.valueOf(p.getLatitude()),Double.valueOf( p.getLongitude()));
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(latLng);
                                markerOptions.title(p.getCase_title());
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                                Marker marker = mGoogleMap.addMarker(markerOptions);
                                marker.setTag(p.getID());
                            }
                            mGoogleMap.setOnMarkerClickListener(m -> {
                                if (Objects.requireNonNull(m.getTag()).toString().equals("CurrentUserLocation")) {
                                    new AlertDialog.Builder(GoogleLocationActivity.this)
                                            .setCancelable(true)
                                            .setTitle("Your Location")
                                            .setMessage("This is You on Map")
                                            .setPositiveButton("OK", (dialog, which) -> dialog.cancel()).create().show();
                                    return true;
                                }

                                for (VirusModel p : virusList) {
                                    if (p.getID().equals(Objects.requireNonNull(m.getTag()).toString())) {
                                        VirusDetailDialogFragment dialogFragment = new VirusDetailDialogFragment(p);
                                        dialogFragment.show(getSupportFragmentManager(), "Show");
                                    }
                                }
                                return true;
                            });

                            if (pDialog.isShowing())
                                pDialog.dismiss();

                        } else {
                            if (pDialog.isShowing())
                                pDialog.dismiss();
                            if (isFirstTimeAlert) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(GoogleLocationActivity.this)
                                        .setCancelable(true)
                                        .setTitle("Message")
                                        .setMessage("No Areas are affected by Virus")
                                        .setPositiveButton("OK", (dialog, which) -> dialog.cancel());

                                AlertDialog alertDialog = builder.create();

                                if (!((GoogleLocationActivity.this)).isFinishing()) {
                                    alertDialog.show();
                                }
                                isFirstTimeAlert = false;
                            }
                        }
                    }
                    else{
                        if (isFirstTimeAlert) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(GoogleLocationActivity.this)
                                    .setCancelable(true)
                                    .setTitle("Message")
                                    .setMessage("No Areas are affected by Virus")
                                    .setPositiveButton("OK", (dialog, which) -> dialog.cancel());

                            AlertDialog alertDialog = builder.create();

                            if (!((GoogleLocationActivity.this)).isFinishing()) {
                                alertDialog.show();
                            }
                            isFirstTimeAlert = false;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    if (pDialog.isShowing())
                        pDialog.dismiss();
                }
            });
        }

    }

    private void addYouOnMap(Location location) {
        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));

        mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);
        mCurrLocationMarker.setTag("CurrentUserLocation");

        //move map camera
        if (isFirstTimeZoom) {
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            isFirstTimeZoom = false;
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        refresh();
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", (dialogInterface, i) -> {
                            //Prompt the user once explanation has been shown
                            ActivityCompat.requestPermissions(GoogleLocationActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    MY_PERMISSIONS_REQUEST_LOCATION);
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // permission was granted, yay! Do the
                // location-related task you need to do.
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

                    mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                    mGoogleMap.setMyLocationEnabled(true);
                }

            } else {
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
                Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
        else if (requestCode == 22) {
            if (grantResults.length > 0)
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted, perform required code
                } else {
                    Toast.makeText(this, "Notification Permission is required for App Notification", Toast.LENGTH_SHORT).show();
                    // not granted
                }
        }
        else if (requestCode == RC_LOCATION_REQUEST && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        }
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, RC_LOCATION_REQUEST);
        } else {
            startService(new Intent(this, LocationTrackerService.class).putExtra("key", "start"));
        }
    }

    private void refresh() {
        //pDialog.show();

        isFirstTimeAlert = true;
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                mGoogleMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            mGoogleMap.setMyLocationEnabled(true);
        }
    }
/*
    public void sendNotification(String deviceToken, String title, String message) {
        ApiServices apiServices = ClientApi.getRetrofit("https://fcm.googleapis.com/").create(ApiServices.class);

        Data data = new Data(title, message);
        NotificationSender notificationSender = new NotificationSender(data, deviceToken);
        Log.d(TAG, "sendNotification: ");

        apiServices.sendNotification(notificationSender).enqueue(new retrofit2.Callback<MyResponse>() {
            @Override
            public void onResponse(@NonNull Call<MyResponse> call, @NonNull Response<MyResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null)
                        if (response.body().success != 1) {
                            Log.d(TAG, "onResponse: SUCCESS");
                            //Toast.makeText(GoogleLocationActivity.this,"Notification Sent",Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d(TAG, "onFailure: FAILED");
                            //Toast.makeText(GoogleLocationActivity.this,"Notification failed",Toast.LENGTH_SHORT).show();
                        }
                }
            }

            @Override
            public void onFailure(@NonNull Call<MyResponse> call, @NonNull Throwable t) {
                Log.d(TAG, "onFailure: FAILED");
            }
        });
    }*/
}