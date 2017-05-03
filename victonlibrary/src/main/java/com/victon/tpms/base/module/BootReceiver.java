package com.victon.tpms.base.module;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.victon.tpms.common.usb.UsbComService;

/**
 * author：Administrator on 2016/12/22 11:07
 * company: xxxx
 * email：1032324589@qq.com
 */

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("com.dbjtech.waiqin.destroy")) {
            //TODO
            //在这里写重新启动service的相关操作
            Intent intent1 = new Intent(context,UsbComService.class);
            context.startService(intent1);
        }else {
            Intent intent1 = new Intent(context,UsbComService.class);
            context.startService(intent1);
        }
    }

}
