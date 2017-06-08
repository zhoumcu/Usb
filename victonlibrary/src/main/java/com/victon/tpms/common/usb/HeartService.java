package com.victon.tpms.common.usb;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import com.victon.tpms.base.VictonBaseApplication;
import com.victon.tpms.common.utils.Logger;

import java.util.Timer;
import java.util.TimerTask;

/**
 * author：Administrator on 2017/6/2 14:30
 * company: xxxx
 * email：1032324589@qq.com
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class HeartService extends Service{

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context paramContext, Intent paramIntent) {
            if (paramIntent.getAction().equals("com.cz.action.exit_app"))
                Logger.i("cz com.cz.action.exit_app");
            try {
                Intent localIntent = new Intent(HeartService.this, UsbComService.class);
                if (localIntent != null)
                    HeartService.this.stopService(localIntent);
                HeartService.this.stopSelf();
                return;
            }
            catch (Exception localException) {
            }
        }
    };
    Timer mTimer = null;
    TimerTask mTimerTask = null;

    private void startTimer() {
        if (this.mTimer == null)
            this.mTimer = new Timer();
        if (this.mTimerTask == null)
            this.mTimerTask = new TimerTask() {
                public void run() {
                    if (!VictonBaseApplication.getInstance().isServiceRunning(HeartService.this,"com.victon.tpms.common.usb.UsbComService")) {
                        Intent localIntent = new Intent(HeartService.this, UsbComService.class);
                        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        HeartService.this.startService(localIntent);
                    }
                }
            };
    }

    private void stopTimer() {
        if (this.mTimer != null) {
            this.mTimer.cancel();
            this.mTimer = null;
        }
        if (this.mTimerTask != null) {
            this.mTimerTask.cancel();
            this.mTimerTask = null;
        }
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Logger.e("cz onCreate ");
        IntentFilter localIntentFilter = new IntentFilter();
        localIntentFilter.addAction("com.cz.action.exit_app");
        this.registerReceiver(mReceiver, localIntentFilter);
    }
    public void onDestroy() {
        super.onDestroy();
        stopTimer();
    }

    public int onStartCommand(Intent paramIntent, int paramInt1, int paramInt2) {
        Logger.e("cz onStartCommand ");
        stopTimer();
        startTimer();
        setTimeNs(1L);
        return START_STICKY;
    }

    void setTimeNs(long paramLong) {
        if ((this.mTimer != null) && (this.mTimerTask != null))
            this.mTimer.schedule(this.mTimerTask, paramLong * 1000L, 1000L * paramLong);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
