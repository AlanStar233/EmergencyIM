package com.alanstar.emergencyim;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;

import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity implements BottomNavigationBar.OnTabSelectedListener {

    //组件声明
    FrameLayout fl_pager;
    BottomNavigationBar bottom_nav_bar;

    //Value
    private long exitTime = 0;      //退出事件时间
    private Fragment SingleChat_Fragment;
    private Fragment GroupChat_Fragment;
    private Fragment Settings_Fragment;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 组件注册
        initAssembly();

        // 蓝牙是否支持
        Toast.makeText(getApplicationContext(), "蓝牙支持:" + isBlueToothSupport(), Toast.LENGTH_SHORT).show();

        // 监听Navbar事件
        bottom_nav_bar.setTabSelectedListener(this)
                .setMode(BottomNavigationBar.MODE_DEFAULT)
                .setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_DEFAULT)
                .setActiveColor(R.color.Tianyi_Blue)    // 天依蓝 (｀・ω・´)
                .setInActiveColor(R.color.Light_Gray)   // 浅灰
                .setBarBackgroundColor(R.color.white)   // 纯白
                // 创建 bar 内元素
                .addItem(new BottomNavigationItem(R.drawable.single_info, "消息"))
                .addItem(new BottomNavigationItem(R.drawable.group_info, "群组"))
                .addItem(new BottomNavigationItem(R.drawable.settings, "设置"))
                .setFirstSelectedPosition(0)        // 第0个
                .initialise();

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

    // TODO: 监听Navbar事件
    @Override
    public void onTabSelected(int position) {       // Tab 选中触发事件
        FragmentManager fragmentManager = getSupportFragmentManager();
        // 开启事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        switch (position) {
            case 0:
                if (SingleChat_Fragment == null) {
                    SingleChat_Fragment = new SingleChat_Fragment();
                }
                transaction.replace(R.id.fl_pager, SingleChat_Fragment);
                break;
            case 1:
                if (GroupChat_Fragment == null) {
                    GroupChat_Fragment = new GroupChat_Fragment();
                }
                transaction.replace(R.id.fl_pager, GroupChat_Fragment);
                break;
            case 2:
                if (Settings_Fragment == null) {
                    Settings_Fragment = new Settings_Fragment();
                }
                transaction.replace(R.id.fl_pager, Settings_Fragment);
                break;
        }
        transaction.commit();       //事务提交
    }

    @Override
    public void onTabUnselected(int position) {

    }

    @Override
    public void onTabReselected(int position) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        // 开启事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        switch (position) {
            case 0:
                if (SingleChat_Fragment == null) {
                    SingleChat_Fragment = new SingleChat_Fragment();
                }
                transaction.replace(R.id.fl_pager, SingleChat_Fragment);
                break;
            case 1:
                if (GroupChat_Fragment == null) {
                    GroupChat_Fragment = new GroupChat_Fragment();
                }
                transaction.replace(R.id.fl_pager, GroupChat_Fragment);
                break;
            case 2:
                if (Settings_Fragment == null) {
                    Settings_Fragment = new Settings_Fragment();
                }
                transaction.replace(R.id.fl_pager, Settings_Fragment);
                break;
        }
        transaction.commit();       //事务提交
    }

}