package com.victon.tpms.common.usb;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;

import com.victon.tpms.R;
import com.victon.tpms.base.VictonBaseApplication;
import com.victon.tpms.base.db.entity.Device;
import com.victon.tpms.base.module.main.NotificationReceiver;
import com.victon.tpms.common.helper.DataHelper;
import com.victon.tpms.common.utils.Constants;
import com.victon.tpms.common.utils.Logger;
import com.victon.tpms.common.utils.SharedPreferences;
import com.victon.tpms.common.view.BleData;
import com.victon.tpms.entity.MyBluetoothDevice;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * author：Administrator on 2016/12/20 09:21
 * company: xxxx
 * email：1032324589@qq.com
 */

public class UsbComService extends Service{
    private final static String TAG = UsbComService.class.getSimpleName();
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    public final static String SCAN_FOR_RESULT = "com.example.bluetooth.le.SCAN_FOR_RESULT";
    public static final String ACTION_STOP_SCAN = "ACTION_STOP_SCAN";
    public static final String ACTION_START_SCAN = "ACTION_START_SCAN";

    private ArrayList<MyBluetoothDevice> connectDevice = new ArrayList<>();
    private Notification messageNotification;
    private NotificationManager messageNotificatioManager;
    private Intent messageIntent;
    private PendingIntent messagePendingIntent;
    // 通知栏消息
    private int messageNotificationID = 1000;
    private Device deviceDetails;
    private Timer timer;
    private boolean isSend;
    private Handler mHandler = new Handler();
    private UsbManager manager;
    private UsbDevice mUsbDevice;
    private PendingIntent mPermissionIntent;

