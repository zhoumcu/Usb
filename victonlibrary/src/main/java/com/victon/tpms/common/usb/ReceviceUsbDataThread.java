package com.victon.tpms.common.usb;

import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbRequest;
import android.util.Log;

import com.victon.tpms.common.utils.DigitalTrans;
import com.victon.tpms.common.utils.Logger;
import com.victon.tpms.common.utils.SharedPreferences;

import java.nio.ByteBuffer;

import static android.content.ContentValues.TAG;

/**
 * author：Administrator on 2016/12/22 10:20
 * company: xxxx
 * email：1032324589@qq.com
 */

public class ReceviceUsbDataThread extends Thread {

    private Context context;
    private UsbDeviceConnection mDeviceConnection;
    private UsbEndpoint epOut;
    private UsbEndpoint epIn;
    private boolean isRecevice;

    public ReceviceUsbDataThread(Context context,UsbDeviceConnection connection, UsbInterface intf) {
        this.context = context;
        this.mDeviceConnection = connection;
        if (intf.getEndpoint(1) != null) {
            epOut = intf.getEndpoint(1);
        }
        if (intf.getEndpoint(0) != null) {
            epIn = intf.getEndpoint(0);
        }
    }

    @Override
    public void run() {
        super.run();
        while (!isRecevice){
            byte[] filebytes = null;
            int outMax = epOut.getMaxPacketSize();
            int inMax = epIn.getMaxPacketSize();
            ByteBuffer byteBuffer = ByteBuffer.allocate(inMax);
            UsbRequest usbRequest = new UsbRequest();
            usbRequest.initialize(mDeviceConnection, epIn);
            usbRequest.queue(byteBuffer, inMax);
            if(mDeviceConnection.requestWait() == usbRequest) {
                filebytes = byteBuffer.array();
                Logger.e("前端接收数据："+DigitalTrans.Bytes2HexString(filebytes));
                parseData(filebytes);
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void broadcastUpdate(String action,UsbData scanRecord) {
        Intent intent = new Intent(action);
        intent.putExtra("SCAN_RECORD", scanRecord);
        context.sendBroadcast(intent);
    }
    // 用UsbDeviceConnection 与 UsbInterface 进行端点设置和通讯
    private void getEndpoint(UsbDeviceConnection connection, UsbInterface intf) {
        if (intf.getEndpoint(1) != null) {
            epOut = intf.getEndpoint(1);
        }
        if (intf.getEndpoint(0) != null) {
            epIn = intf.getEndpoint(0);
        }

    }



    public void sendData(byte[] Sendbytes) {
        //byte[] bt ={1,66,67,68};
//        byte[] Sendbytes = Arrays.copyOf(bt, bt.length);
        // 1,发送准备命令
        // bulk transfer buffer size limited to 16K
        //bulkTransfer (UsbEndpoint endpoint, byte[] buffer, int length, int timeout)
        Logger.i(TAG,"正在发送..."+DigitalTrans.Bytes2HexString(Sendbytes));
        int ret = mDeviceConnection.bulkTransfer(epOut, Sendbytes,
                Sendbytes.length, 5000);//milliseconds
        Log.i(TAG, "已经发送!"+ret);
    }
    public byte[] receviceData() {
        byte[] filebytes = null;
        int outMax = epOut.getMaxPacketSize();
        int inMax = epIn.getMaxPacketSize();
        ByteBuffer byteBuffer = ByteBuffer.allocate(inMax);
        UsbRequest usbRequest = new UsbRequest();
        usbRequest.initialize(mDeviceConnection, epIn);
        usbRequest.queue(byteBuffer, inMax);
        if(mDeviceConnection.requestWait() == usbRequest) {
            filebytes = byteBuffer.array();
            Log.i(TAG, "接收返回值:" + DigitalTrans.Bytes2HexString(filebytes));
            Logger.i("后台接收广播数据："+ SharedPreferences.getInstance().getBoolean("isAppOnForeground",false));
//            broadcastUpdate(UsbComService.SCAN_FOR_RESULT, filebytes);
            return filebytes;
        }
        return new byte[0];
    }
    private UsbData parseData(byte[] filebytes){
        byte sumByte = 0x00;
        for(int i= 0;i<3;i++){
            int fromPos = i * 10;
//            Logger.d(TAG,DigitalTrans.byteToString(filebytes[fromPos])+DigitalTrans.byteToString(filebytes[fromPos+1]));
            if(filebytes[fromPos]!=(byte) 0xFF&&filebytes[fromPos+1]!=(byte)0xF5){
                Logger.d(TAG,"无效数据！");
                continue;
            }
            int lenght = DigitalTrans.byteToBin0x0F(filebytes[fromPos+2]);
//            Logger.d(TAG,"长度="+lenght);
            for (int j= 2;j<=lenght+2;j++){
                sumByte += filebytes[fromPos+j];
//                Logger.d(TAG,DigitalTrans.byteToString(filebytes[fromPos+j]));
            }
            Logger.d(TAG,DigitalTrans.byteToString(sumByte)+DigitalTrans.byteToString(filebytes[lenght+3]));
            if(sumByte!=filebytes[lenght+3]){
                Logger.d(TAG,"校验和出错！");
                continue;
            }
            byte[] data = new byte[lenght-2];
            UsbData usbData = new UsbData();
            usbData.setDeviceID(filebytes[fromPos+3]);
            usbData.setCommand(filebytes[fromPos+4]);
            usbData.setTireType(filebytes[fromPos+5]);
            for (int l= 5;l<lenght+3;l++){
                data[l-5] = filebytes[fromPos+l];
            }
            usbData.setData(data);
            Log.i(TAG, "接收返回值:" + DigitalTrans.Bytes2HexString(data));
//            Logger.i("后台接收广播数据："+ SharedPreferences.getInstance().getBoolean("isAppOnForeground",false));
            broadcastUpdate(UsbComService.SCAN_FOR_RESULT, usbData);
        }
        return null;
    }
    private UsbData parseData(byte[] filebytes,boolean re){
        byte sumByte = 0x00;
//        Logger.d(TAG,DigitalTrans.byteToString(filebytes[0])+DigitalTrans.byteToString(filebytes[1]));
        if(filebytes[0]!=(byte) 0xFF&&filebytes[1]!=(byte)0xF5){
            Logger.d(TAG,"无效数据！");
            return null;
        }
        int lenght = DigitalTrans.byteToBin0x0F(filebytes[2]);
//        Logger.d(TAG,"长度="+lenght);
        for (int i= 2;i<=lenght+2;i++){
             sumByte += filebytes[i];
//            Logger.d(TAG,DigitalTrans.byteToString(filebytes[i]));
        }
//        Logger.d(TAG,DigitalTrans.byteToString(sumByte)+DigitalTrans.byteToString(filebytes[lenght+3]));
        if(sumByte!=filebytes[lenght+3]){
            Logger.d(TAG,"校验和出错！");
            return null;
        }
        byte[] data = new byte[lenght-2];
        UsbData usbData = new UsbData();
        usbData.setDeviceID(filebytes[3]);
        usbData.setCommand(filebytes[4]);
        for (int i= 5;i<=lenght+2;i++){
            data[i-5] = filebytes[i];
        }
        usbData.setData(data);
        return usbData;
    }
}
