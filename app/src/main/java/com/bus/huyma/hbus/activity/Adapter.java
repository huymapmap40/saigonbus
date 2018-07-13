package com.bus.huyma.hbus.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bus.huyma.hbus.R;

import java.util.ArrayList;

public class Adapter extends BaseAdapter{

    private ArrayList<bus> listData;
    private LayoutInflater inflater;

    public ArrayList<bus> getListData() {
        return listData;
    }

    public void setListData(ArrayList<bus> listData) {
        this.listData = listData;
    }

    public Adapter(Context context, ArrayList<bus> listData){
        this.inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.listData=listData;
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public bus getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View converView, ViewGroup parent){
        bus currentBus = getItem(position);
        ImageView imgLogo;
        TextView txtMaSo, txtLoTrinh,txtTuyen;
        if(converView == null){
            converView = inflater.inflate(R.layout.listview_item, null);
        }
        imgLogo = (ImageView) converView.findViewById(R.id.imglogo);
        txtMaSo = (TextView) converView.findViewById(R.id.maso);
        txtLoTrinh = (TextView) converView.findViewById(R.id.lotrinh);
        txtTuyen = (TextView) converView.findViewById(R.id.tuyen);

        txtMaSo.setText(currentBus.getMaso());
        txtLoTrinh.setText(currentBus.getLotrinh());
        txtTuyen.setText(currentBus.getTuyen());

        imgLogo.setImageResource(currentBus.getLogo());
        return converView;
    }
}
