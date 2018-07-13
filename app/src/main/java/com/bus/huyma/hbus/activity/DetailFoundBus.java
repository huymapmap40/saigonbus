package com.bus.huyma.hbus.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bus.huyma.hbus.R;
import com.bus.huyma.hbus.activity.Services.MyService;
import com.bus.huyma.hbus.fragments.DetailAllStationRoute;
import com.bus.huyma.hbus.fragments.DetailRoute;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.bus.huyma.hbus.activity.Services.MyService;
import com.bus.huyma.hbus.fragments.DetailRoute;

public class DetailFoundBus extends AppCompatActivity implements OnMapReadyCallback /*, android.location.LocationListener */{

    DatabaseHelper DBHelper;
    private TextView distance, duration, startTime, endTime;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Toolbar toolbar;
    public GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private Geocoder geo;
    private FloatingActionButton subChiDan;
    private LinearLayout generalInfoRoute;

    private String diachidau, diachisau;
    private LocationManager locationManager;
    private Vibrator rung, rung1;
    private Uri uriSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
    public AlertDialog.Builder aB ; // Bao xuong tram
    public AlertDialog.Builder aB1;//Bao di bo toi tram
    private static int checkList=0;//index cho kiem tra so thu tu vi tri tram
    private ArrayList<LatLng> pathLatLng = new ArrayList<>();
    private ArrayList<LatLng> listBusStop = new ArrayList<>();
    public ArrayList<String> nameBusStop = new ArrayList<>();
    //MyReceiver myReceiver;

    //Cac bien lay gia tri cua fragment 'tim duong' chuyen qua
    private double latA,lngA,latB,lngB,EastingA, EastingB,NorthingA,NorthingB;
    String distanceTransit,durationTransit, startAddress, endAddress,arrivalTime, departureTime;
    private ArrayList<String> instructionsMove = new ArrayList<>();
    public ArrayList<String> codeBus = new ArrayList<>();
    private int demSoTuyenXe=1;

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            Double currentLatitude = intent.getDoubleExtra("lat", 0);
            Double currentLongitude = intent.getDoubleExtra("lng", 0);

