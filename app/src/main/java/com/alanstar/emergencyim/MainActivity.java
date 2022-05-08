package com.alanstar.emergencyim;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    //组件声明
    FrameLayout fl_pager;
    BottomNavigationView bottom_nav_bar;

    //Value
    private long exitTime = 0;      //退出事件时间

    // TODO: 两次返回监听事件
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {        //当按下系统返回键 && 手指第一次触碰屏幕时
            if ((System.currentTimeMillis() - exitTime) > 2000)         //系统级时间转换 - 退出时间 > 2000ms
            {
                Toast.makeText(getApplicationContext(), "再按一次返回桌面", Toast.LENGTH_SHORT).show();     //Toast 消息
                exitTime = System.currentTimeMillis();      //exitTime 接入系统时间转换
            } else {
                Intent i = new Intent(Intent.ACTION_MAIN);      //创建Intent
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);      //创建新堆栈任务标志
                i.addCategory(Intent.CATEGORY_HOME);            //指定桌面

                startActivity(i);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    // TODO: onCreate主事件
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 组件注册
        initAssembly();

        // Navigation
        setDefaultFragment();
        bottom_nav_bar.setOnNavigationItemSelectedListener(this);

        // 蓝牙是否支持
        Toast.makeText(getApplicationContext(), "蓝牙支持:" + isBlueToothSupport(), Toast.LENGTH_SHORT).show();

    }

// TODO: function

    // TODO: 组件注册
    public void initAssembly() {
        fl_pager = findViewById(R.id.fl_pager);
        bottom_nav_bar = findViewById(R.id.bottom_nav_bar);
    }

    // TODO: 蓝牙支持检测
    public boolean isBlueToothSupport() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();   //实例化蓝牙
        if (bluetoothAdapter != null) {
            return true;       //不支持蓝牙
        } else {
            return false;       //支持蓝牙
        }
    }

    // Menu注册
    //TODO: 添加 Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    //TODO: 反射方式给 Menu 赋予图标
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (menu != null) {
            try {
                Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                method.setAccessible(true);
                method.invoke(menu, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    // TODO: Menu被选中后执行事件   (待开发)
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.m_start_server:
                Toast.makeText(this, "启用主机服务", Toast.LENGTH_SHORT).show();      //占位用
            case R.id.m_wifi_config:
                Toast.makeText(this, "应急接入点设置", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    // TODO: NaviBar 选择响应事件
    @SuppressLint("NonConstantResourceId")
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        switch (item.getItemId()) {
            case R.id.navi_SingleChat:
                SingleChat_Fragment singleChat_fragment = new SingleChat_Fragment();
                transaction.replace(R.id.fl_pager, singleChat_fragment);
                break;
            case R.id.navi_GroupChat:
                GroupChat_Fragment groupChat_fragment = new GroupChat_Fragment();
                transaction.replace(R.id.fl_pager, groupChat_fragment);
                break;
            case R.id.navi_Settings:
                Settings_Fragment settings_fragment = new Settings_Fragment();
                transaction.replace(R.id.fl_pager, settings_fragment);
                break;
        }
        transaction.commit();
        return true;
    }

    //TODO: 默认Fragment
    private void setDefaultFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        SingleChat_Fragment singleChat_fragment = new SingleChat_Fragment();
        transaction.replace(R.id.fl_pager, singleChat_fragment);
        transaction.commit();
    }
}