package com.bus.huyma.hbus.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bus.huyma.hbus.R;
import com.bus.huyma.hbus.activity.choicePositionActivity;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;

public class MyAlertDialogFragment extends DialogFragment {
    //private PlaceAutocompleteFragment xuatPhat,ketThuc; //khai bao bien fragment autocomplete place de nhap place
    private EditText xuatPhat,ketThuc; //khai bao bien edittext neu su dung edittext de nhap place
    private ImageView start ,finish,swap;
    private Button find,thoat ;
    private Spinner soXe ;
    int demSoTuyenXe=1;
    View view=null;
    public MyAlertDialogFragment(){}
    private int PLACE_AUTOCOMPLETE_REQUEST_CODE=1;//request code cho editext xuat phat
    private int PLACE_AUTOCOMPLETE_REQUEST_CODE1=2;//request code cho editext ket thuc
    public static final int REQUEST_CODE_2 = 14;
    public static final int REQUEST_CODE = 11;
    public static final int RESULT_CODE = 12;//result code de tra ve tu chon noi bat dau
    public static final int RESULT_CODE1 = 13;//result code de tra ve tu chon noi ket thuc
    private double latA,lngA,latB,lngB;
    private String diaChiDau,diaChiSau;

    //Cac bien su dung cho ham hoan doi gia tri
    private static double Temp2=0,Temp3=0;

    public interface MyCustomDialogListener {
        void onFinishEditDialog(double lat_A,double lng_A,double lat_B,double lng_B,int countRoute);
    }

    // Call this method to send the data back to the parent fragment
    public void sendBackResult() {
        // Notice the use of `getTargetFragment` which will be set when the dialog is displayed
        MyCustomDialogListener listener = (MyCustomDialogListener) getTargetFragment();

        listener.onFinishEditDialog(latA, lngA, latB, lngB ,demSoTuyenXe);
        dismiss();
    }

    public static MyAlertDialogFragment newInstance(String title){
        MyAlertDialogFragment frag = new MyAlertDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        view =inflater.inflate(R.layout.custom_dialog_findroute, container);

        //TODO: Nếu dùng PlaceAutocompleteFragment thì thay lại 2 EditText trong custom_dialog_findroute là thẻ <frament>
        //xuatPhat = (PlaceAutocompleteFragment) getActivity().getFragmentManager().findFragmentById(R.id.editXuatPhat);
        //ketThuc = (PlaceAutocompleteFragment) getActivity().getFragmentManager().findFragmentById(R.id.editKetThuc);

        xuatPhat = (EditText) view.findViewById(R.id.editXuatPhat);
        ketThuc = (EditText) view.findViewById(R.id.editKetThuc);

        start = (ImageView) view.findViewById(R.id.btnStart);
        finish = (ImageView) view.findViewById(R.id.btnFinish);
        swap = (ImageView) view.findViewById(R.id.btnSwap);
        find = (Button) view.findViewById(R.id.btnFind);
        thoat = (Button) view.findViewById(R.id.btnThoat);
        soXe = (Spinner) view.findViewById(R.id.spinnerSoXe);

        // Fetch arguments from bundle and set title
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return view;
    }

