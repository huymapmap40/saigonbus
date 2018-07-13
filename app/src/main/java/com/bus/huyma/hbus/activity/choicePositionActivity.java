package com.bus.huyma.hbus.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bus.huyma.hbus.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Locale;

public class choicePositionActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, LocationListener {

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Button location, confirm;
    private SupportMapFragment mapFrag;
    private Geocoder myGeo;
    private GoogleMap myMap;
    private static double vido = 0;
    private static double kinhdo = 0;
    private LatLng toaDoHienTai = new LatLng(0, 0);
    private LatLng toaDoChon = new LatLng(0, 0);
    private String viTriHienTai, viTriChon;
    private String diaChiViTriHienTai = null;
    private String viTriLuaChon = null;
    private int checkChoice = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choice_position_on_map);

        location = (Button) findViewById(R.id.btnCurrentLocation);
        confirm = (Button) findViewById(R.id.btnConfirmPosition);

        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapChoice);
        mapFrag.getMapAsync(this);


        //lang nghe su kien tren nut 'OK'
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = getIntent();

                if (checkChoice == 1) {
                    myIntent.putExtra("check", checkChoice);
                    myIntent.putExtra("toaDo", toaDoHienTai);
                    myIntent.putExtra("textToaDo", viTriHienTai);
                    //myIntent.putExtra("diachihientai",diaChiViTriHienTai);
                    setResult(12, myIntent);
                    finish();
                } else if (checkChoice == 2) {
                    myIntent.putExtra("check", checkChoice);
                    myIntent.putExtra("toaDoChon", toaDoChon);
                    myIntent.putExtra("textChon", viTriChon);
                    //myIntent.putExtra("diachidachon",viTriLuaChon);
                    setResult(12, myIntent);
                    finish();
                }
            }
        });
    }

    @Override
    public void onPause(){
        super.onPause();
        //stop location updates when Activity is no longer active
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    //Ham lay vi tri hien tai
    private void myLocation(GoogleMap map) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location loc = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(loc!=null){
            LatLng latLng = new LatLng(loc.getLatitude(),loc.getLongitude());
            vido = loc.getLatitude();
            kinhdo = loc.getLongitude();

            //map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,13));
            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng)
                    .zoom(15)
                    .build();

            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap=googleMap;
        myMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
                myMap.setMyLocationEnabled(true);
                myMap.getUiSettings().setZoomControlsEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        }
        else {
            buildGoogleApiClient();
            myMap.setMyLocationEnabled(true);
            myMap.getUiSettings().setZoomControlsEnabled(true);
        }
        myGeo = new Geocoder(this, Locale.getDefault());

        //lang nghe su kien tren nut chon vi tri hien tai
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myLocation(myMap);
                toaDoHienTai = new LatLng(vido, kinhdo);
                viTriHienTai = "[Tọa độ vị trí hiện tại]";
                checkChoice = 1;
                location.setBackgroundColor(Color.GREEN);
                Toast.makeText(choicePositionActivity.this,"Chọn OK để xác nhận vị trí.",Toast.LENGTH_SHORT).show();
            }
        });

        //Lang nghe su kien click 1 diem tren ban do
        myMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                toaDoChon = latLng;
                myMap.addMarker(new MarkerOptions()
                        .title("Bạn đã chọn điểm này.")
                        .position(toaDoChon)
                        .icon(BitmapDescriptorFactory.defaultMarker()));
                viTriChon = "[Điểm tọa độ]";
                checkChoice = 2;
                location.getBackground().clearColorFilter();
                Toast.makeText(choicePositionActivity.this,"Chọn OK để xác nhận vị trí.",Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(choicePositionActivity.this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(choicePositionActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(choicePositionActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(choicePositionActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }

    //Thoat khoi ung dung khi nhan nut Back
    @Override
    public void onBackPressed(){
        setResult(13);
        finish();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {}

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }
}
