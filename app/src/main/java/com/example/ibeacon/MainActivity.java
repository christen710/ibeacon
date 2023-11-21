package com.example.ibeacon;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ViewPager viewPager;
    Button ibeaconbutton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        ibeaconbutton = (Button) findViewById(R.id.ibeacon);
        bottomNavigationView.setOnNavigationItemSelectedListener((item) -> {
            switch (item.getItemId()) {
                case R.id.nav1:
                    viewPager.setCurrentItem(0);
                    break;
                case R.id.nav2:
                    viewPager.setCurrentItem(1);
                    break;
                case R.id.nav3:
                    viewPager.setCurrentItem(2);
                    break;
                case R.id.nav4:
                    viewPager.setCurrentItem(3);
                    break;
            }
            return true;
        });
        setViewPager();
    }
    private void setViewPager(){
        compassFragment Compass = new compassFragment();
        homeFragment Home = new homeFragment();
        settingFragment Setting = new settingFragment();
        userFragment User = new userFragment();
        List<Fragment> fragmentList = new ArrayList<Fragment>();
        fragmentList.add(Compass);
        fragmentList.add(Home);
        fragmentList.add(Setting);
        fragmentList.add(User);
        ViewPagerFragmentAdapter myFragmentAdapter = new ViewPagerFragmentAdapter(getSupportFragmentManager(), fragmentList);
        viewPager.setAdapter(myFragmentAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 設置要用哪個menu檔做為選單
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        // 取得點選項目的id
//        int id = item.getItemId();
//        // 依照id判斷點了哪個項目並做相應事件
//        if (id == R.id.chinese) {
//            Toast.makeText(this, "繁體中文", Toast.LENGTH_SHORT).show();
//            return true;
//        }
//        else if (id == R.id.english) {
//            Toast.makeText(this, "English", Toast.LENGTH_SHORT).show();
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

}