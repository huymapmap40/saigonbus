package com.bus.huyma.hbus.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bus.huyma.hbus.R;
import com.bus.huyma.hbus.activity.DatabaseHelper;
import com.bus.huyma.hbus.activity.DetailFoundBus;
import com.bus.huyma.hbus.activity.DirectionsJSONParser;
import com.bus.huyma.hbus.activity.DirectionsTransitJSONParser;
import com.bus.huyma.hbus.activity.PointD;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.common.ConnectionResult;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OneFragment extends SupportMapFragment implements MyAlertDialogFragment.MyCustomDialogListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    public OneFragment() {
    }

    //Tao bien handler, de xu ly cac thread ve marker len ban do
    private LinearLayout walking_layout_detail;
    android.os.Handler myhandler = new android.os.Handler(Looper.getMainLooper());
    private SupportMapFragment sup; //Biến để gọi đến map id trong layout, vẽ ra giao diện bản đồ
    private GoogleMap mMap; //Biến để thao tác trên bản đồ
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    Location mLastLocation;
    private Geocoder geocoder;
    private static double vido = 0;
    private static double kinhdo = 0;
    private static ArrayList<LatLng> markerList;
    DatabaseHelper DBHelper;
    private View root1 = null; //biến load layout fragment_one.xml
    private FloatingActionMenu fabMenu;
    private FloatingActionButton fabNearby, fabWalk, fabDirect;
    private ArrayList<LatLng> pointWalkingRoute = new ArrayList<>(); //Bien kiem luu toa do cac diem tren duong di bo (den tram xe bus)
    private ArrayList<LatLng> pointTransitRoute = new ArrayList<>();//Toa do route xe bus
    private Polyline poly; //Bien de ve duong di bo
    private ProgressDialog mProgressDialog;
    private TextView walkingDistance, walkingDuration, walkingDetail;

    //Cac bien lay gia tri cua fragment 'tim duong' chuyen qua
    private double latA, lngA, latB, lngB;
    private LatLng origin, finish;
    private int demSoTuyenXe = 1;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
        //stop location updates when Activity is no longer active
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        super.onCreateView(inflater, container, saveInstanceState);

        //Tạo giao diện fragment_one.xml
        //Dùng FragmentManager để quản lý SupportMapFragment
        root1 = inflater.inflate(R.layout.fragment_one, container, false);

        FragmentManager fm = getChildFragmentManager();
        sup = (SupportMapFragment) fm.findFragmentById(R.id.map);
        sup.getMapAsync(this);

        //Get layout walking detail
        walking_layout_detail = (LinearLayout) root1.findViewById(R.id.layout_detail_walk);

        //Init textview of walking detail route
        walkingDistance = (TextView) root1.findViewById(R.id.walking_distance_text);
        walkingDuration = (TextView) root1.findViewById(R.id.walking_duration_text);
        walkingDetail = (TextView) root1.findViewById(R.id.walking_detail);

        //Khoi tao cac float button
        fabMenu = (FloatingActionMenu) root1.findViewById(R.id.fab_1);
        fabNearby = (FloatingActionButton) root1.findViewById(R.id.fabSubNearestPlace);
        fabDirect = (FloatingActionButton) root1.findViewById(R.id.fabSubDirectionGuide);
        fabWalk = (FloatingActionButton) root1.findViewById(R.id.fabSubWalking);

        return root1;//Trả về giao diện chính của fragment (fragment_one.xml)
    }

    @Override
    public void onInflate(Activity arg0, AttributeSet arg1, Bundle arg2) {
        super.onInflate(arg0, arg1, arg2);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Xac dinh cac tram quanh day
        fabMenu.setClosedOnTouchOutside(true);
        fabNearby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();
                myLocation(mMap);
                fabMenu.close(true);
            }
        });

        //Nut tim duong di bo
        //Hoat dong khi da click nut "tram quanh day"
        //Ngu canh: phai co toa do vi tri hien tai, thi moi lay duoc dia diem tram xung quanh, tu do moi tim ra duong di bo
        fabWalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vido != 0 && kinhdo != 0) {
                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            LatLng origin = new LatLng(vido, kinhdo);
                            LatLng dest = marker.getPosition();

                            //tien hanh ve duong di bo moi
                            if (!markerList.isEmpty() && origin != dest) {
                                if (!pointWalkingRoute.isEmpty()) {
                                    poly.remove();
                                }

                                String url = getDirectionsUrl(origin, dest);
                                DownloadTask downloadTask = new DownloadTask();
                                downloadTask.execute(url);
                            }
                            return false;
                        }
                    });
                    Toast.makeText(getActivity().getApplicationContext(), "Vui lòng chọn một trạm xe bus trên bản đồ.", Toast.LENGTH_LONG).show();
                    fabMenu.close(true);
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Vui lòng chọn chức năng 'trạm quanh đây' trước.", Toast.LENGTH_LONG).show();
                    fabMenu.close(true);
                }
            }
        });

        fabDirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();
                showEditDialog();
                fabMenu.close(true);
            }
        });

        walking_layout_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                walking_layout_detail.setVisibility(View.INVISIBLE);
            }
        });
    }

    // Goi ham nay khi custom dialog dc goi den va ket qua duoc tra ve
    @Override
    public void onFinishEditDialog(double lat_A, double lng_A, double lat_B, double lng_B, int countRoute) {

        //Toa do cua tram xe bus gan (diem dau va diem cuoi) nhat, dua tren vi tri' dau/cuoi da chon.
//        LatLng start = getNearestStation(lat_A, lng_A);
//        LatLng finish = getNearestStation(lat_B, lng_B);

        //Toa do chi tiet cua tram diem dau va tram diem cuoi
        latA = lat_A;
        lngA = lng_A;
        latB = lat_B;
        lngB = lng_B;

        origin = new LatLng(latA, lngA);
        finish = new LatLng(latB, lngB);
        String urlTransit = getTransitDirectionUrl(origin, finish);
        demSoTuyenXe = countRoute; //Dem so tuyen xe

        DownloadTaskTransit d = new DownloadTaskTransit();
        d.execute(urlTransit);
    }

    //Ham show custom dialog
    private void showEditDialog() {
        FragmentManager fm = getFragmentManager();

        MyAlertDialogFragment editNameDialogFragment = MyAlertDialogFragment.newInstance("Some Title");
        editNameDialogFragment.setTargetFragment(OneFragment.this, 300);
        editNameDialogFragment.show(fm, "fragment_edit_name");
    }

    //Ham lay ve cac tram xung quanh vi tri hien tai
    private void getBusOnStation() {
        //Reference link for detail : http://stackoverflow.com/questions/3695224/sqlite-getting-nearest-locations-with-latitude-and-longitude

        double x = vido;
        double y = kinhdo;
        //Toast.makeText(this.getActivity().getApplicationContext(),"vido: "+x + "\n" +"kinhdo: "+y,Toast.LENGTH_LONG).show();

        PointD center = new PointD(x, y);
        final double mult = 1;//mult=1.1 more reliable
        PointD p1 = calculateDerivedPosition(center, mult * 1000, 0); //ban kinh timkiem trong vong 1km
        PointD p2 = calculateDerivedPosition(center, mult * 1000, 90);
        PointD p3 = calculateDerivedPosition(center, mult * 1000, 180);
        PointD p4 = calculateDerivedPosition(center, mult * 1000, 270);

        //Where clause in query
        String strWhere = " WHERE " + "latitude" + " > " + String.valueOf(p3.x) + " AND "
                + "latitude" + " < " + String.valueOf(p1.x) + " AND "
                + "longitude" + " < " + String.valueOf(p2.y) + " AND "
                + "longitude" + " > " + String.valueOf(p4.y) + ";";

        DBHelper = new DatabaseHelper(this.getActivity().getApplicationContext());
        try {
            DBHelper.createDataBase();
        } catch (IOException e) {
            throw new Error("unable to create database.", e);
        }

        try {
            DBHelper.openDataBase();
        } catch (SQLException s) {
            throw s;
        }

        Cursor c = DBHelper.rawQuery("select latitude,longitude from Bus_stop" + strWhere + ";", null);

        ArrayList<LatLng> markerPoints = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                double a = c.getDouble(c.getColumnIndex("latitude"));
                double b = c.getDouble(c.getColumnIndex("longitude"));
                //Toast.makeText(this.getActivity().getApplicationContext(),"lat: "+ a+"\n"+"long "+b,Toast.LENGTH_LONG).show();
                PointD placeBusStop = new PointD(a, b);//Diem cac toa do cua cac tram xe bus
                LatLng dest = new LatLng(a, b);
                markerPoints.add(dest);//Them vao mang cac toa do tram xe bus


                //Tao cursor c1 de truy van cac tuyen xe bus di qua 1 tram, khi ta click vao 1 tram.
                Cursor c1 = DBHelper.rawQuery("select code,name from Bus " +
                        "where id in (select Bus_BStop.bus_id from Bus_BStop " +
                        "where Bus_BStop.bstop_id in (select Bus_stop.id from Bus_stop" +
                        " where longitude='" + b + "' and latitude='" + a + "'))", null);

                String tuyenXe = "", addrTram = "";
                if (c1.moveToFirst()) {
                    do {
                        String maTuyen = c1.getString(c1.getColumnIndex("code"));
                        String tenTuyen = c1.getString(c1.getColumnIndex("name"));
                        String xe = maTuyen + " : " + tenTuyen + "\n";
                        tuyenXe += xe;
                    } while (c1.moveToNext());
                    c1.close();
                }

                //Tao cursor c2 de truy van dia chi tram xe buyt
                Cursor c2 = DBHelper.rawQuery("select address from Bus_stop where longitude='" + b + "' and latitude='" + a + "'", null);
                if (c2.moveToFirst()) {
                    do {
                        String diaChiTram = c2.getString(c2.getColumnIndex("address"));
                        addrTram = "Địa chỉ: " + diaChiTram;
                    } while (c2.moveToNext());
                    c2.close();
                }

                if (pointIsInCircle(placeBusStop, center, 1000)) {
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(a, b))
                            .title("Các tuyến xe bus đi qua trạm này \n" + "(" + addrTram + ")")
                            .snippet(tuyenXe)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_marker)));

                    //Tao phuong thuc custom lai info window cua marker,de co duoc multi line snippet
                    mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                        @Override
                        public View getInfoWindow(Marker marker) {
                            return null;
                        }

                        //override phuong thuc nay, de tao giao dien
                        @Override
                        public View getInfoContents(Marker marker) {
                            Context context = getActivity().getApplicationContext();

                            LinearLayout info = new LinearLayout(context);
                            info.setOrientation(LinearLayout.VERTICAL);

                            TextView title = new TextView(context);
                            title.setTextColor(Color.BLACK);
                            title.setGravity(Gravity.CENTER);
                            title.setTypeface(null, Typeface.BOLD);
                            title.setText(marker.getTitle());

                            TextView snippet = new TextView(context);
                            snippet.setTextColor(Color.GRAY);
                            snippet.setText(marker.getSnippet());

                            info.addView(title);
                            info.addView(snippet);

                            return info;
                        }
                    });
                }
            } while (c.moveToNext());
            c.close();
            DBHelper.close();
            markerList = markerPoints;
        }
    }

    //Ham lay ve vi tri hien tai , va ve no len ban do
    //Dong thoi goi den ham 'getBusOnStation' , ve cac tram xung quanh vi tri hien tai len ban do
    private void myLocation(final GoogleMap map) {
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {

                LatLng latLng = null;
                CameraPosition cameraPosition = null;
                String address = null;
                String country = null;
                String subadminarea = null;
                String city = null;

                if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    // ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    // public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    // int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                final Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if (location != null) {
                    latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    vido = location.getLatitude();
                    kinhdo = location.getLongitude();
                    List<Address> addresses = null;
                    try {
                        addresses = geocoder.getFromLocation(latLng.latitude,
                                latLng.longitude, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Address ad = null;
                    if (addresses != null) {
                        ad = addresses.get(0);
                    }
                    cameraPosition = new CameraPosition.Builder().target(latLng)
                            .zoom(16)
                            .bearing(0)
                            .tilt(0)
                            .build();
                    if (ad != null) {
                        address = ad.getAddressLine(0);
                        country = ad.getCountryName();
                        city = ad.getLocality();
                        subadminarea = ad.getSubAdminArea();
                    }

                }
                //Dung tien trinh trong 100ms
                SystemClock.sleep(100);

                //Khoi tao cac bien temp ,de tien hanh lay cac gia tri can thiet
                //va gui ve main thread, de ve giao dien

                //Tien hanh gui cac handler ve cho main thread
                final CameraPosition finalCameraPosition = cameraPosition;
                final String finalAddress = address;
                final String finalSubadminarea = subadminarea;
                final String finalCity = city;
                final String finalCountry = country;
                final LatLng finalLatLng = latLng;
                myhandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (location != null) {
                            map.animateCamera(CameraUpdateFactory.newCameraPosition(finalCameraPosition));
                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.title("Vị trí: " + finalAddress
                                    + "," + finalSubadminarea
                                    + "," + finalCity
                                    + "," + finalCountry);

                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                            markerOptions.position(finalLatLng);
                            Marker marker = map.addMarker(markerOptions);
                            marker.showInfoWindow();
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(finalLatLng, 15));
                        }
                        getBusOnStation();
                    }
                });
            }
        });
        //Khoi chay Thread
        th.start();
    }

    //Ham tính toan cac diem nam tren duong tron
    public static PointD calculateDerivedPosition(PointD point, double range, double bearing) {
        double EarthRadius = 6371000; // ban kinh trai dat (don vi met)

        double latA = Math.toRadians(point.x);
        double lonA = Math.toRadians(point.y);
        double angularDistance = range / EarthRadius;
        double trueCourse = Math.toRadians(bearing);

        double lat = Math.asin(
                Math.sin(latA) * Math.cos(angularDistance) +
                        Math.cos(latA) * Math.sin(angularDistance)
                                * Math.cos(trueCourse));

        double dlon = Math.atan2(
                Math.sin(trueCourse) * Math.sin(angularDistance)
                        * Math.cos(latA),
                Math.cos(angularDistance) - Math.sin(latA) * Math.sin(lat));

        double lon = ((lonA + dlon + Math.PI) % (Math.PI * 2)) - Math.PI;

        lat = Math.toDegrees(lat);
        lon = Math.toDegrees(lon);

        PointD newPoint = new PointD(lat, lon);

        return newPoint;
    }

    //Ham xac dinh cac diem nao trong database se o trong pham vi hinh tron
    //co' tam la vi tri hien tai cua minh
    public static boolean pointIsInCircle(PointD pointForCheck, PointD center, double radius) {
        if (getDistanceBetweenTwoPoints(pointForCheck, center) <= radius)
            return true;
        else
            return false;
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

    /** Ham download json data tu url */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Error", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    protected String getTransitDirectionUrl(LatLng origin, LatLng dest) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=true";

        // Travelling Mode
        String mode = "mode=transit";

        //Transit mode
        String transit_mode = "transit_mode=bus";

        //Transit routing preference
        String transit_routing_preference = "transit_routing_preference=less_walking";

        //Language result return
        String language = "language=vi";

        //API key for google direction api
        String apiKey = "key=AIzaSyCzYKfV6-mn-Itzz-jn2YNV-056x48_DcM";

        // Building the parameters to the web service
        String parameters = str_origin + "&" +
                str_dest + "&" +
                sensor + "&" +
                mode + "&"
                + transit_mode + "&" +
//                            transit_routing_preference +"&"+
                language + "&" +
                apiKey;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }

    protected String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Travelling Mode
        String mode = "mode=walking";

        //Language result return
        String language = "language=vi";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode + "&" + language;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this.getActivity().getApplicationContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setZoomControlsEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
        }