            if(checkList<listBusStop.size()) {

                if((!codeBus.get(checkList).equals("0"))&&
                        getDistanceBetweenTwoPoints(new PointD(currentLatitude,currentLongitude),new PointD(listBusStop.get(checkList).latitude, listBusStop.get(checkList).longitude))<=200 ){
                    //Check distinct location busStop
                    if(listBusStop.get(checkList).latitude!=listBusStop.get(checkList-1).latitude
                            && listBusStop.get(checkList).longitude!=listBusStop.get(checkList-1).longitude){
                        aB.setTitle("Lưu ý");
                        aB.setMessage("Sắp tới trạm " + nameBusStop.get(checkList) );
                        aB.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        aB.create();
                        aB.show();

                        long pattern[]={0,200,250,500,250,500};
                        rung = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        rung.vibrate(pattern, 0);

                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(DetailFoundBus.this);
                        Intent targetIntent = new Intent(getApplicationContext(), DetailFoundBus.class);
                        targetIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        PendingIntent contentIntent = PendingIntent.getActivity(DetailFoundBus.this, 0, targetIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                        mBuilder.setSmallIcon(R.drawable.chidan);
                        mBuilder.setContentTitle("Cảnh báo xuống trạm.");
                        mBuilder.setContentText("Xin mời xuống trạm tiếp theo.");
                        mBuilder.setSound(uriSound);
                        int NOTIFICATION_ID = 12345;

                        mBuilder.setContentIntent(contentIntent);
                        NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        nManager.notify(NOTIFICATION_ID, mBuilder.build());

                        checkList=checkList+1;// duyet qua cac tram
//                        Toast.makeText(DetailFoundBus.this,"Checklist hien tai: "+checkList,Toast.LENGTH_LONG).show();

                    } else if (listBusStop.get(checkList).latitude==listBusStop.get(checkList-1).latitude
                            && listBusStop.get(checkList).longitude==listBusStop.get(checkList-1).longitude){
                        aB.setTitle("Lưu ý");
                        aB.setMessage("Đang ở trạm " + nameBusStop.get(checkList) + " vui lòng bắt xe bus khác để đi tiếp.");
                        aB.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        aB.create();
                        aB.show();

                        long pattern[]={0,200,250,500,250,500};
                        rung = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        rung.vibrate(pattern, 0);

                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(DetailFoundBus.this);
                        Intent targetIntent = new Intent(getApplicationContext(), DetailFoundBus.class);
                        targetIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        PendingIntent contentIntent = PendingIntent.getActivity(DetailFoundBus.this, 0, targetIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                        mBuilder.setSmallIcon(R.drawable.chidan);
                        mBuilder.setContentTitle("Cảnh báo đang ở trạm "+nameBusStop.get(checkList));
                        mBuilder.setContentText("Xin mời bắt xe để đi tiếp.");
                        mBuilder.setSound(uriSound);
                        int NOTIFICATION_ID = 12345;

                        mBuilder.setContentIntent(contentIntent);
                        NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        nManager.notify(NOTIFICATION_ID, mBuilder.build());
                        checkList=checkList+1;// duyet qua cac tram
//                        Toast.makeText(DetailFoundBus.this,"Checklist hien tai: "+checkList,Toast.LENGTH_LONG).show();
                    }

                } else if(codeBus.get(checkList).equals("0")){
                    if(getDistanceBetweenTwoPoints(new PointD(currentLatitude,currentLongitude),new PointD(listBusStop.get(checkList).latitude, listBusStop.get(checkList).longitude))<=120){
                        aB1.setTitle("Lưu ý");
                        aB1.setMessage("Gần đến trạm " + nameBusStop.get(checkList) + ".");
                        aB1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        aB1.create();
                        aB1.show();

                        long pattern[]={0,200,250,1000,250,500};
                        rung1 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        rung1.vibrate(pattern, 1);
                        checkList=checkList+1;// duyet qua cac tram
//                        Toast.makeText(DetailFoundBus.this,"Checklist hien tai: "+checkList,Toast.LENGTH_LONG).show();
                    }
                }
            }

            CameraPosition camera = new CameraPosition.Builder().target(new LatLng(currentLatitude,currentLongitude))
                    .zoom(15)
                    .build();

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.found_bus_route_detail);

        distance = findViewById(R.id.distance_text);
        duration = findViewById(R.id.duration_text);
        startTime = findViewById(R.id.start_time_text);
        endTime = findViewById(R.id.end_time_text);
        generalInfoRoute = (LinearLayout) findViewById(R.id.layout_info_route);
        subChiDan = (FloatingActionButton) findViewById(R.id.fabSubChiDan);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFoundBus);
        mapFragment.getMapAsync(this);

        //Goi Inten lay ve cac gia tri gui tu fragment 2
        Intent callIntent = getIntent();
        Bundle getBundle = callIntent.getBundleExtra("package");
        pathLatLng = getBundle.getParcelableArrayList("PATH_LATLNG");
        listBusStop = getBundle.getParcelableArrayList("listBusStop");
        latA = getBundle.getDouble("LAT-A");
        lngA = getBundle.getDouble("LNG-A");
        latB = getBundle.getDouble("LAT-B");
        lngB = getBundle.getDouble("LNG-B");
        distanceTransit = getBundle.getString("distance");
        durationTransit = getBundle.getString("duration");
        startAddress = getBundle.getString("start_address");
        endAddress = getBundle.getString("end_address");
        arrivalTime = getBundle.getString("arrival_time");
        departureTime = getBundle.getString("departure_time");
        instructionsMove = getBundle.getStringArrayList("instructions_move");
        codeBus = getBundle.getStringArrayList("codeBus");
        nameBusStop = getBundle.getStringArrayList("nameBusStop");

