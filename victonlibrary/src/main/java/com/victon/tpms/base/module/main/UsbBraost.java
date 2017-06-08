package com.victon.tpms.base.module.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbManager;

import com.victon.tpms.base.VictonBaseApplication;
import com.victon.tpms.base.module.main.activity.MainForServiceActivity;
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
        if (action.equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
//            Intent intent2 = new Intent(context,MainForServiceActivity.class);
////            if(VictonBaseApplication.getInstance().isRunningForeground(context))
////                intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.addCategory(Intent.CATEGORY_LAUNCHER);
//            intent.setAction(Intent.ACTION_MAIN);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
//            if(intent2!=null)
//                context.startActivity(intent2);
//            VictonBaseApplication.getInstance().launchApp(context);
//            Intent intent2 = new Intent(context, UsbComService.class);
//            context.startService(intent2);
            if(!VictonBaseApplication.getInstance().isRunningForeground(context)){
                Intent mainActivityIntent = new Intent(context, MainForServiceActivity.class);  // 要启动的Activity
                mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(mainActivityIntent);
            }
//            Intent intent1 = new Intent(context,UsbComService.class);
//            intent1.addFlags(1610612736);
//            context.startService(intent1);

        }else if(action.equals(UsbManager.ACTION_USB_DEVICE_DETACHED)){
            VictonBaseApplication.getInstance().exit();
            Intent intent2 = new Intent(context,UsbComService.class);
//            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            UsbComService.receviceUsb = null;
            if(intent2!=null)
                context.stopService(intent2);
        }
    }
}
