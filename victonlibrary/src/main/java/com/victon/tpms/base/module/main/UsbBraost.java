package com.victon.tpms.base.module.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.victon.tpms.common.usb.UsbComService;

/**
 * author：Administrator on 2016/12/20 16:10
 * company: xxxx
 * email：1032324589@qq.com
 */

public class UsbBraost extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        String action = intent.getAction();

//        if (!action.equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
//            Log.e("pangsheng", "静态广播接收器设备插入");
//            if (!VictonBaseApplication.getInstance().isRunningForeground(context)) {
////                Intent mainActivityIntent = new Intent(context, MainForServiceActivity.class);  // 要启动的Activity
////                mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                context.startActivity(mainActivityIntent);
//                Intent mainActivityIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
//                context.startActivity(mainActivityIntent);
//            }
//            if (!VictonBaseApplication.getInstance().isServiceRunning(context)) {
//                Intent intent1 = new Intent(context, UsbComService.class);
//                context.startService(intent1);
//            }

//        }
//        } else if (action.equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
//            Log.e("pangsheng", "静态广播接收器设备拔出");
//        }
        Intent intent2 = new Intent(context,UsbComService.class);
        intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if(intent2!=null)
            context.startService(intent2);
    }
}