    /*@Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
    }*/

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //lang nghe su kien tren editext xuat phat
        xuatPhat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .build(getActivity());
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                    //PLACE_AUTOCOMPLETE_REQUEST_CODE is integer for request code
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
            }
        });

        //lang nghe su kien tren editext ket thuc
        ketThuc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .build(getActivity());
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE1);
                    //PLACE_AUTOCOMPLETE_REQUEST_CODE is integer for request code
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
            }
        });

        //Lang nghe su kien tren nut swap 2 vi tri dau va cuoi
        swap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Temp2 = latA;
                latA = latB;
                latB = Temp2;

                Temp3 = lngA;
                lngA = lngB;
                lngB = Temp3;

                swapEditText(xuatPhat,ketThuc);
            }
        });

        //Set su kien tren nut chon vi tri cu the tu ban do
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapIntent = new Intent(getActivity().getApplicationContext(), choicePositionActivity.class);
                startActivityForResult(mapIntent, REQUEST_CODE);
            }
        });

        //Set lang nghe su kien cho nut chon vi tri cu the tu ban do
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapIntent = new Intent(getActivity().getApplicationContext(), choicePositionActivity.class);
                startActivityForResult(mapIntent, REQUEST_CODE_2);
            }
        });

        ArrayAdapter<CharSequence> sp_adapter = ArrayAdapter.createFromResource(this.getActivity(), R.array.maxBus,android.R.layout.simple_spinner_item);
        sp_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        soXe.setEnabled(false);
        soXe.setAdapter(sp_adapter);
        soXe.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int count = position+1;
                demSoTuyenXe = count;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Xu ly nut tim duong
        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBackResult();
            }
        });

        //Xu ly nut quay lai fragmentOne
        thoat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //autocompleteFragment.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this.getActivity(), data);
                latA = place.getLatLng().latitude;
                lngA = place.getLatLng().longitude;
                xuatPhat.setText(place.getName().toString());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this.getActivity(), data);
                Toast.makeText(this.getActivity().getApplicationContext(),status.toString(),Toast.LENGTH_LONG).show();
            } else if (requestCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this.getActivity().getApplicationContext(),"Bạn chưa chọn địa điểm bắt đầu!",Toast.LENGTH_LONG).show();
            }
        } else if(requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE1){
            if (resultCode == Activity.RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this.getActivity(), data);
                latB = place.getLatLng().latitude;
                lngB = place.getLatLng().longitude;
                ketThuc.setText(place.getName().toString());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this.getActivity(), data);
                Toast.makeText(this.getActivity().getApplicationContext(),status.toString(),Toast.LENGTH_LONG).show();
            } else if (requestCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this.getActivity().getApplicationContext(),"Vui lòng chọn địa chỉ đi và đến!",Toast.LENGTH_LONG).show();
            }
        }
        //-----------------------------------------------------------------------------------------------------------------
        //Ket qua tra ve tu acitivy 'chon vi tri ban dau tu ban do'
        if (requestCode == REQUEST_CODE) {
            switch (resultCode) {
                case RESULT_CODE: {
                    int kiemTra = data.getIntExtra("check",0);
                    if(kiemTra==1){
                        String text = data.getStringExtra("textToaDo");
                        LatLng position = data.getParcelableExtra("toaDo");
                        latA = position.latitude;
                        lngA = position.longitude;
                        //diaChiDau = data.getStringExtra("diachihientai");
                        xuatPhat.setText(text);
                    } else if (kiemTra == 2){
                        String text = data.getStringExtra("textChon");
                        LatLng position = data.getParcelableExtra("toaDoChon");
                        latA = position.latitude;
                        lngA = position.longitude;
                        //diaChiDau = data.getStringExtra("diachidachon");
                        xuatPhat.setText(text);
                    }
                }
                case RESULT_CODE1: {
                    break;//Neu ko co chon bat cu toa do nao thi ung dung se ko lam gi ca
                }
            }

        } else if (requestCode == REQUEST_CODE_2) {
            switch (resultCode) {
                case RESULT_CODE: {
                    int kiemTra = data.getIntExtra("check",0);
                    if(kiemTra==1){
                        String text = data.getStringExtra("textToaDo");
                        LatLng position = data.getParcelableExtra("toaDo");
                        latB = position.latitude;
                        lngB = position.longitude;
                        //diaChiSau = data.getStringExtra("diachihientai");
                        ketThuc.setText(text);
                    } else if (kiemTra == 2){
                        String text = data.getStringExtra("textChon");
                        LatLng position = data.getParcelableExtra("toaDoChon");
                        latB = position.latitude;
                        lngB = position.longitude;
                        //diaChiSau = data.getStringExtra("diachidachon");
                        ketThuc.setText(text);
                    }
                }
                case RESULT_CODE1: {
                    //Neu ko co chon bat cu toa do nao thi ung dung se ko lam gi ca
                    break;
                }
            }
        }
    }

    //Ham hoan doi gia tri text cua 2 edit text
    private void swapEditText(EditText a,EditText b){

        String temp ;
        String chuoi1 = a.getText().toString();
        String chuoi2 = b.getText().toString();
        temp = chuoi1;
        chuoi1 = chuoi2;
        chuoi2 = temp;

        a.setText(chuoi1);
        b.setText(chuoi2);
    }
}
