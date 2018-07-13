package com.bus.huyma.hbus.fragments;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bus.huyma.hbus.R;
import com.bus.huyma.hbus.activity.Adapter;
import com.bus.huyma.hbus.activity.BusRouteActivity;
import com.bus.huyma.hbus.activity.bus;

import java.util.ArrayList;
import java.util.List;

public class TwoFragment extends Fragment {
    ArrayList<bus> listData = new ArrayList<>();
    public TwoFragment (){}
    private ListView list;
    Adapter adt;
    ImageButton btnXoa;
    EditText edTimKiem;
    TextView txtMaSo;
    private static View root2;
    Button bttest;
    private final int REQ_SPEECH_INPUT = 110;

//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//    }

    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        listData = getData();
        adt = new Adapter(getActivity(),listData);
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        super.onCreateView(inflater, container, saveInstanceState);

//        if(root2==null){
        root2 = inflater.inflate(com.bus.huyma.hbus.R.layout.fragment_two,container,false);
        list = (ListView) root2.findViewById(com.bus.huyma.hbus.R.id.lvData);
        btnXoa = (ImageButton) root2.findViewById(com.bus.huyma.hbus.R.id.btnClearSearch);
        edTimKiem = (EditText) root2.findViewById(com.bus.huyma.hbus.R.id.edSearch);
        txtMaSo = (TextView ) root2.findViewById(com.bus.huyma.hbus.R.id.maso);
        bttest = (Button) root2.findViewById(com.bus.huyma.hbus.R.id.bttest);
//        }
        return root2;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        list.setAdapter(adt);

