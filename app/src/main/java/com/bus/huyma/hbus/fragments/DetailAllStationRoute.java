package com.bus.huyma.hbus.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.bus.huyma.hbus.R;
import com.bus.huyma.hbus.activity.DetailFoundBus;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import com.bus.huyma.hbus.activity.DetailFoundBus;

public class DetailAllStationRoute extends Fragment{
    private ArrayList<LatLng> mangToaDoCuoi = new ArrayList<>();
    private ArrayList<String> listTramCuoi = new ArrayList<>();
    private ListView tramCuoi;
    private int indexNameBusStop = 0;
    MarkerOptions maker = new MarkerOptions();

    public DetailAllStationRoute(){}

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(com.bus.huyma.hbus.R.layout.detail_all_station_fragment,container,false);
        tramCuoi = (ListView) v.findViewById(com.bus.huyma.hbus.R.id.listTram);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        super.onCreate(savedInstanceState);

        final DetailFoundBus mActivity = (DetailFoundBus) this.getActivity();
        ArrayList<String> matuyen=mActivity.codeBus;
        ArrayList<String> nameBusStop = mActivity.nameBusStop;
        mangToaDoCuoi = mActivity.sendToaDoCuoiToFragment();
        int maTuyenSize = matuyen.size();
        int nameBusStopSize = nameBusStop.size();

        for(int i=0;i<maTuyenSize; i++){
            if(maTuyenSize==nameBusStopSize){
                if(i==0 && matuyen.get(i).equals("0")){
                    nameBusStop.set(i,nameBusStop.get(i)+" (Lên Bus " + matuyen.get(i+1)+")");
                } else if(i!=0){
                    if(i%2!=0 && matuyen.get(i).equals("0")){
                        nameBusStop.set(i,nameBusStop.get(i)+" (Xuống Bus " + matuyen.get(i-1)+")");
                    } else if(i%2==0 && matuyen.get(i).equals("0")){
                        nameBusStop.set(i,nameBusStop.get(i)+" (Lên Bus " + matuyen.get(i+1)+")");
                    } else if((i-1==0) && !matuyen.get(i).equals("0")){
                        nameBusStop.set(i,nameBusStop.get(i)+" (Xuống Bus " + matuyen.get(i)+")");
                    } else if(!matuyen.get(i).equals("0")) {
                        nameBusStop.set(i,nameBusStop.get(i)+" (Lên Bus " + matuyen.get(i)+")");
                    } else nameBusStop.set(i,nameBusStop.get(i)+" (Xuống Bus " + matuyen.get(i)+")");
                }
            } else if(nameBusStopSize==2){
                if(i==0){
                    if( matuyen.get(i).equals("0")){
                        nameBusStop.set(i,nameBusStop.get(i)+" (Lên Bus " + matuyen.get(i+1)+")");
                    } else {
                        nameBusStop.set(i,nameBusStop.get(i)+" (Lên Bus " + matuyen.get(i)+")");
                    }
                } else if(i-1==0){
                    if(!matuyen.get(i).equals("0")){
                        nameBusStop.set(i,nameBusStop.get(i)+" (Xuống Bus " + matuyen.get(i)+")");
                    } else {
                        nameBusStop.set(i,nameBusStop.get(i)+" (Xuống Bus " + matuyen.get(i-1)+")");
                    }
                }
            }
//            else {
//                if(i==0){
//                    if(matuyen.get(i).equals("0")){
//                        nameBusStop.set(indexNameBusStop,nameBusStop.get(indexNameBusStop)+" (Lên Bus " + matuyen.get(i+1)+")");
//                        indexNameBusStop++;
//                    } else {
//                        nameBusStop.set(indexNameBusStop,nameBusStop.get(indexNameBusStop)+" (Lên Bus " + matuyen.get(i)+")");
//                        indexNameBusStop++;
//                    }
//                } else if(i!=0 && indexNameBusStop<nameBusStopSize){
//                    if(!matuyen.get(i).equals("0")){
//                        if(i-1==0){
//                            nameBusStop.set(indexNameBusStop,nameBusStop.get(indexNameBusStop)+" (Xuống Bus " + matuyen.get(i)+")");
//                            indexNameBusStop++;
//                        } else if (i-1!=0){
//                            nameBusStop.set(indexNameBusStop,nameBusStop.get(indexNameBusStop)+" (Lên Bus " + matuyen.get(i)+")");
//                            indexNameBusStop++;
//                        } else if(indexNameBusStop<nameBusStopSize-1){
//                            nameBusStop.set(indexNameBusStop,nameBusStop.get(indexNameBusStop)+" (Xuống Bus " + matuyen.get(i)+")");
//                        }
//                        indexNameBusStop++;
//                    } else if (matuyen.get(i).equals("0")){
//                        if(i%2!=0){
//                            nameBusStop.set(indexNameBusStop,nameBusStop.get(indexNameBusStop)+" (Xuống Bus " + matuyen.get(i-1)+")");
//                            indexNameBusStop++;
//                        } else if(i%2==0){
//                            nameBusStop.set(indexNameBusStop,nameBusStop.get(indexNameBusStop)+" (Lên Bus " + matuyen.get(i+1)+")");
//                            indexNameBusStop++;
//                        }
//                    }
//                }
//            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity().getApplicationContext(), com.bus.huyma.hbus.R.layout.custom_color_text_listview, nameBusStop);
        tramCuoi.setAdapter(adapter);

        //Lang nghe su kien click vao mot dong tren list view
        tramCuoi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mActivity.mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mangToaDoCuoi.get(position).latitude, mangToaDoCuoi.get(position).longitude), 15));
            }
        });
    }

}
