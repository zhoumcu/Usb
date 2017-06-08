package com.victon.tpms.base.module;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.victon.tpms.base.module.main.activity.MainForServiceActivity;

/**
 * author：Administrator on 2016/12/22 11:07
 * company: xxxx
 * email：1032324589@qq.com
 */

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
//        if (intent.getAction().equals("com.dbjtech.waiqin.destroy")) {
            //TODO
            //在这里写重新启动service的相关操作
//            Intent intent1 = new Intent(context,MainForServiceActivity.class);
//            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(intent1);
//            Intent intent2 = new Intent(context,UsbComService.class);
//            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            if(intent2!=null)
//                context.startService(intent2);
//            PendingIntent sender = PendingIntent.getBroadcast(context, 0,   intent, 0);
//             long firstime = SystemClock.elapsedRealtime();
//             AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//
//            // 10秒一个周期，不停的发送广播
//             am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstime,
//                            10 * 1000, sender);
//        }else {
//            Intent intent1 = new Intent(context,MainForServiceActivity.class);
//            context.startActivity(intent1);
//        }
        //接收广播：安装更新后，自动启动自己。
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED) || intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) {
            Intent ootStartIntent = new Intent(context, MainForServiceActivity.class);
            ootStartIntent.addFlags(1610612736);
            context.startActivity(ootStartIntent);
        }else{
            Intent intent1 = new Intent(context,MainForServiceActivity.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent1);
        }
    }

}
