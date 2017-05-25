package com.victon.tpms.base.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.victon.tpms.R;
import com.victon.tpms.base.module.config.ConfigTablentDevice;
import com.victon.tpms.common.utils.SharedPreferences;
import com.victon.tpms.common.view.activity.BaseActivity;


/**
 * Created by Administrator on 2016/5/27.
 */
public class NotifyDialog extends BaseActivity implements View.OnClickListener {
    public static final String ACTION_BTN_STATE = "button_state";
    public static final String ACTION_BTN_NEXT = "button_next";
    public static final String BTN_STATE = "btn_state";
    public static final String ACTION_CHANGE_STATE = "action_change_state";
    TextView tvNotify;
    Button btnState;
    Button btnNext;
    ImageView imgIcon;
    Button btnFinish;
    LinearLayout lnFinish;
    LinearLayout btnNofinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_notify_dialog);
        initUi();
        String pairedOk = getIntent().getExtras().getString(ConfigTablentDevice.PAIRED_OK);
        boolean noneNext = getIntent().getExtras().getBoolean(ConfigTablentDevice.NONE_NEXT);
        btnState.setText(pairedOk);
        if (pairedOk.equals("完成")) {
            onFinish();
            tvNotify.setText("完成配置，请点击完成，进行下一个配置");
        } else {
            tvNotify.setText("配置超时，请点击重试按钮进行重新配置;如果多次配置均已无法配置，请点击跳过,进行配置下一个，" +
                    "之后在绑定功能中进行绑定即可");
        }
        SharedPreferences.getInstance().putBoolean("isAppOnForeground",false);
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    private void initUi() {
        tvNotify = (TextView)findViewById(R.id.tv_notify);
        btnState = (Button)findViewById(R.id.btn_state);
        btnNext = (Button)findViewById(R.id.btn_next);
        imgIcon = (ImageView)findViewById(R.id.img_icon);
        btnFinish = (Button)findViewById(R.id.btn_finish);
        lnFinish = (LinearLayout)findViewById(R.id.ln_finish);
        btnNofinish = (LinearLayout)findViewById(R.id.btn_nofinish);
        btnState.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnFinish.setOnClickListener(this);
    }

    private void onFinish() {
        lnFinish.setVisibility(View.VISIBLE);
        btnNofinish.setVisibility(View.GONE);
        imgIcon.setImageDrawable(getResources().getDrawable(R.mipmap.b_right));
    }

    private void sendResultForFrom(String action, int responseCode) {
        Intent intent = new Intent(action);
        intent.putExtra(BTN_STATE, responseCode);
        sendBroadcast(intent);
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            return false;
        }
        return super.onTouchEvent(event);
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_CHANGE_STATE);
        return intentFilter;
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            String action = intent.getAction();
            if (ACTION_CHANGE_STATE.equals(action)) {
                btnState.setText(intent.getExtras().getString(BTN_STATE));
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.btn_next) {
            sendResultForFrom(ACTION_BTN_NEXT, 101);
        } else if (i == R.id.btn_state) {
            if (btnState.getText().equals("完成")) {
                sendResultForFrom(ACTION_BTN_NEXT, 100);
            } else {
                sendResultForFrom(ACTION_BTN_STATE, 102);
            }
        }else if (i == R.id.ln_finish) {
            sendResultForFrom(ACTION_BTN_NEXT, 100);
        } else if (i == R.id.btn_nofinish) {
            sendResultForFrom(ACTION_BTN_NEXT, 100);
        }else if (i == R.id.btn_finish) {
            sendResultForFrom(ACTION_BTN_NEXT, 100);
        }
    }
}
