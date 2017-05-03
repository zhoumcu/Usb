package com.victon.tpms.common.usb;

/**
 * author：Administrator on 2016/12/22 15:31
 * company: xxxx
 * email：1032324589@qq.com
 */

public interface UsbControl {
    public void sendData(byte[] bt);
    public byte[] receviceData();
    public void stopUsbDevice();
    public void startUsbDevice();
}
