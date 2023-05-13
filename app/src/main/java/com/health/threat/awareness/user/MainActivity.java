package com.health.threat.awareness.user;

import static com.health.threat.awareness.user.GoogleLocationActivity.MY_PERMISSIONS_REQUEST_LOCATION;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.health.threat.awareness.user.adapter.CustomDrawerAdapter;
import com.health.threat.awareness.user.app.AppPreferenceManager;
import com.health.threat.awareness.user.fragment.AllVirusCasesFragment;
import com.health.threat.awareness.user.fragment.AppointmentHistoryFragment;
import com.health.threat.awareness.user.fragment.GetAppointmentFragment;
import com.health.threat.awareness.user.fragment.HomeFragment;
import com.health.threat.awareness.user.model.DrawerItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    CustomDrawerAdapter adapter;
    List<DrawerItem> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UploadDeviceToken();

        // Initializing
        dataList = new ArrayList<DrawerItem>();
        mTitle = mDrawerTitle = getTitle();
        drawerLayout = findViewById(R.id.my_drawer_layout);
        mDrawerList = findViewById(R.id.left_drawer);

        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        // Add Drawer Item to dataList
        dataList.add(new DrawerItem("All Virus Cases", R.drawable.img));
        //dataList.add(new DrawerItem("Appointment History", R.drawable.appointment));
        dataList.add(new DrawerItem("Show Google Map", R.drawable.outline_location_on_24));
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

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, new HomeFragment());
        //fragmentTransaction.addToBackStack("HomeFragment");
        fragmentTransaction.commit();

        GetLocationAndNotificationPermission();
        //request_notification_api13_permission();
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
                                ActivityCompat.requestPermissions(MainActivity.this,
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

    private void request_notification_api13_permission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (this.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 22);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 22) {
            if (grantResults.length > 0)
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted, perform required code
                } else {
                    Toast.makeText(this, "Notification Permission is required for App Notification", Toast.LENGTH_SHORT).show();
                    // not granted
                }
        } else if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted
                // do your location-related task here
                //Toast.makeText(this, "Location is needed to get You Notified about Treats in Your Area", Toast.LENGTH_SHORT).show();
            } else {
                // permission denied
                // disable the functionality that depends on this permission
                Toast.makeText(this, "Location is needed to get You Notified about Treats in Your Area", Toast.LENGTH_SHORT).show();
            }
            return;
        }
    }

    private void UploadDeviceToken() {
        String token = new AppPreferenceManager(MainActivity.this).getDeviceToken();
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

                return;

           /* case 1:
                if (drawerLayout.isOpen())
                    drawerLayout.close();

                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, new AppointmentHistoryFragment());
                fragmentTransaction.addToBackStack("HomeFragment");
                fragmentTransaction.commit();

                return;*/

            case 1:
                if (drawerLayout.isOpen())
                    drawerLayout.close();

                startActivity(new Intent(this, LocationActivity.class));

                return;

            case 2:
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

    public class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            SelectItem(position);
            mDrawerList.setItemChecked(position, false);
        }
    }
}