//        if (ActivityCompat.checkSelfPermission(this.getActivity().getApplicationContext(),
//                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//                ActivityCompat.checkSelfPermission(this.getActivity().getApplicationContext(),
//                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        mMap.setMyLocationEnabled(true);
//        mMap.getUiSettings().setZoomControlsEnabled(true);

        //Doi so thu 1 cua geocoder phai tra ve context cua application
        geocoder = new Geocoder(this.getContext(), Locale.getDefault());
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this.getActivity().getApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected( Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this.getActivity().getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
//        if (mCurrLocationMarker != null) {
//            mCurrLocationMarker.remove();
//        }
//
//        //Place current location marker
//        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(latLng);
//        markerOptions.title("Current Position");
//        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
//        mCurrLocationMarker = mMap.addMarker(markerOptions);
//
//        //move map camera
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,11));

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this.getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this.getActivity().getApplicationContext())
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }

    //Class to get api route transit from google direction api
    private class DownloadTaskTransit extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage("Đang tìm đường đi xe bus...");
            mProgressDialog.show();
        }

        @Override
        protected String doInBackground(String... url) {
            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTaskTransit p = new ParserTaskTransit();
            p.execute(result);
        }
    }

    private class ParserTaskTransit extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> > {

        HashMap<String, String> hmStringDataBus = new HashMap<>();
        ArrayList<String> instructionsMove = new ArrayList<>();
        ArrayList<String> prettyInstructionMove = new ArrayList<>();
        ArrayList<String> codeBus = new ArrayList<>();
        ArrayList<String> nameBusStop = new ArrayList<>();
        ArrayList<LatLng> listBusStop = new ArrayList<>();
        String status = "",distanceTransit = "", durationTransit = "", startAddress = "",
                endAddress = "", arrivalTime = "", departureTime = "";

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsTransitJSONParser parser = new DirectionsTransitJSONParser();

                // Starts parsing data
                routes = parser.parseRespondeTransit(jObject);//Parse some data return from api
                hmStringDataBus = parser.getInfoRoute();//Get info relate bus route in below.
                instructionsMove = parser.getInstructionsMove();//Get instruction move from bus route api google
                listBusStop = parser.getListBusStop();//Get list all bus top need to go througth
                nameBusStop = parser.getNameBusStop();
                for(int i=0; i<instructionsMove.size(); i++){
                    String elementInstruction = instructionsMove.get(i);
                    String instructionPattern = "(<[^>]*>)|(&nbsp;)";
                    Pattern r = Pattern.compile(instructionPattern);
                    Matcher m =r.matcher(elementInstruction);
                    if(m.find()){
                        String instructionReplace = m.replaceAll("");
                        prettyInstructionMove.add(instructionReplace);
                    } else {
                        prettyInstructionMove.add(elementInstruction);
                    }
                }
                codeBus = parser.getCodeBus(); //Get code bus

                status = hmStringDataBus.get("status");
                distanceTransit = hmStringDataBus.get("distance");
                durationTransit = hmStringDataBus.get("duration");
                startAddress = hmStringDataBus.get("start_address");
                endAddress = hmStringDataBus.get("end_address");
                arrivalTime = hmStringDataBus.get("arrival_time");
                departureTime = hmStringDataBus.get("departure_time");


            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            super.onPostExecute(result);
            ArrayList<LatLng> points;


            if(status.equals("OK")){
                // Traversing through all the routes
                for(int i=0;i<result.size();i++){
                    points = new ArrayList<>();

                    // Fetching legs data
                    List<HashMap<String, String>> path = result.get(i);

                    // Fetching all the points steps
                    for(int j=0;j<path.size();j++){
                        HashMap<String,String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                    }
                    pointTransitRoute = points;
                }

                Intent mIntent = new Intent(getActivity().getApplicationContext(), DetailFoundBus.class);
                Bundle b = new Bundle();

                b.putParcelableArrayList("PATH_LATLNG", pointTransitRoute);
                b.putParcelableArrayList("listBusStop", listBusStop);
                b.putDouble("LAT-A", latA);
                b.putDouble("LNG-B", lngB);
                b.putDouble("LAT-B", latB);
                b.putDouble("LNG-A", lngA);
                b.putString("distance", distanceTransit);
                b.putString("duration", durationTransit);
                b.putString("start_address", startAddress);
                b.putString("end_address", endAddress);
                b.putString("arrival_time", arrivalTime);
                b.putString("departure_time", departureTime);
                b.putStringArrayList("instructions_move",prettyInstructionMove);
                b.putStringArrayList("codeBus",codeBus);
                b.putStringArrayList("nameBusStop", nameBusStop);

                mIntent.putExtra("package", b);

                mProgressDialog.dismiss();
                startActivity(mIntent);
            } else {
                mProgressDialog.dismiss();

                AlertDialog.Builder aB = new AlertDialog.Builder(getActivity());
                aB.setTitle("Thông báo !!");
                aB.setMessage("Không tìm thấy bất kì đường đi nào.");
                aB.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                aB.create();
                aB.show();
            }

            //Reset du lieu
            pointTransitRoute.clear();
            listBusStop.clear();
            codeBus.clear();
            nameBusStop.clear();
            prettyInstructionMove.clear();

        }
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {
        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        HashMap<String, String> hmWalking = new HashMap<>();
        String distance_walking="",duration_walking="",moveInstruction="";

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
                hmWalking = parser.getInfoWalking();
                distance_walking = hmWalking.get("distance");
                duration_walking = hmWalking.get("duration");
                StringBuilder moveWalking = parser.getInstructionsWalking();
                String instructionPattern = "(<[^>]*>)|(&nbsp;)";
                Pattern r = Pattern.compile(instructionPattern);
                Matcher m =r.matcher(moveWalking.toString());
                if(m.find()){
                    moveInstruction = m.replaceAll(" ");
                }

            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        //Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                pointWalkingRoute = points;

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(8);
                lineOptions.color(Color.BLUE);
            }

            if(result.size()<1){
                Toast.makeText(getActivity().getApplicationContext(), "No Points", Toast.LENGTH_SHORT).show();
                return;
            }

            // Drawing polyline in the Google Map for the i-th route
            poly = mMap.addPolyline(lineOptions);

            //Display info walking route on top screen
            walkingDistance.setText(distance_walking);
            walkingDuration.setText(duration_walking);
            walkingDetail.setText(moveInstruction);
            walking_layout_detail.setVisibility(View.VISIBLE);
        }
    }
}
