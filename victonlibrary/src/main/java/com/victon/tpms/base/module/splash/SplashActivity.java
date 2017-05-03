package com.victon.tpms.base.module.splash;

import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.victon.tpms.R;
import com.victon.tpms.base.module.main.activity.MainForServiceActivity;
import com.victon.tpms.common.view.activity.BaseFragmentActivity;
import com.victon.tpms.common.utils.Constants;
import com.victon.tpms.common.utils.SharedPreferences;
import com.victon.tpms.common.utils.SoundPlayUtils;

/**
 * Created by tiansj on 15/7/29.
 */
public class SplashActivity extends BaseFragmentActivity {

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_splash);
        imageView = (ImageView)findViewById(R.id.imageView);
        SoundPlayUtils.play(1);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initActivity();
            }
        }, 4000);
    }

    private void initActivity() {
        boolean firstTimeUse = SharedPreferences.getInstance().getBoolean(Constants.FIRST_CONFIG, false);
        if (firstTimeUse) {
            this.goActivity(MainForServiceActivity.class,null);
            finish();
        } else {
            this.goActivity(MainForServiceActivity.class,null);
            finish();
        }
    }
}
