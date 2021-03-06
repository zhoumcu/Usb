package com.victon.tpms.common.utils;


import android.widget.Toast;

import com.victon.tpms.base.VictonBaseApplication;


/**
 * ToastUtil
 *
 * @author JiangPing
 */
public class ToastUtil {
    /**
     * 解决多次点击Toast显示问题
     */
    private static Toast mToast = null;

    private ToastUtil() {
        throw new AssertionError();
    }

    public static void show(int resId) {
        if (mToast == null) {
            mToast = Toast.makeText(VictonBaseApplication.getInstance(), VictonBaseApplication.getInstance().getResources().getText(resId), Toast.LENGTH_SHORT);
        } else {
            mToast.setText(VictonBaseApplication.getInstance().getResources().getText(resId));
            mToast.setDuration(Toast.LENGTH_SHORT);
        }

        mToast.show();


    }

    public static void show(int resId, int duration) {
        if (mToast == null) {
            mToast = Toast.makeText(VictonBaseApplication.getInstance(), VictonBaseApplication.getInstance().getResources().getText(resId), duration);
        } else {
            mToast.setText(VictonBaseApplication.getInstance().getResources().getText(resId));
            mToast.setDuration(duration);
        }

        mToast.show();

    }

    public static void show(CharSequence text) {


        if (mToast == null) {
            mToast = Toast.makeText(VictonBaseApplication.getInstance(), text, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(text);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }

        mToast.show();


    }

    public static void show(int resId, Object... args) {

        if (mToast == null) {
            mToast = Toast.makeText(VictonBaseApplication.getInstance(), String.format(VictonBaseApplication.getInstance().getResources()
                    .getString(resId), args), Toast.LENGTH_SHORT);
        } else {
            mToast.setText(String.format(VictonBaseApplication.getInstance().getResources()
                    .getString(resId), args));
            mToast.setDuration(Toast.LENGTH_SHORT);
        }

        mToast.show();

    }

    public static void show(String format, Object... args) {

        if (mToast == null) {
            mToast = Toast.makeText(VictonBaseApplication.getInstance(), String.format(format, args), Toast.LENGTH_SHORT);
        } else {
            mToast.setText(String.format(format, args));
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

    public static void show(int resId, int duration, Object... args) {

        if (mToast == null) {
            mToast = Toast.makeText(VictonBaseApplication.getInstance(), String.format(VictonBaseApplication.getInstance().getResources()
                    .getString(resId), args), duration);
        } else {
            mToast.setText(String.format(VictonBaseApplication.getInstance().getResources()
                    .getString(resId), args));
            mToast.setDuration(duration);
        }
        mToast.show();

    }

    public static void show(String format, int duration, Object... args) {

        if (mToast == null) {
            mToast = Toast.makeText(VictonBaseApplication.getInstance(), String.format(format, args), duration);
        } else {
            mToast.setText(String.format(format, args));
            mToast.setDuration(duration);
        }
        mToast.show();
    }

    public static void show(CharSequence text, int duration) {
        if (mToast == null) {
            mToast = Toast.makeText(VictonBaseApplication.getInstance(), text, duration);
        } else {
            mToast.setText(text);
            mToast.setDuration(duration);
        }
        mToast.show();
    }

}
