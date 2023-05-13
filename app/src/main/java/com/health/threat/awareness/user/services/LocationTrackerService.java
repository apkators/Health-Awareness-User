package com.health.threat.awareness.user.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.health.threat.awareness.user.notification.LocationNotification;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LocationTrackerService extends Service {
    DatabaseReference ref;
    private FusedLocationProviderClient mLocationProviderClient;
    private LocationCallback locationUpdatesCallback;
    private LocationRequest locationRequest;

    public LocationTrackerService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        ref = FirebaseDatabase.getInstance().getReference().child("AppUsers").child("Seeker").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        mLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        setUpLocationRequest();
    }

    private void setUpLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(15000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String keyValue = intent.getStringExtra("key");
            if (keyValue != null && keyValue.equals("stop")) {
                {
                    Map<String, Object> data = new HashMap<>();
                    data.put("isActive", "0");
                    data.put("Latitude", null);
                    data.put("Longitude", null);
                    //data.put("time", lastLocation.getTime());
                    ref.updateChildren(data).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.i("tag", "Location Stopped");
                        } else
                            Log.i("tag", "error on Location Stopped");
                    });
                    sendBroadcast(false);
                    stopForeground(true);
                    stopSelf();
                }
            } else {
                sendBroadcast(true);
                setUpLocationUpdatesCallback();
                mLocationProviderClient.requestLocationUpdates(locationRequest, locationUpdatesCallback, Looper.getMainLooper());
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        LocationNotification.cancel(this);
        stopForeground(true);
        stopSelf();

        Map<String, Object> data = new HashMap<>();
        data.put("isActive", "0");
        data.put("Latitude", null);
        data.put("Longitude", null);
        //data.put("time", lastLocation.getTime());
        ref.updateChildren(data).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.i("tag", "Location Stopped");
            } else
                Log.i("tag", "error on Location Stopped");
        });

        sendBroadcast(false);

        if (locationUpdatesCallback != null)
            mLocationProviderClient.removeLocationUpdates(locationUpdatesCallback);

        super.onDestroy();
    }

    private void setUpLocationUpdatesCallback() {
        locationUpdatesCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@Nullable LocationResult locationResult) {

                if (locationResult != null) {
                    sendBroadcast(true);
                    Location lastLocation = locationResult.getLastLocation();
                    Map<String, Object> data = new HashMap<>();

                    data.put("isActive", "1");
                    data.put("Latitude", lastLocation.getLatitude());
                    data.put("Longitude", lastLocation.getLongitude());
                    //data.put("time", lastLocation.getTime());
                    ref.updateChildren(data).addOnSuccessListener(aVoid -> Log.i("tag", "Location update saved"));
                    LocationNotification.notify(LocationTrackerService.this, "Location Tracking",
                            "Lat:" + lastLocation.getLatitude() + " - Lng:" + lastLocation.getLongitude());
                } else {
                    sendBroadcast(false);
                    Log.i("tag", "Location null");
                }
            }
        };
    }

    private void sendBroadcast (boolean success){
        Intent intent = new Intent ("Connection"); //put the same message as in the filter you used in the activity when registering the receiver
        intent.putExtra("isConnected", String.valueOf(success).toLowerCase());
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
