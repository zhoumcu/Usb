package com.victon.tpms.common.view.frame;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import com.victon.tpms.common.ble.BluetoothLeService;
import com.victon.tpms.common.usb.UsbComService;
import com.victon.tpms.common.usb.UsbData;
import com.victon.tpms.common.utils.DigitalTrans;
import com.victon.tpms.common.utils.Logger;
import com.victon.tpms.entity.ManageDevice;

/**
 * Created by Administrator on 2016/7/28.
 */
public abstract class BaseBleConnetFragment extends Fragment{
    private final static String TAG = BaseBleConnetFragment.class.getSimpleName();
    private FragmentActivity context;
    private static final int REQUEST_ENABLE_BT = 1;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 1000000;
    public  BluetoothAdapter mBluetoothAdapter = null;
    private boolean mScanning;
    private ManageDevice manageDevice;

    protected abstract void initData();

    protected abstract void initRunnable();

    protected abstract void initConfig();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        initConfig();
        initRunnable();
        initUI();
        initData();
        //注册广播
        getActivity().registerReceiver(mHomeKeyEventReceiver, makeGattUpdateIntentFilter());
    }
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UsbComService.SCAN_FOR_RESULT);
        intentFilter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        return intentFilter;
    }
    /**
     * 监听是否点击了home键将客户端推到后台
     */
    private BroadcastReceiver mHomeKeyEventReceiver = new BroadcastReceiver() {
        String SYSTEM_REASON = "reason";
        String SYSTEM_HOME_KEY = "homekey";
        String SYSTEM_HOME_KEY_LONG = "recentapps";

        @Override
        public void onReceive(Context context, final Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_REASON);
                if (TextUtils.equals(reason, SYSTEM_HOME_KEY)) {
                    //表示按了home键,程序到了后台
                    //ToastUtil.show(getActivity(), "home", 1).show();
//                    mActivity.isQuiting = true;
                    Logger.e("表示按了home键,程序到了后台");
                }else if(TextUtils.equals(reason, SYSTEM_HOME_KEY_LONG)){
                    //表示长按home键,显示最近使用的程序列表
                }
            }else if (UsbComService.SCAN_FOR_RESULT.equals(action)) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        UsbData device = (UsbData) intent.getSerializableExtra("SCAN_RECORD");
                        Logger.i(TAG,"从USB上接收到广播：" +"命令类型"+ DigitalTrans.byteToString(device.getCommand())+"内容："+DigitalTrans.Bytes2HexString(device.getData()));
                        if(device.getCommand()==(byte) 0x00){
                            broadcastUpdate(BluetoothLeService.ACTION_RETURN_OK,device);
                        }
                    }
                });
            }
        }
    };
    private void initUI() {

    }
    protected void setManageDevice(ManageDevice device) {
        this.manageDevice = device;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        System.gc();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            getActivity().unregisterReceiver(mHomeKeyEventReceiver);
        } catch (IllegalArgumentException e) {
            Logger.e("mHomeKeyEventReceiver:" + e.toString());
        }
        Logger.e("onDestroy");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            context.finish();
            return;
        }
        mScanning = true;
        super.onActivityResult(requestCode, resultCode, data);
    }

    public abstract void broadcastUpdate(final String action, UsbData gatt);

    private boolean isNull(BluetoothDevice device,String str) {
        if(str==null) return false;
        if(device.getAddress().contains(str)) {
            return true;
        }
        return false;
    }
}
