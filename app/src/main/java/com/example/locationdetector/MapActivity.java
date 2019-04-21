package com.example.locationdetector;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback{

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this,"map is ready", Toast.LENGTH_LONG).show();
        Log.d(TAG,"Map is ready");
        mMap = googleMap;

        if (mLocationPermissionGranted){
            getDeviceLocation();

        }
    }
    private static final String TAG = "MapActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
//    var
    private Boolean mLocationPermissionGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        getLocationPermission();
    }

    private void getDeviceLocation(){
        Toast.makeText(MapActivity.this,"vaena ni dost", Toast.LENGTH_LONG).show();
        Log.d(TAG,"getting the current device location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mLocationPermissionGranted){
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()){
                            Log.d(TAG, "onComplete: Found Location");
                            Location currentLocation = (Location) task.getResult();

                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),15f);

                        }else {
                            Log.d(TAG,"Current location is null");
                            Toast.makeText(MapActivity.this, "unable to get current location", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e(TAG,"get device location : SecurityException"+e.getMessage());
            Toast.makeText(MapActivity.this,"vaena ni dost", Toast.LENGTH_LONG).show();
        }
    }

    private void moveCamera(LatLng latLng, float zoom){
        Log.d(TAG,"moving the camera to : lat"+latLng.latitude +",lng: "+latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }
    private void initMap(){
        Log.d(TAG, "initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapActivity.this);
    }
    private void getLocationPermission(){
        Log.d(TAG,"Getting permission");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COURSE_LOCATION)== PackageManager.PERMISSION_GRANTED){
                mLocationPermissionGranted = true;
                initMap();
            }else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG,"called");
        mLocationPermissionGranted = false;

        switch (requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if (grantResults.length > 0){
                    for (int i = 0; i< grantResults.length; i++){
                         if (grantResults[i] != PackageManager.PERMISSION_GRANTED){
                             mLocationPermissionGranted = false;
                             Log.d(TAG,"Permission failed");
                             return;
                         }
                    }
                    Log.d(TAG,"Permission granted");
                    mLocationPermissionGranted = true;
                    //initialize map
                    initMap();
                }
            }
        }
    }


}
