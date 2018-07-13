package com.bus.huyma.hbus.activity.sendFeedBack;

/**
 * Created by huyma on 12/11/2016.
 */
import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bus.huyma.hbus.R;

import java.util.ArrayList;

public class Adapter_Attach_listview extends ArrayAdapter<Item_attach> {
    private Activity activity;
    int id;
    ArrayList<Item_attach> items;

    public Adapter_Attach_listview(Activity context, int resource, ArrayList<Item_attach> objects) {
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
        Item_attach item = items.get(position);
        TextView tv_name = (TextView) convertView.findViewById(R.id.txtView);
        ImageView imgview = (ImageView) convertView.findViewById(R.id.imgView);
        imgview.setImageResource(item.getImage());
        tv_name.setText(item.getName());
        convertView.setBackgroundColor((position % 2) == 0 ? Color.BLACK : Color.red(1));

        return convertView;
    }
}