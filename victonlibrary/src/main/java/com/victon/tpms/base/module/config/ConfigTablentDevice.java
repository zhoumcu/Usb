package com.victon.tpms.base.module.config;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.victon.tpms.R;
import com.victon.tpms.base.VictonBaseApplication;
import com.victon.tpms.base.db.dao.UserDao;
import com.victon.tpms.base.db.entity.Device;
import com.victon.tpms.base.db.entity.User;
import com.victon.tpms.base.widget.LoadingDialog;
import com.victon.tpms.base.widget.NotifyDialog;
import com.victon.tpms.common.usb.UsbComService;
import com.victon.tpms.common.usb.UsbData;
import com.victon.tpms.common.utils.Constants;
import com.victon.tpms.common.utils.DigitalTrans;
import com.victon.tpms.common.utils.Logger;
import com.victon.tpms.common.utils.SharedPreferences;
import com.victon.tpms.common.view.activity.BaseActionBarActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sid-fu on 2016/5/16.
 */
public class ConfigTablentDevice extends BaseActionBarActivity implements View.OnClickListener{
    private final static String TAG = ConfigTablentDevice.class.getSimpleName();
    private static final int REQUEST_ENABLE_BT = 1;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 1000000;
    private static final int NOTIFY_REQUEST_CODE = 2;
    public static final String PAIRED_OK = "paired_ok";
    public static final String NONE_NEXT = "nono_next";
    private boolean mScanning;
    private List<UsbData> mDeviceList = new ArrayList<>();
    private int ScanTimeOut = 10000;
    public static int leftF = 0;
    public static int rightF = 1;
    public static int leftB = 2;
    public static int rightB =3;
    public static int none =5;

