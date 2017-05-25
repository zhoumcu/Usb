package com.victon.tpms.common.helper;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.victon.tpms.R;
import com.victon.tpms.base.module.main.adapter.PressureAdapter;
import com.victon.tpms.entity.ManageDevice;
import com.victon.tpms.common.view.BleData;
import com.victon.tpms.common.utils.SoundPlayUtils;
import com.victon.tpms.common.utils.VibratorUtil;

import java.util.List;
import java.util.Map;

/**
 * Created by zhoumcu on 2016/8/26.
 */
public class OnMainPagerViewHelper extends ViewHelper{

    private static PressureAdapter adpter;
    private static GridView gridView;
    private static long vibratorTime = 1500;
    private static Map<Integer, BleData> mapList1;

    public static View generatePressuserGrid(Context context, LinearLayout container, List<BleData> list) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_grid_pressuer,container,false);
        gridView = (GridView) view.findViewById(R.id.gridView);
        if(list.size()>0){
            adpter = new PressureAdapter(context,gridView,list);
            gridView.setAdapter(adpter);
//            adpter.setListViewHeightBasedOnChildren((Activity) context,gridView);
        }
        return view;
    }

    public static View generatePressuserGrid(Context context, LinearLayout container, Map<Integer,BleData> mapList) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_grid_pressuer,container,false);
        gridView = (GridView) view.findViewById(R.id.gridView);
        mapList1 = mapList;
        if(mapList.size()>0){
            adpter = new PressureAdapter(context,gridView,mapList);
            gridView.setAdapter(adpter);
//            adpter.setListViewHeightBasedOnChildren((Activity) context,gridView);
        }
        return view;
    }
    public static void updatePressuserGrid(List<BleData> bleData) {
        if(adpter!=null&&gridView!=null)
            adpter.updateItem(bleData);
    }
    public static void updatePressuserGrid() {
        if(adpter!=null&&gridView!=null)
            adpter.updateItem(mapList1);
    }

    public static void updatePressuserGridItem(int positionOfView, BleData bleData) {
        if(adpter!=null&&gridView!=null){
            adpter.updateItem(gridView,bleData,positionOfView);
        }
    }

    public static void updatePressuserGrid(Map<Integer, BleData> bleDataMap) {
        if(adpter!=null&&gridView!=null)
            adpter.updateItem(bleDataMap);
    }

    /**
     * 白天模式下，接收到蓝牙发送数据，进行异常报警UI
     * @param context
     * @param manageDevice
     * @param deviceAddress
     * @param strAddress
     * @param noticeStr
     */
    public static void bleIsExceptionForDay(Activity context, ManageDevice manageDevice, String deviceAddress, String strAddress, String noticeStr) {
        if(strAddress.equals(manageDevice.getLeftBDevice())) {
            if(!noticeStr.equals(manageDevice.leftB_preContent))
                manageDevice.leftB_notify = false;
            manageDevice.leftB_preContent = noticeStr;
            if(!manageDevice.leftB_notify) {
                manageDevice.leftB_notify = true;
                SoundPlayUtils.play(5);
                VibratorUtil.Vibrate(context, vibratorTime);   //震动100ms
            }
        }else if(strAddress.equals(manageDevice.getRightBDevice())) {

            if(!noticeStr.equals(manageDevice.rightB_preContent))
                manageDevice.rightB_notify = false;
            manageDevice.rightB_preContent = noticeStr;
            if(!manageDevice.rightB_notify) {
                manageDevice.rightB_notify = true;
                SoundPlayUtils.play(3);
                VibratorUtil.Vibrate(context, vibratorTime);   //震动100ms
            }
        }else if(strAddress.equals(manageDevice.getLeftFDevice())) {
            if(!noticeStr.equals(manageDevice.leftF_preContent))
                manageDevice.leftF_notify = false;
            manageDevice.leftF_preContent = noticeStr;
            if(!manageDevice.leftF_notify) {
                manageDevice.leftF_notify = true;
                SoundPlayUtils.play(6);
                VibratorUtil.Vibrate(context, vibratorTime);   //震动100ms
            }
        }else if(strAddress.equals(manageDevice.getRightFDevice())) {
            if(!noticeStr.equals(manageDevice.rightF_preContent))
                manageDevice.rightF_notify = false;
            manageDevice.rightF_preContent = noticeStr;
            if(!manageDevice.rightF_notify) {
                manageDevice.rightF_notify = true;
                SoundPlayUtils.play(4);
                VibratorUtil.Vibrate(context, vibratorTime);   //震动100ms
            }
        }
    }
}
