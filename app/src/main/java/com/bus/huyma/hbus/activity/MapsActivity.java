package com.bus.huyma.hbus.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.bus.huyma.hbus.R;
import com.bus.huyma.hbus.fragments.OneFragment;
import com.bus.huyma.hbus.fragments.ThreeFragment;
import com.bus.huyma.hbus.fragments.TwoFragment;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int[] tabIcons={R.drawable.mapmarkerradius,
            R.drawable.bus,R.drawable.magnify};
    public int so=1;
    public MapsActivity(){}

    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
//        viewPager.setOnPageChangeListener(this);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setTitle(getTitleFromPosition(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
    }

    private void setupTabIcons(){
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
    }

    private void setupViewPager (ViewPager viewPager){
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new OneFragment(),"ONE");
        adapter.addFragment(new TwoFragment(), "TWO");
        adapter.addFragment(new ThreeFragment(), "THREE");
        viewPager.setAdapter(adapter);
    }

    //Override hàm này để lắng nghe sự kiện , thiết lập tiêu đề trên action bar mỗi khi chuyển các tab
//    @Override
//    public void onPageSelected(int position) {
//        setTitle(getTitleFromPosition(position));
//    }

    private String getTitleFromPosition(int vitri){
        String title = "";
        switch (vitri){
            case 0: title = "Trạm xe quanh đây";
                break;
            case 1: title = "Tìm buýt theo mã số";
                break;
            case 2: title = "Tìm buýt theo từ khóa";
                break;
            default: break;
        }
        return title;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    static class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager){
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title){
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position){
            //return mFragmentTitleList.get(position); //Tra ve tab co chua chữ
            return null; //Tra ve tab chi chua hinh
        }
    }

    //Tao menu option tren thanh toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.itemThongTin:{
                Dialog dialog = new Dialog(this);
                //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);//Tat thanh tieu de cua dialog
                dialog.setContentView(R.layout.custom_dialog_thongtin);//Thiet lap giao dien
                dialog.onBackPressed();//Lang nghe su kien quay lai man hinh chin, khi nhan nut back
                dialog.show();
                return true;
            }

            case R.id.itemDanhGia:{

            }

            case android.R.id.home: {
                AlertDialog.Builder aB = new AlertDialog.Builder(this);
                aB.setTitle("Thoát ứng dụng !!");
                aB.setMessage("Bạn có muốn thoát khỏi ứng dụng này ??");
                aB.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        //Thoat ung dung, va destroy activity introduce
                        //Bang cach gui du lieu ve cho introduce, va introduce se goi OnDestroy
                        Intent myIntent = getIntent();
                        setResult(114, myIntent);

                        finish();

                    }
                });

                aB.setNegativeButton("Hủy bỏ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                aB.create();
                aB.show();
                return true;
            }

            default :return super.onOptionsItemSelected(item);
        }
    }


    //Thoat khoi ung dung khi nhan nut Back
    @Override
    public void onBackPressed(){
        AlertDialog.Builder aB = new AlertDialog.Builder(MapsActivity.this);
        aB.setTitle("Thoát ứng dụng !!");
        aB.setMessage("Bạn có muốn thoát khỏi ứng dụng này ??");
        aB.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                //Thoat ung dung, va destroy activity introduce
                //Bang cach gui du lieu ve cho introduce, va introduce se goi OnDestroy
                Intent myIntent = getIntent();
                setResult(115, myIntent);

                finish();
            }
        });

        aB.setNegativeButton("Hủy bỏ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        aB.create();
        aB.show();
    }
}
