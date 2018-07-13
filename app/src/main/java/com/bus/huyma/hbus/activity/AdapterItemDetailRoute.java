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

public class AdapterItemDetailRoute extends BaseAdapter {
    ArrayList<ItemDetailRoute> listData;
    LayoutInflater inflater;

    public AdapterItemDetailRoute(Context context, ArrayList<ItemDetailRoute> listData){
        this.inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.listData=listData;
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public ItemDetailRoute getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemDetailRoute current = getItem(position);
        ImageView hinhHuongDan;
        TextView noiDungHuongDan;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.item_go_bus_fragment, null);
        }
        hinhHuongDan = (ImageView) convertView.findViewById(R.id.imgHuongDan);
        noiDungHuongDan = (TextView) convertView.findViewById(R.id.txtHuongDanDi);

        noiDungHuongDan.setText(current.getCachDi());
        hinhHuongDan.setImageResource(current.getLogo());

        return convertView;
    }
}
