package com.victon.tpms.common.usb;

import com.victon.tpms.common.utils.DigitalTrans;

import java.io.Serializable;

/**
 * author：Administrator on 2016/12/26 11:09
 * company: xxxx
 * email：1032324589@qq.com
 */

public class UsbData implements Serializable{
    private byte deviceID;
    private byte command;
    private byte[] data;
    private byte tireType;
    private byte tempValues;
    private byte pressValues;
    private byte tireStatus;

    public byte getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(byte deviceID) {
        this.deviceID = deviceID;
    }

    public byte getCommand() {
        return command;
    }

    public void setCommand(byte command) {
        this.command = command;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getTireType(boolean isbund) {
        if(isbund) return DigitalTrans.byteToString((byte) ((tireType&0xF0)>>4));
        return DigitalTrans.byteToString(tireType);
    }
    public String getTireType() {
        return DigitalTrans.byteToString(tireType);
    }
    public void setTireType(byte tireType) {
        this.tireType = tireType;
    }

    public byte getTempValues() {
        return tempValues;
    }

    public void setTempValues(byte tempValues) {
        this.tempValues = tempValues;
    }

    public byte getPressValues() {
        return pressValues;
    }

    public void setPressValues(byte pressValues) {
        this.pressValues = pressValues;
    }

    public byte getTireStatus() {
        return tireStatus;
    }

    public void setTireStatus(byte tireStatus) {
        this.tireStatus = tireStatus;
    }

    public int getViewPosition() {
        return 0;
    }
}
