package com.victon.tpms.common.helper;

import com.victon.tpms.common.usb.UsbData;
import com.victon.tpms.common.utils.Constants;
import com.victon.tpms.common.utils.DataUtils;
import com.victon.tpms.common.utils.DigitalTrans;
import com.victon.tpms.common.utils.Logger;
import com.victon.tpms.common.utils.SharedPreferences;
import com.victon.tpms.common.view.BleData;
import com.victon.tpms.entity.ManageDevice;

import java.text.DecimalFormat;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * author：Administrator on 2016/9/28 08:54
 * company: xxxx
 * email：1032324589@qq.com
 */

public class DataHelper {

    private static final String TAG = DataHelper.class.getSimpleName();

    public DataHelper() {
    }
    public static BleData getScanData(byte[] scanRecord){
        Logger.e(":"+ DataUtils.bytesToHexString(scanRecord));
        byte[] data = DataUtils.parseData(scanRecord).datas;
        return getData(data);
    }
    public static BleData getData(String device, ManageDevice defaultDevice, byte[] data){
        BleData bleData = getData(data);
        bleData.setNoReceviceData(false);
        bleData.setDeviceAddress(device);
        if(defaultDevice!=null)
            bleData.setViewPosition(getPositionOfView(device,defaultDevice));
        return bleData;
    }

    public static BleData getData(UsbData device, ManageDevice defaultDevice){
        BleData bleData = getData(device.getData());
        bleData.setNoReceviceData(false);
        bleData.setDeviceAddress(device.getTireType());
        if(defaultDevice!=null)
            bleData.setViewPosition(getPositionOfView(device.getTireType(),defaultDevice));
        return bleData;
    }

