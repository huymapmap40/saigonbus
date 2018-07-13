package com.bus.huyma.hbus.fragments;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bus.huyma.hbus.R;
import com.bus.huyma.hbus.activity.Adapter_Listview;
import com.bus.huyma.hbus.activity.BusRouteActivity;
import com.bus.huyma.hbus.activity.DatabaseHelper;
import com.bus.huyma.hbus.activity.Item;
import com.bus.huyma.hbus.activity.sendFeedBack.MainActivity;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.io.IOException;
import java.util.ArrayList;

public class ThreeFragment extends Fragment {
    private Fragment f;
    public ThreeFragment (){}
    ArrayList<Item> listitem = new ArrayList<Item>();
    private ListView listView = null;
    private DatabaseHelper DBHelper;

    EditText et_Search;
    ImageButton imbt_Search;
    String Search  ;
    String arr[] ={"Đường đi","Quận","Trạm xe"};
    Spinner sp_Search;
    String checkSearching;
    TextView txtMaSo;
    private FloatingActionButton call,voice;
    private FloatingActionMenu fabMenu;
    public static View root3=null;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        super.onCreateView(inflater,container,saveInstanceState);

//        if(root3==null){
        root3 = inflater.inflate(com.bus.huyma.hbus.R.layout.fragment_three,container,false);

        fabMenu = (FloatingActionMenu) root3.findViewById(com.bus.huyma.hbus.R.id.fab_3);
        call = (FloatingActionButton) root3.findViewById(com.bus.huyma.hbus.R.id.fabSubCall);
        //voice = (FloatingActionButton) root3.findViewById(R.id.fabSubVoiceSearch);

        imbt_Search = (ImageButton) root3.findViewById(com.bus.huyma.hbus.R.id.imgbt_Search);
        et_Search = (EditText) root3.findViewById(com.bus.huyma.hbus.R.id.et_Search);
        sp_Search = (Spinner) root3.findViewById(com.bus.huyma.hbus.R.id.sp_search);
        listView = (ListView) root3.findViewById(com.bus.huyma.hbus.R.id.LV_1);
        txtMaSo = (TextView) root3.findViewById(com.bus.huyma.hbus.R.id.tv_ID);
//        }
        return root3;
    }

    @Override
    public void onActivityCreated(Bundle savesInstanceState) {
        super.onActivityCreated(savesInstanceState);

        fabMenu.setClosedOnTouchOutside(true);//Tat cac nut float khi cham vao man hinh
        //Lang nghe su kien cho nut "goi tong dai"
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabMenu.close(true);

                //Mo Activity gui phan hoi
                Intent myIntent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
                startActivity(myIntent);

                //Toast.makeText(getActivity().getApplicationContext(),"Chưa có chức năng call.",Toast.LENGTH_SHORT).show();
            }
        });

        //Lang nghe su kien cho nut "tim kiem bang giong noi"
        /*voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabMenu.close(true);
                Toast.makeText(getActivity().getApplicationContext(),"Chưa có chức năng voice search.",Toast.LENGTH_SHORT).show();
            }
        }); */

        DBHelper = new DatabaseHelper(this.getActivity().getApplicationContext());
        try {
            DBHelper.createDataBase();
        } catch (IOException e) {
            throw new Error("unable to create database.", e);
        }
        //Mo ket noi voi database da co san
        try{
            DBHelper.openDataBase();
        }catch (SQLException s){
            throw s;
        }

        //---------------------------------------------------------------
        //Chu y: context cua ArayAdapter la this.getActivity().getApplicationContext() thi sẽ làm cho màu chữ của item spinner biến thành màu trắng
        //Do đó context phải để là this.getActivity()
        ArrayAdapter<String> sp_adapter = new ArrayAdapter<String>(this.getActivity(),android.R.layout.simple_spinner_item,arr);
        sp_adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        sp_Search.setAdapter(sp_adapter);
        sp_Search.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                checkSearching = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //---------------------------------------------------------------

        imbt_Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Search = et_Search.getText().toString();
                listitem.clear();
                if (checkSearching == "Đường đi") {

                    try {
                        Cursor cursor = DBHelper.rawQuery("select  code, name from  Bus " +
                                "where id in (select Routes.bus_id from Routes " +
                                "where Routes.street_id in (select Streets.id from Streets where Streets.name='" + Search + "' ))",null);
                        if (cursor != null) {
                            if (cursor.moveToFirst()) {
                                do {
                                    Item item = new Item();
                                    item.setId(cursor.getString(0));
                                    item.setName(cursor.getString(1));

                                    listitem.add(item);
                                }
                                while (cursor.moveToNext());
                                cursor.close();
                            }
                        } else Toast.makeText(getActivity().getApplicationContext(),"Không tìm thấy dữ liệu tên đường là" + Search, Toast.LENGTH_LONG).show();
                    } catch (SQLException e) {

                    }
                    ShowListView();
                } else {
                    if (checkSearching == "Quận") {

                        try {
                            Cursor cursor = DBHelper.rawQuery("select  code, name from  Bus " +
                                    "where id in (select Routes.bus_id from Routes " +
                                    "where Routes.street_id in (select Streets.id from Streets where Streets.district='" + Search + "' ))",null);
                            if (cursor != null) {
                                if (cursor.moveToFirst()) {
                                    do {
                                        Item item = new Item();
                                        item.setId(cursor.getString(0));
                                        item.setName(cursor.getString(1));
                                        listitem.add(item);
                                    } while (cursor.moveToNext());
                                    cursor.close();
                                }
                            } else Toast.makeText(getActivity().getApplicationContext(),"Không tìm thấy dữ liệu quận là" + Search, Toast.LENGTH_LONG).show();
                        } catch (SQLException e) {

                        }
                        ShowListView();
                    }
                    else {
                        if (checkSearching == "Trạm xe") {
                            try {
                                Cursor cursor = DBHelper.rawQuery("select code as 'xe' ,name as 'ten tuyen' from Bus " +
                                        "where id in (select Bus_BStop.bus_id from Bus_BStop " +
                                        "where Bus_BStop.bstop_id in (select Bus_stop.id from Bus_stop where Bus_stop.name = '"+Search+"'))",null);
                                if (cursor != null) {
                                    if (cursor.moveToFirst()) {
                                        do {
                                            Item item = new Item();
                                            item.setId(cursor.getString(0));
                                            item.setName(cursor.getString(1));
                                            listitem.add(item);
                                        } while (cursor.moveToNext());
                                        cursor.close();
                                    }
                                } else Toast.makeText(getActivity().getApplicationContext(),"Không tìm thấy dữ liệu trạm xe buýt là" + Search, Toast.LENGTH_LONG).show();
                            } catch (SQLException e) {

                            }
                            ShowListView();
                        }
                    }
                }
            }
        });

        //--------------------------------------------------------------------------------
        //Lang nghe su kien khi click item listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String maSoBus=null;
                txtMaSo = (TextView) view.findViewById(com.bus.huyma.hbus.R.id.tv_ID);
                maSoBus = txtMaSo.getText().toString();

                Intent myIntent = new Intent(getActivity().getApplicationContext(), BusRouteActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("code",maSoBus);

                myIntent.putExtra("Package",bundle);
                startActivity(myIntent);
            }
        });
    }

    public void ShowListView()
    {
        Adapter_Listview adapter_listview = new Adapter_Listview(this.getActivity(), com.bus.huyma.hbus.R.layout.custom_listview_result_fragment3, listitem);
        listView.setAdapter(adapter_listview);
        adapter_listview.notifyDataSetChanged();
    }
}
