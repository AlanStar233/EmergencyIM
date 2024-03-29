package com.alanstar.imtencentui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.alanstar.imtencentui.bean.UserInfo;
import com.alanstar.imtencentui.login.LoginForDevActivity;
import com.alanstar.imtencentui.main.MainActivity;
import com.alanstar.imtencentui.utils.DemoLog;
import com.tencent.qcloud.tuicore.TUILogin;
import com.tencent.qcloud.tuicore.component.activities.BaseLightActivity;
import com.tencent.qcloud.tuicore.interfaces.TUICallback;
import com.tencent.qcloud.tuicore.util.ToastUtil;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends BaseLightActivity {

    private static final String TAG = SplashActivity.class.getSimpleName();

    @SuppressLint("ObsoleteSdkInt")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
        }

        handleData();
    }

    private void handleData() {
        UserInfo userInfo = UserInfo.getInstance();
        if (userInfo != null && userInfo.isAutoLogin()) {
            MyApplication.instance().init(0);
            login();
        } else {
            startLogin();
        }
    }

    private void login() {
        UserInfo userInfo = UserInfo.getInstance();
        TUILogin.login(MyApplication.instance(), MyApplication.instance().getSdkAppId(), userInfo.getUserId(), userInfo.getUserSig(), new TUICallback() {
            @Override
            public void onError(final int code, final String desc) {
                runOnUiThread(() -> {
                    ToastUtil.toastLongMessage(getString(R.string.failed_login_tip) + ", errCode = " + code + ", errInfo = " + desc);
                    startLogin();
                });
                DemoLog.i(TAG, "imLogin errorCode = " + code + ", errorInfo = " + desc);
            }

            @Override
            public void onSuccess() {
                startMain();
            }
        });
    }

    private void startLogin() {
        Intent intent = new Intent(SplashActivity.this, LoginForDevActivity.class);
        intent.putExtras(getIntent());
        startActivity(intent);
        finish();
    }

    private void startMain() {
        DemoLog.i(TAG, "MainActivity" );

        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        intent.putExtras(getIntent());
        intent.setData(getIntent().getData());
        startActivity(intent);
        finish();
    }
}