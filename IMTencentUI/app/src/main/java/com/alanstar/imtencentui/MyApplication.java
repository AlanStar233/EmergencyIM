package com.alanstar.imtencentui;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.multidex.BuildConfig;
import androidx.multidex.MultiDex;

import com.alanstar.imtencentui.bean.UserInfo;
import com.alanstar.imtencentui.login.LoginForDevActivity;
import com.alanstar.imtencentui.main.MainActivity;
import com.alanstar.imtencentui.signature.GenerateTestUserSig;
import com.alanstar.imtencentui.utils.DemoLog;
import com.alanstar.imtencentui.utils.PrivateConstants;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMValueCallback;
import com.tencent.liteav.trtccalling.model.util.BrandUtil;
import com.tencent.qcloud.tuicore.TUILogin;
import com.tencent.qcloud.tuicore.TUIThemeManager;
import com.tencent.qcloud.tuicore.interfaces.TUILoginListener;
import com.tencent.qcloud.tuicore.util.ErrorMessageConverter;
import com.tencent.qcloud.tuicore.util.PermissionRequester;
import com.tencent.qcloud.tuicore.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MyApplication extends Application {
    private static final String TAG = MyApplication.class.getSimpleName();

    private static MyApplication instance;

    public static MyApplication instance() {
        return instance;
    }

    private int sdkAppId = 0;

    @Override
    public void onCreate() {
        DemoLog.i(TAG, "onCreate");
        super.onCreate();

        if (isMainProcess()) {
            instance = this;
            MultiDex.install(this);

            // 添加 Demo 主题
            TUIThemeManager.addLightTheme(R.style.DemoLightTheme);
            TUIThemeManager.addLivelyTheme(R.style.DemoLivelyTheme);
            TUIThemeManager.addSeriousTheme(R.style.DemoSeriousTheme);

            registerActivityLifecycleCallbacks(new StatisticActivityLifecycleCallback());
            initLoginStatusListener();
            setPermissionRequestContent();
        }
    }

    private void initBugly() {
        // bugly上报
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(getApplicationContext());
        strategy.setAppVersion(BuildConfig.VERSION_NAME);
        strategy.setDeviceModel(BrandUtil.getBuildModel());
        CrashReport.initCrashReport(getApplicationContext(), PrivateConstants.BUGLY_APPID, true, strategy);
    }

    private void initBuildInformation() {
        try {
            JSONObject buildInfoJson = new JSONObject();
            buildInfoJson.put("buildBrand", BrandUtil.getBuildBrand());
            buildInfoJson.put("buildManufacturer", BrandUtil.getBuildManufacturer());
            buildInfoJson.put("buildModel", BrandUtil.getBuildModel());
            buildInfoJson.put("buildVersionRelease", BrandUtil.getBuildVersionRelease());
            buildInfoJson.put("buildVersionSDKInt", BrandUtil.getBuildVersionSDKInt());
            // 工信部要求 app 在运行期间只能获取一次设备信息。因此 app 获取设备信息设置给 SDK 后，SDK 使用该值并且不再调用系统接口。
            V2TIMManager.getInstance().callExperimentalAPI("setBuildInfo", buildInfoJson.toString(), new V2TIMValueCallback<Object>() {
                @Override
                public void onSuccess(Object o) {
                    DemoLog.i(TAG, "setBuildInfo success");
                }

                @Override
                public void onError(int code, String desc) {
                    DemoLog.i(TAG, "setBuildInfo code:" + code + " desc:" + ErrorMessageConverter.convertIMError(code, desc));
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void init(int imsdkAppId) {
        initBuildInformation();
        initBugly();
        if (imsdkAppId != 0) {
            sdkAppId = imsdkAppId;
        } else {
            sdkAppId = GenerateTestUserSig.SDKAPPID;
        }
    }

    public int getSdkAppId() { return sdkAppId; }

    private void initLoginStatusListener() {
        TUILogin.addLoginListener(loginStatusListener);
    }

    private final TUILoginListener loginStatusListener = new TUILoginListener() {
        @Override
        public void onKickedOffline() {
            ToastUtil.toastLongMessage(MyApplication.instance().getString(R.string.repeat_login_tip));
            logout();
        }

        @Override
        public void onUserSigExpired() {
            ToastUtil.toastLongMessage(MyApplication.instance().getString(R.string.expired_login_tip));
            logout();
        }
    };

    public void logout() {
        DemoLog.i(TAG, "logout");
        UserInfo.getInstance().cleanUserInfo();

        // TODO: 这里调的测试用activity, 用户名可为任意非空字符, 缺少密码, 需要 SDKAPPID 与 PRIVATEKEY 正确配置
        Intent intent = new Intent(this, LoginForDevActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("LOGOUT", true);
        startActivity(intent);

        MainActivity.finishMainActivity();
    }

    private void setPermissionRequestContent() {
        ApplicationInfo applicationInfo = this.getApplicationInfo();
        Resources resources = this.getResources();
        String appName = resources.getString(applicationInfo.labelRes);

        PermissionRequester.PermissionRequestContent microphoneContent = new PermissionRequester.PermissionRequestContent();
        microphoneContent.setReasonTitle(getString(R.string.demo_permission_mic_reason_title, appName));
        microphoneContent.setReason(getString(R.string.demo_permission_mic_reason));
        microphoneContent.setIconResId(R.drawable.demo_permission_icon_mic);
        microphoneContent.setDeniedAlert(getString(R.string.demo_permission_mic_dialog_alert, appName));
        PermissionRequester.setPermissionRequestContent(PermissionRequester.PermissionConstants.MICROPHONE, microphoneContent);

        PermissionRequester.PermissionRequestContent cameraContent = new PermissionRequester.PermissionRequestContent();
        cameraContent.setReasonTitle(getString(R.string.demo_permission_camera_reason_title, appName));
        cameraContent.setReason(getString(R.string.demo_permission_camera_reason));
        cameraContent.setIconResId(R.drawable.demo_permission_icon_camera);
        cameraContent.setDeniedAlert(getString(R.string.demo_permission_camera_dialog_alert, appName));
        PermissionRequester.setPermissionRequestContent(PermissionRequester.PermissionConstants.CAMERA, cameraContent);
    }

    private boolean isMainProcess() {
        ActivityManager am = ((ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE));
        String mainProcessName = this.getPackageName();
        int myPid = android.os.Process.myPid();

        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        if (processInfos == null) {
            Log.i(TAG, "isMainProcess get getRunningAppProcesses null");
            List<ActivityManager.RunningServiceInfo> processList = am.getRunningServices(Integer.MAX_VALUE);
            if (processList == null) {
                Log.i(TAG, "isMainProcess get getRunningServices null");
                return false;
            }
            for (ActivityManager.RunningServiceInfo rsi : processList) {
                if (rsi.pid == myPid && mainProcessName.equals(rsi.service.getPackageName())) {
                    return true;
                }
            }
            return false;
        }

        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    private class StatisticActivityLifecycleCallback implements ActivityLifecycleCallbacks {
        @Override
        public void onActivityCreated(Activity activity, Bundle bundle) {
            DemoLog.i(TAG, "onActivityCreated bundle: " + bundle);
            if (bundle != null) { // 若bundle不为空则程序异常结束
                // 重启整个程序
                Intent intent = new Intent(activity, SplashActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }

        @Override
        public void onActivityStarted(@NonNull Activity activity) {

        }

        @Override
        public void onActivityResumed(@NonNull Activity activity) {

        }

        @Override
        public void onActivityPaused(@NonNull Activity activity) {

        }

        @Override
        public void onActivityStopped(@NonNull Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(@NonNull Activity activity) {

        }
    }
}
