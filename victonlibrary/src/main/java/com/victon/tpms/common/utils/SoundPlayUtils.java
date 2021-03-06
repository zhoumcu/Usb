package com.victon.tpms.common.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;

import com.victon.tpms.R;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

/**
 * Created by Administrator on 2016/9/2.
 */
public class SoundPlayUtils {
    // SoundPool对象
    public static SoundPool mSoundPlayer = new SoundPool(10,
            AudioManager.STREAM_ALARM, 5);
    public static SoundPlayUtils soundPlayUtils;
    // 上下文
    static Context mContext;
    private static Handler mHandler = new Handler();
    private static Vector<Integer> mKillSoundQueue = new Vector<Integer>();
    private static Vector<Integer> mStopSoundQueue = new Vector<Integer>();
    private static boolean isWait = false;
    private static int countNum;
    /**
     * 初始化
     *
     * @param context
     */
    public static SoundPlayUtils init(Context context) {
        if (soundPlayUtils == null) {
            soundPlayUtils = new SoundPlayUtils();
        }
        // 初始化声音
        mContext = context;
        mSoundPlayer.load(mContext, R.raw.welcome, 1);// 1
        mSoundPlayer.load(mContext, R.raw.kspd, 1);// 2
        mSoundPlayer.load(mContext, R.raw.yh, 1);// 3
        mSoundPlayer.load(mContext, R.raw.yq, 1);// 4
        mSoundPlayer.load(mContext, R.raw.zh, 1);// 5
        mSoundPlayer.load(mContext, R.raw.zq, 1);// 6
        // 初始化播放音乐线程池
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                countNum = mKillSoundQueue.size();
                if(countNum>0){
                    mSoundPlayer.play(mKillSoundQueue.get(countNum-1), 1, 1, 0, 0, 1);
                    mKillSoundQueue.remove(countNum-1);
                }
            }
        },100,2000);
        return soundPlayUtils;
    }

    /**
     * 播放声音
     *
     * @param soundID
     */
    public static void play(int soundID) {
        isWait = false;
        if(!sortListData(mKillSoundQueue,soundID)){
            mKillSoundQueue.add(soundID);
        }
    }
    /**
     * 播放声音
     *
     * @param soundID
     */
    public static void stop(int soundID) {
        isWait = false;
        for (int i=0;i<mKillSoundQueue.size();i++){
            if(soundID !=i){
                mSoundPlayer.stop(i);
                mKillSoundQueue.remove(i);
                mStopSoundQueue.add(i);
            }
        }
        mSoundPlayer.play(soundID, 1, 1, 0, 0, 1);
        mKillSoundQueue = mStopSoundQueue;
    }

    private static boolean sortListData(Vector<Integer> mKillSoundQueue, int soundID) {
        if(mKillSoundQueue.size()==0) return false;
        for (Integer data : mKillSoundQueue){
            if(data==soundID){
                return true;
            }
        }
        return false;
    }

}