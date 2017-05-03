package com.victon.tpms.common.view.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;

import com.umeng.analytics.MobclickAgent;
import com.victon.tpms.common.utils.SharedPreferences;

/**
 * Created by Administrator on 2016/7/18.
 */
public class BaseActionBarActivity extends ActionBarActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences.getInstance().putBoolean("isAppOnForeground",true);
    }

    public void onResume() {
        super.onResume();
        SharedPreferences.getInstance().putBoolean("isAppOnForeground",false);
        MobclickAgent.onPageStart("SplashScreen"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。"SplashScreen"为页面名称，可自定义)
        MobclickAgent.onResume(this);          //统计时长
    }
    public void onPause() {
        super.onPause();
        SharedPreferences.getInstance().putBoolean("isAppOnForeground",true);
        MobclickAgent.onPageEnd("SplashScreen"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息。"SplashScreen"为页面名称，可自定义
        MobclickAgent.onPause(this);
    }
}