        //-------------------------------------------------------------
        viewPager = (ViewPager) findViewById(R.id.viewpagerFoundBus);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabsInFoundBusLayout);
        tabLayout.setupWithViewPager(viewPager);
        //-------------------------------------------------------------

        toolbar = (Toolbar) findViewById(R.id.toolbar_FoundBus);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //lang nghe su kien thay doi vi tri hien tai
        /*locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                10000, 1, this);*/
        aB = new AlertDialog.Builder(DetailFoundBus.this);
        aB1 = new AlertDialog.Builder(DetailFoundBus.this);

        aB.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                rung.cancel();
            }
        });
        aB1.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                rung1.cancel();
            }
        });

        //Register BroadcastReceiver
        //to receive event from our service
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("MY_ACTION"));

        //lang nghe su kien khi click vao nut tracking
        subChiDan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder ab = new AlertDialog.Builder(DetailFoundBus.this);
                ab.setPositiveButton("Bắt đầu đi", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                //Bat dau chay dich vu tai day
                startService(new Intent(DetailFoundBus.this, MyService.class));
                subChiDan.setEnabled(false);
                ab.setTitle("Hướng dẫn đi");
                ab.setMessage("Đi tới trạm " + nameBusStop.get(0) + " để bắt đầu cuộc hành trình.");
                ab.create();
                ab.show();
            }
        });

        //Get general info text about the found route
        distance.setText(distanceTransit);
        duration.setText(durationTransit);
        startTime.setText(departureTime);
        endTime.setText(arrivalTime);

        //Lang nghe su kien click vao layout thong tin route
        generalInfoRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generalInfoRoute.setVisibility(View.INVISIBLE);
            }
        });
    }

    //Ham tinh khoang cach giua 2 diem
    public static double getDistanceBetweenTwoPoints(PointD p1, PointD p2) {
        double R = 6371000; // m
        double dLat = Math.toRadians(p2.x - p1.x);
        double dLon = Math.toRadians(p2.y - p1.y);
        double lat1 = Math.toRadians(p1.x);
        double lat2 = Math.toRadians(p2.x);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2)
                * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c;

        return d;
    }

    //Ham lay vi tri hien tai
    private void myLocation(GoogleMap map) {
        LocationManager locM = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria cr = new Criteria();
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
        Location loc = locM.getLastKnownLocation(locM.getBestProvider(cr, true));
        if(loc!=null){
            LatLng latLng = new LatLng(loc.getLatitude(),loc.getLongitude());
            //map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,13));
            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng)
                    .zoom(15)
                    .build();

            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    //Ham View Pager
    private void setupViewPager(ViewPager viewPager) {
//        MapsActivity.ViewPagerAdapter adapter = new MapsActivity.ViewPagerAdapter(getSupportFragmentManager());
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new DetailRoute(), "Hướng dẫn chi tiết");
        adapter.addFragment(new DetailAllStationRoute(), "Tất cả các trạm");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
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
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        geo = new Geocoder(this, Locale.getDefault());
//        myLocation(mMap);

        //Listen on click event on map UI
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (generalInfoRoute.getVisibility()==View.VISIBLE){
                    generalInfoRoute.setVisibility(View.INVISIBLE);
                } else {
                    generalInfoRoute.setVisibility(View.VISIBLE);
                }
            }
        });

        //Lay dia chi vi tri dau va cuoi
        List<Address> diaChiA;
        try {
            diaChiA = geo.getFromLocation(latA,lngA,1);
            String address = diaChiA.get(0).getAddressLine(0);
            String city = diaChiA.get(0).getLocality();
            String state = diaChiA.get(0).getAdminArea();
            String country = diaChiA.get(0).getCountryName();
            String knowName = diaChiA.get(0).getFeatureName();
            diachidau = address+"," + city+"," + state+"," + country+"," + knowName;
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Address> diaChiB;
        try {
            diaChiB = geo.getFromLocation(latB,lngB,1);
            String address = diaChiB.get(0).getAddressLine(0);
            String city = diaChiB.get(0).getLocality();
            String state = diaChiB.get(0).getAdminArea();
            String country = diaChiB.get(0).getCountryName();
            String knowName = diaChiB.get(0).getFeatureName();
            diachisau = address+"," + city+"," + state+"," + country+"," + knowName;
        } catch (IOException e) {
            e.printStackTrace();
        }

        //them 2 marker vi tri xuat phat, va ket thuc
        mMap.addMarker(new MarkerOptions().position(new LatLng(latA,lngA)).icon(BitmapDescriptorFactory.fromResource(R.drawable.flag)).title(diachidau));
        mMap.addMarker(new MarkerOptions().position(new LatLng(latB, lngB)).icon(BitmapDescriptorFactory.fromResource(R.drawable.race_flag)).title(diachisau));
        for(int j=0; j<listBusStop.size(); j++){
            mMap.addMarker(new MarkerOptions().position(listBusStop.get(j)).icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_marker)).title(nameBusStop.get(j)));
        }

        PolylineOptions path = new PolylineOptions();
        for(int i = 0; i < pathLatLng.size(); i++){
            path.add( new LatLng( pathLatLng.get(i).latitude, pathLatLng.get(i).longitude)).color(Color.BLUE).width(8);
        }
        mMap.addPolyline(path);

        //Tro man hinh ve vi tri xuat phat
        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(latA,lngA))
                .zoom(15)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }


