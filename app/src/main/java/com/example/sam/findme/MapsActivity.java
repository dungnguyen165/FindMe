package com.example.sam.findme;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback{

    private final String TAG = "MapsActivity";

    private GoogleMap mMap;
    private String email;
    DatabaseReference locations;
    Double lat, lng;

    Float lux;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Log.d(TAG, "onCreate");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        locations = FirebaseDatabase.getInstance().getReference("Locations");

        if (getIntent() != null) {
            email = getIntent().getStringExtra("email");
            lat = getIntent().getDoubleExtra("lat", 0);
            lng = getIntent().getDoubleExtra("lng", 0);
            lux = getIntent().getFloatExtra("light", 0);
        }

        if (!TextUtils.isEmpty(email)) {
            loadLocationForThisUser(email);
        }


    }

    private void loadLocationForThisUser(String email) {
        Query user_location = locations.orderByChild("email").equalTo(email);

        user_location.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                    Tracking tracking = postSnapShot.getValue(Tracking.class);

                    LatLng friendLocation = new LatLng(
                            Double.parseDouble(tracking.getLat()),
                            Double.parseDouble(tracking.getLng()));

                    Location currentUser = new Location("");
                    currentUser.setLatitude(lat);
                    currentUser.setLongitude(lng);

                    Location friend = new Location("");
                    friend.setLatitude(Double.parseDouble(tracking.getLat()));
                    friend.setLongitude(Double.parseDouble(tracking.getLng()));

                    //Create function calculate distance between location
                    distance(currentUser, friend);

                    //Clear old marker
                    mMap.clear();

                    //Add friend marker on Map
                    mMap.addMarker(new MarkerOptions()
                            .position(friendLocation)
                            .title(tracking.getEmail())
                            .snippet("Distance " + new DecimalFormat("#.#").format(distance(currentUser, friend)))
//                            .snippet("My light sensor: " + lux)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng),12.0f));
                }

                LatLng current = new LatLng(lat, lng);
                mMap.addMarker(new MarkerOptions()
                        .position(current)
                        .title(FirebaseAuth.getInstance().getCurrentUser().getEmail()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private double distance(Location currentUser, Location friend) {
        double theta = currentUser.getLongitude() - friend.getLongitude();
        double dist = Math.sin(deg2rad(currentUser.getLatitude())) *
                Math.sin(deg2rad(friend.getLatitude())) *
                Math.cos(deg2rad(currentUser.getLatitude())) *
                Math.cos(deg2rad(friend.getLatitude())) *
                Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return dist;
    }

    private double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onStart");
    }


}
