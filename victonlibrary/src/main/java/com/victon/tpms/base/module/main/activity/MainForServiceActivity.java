package com.victon.tpms.base.module.main.activity;

import android.annotation.TargetApi;
import android.content.Intent;
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

import com.victon.tpms.R;
import com.victon.tpms.base.VictonBaseApplication;
import com.victon.tpms.base.db.entity.Device;
import com.victon.tpms.base.module.config.ConfigTablentDevice;
import com.victon.tpms.base.module.main.fragment.BundTalentDeviceFragment;
import com.victon.tpms.base.module.main.fragment.ChangeTablentDeviceFragment;
import com.victon.tpms.base.module.main.fragment.MainFragment;
import com.victon.tpms.base.module.setting.PersonTabletSetting;
import com.victon.tpms.base.widget.PopupMeumWindow;
import com.victon.tpms.common.utils.CommonUtils;
import com.victon.tpms.common.utils.Constants;
import com.victon.tpms.common.utils.Logger;
import com.victon.tpms.common.utils.SharedPreferences;
import com.victon.tpms.common.view.activity.BaseActionBarActivity;
import com.victon.tpms.entity.ManageDevice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


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
        try{
            findViewById(R.id.back).setOnClickListener(this);
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
        List<Device> deviceDetails = VictonBaseApplication.getDeviceDao().get(Constants.MY_CAR_DEVICE);
        if(deviceDetails.size()<=0) return;
        manageDevice.setLeftFDevice(deviceDetails.get(0).getLeft_FD());
        manageDevice.setRightFDevice(deviceDetails.get(0).getRight_FD());
        manageDevice.setLeftBDevice(deviceDetails.get(0).getLeft_BD());
        manageDevice.setRightBDevice(deviceDetails.get(0).getRight_BD());
        Logger.d(TAG,deviceDetails.get(0).getLeft_FD()+":"+deviceDetails.get(0).getRight_FD()
                +":"+deviceDetails.get(0).getLeft_BD()+":"+deviceDetails.get(0).getRight_BD());
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
                if (SharedPreferences.getInstance().getBoolean(Constants.FIRST_CONFIG, false)) {
                    switchFragment(2);
                } else {
                    Intent intent = new Intent();
                    intent.setClass(mContext, ConfigTablentDevice.class);
                    startActivity(intent);
                }
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
            finish();
        }else {
            switchFragment(0);
        }
    }
}