    public static BleData getData(byte[] data){
        float press = 0, temp =0;
        int temp1 = 0,rssi = 0;
        byte state = 0;
        String pressStr = "";
        DecimalFormat df = new DecimalFormat("######0.0");
        BleData bleData = new BleData();
        if(data==null) return bleData;
        if(data.length==0) return bleData;
        if(data.length==4) {
            Logger.d(TAG,"press"+parsePress(data[1]));
            press = parsePress(data[1]);
            temp = (float)(DigitalTrans.byteToAlgorism(data[2])-40);
//            state = DigitalTrans.byteToBin0x0F(data[3]);
            state = data[3];
            press = Math.round(press*10)*0.1f;
            if(SharedPreferences.getInstance().getString(Constants.PRESSUER_DW, "Bar").equals("Bar")) {
                pressStr = df.format(press);
            }else if(SharedPreferences.getInstance().getString(Constants.PRESSUER_DW, "Bar").equals("Kpa")) {
                press = Math.round(press*102*10)*0.1f;
                pressStr = df.format(press);
            }else{
                press = Math.round(press*14.5f*10)*0.1f;
                pressStr = df.format(press);
            }
            if(SharedPreferences.getInstance().getString(Constants.TEMP_DW, "℃").equals("℃")) {
                temp1 = (int)temp;
            }else {
                temp1 = (int)(temp*1.8f)+32;
            }
        }else if(data.length==9) {
            press = ((float)DigitalTrans.byteToAlgorism(data[2])*160)/51/100;
            temp = (float)(DigitalTrans.byteToAlgorism(data[3])-50);
//            state = DigitalTrans.byteToBin0x0F(data[1]);
            press = Math.round(press*10)*0.1f;
            if(SharedPreferences.getInstance().getString(Constants.PRESSUER_DW, "Bar").equals("Bar")) {
                pressStr = df.format(press);
            }else if(SharedPreferences.getInstance().getString(Constants.PRESSUER_DW, "Bar").equals("Kpa")) {
                press = Math.round(press*102*10)*0.1f;
                pressStr = df.format(press);
            }else{
                press = Math.round(press*14.5f*10)*0.1f;
                pressStr = df.format(press);
            }
            if(SharedPreferences.getInstance().getString(Constants.TEMP_DW, "℃").equals("℃")) {
                temp1 = (int)temp;
            }else {
                temp1 = (int)(temp*1.8f)+32;
            }
        }
        Logger.e("数据长度："+data.length+"状态："+state+"\n"+"压力值："+press+"\n"+"温度："+temp+"\n");
        //计算异常情况
        bleData.setTemp (temp1);
        bleData.setPress(press);
        bleData.setStringPress(pressStr);
        bleData.setStatus(state);
        bleData.setData(DigitalTrans.byte2hex(data));
        return handleException(bleData);
    }
    private static BleData handleException(BleData date) {
        StringBuffer buffer = new StringBuffer();
        float maxPress ,minPress,maxTemp;
        if(SharedPreferences.getInstance().getString(Constants.PRESSUER_DW, "Bar").equals("Bar")) {
            maxPress = Constants.getHighPressValue();
            minPress = Constants.getLowPressValue();
        }else if(SharedPreferences.getInstance().getString(Constants.PRESSUER_DW, "Bar").equals("Kpa")) {
            maxPress = Constants.getHighPressKpaValue();
            minPress = Constants.getLowPressKpaValue();
        }else{
            maxPress = Constants.getHighPressPsiValue();
            minPress = Constants.getLowPressPsiValue();
        }
        if(SharedPreferences.getInstance().getString(Constants.TEMP_DW, "℃").equals("℃")) {
            maxTemp = Constants.getHighTempValue();
        }else {
            maxTemp = Constants.getHighTempFValue();
        }
        Logger.i(TAG,"maxPress"+maxPress+"minPress"+minPress+"maxTemp"+maxTemp);
        buffer.append(date.getPress() > maxPress ? "高压" + " " : "");
        buffer.append(date.getPress() < minPress ? "低压" + " " : "");
        buffer.append(date.getTemp() > maxTemp ? "高温" + " " : "");
        ManageDevice.status[] statusData = ManageDevice.status.values();
        //状态检测
        if((date.getStatus()&0x01)==(byte)0x01) {
            buffer.append(statusData[1] + " ");
        }else if((date.getStatus()&0x02)==(byte)0x02){
            buffer.append(statusData[2] + " ");
        } else if((date.getStatus()&0x80)==(byte)0x80){
            buffer.append(statusData[8] + " ");
        }
//        else if(date.getStatus()==40){
//            buffer.append(statusData[7] + " ");
//        }else if(date.getStatus()==20){
//            buffer.append(statusData[6] + " ");
//        }else if(date.getStatus()==10){
//            buffer.append(statusData[5] + " ");
//        }

        if (buffer.toString().contains("快漏")||buffer.toString().contains("慢漏")||date.getPress() > maxPress || date.getPress() < minPress || date.getTemp() > maxTemp ? true: false) {
            //高压
            date.setIsError(true);
            date.setException(true);
        }else {
            //正常
            date.setIsError(false);
            date.setException(false);
        }
        date.setErrorState(buffer.toString());
        return date;
    }
    private static int getPositionOfView(String tireType, ManageDevice manageDevice) {
        if(tireType.equals(manageDevice.getLeftBDevice())) {
            return 2;
        }else if(tireType.equals(manageDevice.getRightBDevice())) {
            return 3;
        }else if(tireType.equals(manageDevice.getLeftFDevice())) {
            return 0;
        }else if(tireType.equals(manageDevice.getRightFDevice())) {
            return 1;
        }
        return 10;
    }


    private <T> void toSubscribe(Observable<T> o,Subscriber<T> s){
        o.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s);
    }
    private static float parsePress(byte data){
        float integer = DigitalTrans.byteToAlgorism((byte) ((data>>4)&(byte) 0x0F));
        float decimal =DigitalTrans.byteToAlgorism((byte) (data&(byte) 0x0F));
        return integer+decimal/10;
    }
}
