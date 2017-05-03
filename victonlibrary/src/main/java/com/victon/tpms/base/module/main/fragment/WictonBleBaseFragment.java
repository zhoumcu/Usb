package com.victon.tpms.base.module.main.fragment;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.victon.tpms.base.db.DbHelper;
import com.victon.tpms.base.db.DbObervable;
import com.victon.tpms.base.db.entity.RecordData;
import com.victon.tpms.base.module.main.ScanDeviceRunnable;
import com.victon.tpms.common.helper.DataHelper;
import com.victon.tpms.common.usb.UsbComService;
import com.victon.tpms.common.usb.UsbData;
import com.victon.tpms.common.utils.Constants;
import com.victon.tpms.common.utils.DigitalTrans;
import com.victon.tpms.common.utils.Logger;
import com.victon.tpms.common.view.BleData;
import com.victon.tpms.entity.ManageDevice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.functions.Action1;

/**
 * Created by zhoumcu on 2016/8/26.
 */
public abstract class WictonBleBaseFragment extends BaseFragment{
    private final static String TAG = WictonBleBaseFragment.class.getSimpleName();
    private static final long DISTIME = 600000;
    private ViewGroup root;
    private List<BleData> listBleData = new ArrayList<>();
    private List<BleData> listReBleData = new ArrayList<>();
    private Map<Integer,BleData> mapList = new HashMap<>();
    private Map<Integer,BleData> mapReList = new HashMap<>();
    private ManageDevice defaultDevice;
    private ScanDeviceRunnable leftFRunnable;
    private ScanDeviceRunnable rightFRunnable;
    private ScanDeviceRunnable leftBRunnable;
    private ScanDeviceRunnable rightBRunnable;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(root != null) return root;
        root  = new FrameLayout(getContext());
        root.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
        root.addView(createViews(inflater,root));
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
        initFirstData();
    }

    private void initFirstData() {
        List<RecordData> recordDatas = DbHelper.getInstance(getActivity()).getCarDataList(Constants.deviceId);
        DbObervable.getInstance(getContext()).getReBleData(recordDatas,defaultDevice)
//                .subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<BleData>() {
                    @Override
                    public void call(BleData bleData) {
                        bleData.setDeviceAddress(bleData.getDeviceAddress());
                        mapList.put(bleData.getViewPosition(),bleData);
                        listBleData.add(bleData);
                        onReceviceDateSucess(null,listBleData);
                        onReceviceDateSucess(null,mapList);
                        handlerException(bleData.getDeviceAddress(),bleData,defaultDevice);
                    }
                });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void initView() {
        initBle();
        initRunnable();
        initConfig();
    }

    /**
     * 初始化蓝牙设备
     */
    private void initBle(){
        //1分钟报警重复提醒
        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                defaultDevice.clearBoolean();
                mHandler.postDelayed(this, 60000);
                Logger.i(TAG,"重复报警");
            }
        };
        mHandler.postDelayed(runnable, 60000);
    }

    protected void initConfig() {
        getActivity().registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }
    protected void initRunnable() {
        leftFRunnable = new ScanDeviceRunnable(mHandler,0);
        rightFRunnable = new ScanDeviceRunnable(mHandler,1);
        leftBRunnable = new ScanDeviceRunnable(mHandler,2);
        rightBRunnable = new ScanDeviceRunnable(mHandler,3);
    }
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UsbComService.SCAN_FOR_RESULT);
        return intentFilter;
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
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        UsbData device = (UsbData) intent.getSerializableExtra("SCAN_RECORD");
                        Logger.i(TAG,"从USB上接收到广播：" +"命令类型"+ DigitalTrans.byteToString(device.getCommand())+
                                "内容："+DigitalTrans.Bytes2HexString(device.getData()));
                        if(device.getCommand()==(byte) 0x04){
                            bleStringToDouble(device);
                        }
                    }
                });
            }
        }
    };
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 2:
                    Logger.e(TAG,"接收数据超时！！！！！！！");
                    BleData bleData = new BleData();
                    bleData.setNoReceviceData(true);
                    onReceviceDateException(bleData,(Integer)msg.obj);
                    RecordData recordData = new RecordData();
                    recordData.setData(null);
                    recordData.setDeviceId(Constants.deviceId);
                    DbHelper.getInstance(getContext()).update(Constants.deviceId,listBleData.get((Integer)msg.obj).getDeviceAddress(),recordData);
                    break;
            }
        }
    };

    private void bleStringToDouble(UsbData device) {
        BleData bleData = DataHelper.getData(device,defaultDevice);
        if(sortListData(listBleData,bleData)){
            onReceviceDateSucess(device,bleData.getViewPosition(),bleData);
        }else{
            mapList.put(bleData.getViewPosition(),bleData);
            listBleData.add(bleData);
            onReceviceDateSucess(device,listBleData);
            onReceviceDateSucess(device,mapList);
        }
        //保存每一帧数据
        RecordData recordData = new RecordData();
        recordData.setName(device.getTireType());
        recordData.setData(bleData.getData());
        recordData.setViewPosition(bleData.getViewPosition());
        recordData.setDeviceId(Constants.deviceId);
        DbHelper.getInstance(getContext()).update(Constants.deviceId,device.getTireType(),recordData);

        handleException(device,bleData,defaultDevice);
    }

    private boolean sortListData(List<BleData> listBleData,BleData bleData) {
        if(listBleData.size()==0) return false;
        for (BleData data : listBleData){
            if(data.getDeviceAddress().equals(bleData.getDeviceAddress())){
                return true;
            }
        }
        return false;
    }
    private void handleException(UsbData device,BleData bleData,ManageDevice manageDevice){
        this.handlerException(device.getTireType(),bleData,manageDevice);
    }
    private void handlerException(String device,BleData bleData,ManageDevice manageDevice) {
        if(device.equals(manageDevice.getLeftBDevice())) {
            handleException(bleData, manageDevice.getLeftBDevice());
            //开启定时器用于监听数据
            if(manageDevice.disLeftBCount==1) {
                mHandler.removeCallbacks(leftBRunnable);
                manageDevice.disLeftBCount = 0;
            }
            manageDevice.disLeftBCount++;
            mHandler.postDelayed(leftBRunnable, DISTIME);// 打开定时器，执行操作
        }else if(device.equals(manageDevice.getRightBDevice())) {
            handleException(bleData, manageDevice.getRightBDevice());
            if(manageDevice.disRightBCount==1) {
                mHandler.removeCallbacks(rightBRunnable);
                manageDevice.disRightBCount = 0;
            }
            manageDevice.disRightBCount++;
            mHandler.postDelayed(rightBRunnable, DISTIME);// 打开定时器，执行操作

        }else if(device.equals(manageDevice.getLeftFDevice())) {
            handleException(bleData,manageDevice.getLeftFDevice());
            if(manageDevice.disLeftFCount==1) {
                mHandler.removeCallbacks(leftFRunnable);
                manageDevice.disLeftFCount = 0;
            }
            manageDevice.disLeftFCount++;
            mHandler.postDelayed(leftFRunnable, DISTIME);// 打开定时器，执行操作

        }else if(device.equals(manageDevice.getRightFDevice())) {
            handleException(bleData, manageDevice.getRightFDevice());
            if(manageDevice.disRightFCount==1) {
                mHandler.removeCallbacks(rightFRunnable);
                manageDevice.disRightFCount = 0;
            }
            manageDevice.disRightFCount++;
            mHandler.postDelayed(rightFRunnable, DISTIME);// 打开定时器，执行操作
        }
    }
    private void handleException(BleData date, String str) {
        String errorState = date.getErrorState();
        if (date.isError()) {
            //高压
            Logger.e(TAG,"轮胎出现异常情况！");
            date.setException(true);
            bleDicoverException(date,str,errorState);
        }else{
            //正常
            Logger.i(TAG,"轮胎一切正常！");
            date.setException(false);
            bleIsNormal(date,str,errorState,date.getVoltage());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mGattUpdateReceiver);
    }

    private boolean isNull(BluetoothDevice device, String str) {
        if(str==null) return false;
        if(device.getAddress().contains(str)) {
            return true;
        }
        return false;
    }
    protected void initDefaultData(ManageDevice defaultDevice){
        this.defaultDevice = defaultDevice;
    }
    protected abstract void initData();

    protected abstract void bleIsNormal(BleData date, String strAddress, String noticeStr, float voltage);

    protected abstract void bleDicoverException(BleData date, String strAddress, String noticeStr);

    protected abstract void onDisconnectedDevice(UsbData device);

    protected void onReceviceDateSucess(UsbData device, List<BleData> bleDatas){};

    protected void onReceviceDateSucess(UsbData device, Map<Integer,BleData> bleDataMap){};

    protected abstract void onReceviceDateSucess(UsbData device, int positionOfView, BleData bleData);

    protected abstract void onReceviceDateException(BleData bleData,int tyreType);

    protected abstract View createViews(LayoutInflater inflater, ViewGroup root);
}
