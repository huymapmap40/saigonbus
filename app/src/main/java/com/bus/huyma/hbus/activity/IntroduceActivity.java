package com.bus.huyma.hbus.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;

import com.bus.huyma.hbus.R;

public class IntroduceActivity extends Activity {
    private Handler handler;
    private Runnable delayRunnable;
    boolean GPSEnable;
    public static final int REQUEST_CODE = 113;
    public static final int RESULT_CODE = 114;
    public static final int RESULT_CODE1 = 115;
    GPSTracker gps;
    boolean check = false;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.introduce_layout);
        handler = new Handler();

        gps = new GPSTracker(IntroduceActivity.this);

        if(!gps.isWifi){
            showWifiSetting();
            Thread t = new Thread(){
                @Override
                public void run(){
                    try{
                        while (!gps.isWifi){
                            Thread.sleep(1000);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            };
            t.start();
        } else if(!gps.isGPSEnable){
            showSettingAlert();
            Thread t1 = new Thread(){
                @Override
                public void run(){
                    try{
                        while (!gps.isGPSEnable){
                            Thread.sleep(1000);
                        }
//                        startActivityForResult(new Intent(IntroduceActivity.this, MapsActivity.class), REQUEST_CODE);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            };
            t1.start();
        } else if(gps.canGetLocation()){
            check = true;
            delayRunnable = new Runnable() {
                @Override
                public void run() {
                    startActivityForResult(new Intent(IntroduceActivity.this, MapsActivity.class), REQUEST_CODE);
                }
            };
            //chay activity gioi thieu trong vong 3s
            handler.postDelayed(delayRunnable,3000);
        }
    }

    /**Function to show setting dialog alert
     * This function will load when you open the app
     * **/
    public void showSettingAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Kích hoạt định vị GPS");
        alertDialog.setMessage("GPS chưa được kích hoạt. Kích hoạt và khởi động lại ứng dụng ngay ?");
        alertDialog.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
                onDestroy();
            }
        });
        alertDialog.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                onDestroy();
            }
        });

        alertDialog.create();
        alertDialog.show();
    }

    public void showWifiSetting(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Kích hoạt Wifi");
        alertDialog.setMessage("Wifi chưa được kích hoạt. Kích hoạt và khởi động lại ứng dụng ngay ?");
        alertDialog.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                startActivity(intent);
                onDestroy();
            }
        });
        alertDialog.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                onDestroy();
            }
        });

        alertDialog.create();
        alertDialog.show();
    }

    //Tien hanh kill activity khi nguoi dung nhan nut thoat ung dung tu 'MapsActivity'
    //Nhan ve cac result code,va tien hanh goi ham onDestroy, de kill activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            switch (resultCode) {
                case RESULT_CODE: {
                    onDestroy();
                }

                case RESULT_CODE1: {
                    onDestroy();
                }
            }

        }
    }

    @Override
    protected  void onDestroy(){
        super.onDestroy();
        finish();
    }
}
