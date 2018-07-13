package com.bus.huyma.hbus.fragments;

import android.database.Cursor;
import android.database.SQLException;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bus.huyma.hbus.R;
import com.bus.huyma.hbus.activity.BusRouteActivity;
import com.bus.huyma.hbus.activity.DatabaseHelper;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by huyma on 11/04/2016.
 */
public class TimeBusDetail extends Fragment{

    private static String maSoBus;
    private static String getCheck;
    private static int huongDiXeBus;
    DatabaseHelper DBHelper;
    private ListView listTime;
    private TextView txtTime;

    public TimeBusDetail(){}
        @Override
        public void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
        }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        View v = inflater.inflate(com.bus.huyma.hbus.R.layout.detail_time_bus,container,false);
        listTime = (ListView) v.findViewById(com.bus.huyma.hbus.R.id.listBusTime);
        txtTime = (TextView) v.findViewById(com.bus.huyma.hbus.R.id.txtTimeBus);

        //Lay du lieu check va codeBus (ma so xe bus) tu activity BusRouteActivity
        BusRouteActivity activity = (BusRouteActivity) this.getActivity();
        maSoBus = activity.sendCodeBusToFragment();
        getCheck = activity.sendDataToFragment();
        huongDiXeBus = activity.sendDirectionBusToFragment();
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        if(getCheck.equals("true")){
            getBusTime();
        }else {
            txtTime.setText("Không có dữ liệu thời gian của tuyến xe bus này.");
        }
    }

    private void getBusTime(){
        DBHelper = new DatabaseHelper(this.getActivity().getApplicationContext());
        try{
            DBHelper.createDataBase();
        }catch (IOException e){
            throw new Error("Can not create database");
        }

        try{
            DBHelper.openDataBase();
        }catch (SQLException s){
            throw s;
        }

        if(huongDiXeBus==1 || huongDiXeBus ==3){
            Cursor c = DBHelper.rawQuery("select hours,minutes from Bus_Time where direction='1' and bus_id in (select id from Bus where code='"+maSoBus+"')",null);
            if(c.moveToFirst()){

                ArrayList<String> timeString = new ArrayList<String>();

                do{
                    String hh = c.getString(c.getColumnIndex("hours"));
                    String mm = c.getString(c.getColumnIndex("minutes"));
                    timeString.add(hh+":"+mm);
                }while (c.moveToNext());

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity().getApplicationContext(), com.bus.huyma.hbus.R.layout.custom_color_text_listview,timeString);
                listTime.setAdapter(adapter);
            }
        } else if (huongDiXeBus ==2){
            Cursor c = DBHelper.rawQuery("select hours,minutes from Bus_Time where direction='2' and bus_id in (select id from Bus where code='"+maSoBus+"')",null);
            if(c.moveToFirst()){

                ArrayList<String> timeString = new ArrayList<String>();

                do{
                    String hh = c.getString(c.getColumnIndex("hours"));
                    String mm = c.getString(c.getColumnIndex("minutes"));
                    timeString.add(hh+":"+mm);
                }while (c.moveToNext());

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity().getApplicationContext(), com.bus.huyma.hbus.R.layout.custom_color_text_listview,timeString);
                listTime.setAdapter(adapter);
            }
        }
    }
}