//    @Override
//    public void onLocationChanged(Location location) {
//
//    }
//
//    @Override
//    public void onStatusChanged(String provider, int status, Bundle extras) {
//
//    }
//
//    @Override
//    public void onProviderEnabled(String provider) {
//        Toast.makeText(DetailFoundBus.this, "Gps is turned on!! ",
//                Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void onProviderDisabled(String provider) {
//        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//        startActivity(intent);
//        Toast.makeText(DetailFoundBus.this, "Gps is turned off!! ",
//                Toast.LENGTH_SHORT).show();
//    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    //Ham gui du lieu qua cho fragment DetailRoute
    public ArrayList<String> sendMaTuyenToFragment(){
        return codeBus;
    }
    public ArrayList<String> sendCachDiFragment(){
        return instructionsMove;
    }
//    public ArrayList<String> sendTenTramCuoiToFragment() {return mangTramCuoi;}
    public ArrayList<LatLng> sendToaDoCuoiToFragment(){return listBusStop;}

    @Override
    public void onBackPressed(){

        instructionsMove.clear();
        nameBusStop.clear();
        listBusStop.clear();
        pathLatLng.clear();
        codeBus.clear();
        mMap.clear();

        //Kiem tra service update location co dang chay hay khong
        //Neu co (nut subChiDan da dc click) thi stop services
        if(!subChiDan.isEnabled()){
            stopService(new Intent(DetailFoundBus.this, MyService.class));// ngung dich vu chi duong
            AlertDialog.Builder goback = new AlertDialog.Builder(DetailFoundBus.this);
            goback.setTitle("LƯU Ý..!");
            goback.setMessage("Bạn có muốn dừng theo dõi trạm xuống...?\n (Tips : Bấm nút HOME để ẩn ứng dụng và theo dõi)");
            goback.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });
            goback.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            goback.create();
            goback.show();
        } else  finish();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        //LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onStop();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        //ket thuc activity thi tien hanh huy dang ky nhan thong tin tu services
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case android.R.id.home: {
                instructionsMove.clear();
                nameBusStop.clear();
                listBusStop.clear();
                pathLatLng.clear();
                codeBus.clear();
                mMap.clear();

                //Kiem tra service update location co dang chay hay khong
                //Neu co (nut subChiDan da dc click) thi stop services
                if(!subChiDan.isEnabled()){
                    stopService(new Intent(DetailFoundBus.this, MyService.class));// ngung dich vu chi duong
                    AlertDialog.Builder goback = new AlertDialog.Builder(DetailFoundBus.this);
                    goback.setTitle("LƯU Ý..!");
                    goback.setMessage("Bạn có muốn dừng theo dõi trạm xuống...?\n (Tips : Bấm nút HOME để ẩn ứng dụng và theo dõi)");
                    goback.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    });
                    goback.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    goback.create();
                    goback.show();
                } else  finish();
            }

            default :return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
