package com.bus.huyma.hbus.activity.sendFeedBack;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bus.huyma.hbus.R;

import java.util.ArrayList;
/**
 * Created by huyma on 12/11/2016.
 */
public class Adapter_info_listview  extends ArrayAdapter<Info_main>{
    private Activity activity;
    int id;
    ArrayList<Info_main> item_info;

    public Adapter_info_listview(Activity context, int resource, ArrayList<Info_main> objects) {
        super(context, resource, objects);
        this.activity=context;
        this.id=resource;
        this.item_info=objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView==null)
        {
            LayoutInflater inflater = activity.getLayoutInflater();
            convertView=inflater.inflate(id,null);

        }
        Info_main infor = item_info.get(position);
        TextView tv_name = (TextView) convertView.findViewById(R.id.txtmain);
        tv_name.setText(infor.getName());
        convertView.setBackgroundColor((position % 2) == 0 ? Color.BLACK : Color.parseColor("#f6f4ff"));

        return convertView;
    }

}