    private final int maxLenght = -100;
    private TextView tv_note_left_from;
    private TextView tv_note_right_from;
    private TextView tv_note_left_back;
    private TextView tv_note_right_back;
    private Device deviceDate;
    private LoadingDialog loadDialog;
    private boolean isConneting = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_config);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
         /*显示App icon左侧的back键*/
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        initConfig();
        initUI();
       registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    private void initConfig() {
        User user = new UserDao(this).get(1);
        User u = new User();
        u.setName("test_user");
        new UserDao(this).add(u);
        deviceDate = new Device();
        deviceDate.setId(Constants.MYSQL_DEVICE_ID);
        deviceDate.setDeviceName(Constants.MY_CAR_DEVICE);
        deviceDate.setUser(user);
    }
    private void initUI() {
        loadDialog = new LoadingDialog(this);
        loadDialog.setBackgroundColor();
        tv_note_left_from = (TextView) findViewById(R.id.tv_note_left_from);
        tv_note_right_from = (TextView) findViewById(R.id.tv_note_right_from);
        tv_note_left_back = (TextView) findViewById(R.id.tv_note_left_back);
        tv_note_right_back = (TextView) findViewById(R.id.tv_note_right_back);
        showDialog(getResources().getString(R.string.step1),false);
        bundDevice(state);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT ) {
            if(resultCode == Activity.RESULT_CANCELED) {
                finish();
                return;
            }
        }
    }

    @Override
    public void onClick(View v) {

    }
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(state==none) return;
            Logger.e("重试");
            isFirst = true;
            loadDialog.dismiss();
            Intent it =new Intent(ConfigTablentDevice.this,NotifyDialog.class);
            it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            it.putExtra(PAIRED_OK,"重试");
            startActivity(it);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //开启service下蓝牙扫描
        unregisterReceiver(mGattUpdateReceiver);
    }

    private int state = 0;
    private boolean isFirst = true;

    private void bleIsFind(UsbData device) {
        byte data = device.getData()[0];
        byte status = (byte) (data&0xF0);
        switch (status){
            case (byte) 0x00:
                paresData(data,device);
                break;
            case (byte) 0x10:
                paresData(data,device);
                break;
            case (byte) 0x20:
                paresData(data,device);
                break;
            case (byte) 0x30:
                paresData(data,device);
                break;
        }
    }
    private void paresData( byte status,UsbData device) {
        switch (status&(byte) 0x0F){
            case (byte) 0x00://进入配对
                showDialog("进入配对。。。",false);
                break;
            case (byte) 0x01://配对成功
                onSuccess(device);
                Logger.e(TAG,"配对成功！");
                break;
            case (byte) 0x02://配对失败或超时退出
                handler.sendEmptyMessage(0);
                break;
        }
    }
    private void onSuccess(UsbData device) {
        if(!isFirst) return;
        isFirst = false;
        switch (device.getData()[0]&(byte) 0xF0) {
            case  (byte) 0x00:
                setUsedToTrue(device,tv_note_left_from);
                //保存数据库
                deviceDate.setLeft_FD(device.getTireType(true));
                break;
            case (byte) 0x10:
                setUsedToTrue(device,tv_note_right_from);
                //保存数据库
                deviceDate.setRight_FD(device.getTireType(true));
                break;
            case (byte) 0x20:
                setUsedToTrue(device,tv_note_left_back);
                //保存数据库
                deviceDate.setLeft_BD(device.getTireType(true));
                break;
            case (byte) 0x30:
                setUsedToTrue(device,tv_note_right_back);
                //保存数据库
                deviceDate.setRight_BD(device.getTireType(true));
                break;
        }
    }


    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            String action = intent.getAction();
           if (NotifyDialog.ACTION_BTN_STATE.equals(action)) {
                intent.getExtras().getInt(NotifyDialog.BTN_STATE);
                Logger.e("重试");
                isFirst = true;
                showDialog(getResources().getString(R.string.step1),false);
               bundDevice(state);
            }else if (NotifyDialog.ACTION_BTN_NEXT.equals(action)) {
                intent.getExtras().getInt(NotifyDialog.BTN_STATE);
                state++;
                Logger.e("下一步"+state);
                isFirst = true;
                if(state==5) {
                    VictonBaseApplication.getDeviceDao().add(deviceDate);
                    SharedPreferences.getInstance().putBoolean(Constants.FIRST_CONFIG,true);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(loadDialog.isShowing())
                                loadDialog.dismiss();
                            finish();
                        }
                    },200);
                    return;
                }else {
                    showDialog(getResources().getString(R.string.step1),false);
                }
            }else if (UsbComService.SCAN_FOR_RESULT.equals(action)) {
               runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       UsbData device = (UsbData) intent.getSerializableExtra("SCAN_RECORD");
                       Logger.i(TAG,"从USB上接收到广播：" +"命令类型"+ DigitalTrans.byteToString(device.getCommand())+"内容："+DigitalTrans.Bytes2HexString(device.getData()));
                       if(device.getCommand()==(byte) 0x00){
                           bleIsFind(device);
                       }
                   }
               });
           }
        }
    };

    private void setUsedToTrue(UsbData device,TextView currentTv) {
        //保存数据到本地
        currentTv.setText(device.getTireType(true));
        mDeviceList.add(device);
        nextBund();
    }

    private void showDialog(String str,boolean isConnect) {
        if(!loadDialog.isShowing()&&state<4) {
            loadDialog.setText(getResources().getStringArray(R.array.staticText)[state]+str);
            loadDialog.setCountNum(20);
            loadDialog.startCount(new LoadingDialog.OnListenerCallBack() {
                @Override
                public void onListenerCount() {
                    handler.sendEmptyMessage(state);
                }
            });
            loadDialog.show();
//            App.getInstance().speak(getResources().getStringArray(R.array.staticText)[state-1]+str);
        }else{
            if(state>=4) return;
            loadDialog.reStartCount(getResources().getStringArray(R.array.staticText)[state]+str,20);
        }
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(NotifyDialog.ACTION_BTN_STATE);
        intentFilter.addAction(NotifyDialog.ACTION_BTN_NEXT);
        intentFilter.addAction(UsbComService.SCAN_FOR_RESULT);
        return intentFilter;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void nextBund(){
        state++;
        Logger.e("下一步"+state);
        if(state>=4) {
           handler.postDelayed(new Runnable() {
               @Override
               public void run() {
                   VictonBaseApplication.getDeviceDao().add(deviceDate);
                   SharedPreferences.getInstance().putBoolean(Constants.FIRST_CONFIG,true);
                   loadDialog.dismiss();
                   finish();
               }
           },300);
            return;
        }else {
            showDialog(getResources().getString(R.string.step1),false);
            bundDevice(state);
        }
    }
    private void bundDevice(int states) {
        showDialog("开始配对模块。。。",false);
        if(VictonBaseApplication.getInstance().usbService.receviceUsb==null) return;
        switch (state) {
            case 0:
                VictonBaseApplication.getInstance().usbService.receviceUsb.sendData(DigitalTrans.hex2byte(Constants.PAIRED_LEFT_FROM));
//                VictonBaseApplication.getInstance().usbService.receviceUsb.sendData(DigitalTrans.hex2byte(Constants.PAIRED_LEFT_FROM));
                break;
            case 1:
                VictonBaseApplication.getInstance().usbService.receviceUsb.sendData(DigitalTrans.hex2byte(Constants.PAIRED_RIGHT_FROM));
//                VictonBaseApplication.getInstance().usbService.receviceUsb.sendData(DigitalTrans.hex2byte(Constants.PAIRED_RIGHT_FROM));
                break;
            case 3:
                VictonBaseApplication.getInstance().usbService.receviceUsb.sendData(DigitalTrans.hex2byte(Constants.PAIRED_LEFT_BACK));
//                VictonBaseApplication.getInstance().usbService.receviceUsb.sendData(DigitalTrans.hex2byte(Constants.PAIRED_LEFT_BACK));
                break;
            case 2:
                VictonBaseApplication.getInstance().usbService.receviceUsb.sendData(DigitalTrans.hex2byte(Constants.PAIRED_RIGHT_BACK));
//                VictonBaseApplication.getInstance().usbService.receviceUsb.sendData(DigitalTrans.hex2byte(Constants.PAIRED_RIGHT_BACK));
                break;
        }
        isFirst = true;
    }
}
