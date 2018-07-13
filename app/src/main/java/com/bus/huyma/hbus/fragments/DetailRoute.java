package com.bus.huyma.hbus.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.bus.huyma.hbus.R;
import com.bus.huyma.hbus.activity.AdapterItemDetailRoute;
import com.bus.huyma.hbus.activity.DetailFoundBus;
import com.bus.huyma.hbus.activity.ItemDetailRoute;

import java.util.ArrayList;

public class DetailRoute extends Fragment {
    private ListView huongDan;
    private ArrayList<String> matuyen = new ArrayList<>();
    private ArrayList<String> cachdi = new ArrayList<>();
    private ArrayList<ItemDetailRoute> listData = new ArrayList<>();
    AdapterItemDetailRoute adapter;

    public DetailRoute(){}

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.detail_go_bus_fragment,container,false);
        huongDan = (ListView) v.findViewById(R.id.listGoBus);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        DetailFoundBus mActivity = (DetailFoundBus) this.getActivity();
        cachdi=mActivity.sendCachDiFragment();
        matuyen=mActivity.codeBus;
        for(int i=0;i<matuyen.size();i++){
            if(matuyen.get(i).equals("0")){
                listData.add(new ItemDetailRoute(R.drawable.walking_guide, cachdi.get(i)));
            } else listData.add(new ItemDetailRoute(R.drawable.bus_guide, cachdi.get(i)));
        }

        adapter = new AdapterItemDetailRoute(this.getActivity().getApplicationContext(),listData);
        huongDan.setAdapter(adapter);
    }
}
