package com.xiaoan.tpms.usb;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.victon.tpms.common.usb.UsbComService;
import com.victon.tpms.common.utils.Constants;
import com.victon.tpms.common.utils.DigitalTrans;
import com.victon.tpms.common.utils.Logger;

public class MainActivity extends Activity {

    private static final String TAG = "MissileLauncherActivity";
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

    private Button btsend; // 发送按钮
    private UsbManager manager; // USB管理器
    private UsbDevice mUsbDevice; // 找到的USB设备
    private ListView lsv1; // 显示USB信息的
    private TextView tv;
    private UsbInterface mInterface;
    private UsbDeviceConnection mDeviceConnection;
    private PendingIntent mPermissionIntent;

    private BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if(device != null){
                            //call method to set up device communication
                        }
                    }
                    else {
                        Log.d(TAG, "permission denied for device " + device);
                    }
                }
            }else if(UsbComService.SCAN_FOR_RESULT.equals(action)){
                Logger.i("前端接收数据："+Bytes2HexString(intent.getExtras().getByteArray("SCAN_RECORD"))+"\n");
                tv.append(Bytes2HexString(intent.getExtras().getByteArray("SCAN_RECORD")));
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        btsend = (Button) findViewById(R.id.btsend);
        btsend.setOnClickListener(btsendListener);

        tv = (TextView) findViewById(R.id.tv);

        lsv1 = (ListView) findViewById(R.id.lsv1);

        tv.setText("初始化");
        // 启动的时候就去获取设备
        // 获取USB设备
//        manager = (UsbManager) getSystemService(Context.USB_SERVICE);
//        if (manager == null) {
//            return;
//        } else {
//            Log.i(TAG, "usb设备：" + String.valueOf(manager.toString()));
//        }
//        //获取USB设备列表
//        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
//        Log.i(TAG, "usb设备：" + String.valueOf(deviceList.size()));
//        tv.setText("usb设备：" + String.valueOf(deviceList.size()));
//        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
//        ArrayList<String> USBDeviceList = new ArrayList<String>(); // 存放USB设备的数量
//        while (deviceIterator.hasNext()) {
//            UsbDevice device = deviceIterator.next();
//            USBDeviceList.add(String.valueOf(device.getVendorId()));
//            USBDeviceList.add(String.valueOf(device.getProductId()));
//            tv.setText("VendorId"+String.valueOf(device.getVendorId())+
//                    "ProductId" +String.valueOf(device.getProductId()));
//            // 在这里添加处理设备的代码 根据VID和PID查找指定的设备
//            if (device.getVendorId() == 6790 && device.getProductId() == 29987) {
//                mUsbDevice = device;
//                Log.i(TAG, "找到设备");
//                tv.setText("找到设备");
//
//                mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
//                IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
//                registerReceiver(mUsbReceiver, filter);
//                manager.requestPermission(mUsbDevice, mPermissionIntent);
//            }
//        }
//        // 创建一个ArrayAdapter
//        lsv1.setAdapter(new ArrayAdapter<String>(this,
//                android.R.layout.simple_list_item_1, USBDeviceList));
//
//        //查找通信端点
//        findIntfAndEpt();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        intentFilter.addDataScheme("file");
        intentFilter.addAction(UsbComService.SCAN_FOR_RESULT);
        registerReceiver(mUsbReceiver, intentFilter);
//        Intent intent = new Intent(this,UsbComService.class);
//        startService(intent);
        Intent intent = new Intent(this, UsbComService.class);
        bindService(intent,mServiceConnection,Context.BIND_AUTO_CREATE);
    }

    private UsbComService usbService = null;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            usbService = ((UsbComService.LocalBinder) iBinder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            usbService = null;
        }
    };
    @Override
    public void onDestroy() {
        unregisterReceiver(mUsbReceiver);
        super.onDestroy();
    }

    // 寻找接口和分配结点
    private void findIntfAndEpt() {
        if (mUsbDevice == null) {
            Log.i(TAG, "没有找到设备");
            tv.append("没有找到设备");
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

        if (mInterface != null) {
            UsbDeviceConnection connection = null;
            // 判断是否有权限
            if (manager.hasPermission(mUsbDevice)) {
                // 打开设备，获取 UsbDeviceConnection 对象，连接设备，用于后面的通讯
                connection = manager.openDevice(mUsbDevice);
                if (connection == null) {
                    return;
                }
                //
                if (connection.claimInterface(mInterface, true)) {
                    Log.i(TAG, "找到接口");
                    tv.setText("找到接口");
                    mDeviceConnection = connection;
                    // 获取USB通讯的读写端点
                    getEndpoint(mDeviceConnection, mInterface);
                } else {
                    connection.close();
                }
            } else {
                Log.i(TAG, "没有权限");
                tv.setText("没有权限");
            }
        } else {
            Log.i(TAG, "没有找到接口");
            tv.setText("没有找到接口");
        }
    }

    private UsbEndpoint epOut;
    private UsbEndpoint epIn;

    // 用UsbDeviceConnection 与 UsbInterface 进行端点设置和通讯
    private void getEndpoint(UsbDeviceConnection connection, UsbInterface intf) {
        if (intf.getEndpoint(1) != null) {
            epOut = intf.getEndpoint(1);
            tv.setText(epOut.toString());
        }
        if (intf.getEndpoint(0) != null) {
            epIn = intf.getEndpoint(0);
            tv.setText(epIn.toString());
        }
    }
    private OnClickListener btsendListener = new OnClickListener() {

        public void onClick(View v) {
            String bt = "FFF50300050008";
            usbService.receviceUsb.sendData(DigitalTrans.hex2byte(Constants.PAIRED_RIGHT_BACK));
        }
    };

    public static String Bytes2HexString(byte[] b) {
        String ret = "";
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            ret += hex.toUpperCase();
        }
        return ret;
    }

    public void DisplayToast(CharSequence str) {
        Toast toast = Toast.makeText(this, str, Toast.LENGTH_LONG);
        // 设置Toast显示的位置
        toast.setGravity(Gravity.TOP, 0, 200);
        // 显示Toast
        toast.show();
    }

    public static byte uniteBytes(byte src0, byte src1) {
        byte _b0 = Byte.decode("0x" + new String(new byte[]{src0})).byteValue();
        _b0 = (byte)(_b0 << 4);
        byte _b1 = Byte.decode("0x" + new String(new byte[]{src1})).byteValue();
        byte ret = (byte)(_b0 ^ _b1);
        return ret;
    }

    public static byte[] HexString2Bytes(String src){
        byte[] ret = new byte[16];
        byte[] tmp = src.getBytes();
        for(int i=0; i<16; i++){
            ret[i] = uniteBytes(tmp[i*2], tmp[i*2+1]);
        }
        return ret;
    }
}
