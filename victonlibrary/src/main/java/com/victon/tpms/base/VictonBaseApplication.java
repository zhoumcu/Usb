package com.victon.tpms.base;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.umeng.analytics.MobclickAgent;
import com.victon.tpms.base.db.dao.DeviceDao;
import com.victon.tpms.base.module.main.activity.MainForServiceActivity;
import com.victon.tpms.common.usb.HeartService;
import com.victon.tpms.common.usb.UsbComService;
import com.victon.tpms.common.utils.Logger;
import com.victon.tpms.common.utils.SoundPlayUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by bob on 2015/1/30.
 */
public class VictonBaseApplication extends Application{

    public static VictonBaseApplication app;
    public static DeviceDao getDeviceDao() {
        return deviceDao;
    }
    private static DeviceDao deviceDao;
    private List<Activity> mList = new LinkedList<Activity>();
    public VictonBaseApplication() {
        app = this;
    }

    public static synchronized VictonBaseApplication getInstance() {
        if (app == null) {
            app = new VictonBaseApplication();
        }
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        deviceDao= new DeviceDao(this);
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
//        PushAgent mPushAgent = PushAgent.getInstance(this);
//        mPushAgent.enable();
//        String device_token = UmengRegistrar.getRegistrationId(this);
//        Logger.i("UmengRegistrar device token:"+device_token);
        SoundPlayUtils.init(this);
//        if(!VictonBaseApplication.getInstance().isServiceRunning(this)){
//            Intent intent = new Intent(this, UsbComService.class);
//            startService(intent);
//            Logger.e("UsbComService"+"start service!");
//            Intent intent1 = new Intent(this, HeartService.class);
//            startService(intent1);
//        }
    }

//    public UsbComService usbService;
//
//    private ServiceConnection mServiceConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
//            usbService = ((UsbComService.LocalBinder) iBinder).getService();
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName componentName) {
//            usbService = null;
//        }
//    };
    public void launchApp(Context context){
        Intent intent;
        if (!VictonBaseApplication.getInstance().isRunningForeground(context)){
            intent = new Intent(context, MainForServiceActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }else{
            intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    /**
     * 用来判断服务是否运行.
     * @param mContext
     * @param className 判断的服务名字
     * @return true 在运行 false 不在运行
     */
    public boolean isServiceRunning(Context mContext, String paramString) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager)
                mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList
                = activityManager.getRunningServices(40);
        if (!(serviceList.size()>0)) {
            return false;
        }
        if(paramString!=null){
            for (int i=0; i<serviceList.size(); i++) {
                if (serviceList.get(i).service.getClassName().toString().equals(paramString) == true) {
                    isRunning = true;
                    break;
                }
            }
        }else {
            for (int i=0; i<serviceList.size(); i++) {
                if (serviceList.get(i).service.getClassName().toString().equals(getPackageName()) == true) {
                    isRunning = true;
                    break;
                }
            }
        }
        return isRunning;
    }
    public boolean isRunningForeground (Context context) {
        String className = getPackageName();
        if (context == null || TextUtils.isEmpty(className)) {
            return false;
        }
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            if (className.equals(cpn.getClassName())) {
                return true;
            }
        }
        return false;
    }
    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }
    // add Activity
    public void addActivity(Activity activity) {
        mList.add(activity);
    }
    public void exit() {
        try {
            for (Activity activity : mList) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //System.exit(0);
        }
    }
}
