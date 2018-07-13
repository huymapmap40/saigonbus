package com.bus.huyma.hbus.activity;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bus.huyma.hbus.R;

import java.util.ArrayList;

/**
 * Created by huyma on 16/04/2016.
 */
public class Adapter_Listview extends ArrayAdapter<Item>{
    private Activity activity;
    int id;
    ArrayList<Item> items;

    public Adapter_Listview(Activity context ,int resource , ArrayList<Item> objects) {
        super(context, resource, objects);
        this.activity=context;
        this.id=resource;
        this.items=objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView==null)
        {
            LayoutInflater inflater = activity.getLayoutInflater();
            convertView=inflater.inflate(id,null);

        }
        Item item = items.get(position);
        TextView tv_id = (TextView) convertView.findViewById(R.id.tv_ID);
        TextView tv_name = (TextView) convertView.findViewById(R.id.tv_Name);

        tv_id.setText(item.getId());
        tv_name.setText(item.getName());



        return convertView;
    }
}
