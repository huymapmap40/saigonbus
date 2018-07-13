package com.bus.huyma.hbus.fragments;

import android.database.Cursor;
import android.database.SQLException;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bus.huyma.hbus.R;
import com.bus.huyma.hbus.activity.BusRouteActivity;
import com.bus.huyma.hbus.activity.DatabaseHelper;

import java.io.IOException;

import com.bus.huyma.hbus.activity.BusRouteActivity;
import com.bus.huyma.hbus.activity.DatabaseHelper;

/**
 * Created by huyma on 11/04/2016.
 */

public class InfoBusDetail extends Fragment {
    public InfoBusDetail(){}
    TextView txtInfoDetail;
    DatabaseHelper DBHelper;
    private static String maSoBus;
    private static String getCheck;

    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle saveInstanceState){
        View v = inflater.inflate(com.bus.huyma.hbus.R.layout.detail_info_bus, container, false);
        txtInfoDetail = (TextView) v.findViewById(com.bus.huyma.hbus.R.id.txtDetailBusRoute);

        BusRouteActivity activity = (BusRouteActivity) this.getActivity();
        maSoBus = activity.sendCodeBusToFragment();
        getCheck = activity.sendDataToFragment();

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);


        if(getCheck.equals("true")){
            thongTinChiTiet();
        } else if(getCheck.equals("false")){
            txtInfoDetail.setText("Không tìm thấy dữ liệu xe bus "+ maSoBus);
        }

    }
    private void thongTinChiTiet(){
        DBHelper = new DatabaseHelper(this.getActivity().getApplicationContext());
        try{
            DBHelper.createDataBase();
        }catch (IOException e){
            throw new Error("Error when create database",e);
        }

        try{
            DBHelper.openDataBase();
        }catch (SQLException s){
            throw s;
        }

        Cursor c = DBHelper.rawQuery("select info from Bus where code='" + maSoBus + "'", null);
        if(c.moveToFirst()){
            do{
                String info = c.getString(c.getColumnIndex("info"));
                txtInfoDetail.setText(info);
            }while(c.moveToNext());
        }
    }
}
