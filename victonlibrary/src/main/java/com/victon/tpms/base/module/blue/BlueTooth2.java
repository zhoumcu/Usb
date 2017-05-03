package com.victon.tpms.base.module.blue;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.victon.tpms.R;
import com.victon.tpms.common.utils.Logger;
import com.victon.tpms.common.view.activity.BaseActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * author：Administrator on 2016/10/20 15:36
 * company: xxxx
 * email：1032324589@qq.com
 */

public class BlueTooth2 extends BaseActivity {
    private static final String DEVICE_NAME1 = "JIDOU-D1";
    private static final String DEVICE_NAME2 = "JIDOU-D1";
    private static final String DEVICE_NAME3= "JIDOU-D1";
    private static final UUID MY_UUID = UUID.fromString("00001103-0000-1000-8000-00805F9B34FB");
    private TextView ett;
    private BluetoothSocket mBTHSocket;
    private InputStream mmInStream;
    private OutputStream mmOutStream;
    private BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_ble_test);
        initUi();
        initBlt();
    }

    private void initUi() {
        ett = (TextView) findViewById(R.id.ett);
        Button btnSend = (Button) findViewById(R.id.btn_send);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage("AT");
            }
        });
    }

    private BluetoothHeadset mBluetoothHeadset;
    private List<BluetoothDevice> devices = new ArrayList<>();
    // Define Service Listener of BluetoothProfile
    private BluetoothProfile.ServiceListener mProfileListener = new BluetoothProfile.ServiceListener() {
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            if (profile == BluetoothProfile.HEADSET) {
                mBluetoothHeadset = (BluetoothHeadset) proxy;
                devices = mBluetoothHeadset.getConnectedDevices();
            }
        }
        public void onServiceDisconnected(int profile) {
            if (profile == BluetoothProfile.HEADSET) {
                mBluetoothHeadset = null;
            }
        }
    };
    private void initBlt() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();//获取适配器
        final Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        mBluetoothAdapter.enable();//将适配器设置可用
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);// 使得蓝牙处于可发现模式，持续时间150s
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 150);

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (pairedDevices.size() > 0) {
                    // Loop through paired devices
                    for (BluetoothDevice device : pairedDevices) {
                        Logger.e(device.getName() + "\n" + device.getAddress() + "\n" + device.getBondState());
                        // Add the name and address to an array adapter to show in a ListView

//                if (DEVICE_NAME1.equals(device.getName())||DEVICE_NAME2.equals(device.getName())
//                        ||DEVICE_NAME3.equals(device.getName())) {
                        try {

//                        BluetoothSocket mBTHSocket = device.createRfcommSocketToServiceRecord(MY_UUID);//获取下位机的socket
                            int sdk = Integer.parseInt(Build.VERSION.SDK);
                            if (sdk >= 10) {
                                mBTHSocket = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
                            } else {
                                mBTHSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
                            }
//                        BluetoothServerSocket mBThServer = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(
//                                "myServerSocket", MY_UUID);//监听可用的设备
                            mBTHSocket.connect(); // 建立连接
                            mmInStream = mBTHSocket.getInputStream();// 获取输入流
                            mmOutStream = mBTHSocket.getOutputStream();// 获取输出流
                            if (mBTHSocket.isConnected()) {
//                                ett.setText(device.getName()+"设备连接成功！"+"\n");
                                Message message = new Message();
                                message.obj = device.getName();
                                message.what = 0;
                                handler.sendMessage(message);
//                            new Thread(new ReceiveDatas(mBTHSocket)).start();
                            } else {
//                                ett.setText(device.getName()+"设备连接失败！"+"\n");
                                Message message = new Message();
                                message.obj = device.getName();
                                message.what = 1;
                                handler.sendMessage(message);
                            }
                        } catch (IOException e) {
//                            ett.setText(device.getName()+"设备连接异常！"+"\n");
                            Message message = new Message();
                            message.obj = device.getName();
                            message.what = 2;
                            handler.sendMessage(message);
                        }
                        if (null != mmOutStream) {
                            //向服务器端发送一个字符串
                            try {
                                mmOutStream.write("这是另一台手机发送过来的数据".getBytes("utf-8"));
//                            Toast.makeText(MainActivity.this,"发送成功",1000);
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
//                            Toast.makeText(MainActivity.this,"发送失败",1000);
                                e.printStackTrace();
                            }
                            break;
                        }
                    }
//                    }
                }
            }
        }).start();
        new AcceptThread(mBluetoothAdapter,handler).start();
    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){

                case 0:
                    ett.append(msg.obj.toString()+"设备连接成功！"+"\n");
                    break;
                case 1:
                    ett.append(msg.obj.toString()+"设备连接失败！"+"\n");
                    break;
                case 2:
                    ett.append(msg.obj.toString()+"设备连接异常！"+"\n");
                    break;
                case 3:
                    Logger.e(msg.obj.toString());
                    ett.append("\n"+msg.obj.toString());
                    break;
            }
        }
    };
    // 写数据
    /* Call this from the main Activity to send data to the remote device */
    public void sendMessage(String msg) {
        byte[] buffer = new byte[16];
        try {
            if (mmOutStream == null) {
                Log.i("info", "输出流为空");
                return;
            }
            // 写数据
            buffer = msg.getBytes("utf-8");
            mmOutStream.write(buffer);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (mmOutStream != null) {
                    mmOutStream.flush();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    private class AcceptThread extends Thread{
        private BluetoothAdapter bluetoothAdapter;
        private Handler handler;
        private BluetoothServerSocket serverSocket;
        private BluetoothSocket socket;
        private InputStream is;
        private OutputStream os;
        private boolean isTry;

        public AcceptThread(BluetoothAdapter bluetoothAdapter, Handler handler) {
            //创建BluetoothServerSocket对象
            this.handler = handler;
            this.bluetoothAdapter = bluetoothAdapter;
        }
        @Override
        public void run() {
            // TODO Auto-generated method stub
            //等待接受蓝牙客户端的请求
            while(!isTry) {
                try {
                    serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord("myServerSocket", MY_UUID);
                    if(serverSocket!=null)
                        isTry = true;
                    Thread.sleep(500);
                    Logger.e("socket is fails try to accept");
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            try {
                    socket = serverSocket.accept();
                    if (socket.isConnected()) {
                        is = socket.getInputStream();
                        os = socket.getOutputStream();
                        while (true) {
                            byte[] buffer = new byte[128];
                            int count = is.read(buffer);
                            Message message = new Message();
                            message.obj = new String(buffer, 0, count, "utf-8");
                            message.what = 3;
                            handler.sendMessage(message);
                        }
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
        }
    }
    public class ReceiveDatas extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        // 变量 略过

        // 构造方法
        public ReceiveDatas(BluetoothSocket socket) {

            this.mmSocket = socket;
            InputStream tempIn = null;

            // 获取输入流
            try {
                tempIn = socket.getInputStream();
            } catch (IOException e) {
                // TODO Auto-generated catch block
            }
            mmInStream = tempIn;
        }

        @Override
        public void run() {
            byte[] buffer = new byte[1024];// 缓冲数据流
            int bytes;// 返回读取到的数据
            // 监听输入流
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);
                    // 此处处理数据……
                    Logger.e("接收到的数据："+bytes);
                } catch (IOException e) {
                    try {
                        if (mmInStream != null) {
                            mmInStream.close();
                        }
                        Log.i("info", "异常");
                        break;
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
                try {
                    Thread.sleep(80);// 延迟
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBluetoothAdapter = null;
        try {
            mBTHSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