    public ReceviceUsbDataThread receviceUsb;
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if(device != null){
                            //call method to set up device communication
                            UsbDeviceConnection connection = manager.openDevice(mUsbDevice);
                            if (connection == null) {
                                return;
                            }
                            if (connection.claimInterface(mInterface, true)) {
                                Log.i(TAG, "找到接口");
                                mDeviceConnection = connection;
                                // 获取USB通讯的读写端点
                                receviceUsb = new ReceviceUsbDataThread(UsbComService.this,mDeviceConnection,mInterface);
                                receviceUsb.start();
//                                getEndpoint(mDeviceConnection, mInterface);
//                                sendMsgForUsb(new byte[]{1, 66, 67, 68});
                            } else {
                                connection.close();
                            }
                        }
                    }
                    else {
                        Log.d(TAG, "permission denied for device " + device);
                    }
                }
            }
        }
    };
    private UsbInterface mInterface;
    private UsbDeviceConnection mDeviceConnection;

    public class LocalBinder extends Binder {
        public UsbComService getService() {
            return UsbComService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new UsbComService.LocalBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                isSend = false;
            }
        },1000,20000);
        Logger.i(TAG,"onceate service");
        iniUsb();
        //刷新数据库
        List<Device> device = VictonBaseApplication.getDeviceDao().get(Constants.MY_CAR_DEVICE);
        if(device.size()>0){
            deviceDetails = device.get(0);
        }
        // 初始化
        initNotification();
        //设置点击通知栏的动作为启动另外一个广播
        Intent broadcastIntent = new Intent(this, NotificationReceiver.class);
        broadcastIntent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED); // 关键的一步，设置启动模式
        messagePendingIntent = PendingIntent.getBroadcast(this, 0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.e(TAG,"onStartCommand service");
        iniUsb();
        //刷新数据库
        List<Device> device = VictonBaseApplication.getDeviceDao().get(Constants.MY_CAR_DEVICE);
        if(device.size()>0){
            deviceDetails = device.get(0);
        }
        // 初始化
        initNotification();
        //设置点击通知栏的动作为启动另外一个广播
        Intent broadcastIntent = new Intent(this, NotificationReceiver.class);
        broadcastIntent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED); // 关键的一步，设置启动模式
        messagePendingIntent = PendingIntent.getBroadcast(this, 0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        flags = START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }

    private void iniUsb() {
        // 获取USB设备
        manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        if (manager == null) {
            return;
        } else {
            Log.i(TAG, "usb设备：" + String.valueOf(manager.toString()));
        }
        //获取USB设备列表
        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
        Log.i(TAG, "usb设备：" + String.valueOf(deviceList.size()));
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        ArrayList<String> USBDeviceList = new ArrayList<String>(); // 存放USB设备的数量
        while (deviceIterator.hasNext()) {
            UsbDevice device = deviceIterator.next();
            USBDeviceList.add(String.valueOf(device.getVendorId()));
            USBDeviceList.add(String.valueOf(device.getProductId()));
            // 在这里添加处理设备的代码 根据VID和PID查找指定的设备
            if (device.getVendorId() == 6790 && device.getProductId() == 29987) {
                mUsbDevice = device;
                Log.i(TAG, "找到设备");
                mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
                IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
                filter.addAction(SCAN_FOR_RESULT);
                registerReceiver(mUsbReceiver, filter);
                manager.requestPermission(mUsbDevice, mPermissionIntent);
            }
        }
        findIntfAndEpt();
    }
    // 寻找接口和分配结点
    private void findIntfAndEpt() {
        if (mUsbDevice == null) {
            Log.i(TAG, "没有找到设备");
            return;
        }
        //查找接口
        for (int i = 0; i < mUsbDevice.getInterfaceCount();) {
            // 获取设备接口，一般都是一个接口，你可以打印getInterfaceCount()方法查看接
            // 口的个数，在这个接口上有两个端点，OUT 和 IN
            UsbInterface intf = mUsbDevice.getInterface(i);
            Log.d(TAG, i + " " + intf);
            mInterface = intf;
            break;
        }
        if (mInterface == null) {
            Log.i(TAG, "没有找到接口");
        }
    }

    private void initNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            messageNotification = new Notification.Builder(this)
                    .setSmallIcon(R.mipmap.ic_logo)
                    .setTicker("小安科技:" + "您有新短消息，请注意查收！")
                    .setContentTitle("小安胎压监测系统")
                    .setContentText("轮胎异常,请及时处理！")
                    .setLights(0xff0000ff, 300, 0)
                    //获取默认铃声
                    .setDefaults(Notification.DEFAULT_SOUND)
                    //获取自定义铃声
                    //                .setSound(Uri.parse("file:///sdcard/xx/xx.mp3"))
                    //获取Android多媒体库内的铃声
                    .setSound(Uri.withAppendedPath(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, "5"))
                    .setVibrate(new long[] {0,300,500,700})
                    .setContentIntent(messagePendingIntent).setNumber(1).build();
        }
        messageNotification.flags |= Notification.FLAG_AUTO_CANCEL; // FLAG_AUTO_CANCEL表明当通知被用户点击时，通知将被清除。
        messageNotificatioManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent("com.dbjtech.waiqin.destroy");
        sendBroadcast(intent);
        Logger.e(TAG,"service close!");
        stopForeground(true);
        unregisterReceiver(mUsbReceiver);
    }

    private void bleStringToDouble(byte[] data) {
        BleData bleData = DataHelper.getScanData(data);
        if(bleData.getTireType().equals(deviceDetails.getLeft_BD())) {
            handleException(bleData);
        }else if(bleData.getTireType().equals(deviceDetails.getRight_BD())) {
            handleException(bleData);
        }else if(bleData.getTireType().equals(deviceDetails.getLeft_FD())) {
            handleException(bleData);
        }else if(bleData.getTireType().equals(deviceDetails.getRight_FD())) {
            handleException(bleData);
        }
    }
    private void handleException(BleData date) {
        if (date.isError()) {
            Logger.i(TAG,"server 异常发送通知！");
            if(!isSend) {
                isSend = true;
                messageNotificatioManager.notify(messageNotificationID, messageNotification);
                Intent broadcastIntent = new Intent(this, NotificationReceiver.class);
                sendBroadcast(broadcastIntent);
            }
        }
    }
}