        //Lang nghe su kien tren nut xoa
        btnXoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edTimKiem.setText(null);
            }
        });


        //----------------------------------
        //Chi set hien thi BusRouteActivity
        list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position,long id){

                String maSoBus=null;
                txtMaSo = (TextView) view.findViewById(com.bus.huyma.hbus.R.id.maso);
                maSoBus = txtMaSo.getText().toString();

                Intent myIntent = new Intent(getActivity().getApplicationContext(), BusRouteActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("code",maSoBus);

                myIntent.putExtra("Package",bundle);
                startActivity(myIntent);
            }
        });


        //Chuc nang filter trong edit text
        edTimKiem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ArrayList<bus> temp = new ArrayList<>();//tao danh sach tam cac so khi search
                int textLength = edTimKiem.getText().length();//lay do dai cua chuoi khi nhap vao o tim kiem
                temp.clear(); // xoa cac phan tu trong ds tam

                //neu do dai cua chuoi nhap vao be hon ten cua kenh ,
                //khong phan biet chu hoa va chu thuong, neu ten cua kenh nao chua cac ki tu nhap trong edittext thi
                //add cac kenh do vao danh sach tam
                //sau do gan vao adapter, va set hien len list view cac kenh do.
                for (int i = 0; i < listData.size(); i++) {
                    if (textLength <= listData.get(i).getDodaimaso()) {
                        if (edTimKiem.getText().toString().equalsIgnoreCase((String) listData.get(i).getMaso().subSequence(0, textLength))) {
                            temp.add(listData.get(i));
                        }
                    }
                }
                list.setAdapter(new Adapter(getActivity().getApplicationContext(), temp));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //Voice search
        bttest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkvoicedevice();
                speakvoice();
            }

        });
    }

    public void checkvoicedevice()
    {
        PackageManager pkm = getActivity().getPackageManager();
        List<ResolveInfo> act = pkm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (act.size()==0)
        {
            Toast.makeText(getActivity().getApplicationContext(),"Thiết bị không hỗ trợ", Toast.LENGTH_SHORT).show();
        }



    }
    public void speakvoice()
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        Intent intent1 = intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "vi");
        //intent.putExtra(RecognizerIntent.EXTRA_PROMPT,getString(R.string.speech_prompt));
        //intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
        try{
            startActivityForResult(intent,REQ_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getActivity().getApplicationContext(), "Your device is not supported!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_SPEECH_INPUT: {

                if (resultCode == Activity.RESULT_OK  && null != data) {
                    //Tao bien chuoi de lay thong tin vua noi
                    ArrayList<String> textMatchList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    //tviewSpeak.setText(textMatchList.get(0));
                    ArrayList<bus> tam = new ArrayList<>();
                    tam.clear();
                    //String test = edtest.getText().toString();
                    String test = textMatchList.get(0);
                    //int lg= edtest.getText().length();
                    int t = test.lastIndexOf(' ');
                    //String testt=t+"";

                    test = test.substring(t+1);
                    //Toast.makeText(getActivity().getApplicationContext(),testt,Toast.LENGTH_SHORT).show();
                    for (int i = 0; i < listData.size(); i++) {
                        if (test.equalsIgnoreCase((String) listData.get(i).getMaso()))
                        {
                            tam.add(listData.get(i));
                        }
                    }
                    list.setAdapter(new Adapter(getActivity().getApplicationContext(), tam));
                }
                break;
            }
        }
    }

    private ArrayList<bus> getData() {
        ArrayList<bus> list = new ArrayList<>();
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","1", "Bến Thành - Bến Xe Chợ Lớn"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","2", "Bến Thành - Bến Xe Miền Tây"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","3", "Bến Thành - Thạnh Lộc"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","4", "Bến Thành - Cộng Hòa - An Sương"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","5", "Bến Xe Chợ Lớn - Biên Hòa"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","6", "Bến Xe Chợ Lớn - Đại Học Nông Lâm"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","7", "Bến Xe Chợ Lớn - Gò Vấp"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","8", "Bến Xe Quận 8 - Đại Học Quốc Gia"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","9", "Bến Xe Chợ Lớn - Bình Chánh - Hưng Long"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","10", "Đại Học Quốc Gia - Bến Xe Miền Tây"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","100", "Bến Xe Củ Chi - Cầu Tân Thái"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","101", "Bến Xe Chợ Lớn - Chợ Tâm Nhựt"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","102", "Bến Thành - Nguyễn Văn Linh - Bến xe Miền Tây"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","103", "Bến xe Chợ Lớn - Bến xe Ngã 4 Ga"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","104", "Bến xe An Sương - Đại học Nông Lâm"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","107", "Bến xe Củ Chi - Bố Heo"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","109", "Công viên 23/9 – Sân bay Tân Sơn Nhất"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","11", "Bến Thành – Đầm Sen"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","110", "Phú Xuân – Hiệp Phước"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","12", "Bến Thành – Thác Giang Điền"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","122", "Bến xe An Sương - Tân Quy"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","123", "Phú Mỹ Hưng (khu H) - Quận 1"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","124", "Phú Mỹ Hưng (khu S) - Quận 1"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","126", "Bến xe Củ Chi - Bình Mỹ"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","127", "An Thới Đông – Ngã ba Bà Xán"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","128", "Tân Điền – An Nghĩa"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","13", "Công viên 23/9 - Bến xe Củ Chi"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","139", "Bến xe Miền Tây - Khu tái định cư Phú Mỹ"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","14", "Bến xe Miền Đông - 3/2 - Bến xe Miền Tây"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","140", "Công viên 23/9 - Phạm Thế Hiển - Ba Tơ"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","141", "KDL BCR - Long Trường - KCX Linh Trung 2"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","144", "Bến xe Miền Tây - Chợ Lớn - CV Đầm Sen - CX Nhiêu Lộc"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","145", "Bến xe Chợ Lớn - Chợ Hiệp Thành"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","146", "Bến xe Miền Đông - Chợ Hiệp Thành"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","148", "Bến xe Miền Tây - Gò Vấp"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","149", "Công viên 23/9 – Khu dân cư Bình Hưng Hòa B"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","15", "Chợ Phú Định - Đầm Sen"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","150", "Bến xe Chợ Lớn - Ngã 3 Tân Vạn"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","151", "Bến xe Miền Tây - Bến xe An Sương"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","152", "Khu dân cư Trung Sơn – Bến Thành - Sân bay Tân Sơn Nhất"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","16", "Bến xe Chợ Lớn – Bến xe Tân Phú"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","17", "Bến xe Chợ Lớn - Đại học Sài Gòn - Khu chế xuất Tân Thuận"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","18", "Bến Thành - Chợ Hiệp Thành"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","19", "Bến Thành - KCX Linh Trung - Đại học Quốc gia"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","20", "Bến Thành – Nhà Bè"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","22", "Bến xe Quận 8 - KCN Lê Minh Xuân"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","23", "Bến xe Chợ Lớn - Ngã 3 Giồng - Cầu Lớn"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","24", "Bến xe Miền Đông - Hóc Môn"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","25", "Bến xe Quận 8 - KDC Vĩnh Lộc A"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","27", "Công viên 23/9 - Âu Cơ - Bến xe An Sương"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","28", "Công viên 23/9 - Chợ Xuân Thới Thượng"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","29", "Phà Cát Lái - Chợ nông sản Thủ Đức"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","30", "Chợ Tân Hương - Đại học Quốc tế"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","31", "KDC Tân Quy - Bến Thành - KDC Bình Lợi"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","32", "Bến xe Miền Tây - Bến xe Ngã 4 Ga"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","33", "Bến xe An Sương - Suối Tiên - Đại học Quốc gia"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","34", "Bến Thành - Đại học Công nghệ Sài Gòn"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","35", "Tuyến xe buýt Quận 1 – Quận 2"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","36", "Bến Thành - Thới An"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","37", "Cảng Quận 4 – Nhơn Đức"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","38", "KDC Tân Quy - Bến Thành - Đầm Sen"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","39", "Bến Thành - Võ Văn Kiệt - Bến xe Miền Tây"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","40", "Bến xe Miền Đông - Bến xe Ngã 4 Ga"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","41", "Bến xe Miền Tây-Ngã tư Bốn Xã-Bến xe An Sương"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","42", "Chợ Cầu Muối-Chợ nông sản Thủ Đức"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","43", "Bến xe Miền Đông - Phà Cát Lái"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","44", "Cảng Quận 4 – Bình Quới"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","45", "Bến xe Quận 8 - Bến Thành - Bến xe Miền Đông"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","46", "Cảng Quận 4 - Bến Mễ Cốc"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","47", "Bến xe Chợ Lớn - Quốc lộ 50 - Hưng Long"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","48", "Bến xe Tân Phú - Chợ Hiệp Thành"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","49", "Sân bay Tân Sơn Nhất - Quận 1"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","50", "Đại học Bách khoa - Đại học Quốc gia"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","51", "Bến xe Miền Đông - Bình Hưng Hòa"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","52", "Bến Thành - Đại học Quốc tế"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","53", "Lê Hồng Phong - Đại học Quốc gia"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","54", "Bến xe Miền Đông - Bến xe Chợ Lớn"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","55", "Công viên phần mềm Quang Trung - Khu Công nghệ cao (Q9)"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","56", "Bến xe Chợ Lớn - Đại học Giao thông Vận tải"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","57", "Chợ Phước Bình - Trường THPT Hiệp Bình"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","58", "Bến xe Ngã 4 Ga - Bình Mỹ"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","59", "Bến xe Quận 8 - Bến xe Ngã 4 Ga"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","60", "Bến xe An Sương - KCN Lê Minh Xuân"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","60-1", "BX Miền Tây - BX Biên Hòa"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","60-2", "Phú Túc - Đại học Nông Lâm"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","60-3", "Bến xe Miền Đông – Khu Công nghiệp Nhơn Trạch"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","60-4", "Bến xe Miền Đông - Bến xe Hố Nai"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","61", "Bến xe Chợ Lớn - KCN Lê Minh Xuân"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","61-1", "Thủ Đức - Dĩ An"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","61-3", "Bến xe An Sương – Thủ Dầu Một"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","61-4", "Bến Dược – Dầu Tiếng"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","61-6", "Bến Thành - Khu Du lịch Đại Nam"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","61-7", "Bến đò Bình Mỹ - Bến xe Bình Dương"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","61-8", "Bến xe Miền Tây – Khu Du lịch Đại Nam"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","62", "Bến xe Quận 8 -Thới An"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","62-1", "Bến xe Chợ Lớn - Bến Lức"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","62-10", "Bến xe Chợ Lớn - Thanh Vĩnh Đông"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","62-11", "Bến xe Quận 8 - Tân Tập"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","62-2", "Bến xe Chợ Lớn - Ngã 3 Tân Lân"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","62-3", "Bến Củ Chi - Bến xe Hậu Nghĩa"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","62-4", "Thị trấn Tân Túc - Chợ Bến Lức"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","62-5", "Bến xe An Sương - Bến xe Hậu Nghĩa"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","62-6", "Bến xe Chợ Lớn-Bến xe Hậu Nghĩa"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","62-7", "Bến xe Chợ Lớn - Bến xe Đức Huệ"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","62-8", "Bến xe Chợ Lớn - Bến xe Tân An"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","62-9", "Bến xe Quận 8 – Cầu Nổi"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","64", "Bến xe Miền Đông - Đầm Sen"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","65", "Bến Thành - CMT8 - Bến xe An Sương"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","66", "Bến xe Chợ Lớn - Bến xe An Sương"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","68", "Bến xe Chợ Lớn - KCX Tân Thuận"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","69", "Công viên 23/9 – KCN Tân Bình"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","70", "Tân Quy - Bến Súc"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","70-1", "Bến xe Củ Chi - Bến xe Gò Dầu"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","70-2", "BX Củ Chi – Hòa Thành"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","70-3", "Bến Thành – Mộc Bài"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","70-5", "Bố Heo - Lộc Hưng"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","71", "Bến xe An Sương - Phật Cô Đơn"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","72", "Công viên 23/9 – Hiệp Phước"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","73", "Chợ Bình Chánh - KCN Lê Minh Xuân"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","74", "Bến xe An Sương - Bến xe Củ Chi"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","75", "Sài Gòn - Cần Giờ"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","76", "Long Phước - Suối Tiên - Đền Vua Hùng"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","77", "Đồng Hòa – Cần Thạnh"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","78", "Thới An – Hóc Môn"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","79", "Bến xe Củ Chi - Đền Bến Dược"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","81", "Bến xe Chợ Lớn - Lê Minh Xuân"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","83", "Bến xe Củ Chi - Cầu Thầy Cai"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","84", "Bến xe Chợ Lớn - Tân Túc"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","85", "Bến xe An Sương – KCN Nhị Xuân"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","86", "Bến Thành - Đại học Tôn Đức Thắng"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","87", "Bến xe Củ Chi - An Nhơn Tây"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","88", "Bến Thành - Chợ Long Phước"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","89", "Đại học Nông Lâm - Trường THPT Hiệp Bình"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","90", "Phà Bình Khánh - Cần Thạnh"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","91", "Bến xe Miền Tây - Chợ nông sản Thủ Đức"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","93", "Bến Thành - Đại học Nông Lâm"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","94", "Bến xe Chợ Lớn - Bến xe Củ Chi"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","95", "Bến xe Miền Đông - KCN Tân Bình"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","96", "Bến Thành - Chợ Bình Điền"));
        list.add(new bus(com.bus.huyma.hbus.R.drawable.buslogo, "Tuyến Số","99", "Chợ Thạnh Mỹ Lợi - Đại học Quốc gia"));
        return list;
    }
}
