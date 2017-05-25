package com.victon.tpms.base.module.main.activity;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.InflateException;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.victon.tpms.R;
import com.victon.tpms.base.VictonBaseApplication;
import com.victon.tpms.base.module.main.fragment.BundTalentDeviceFragment;
import com.victon.tpms.base.module.main.fragment.ChangeTablentDeviceFragment;
import com.victon.tpms.base.module.main.fragment.MainFragment;
import com.victon.tpms.base.module.setting.PersonTabletSetting;
import com.victon.tpms.base.widget.PopupMeumWindow;
import com.victon.tpms.common.usb.UsbComService;
import com.victon.tpms.common.usb.UsbData;
import com.victon.tpms.common.utils.CommonUtils;
import com.victon.tpms.common.utils.Constants;
import com.victon.tpms.common.utils.DigitalTrans;
import com.victon.tpms.common.utils.Logger;
import com.victon.tpms.common.utils.SharedPreferences;
import com.victon.tpms.common.view.activity.BaseActionBarActivity;
import com.victon.tpms.entity.ManageDevice;

import java.util.ArrayList;
import java.util.Arrays;


@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class MainForServiceActivity extends BaseActionBarActivity implements View.OnClickListener {

    private final static String TAG = MainForServiceActivity.class.getSimpleName();
    public static MainForServiceActivity mContext;
    private FragmentManager fragmentManager;
    private int currIndex = 0;
    private ArrayList<String> fragmentTags;
    private PopupMeumWindow menuWindow;
    public FrameLayout background;
    public boolean isQuiting;
    private ManageDevice manageDevice;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 初始化Colorful
        changeThemeWithColorful();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        try {
            setContentView(R.layout.de_activity_main);
        }catch (InflateException e) { }
        mContext =MainForServiceActivity.this;
        initUI();
        initConfig();
        initData();
        showFragment();
    }

    /**
     * 切换主题
     */
    private void changeThemeWithColorful() {
        if (!SharedPreferences.getInstance().getBoolean(Constants.DAY_NIGHT,false)) {
            setTheme(R.style.DayTheme);
        } else {
            setTheme(R.style.NightTheme);
        }
    }

    private void initUI() {
        currIndex = 0;
        fragmentTags = new ArrayList<>(Arrays.asList("HomeFragment", "ImFragment", "InterestFragment", "MemberFragment"));
        background = (FrameLayout)findViewById(R.id.bg_ground);
        ImageView back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(this);
        back.setVisibility(View.GONE);
        try{
            findViewById(R.id.img_set).setOnClickListener(this);
        }catch (NullPointerException e) { }
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    private void initData() {
        if(manageDevice==null)
        manageDevice = new ManageDevice();
//        manageDevice.setLeftFDevice("00");
//        manageDevice.setRightFDevice("01");
//        manageDevice.setLeftBDevice("02");
//        manageDevice.setRightBDevice("03");
        //刷新数据库
//        List<Device> deviceDetails = VictonBaseApplication.getDeviceDao().get(Constants.MY_CAR_DEVICE);
//        if(deviceDetails.size()<=0) return;
//        manageDevice.setLeftFDevice(deviceDetails.get(0).getLeft_FD());
//        manageDevice.setRightFDevice(deviceDetails.get(0).getRight_FD());
//        manageDevice.setLeftBDevice(deviceDetails.get(0).getLeft_BD());
//        manageDevice.setRightBDevice(deviceDetails.get(0).getRight_BD());
//        Logger.d(TAG,deviceDetails.get(0).getLeft_FD()+":"+deviceDetails.get(0).getRight_FD()
//                +":"+deviceDetails.get(0).getLeft_BD()+":"+deviceDetails.get(0).getRight_BD());
    }
    protected void initConfig() {
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }
    /** Handles various events fired by the Service.
     * ACTION_GATT_CONNECTED: connected to a GATT server.
     * ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
     * ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
     * ACTION_DATA_AVAILABLE: received data from the device.
     * This can be a result of read or notification operations.
     **/
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            String action = intent.getAction();
            if (UsbComService.SCAN_FOR_RESULT.equals(action)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        UsbData device = (UsbData) intent.getSerializableExtra("SCAN_RECORD");
                        if(device.getCommand()==(byte) 0x06){
                            Logger.i(TAG,"从USB上接收到广播0x06：" +"命令类型"+ DigitalTrans.byteToString(device.getCommand())+
                                    "内容："+DigitalTrans.Bytes2HexString(device.getData()));
                            for (int i=0;i<device.getData().length;i++){
                                Logger.i(TAG,"从USB上接收到广播0x06："+DigitalTrans.byteToString(device.getData()[i]));
                            }
                            if(device.getData()[0]!=(byte)0xFF||device.getData()[1]!=(byte)0xFF){
                                manageDevice.setLeftFDevice("00");
                                VictonBaseApplication.getDeviceDao().update(0, Constants.MY_CAR_DEVICE,"00");
                            }else {
                                manageDevice.setLeftFDevice("FF");
                                VictonBaseApplication.getDeviceDao().update(0, Constants.MY_CAR_DEVICE,"FF");
                            }
                            if(device.getData()[2]!=(byte)0xFF||device.getData()[3]!=(byte)0xFF){
                                manageDevice.setRightFDevice("01");
                                VictonBaseApplication.getDeviceDao().update(1, Constants.MY_CAR_DEVICE,"01");
                            }else {
                                manageDevice.setRightFDevice("FF");
                                VictonBaseApplication.getDeviceDao().update(1, Constants.MY_CAR_DEVICE,"FF");
                            }
                            if(device.getData()[4]!=(byte)0xFF||device.getData()[5]!=(byte)0xFF){
                                manageDevice.setLeftBDevice("02");
                                VictonBaseApplication.getDeviceDao().update(2, Constants.MY_CAR_DEVICE,"02");
                            }else {
                                manageDevice.setLeftBDevice("FF");
                                VictonBaseApplication.getDeviceDao().update(2, Constants.MY_CAR_DEVICE,"FF");
                            }
                            if(device.getData()[6]!=(byte)0xFF||device.getData()[7]!=(byte)0xFF){
                                manageDevice.setRightBDevice("03");
                                VictonBaseApplication.getDeviceDao().update(3, Constants.MY_CAR_DEVICE,"03");
                            }else {
                                manageDevice.setRightBDevice("FF");
                                VictonBaseApplication.getDeviceDao().update(3, Constants.MY_CAR_DEVICE,"FF");
                            }
                        }
                    }
                });
            }
        }
    };
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UsbComService.SCAN_FOR_RESULT);
        return intentFilter;
    }
    private void showFragment() {
        if(fragmentManager==null)
            fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = fragmentManager.findFragmentByTag(fragmentTags.get(currIndex));
        if(fragment == null) {
            fragment = instantFragment(currIndex);
        }
        for (int i = 1; i < fragmentTags.size(); i++) {
            Fragment f = fragmentManager.findFragmentByTag(fragmentTags.get(i));
            if(f != null && f.isAdded()) {
                fragmentTransaction.remove(f);
                Logger.e("fragment is remove"+fragmentTags.get(i));
            }
        }
        if (fragment.isAdded()) {
            fragmentTransaction.show(fragment);
        } else {
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.add(R.id.fragment_container, fragment, fragmentTags.get(currIndex));
        }
        fragmentTransaction.commitAllowingStateLoss();
        fragmentManager.executePendingTransactions();
    }

    private Fragment instantFragment(int currIndex) {
        switch (currIndex) {
            case 0: return MainFragment.newInstance(manageDevice);
            case 1: return ChangeTablentDeviceFragment.newInstance(manageDevice);
            case 2: return BundTalentDeviceFragment.newInstance(manageDevice);
            default: return null;
        }
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.back) {
            exit();
        } else if (i == R.id.img_set) {
            Logger.d(TAG,CommonUtils.getStatusBarHeight(mContext)+"");
            menuWindow = new PopupMeumWindow(mContext, itemsOnClick);
            //显示窗口
            menuWindow.showAtLocation(mContext.findViewById(R.id.img_set),
                    Gravity.TOP | Gravity.RIGHT, 0, CommonUtils.getStatusBarHeight(mContext)+50); //设置layout在PopupWindow中显示的位置
        }
    }
    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener(){

        public void onClick(View v) {
            menuWindow.dismiss();
            int i = v.getId();
            if (i == R.id.btn_pick_photo) {
//                if (SharedPreferences.getInstance().getBoolean(Constants.FIRST_CONFIG, false)) {
                    switchFragment(2);
//                } else {
//                    Intent intent = new Intent();
//                    intent.setClass(mContext, ConfigTablentDevice.class);
//                    startActivity(intent);
//                }
            } else if (i == R.id.btn_cancel) {
                switchFragment(1);
            } else if (i == R.id.btn_system_setting) {
                Intent intent = new Intent();
                intent.setClass(mContext, PersonTabletSetting.class);
                startActivity(intent);
            } else {

            }
        }
    };

    private void switchFragment(int index) {
        currIndex = index;
        showFragment();
        Logger.e("切换："+index);
    }

    /**
     * 监听Back键按下事件,方法2:
     * 注意:
     * 返回值表示:是否能完全处理该事件
     * 在此处返回false,所以会继续传播该事件.
     * 在具体项目中此处的返回值视情况而定.
     */

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
        Logger.i("MainActivity closed!!!");
    }

    /**
     * 检测四个模块是否断开，彻底断开后退出
     */
    private void exit() {
        if(currIndex==0) {
            isQuiting = true;
//            finish();
            moveTaskToBack(true);// 点击菜单键即转入后台，vivo X6Plus Android5.1也适用
        }else {
            switchFragment(0);
        }
    }
}
