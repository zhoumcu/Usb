package com.victon.tpms.base.module.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.victon.tpms.common.usb.UsbComService;

/**
 * author：Administrator on 2016/12/20 16:10
 * company: xxxx
 * email：1032324589@qq.com
 */

public class ReceviceBraost extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        String action = intent.getAction();

        if (action.equals(UsbComService.SCAN_FOR_RESULT)) {
            Log.e("pangsheng", "接收到数据");
        }
    }
}
