package com.victon.tpms.base.module.main.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.victon.tpms.R;
import com.victon.tpms.common.helper.OnMainPagerViewHelper;
import com.victon.tpms.common.usb.UsbData;
import com.victon.tpms.common.view.BleData;
import com.victon.tpms.entity.ManageDevice;

import java.util.Map;

/**
 * Created by Administrator on 2016/6/6.
 */
public class MainFragment extends WictonBleBaseFragment {

    private LinearLayout container;
    private View view;
    private ManageDevice manageDevice;

    public static MainFragment newInstance(ManageDevice device) {
        MainFragment fragment = new MainFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("BLE_DEVICE", device);
        fragment.setArguments(bundle);
        return fragment;
    }
    @Override
    protected View createViews(LayoutInflater inflater, ViewGroup parentContainer) {
        View root = inflater.inflate(R.layout.frg_grid_pressure,parentContainer,false);
        ImageView ivBcak = (ImageView) root.findViewById(R.id.iv_back);
        container = (LinearLayout) root.findViewById(R.id.container);
        manageDevice = (ManageDevice) getArguments().getSerializable("BLE_DEVICE");
        initView();
        return root;
    }
    private void initView(){
        initDefaultData(manageDevice);
    }

    @Override
    protected void onReceviceDateSucess(UsbData device, int positionOfView, BleData bleData) {
        OnMainPagerViewHelper.updatePressuserGridItem(positionOfView,bleData);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void bleIsNormal(BleData date, String strAddress, String noticeStr, float voltage) {

    }

    @Override
    protected void bleDicoverException(BleData bleData, String strAddress, String noticeStr) {
        OnMainPagerViewHelper.updatePressuserGridItem(bleData.getViewPosition(),bleData);
        OnMainPagerViewHelper.bleIsExceptionForDay(getActivity(),manageDevice,bleData.getDeviceAddress(),strAddress,noticeStr);
    }

    @Override
    protected void onDisconnectedDevice(UsbData device) {

    }

    @Override
    protected void onReceviceDateException(BleData bleData, int tyreType) {
        OnMainPagerViewHelper.updatePressuserGridItem(tyreType,bleData);
    }

    @Override
    protected void onReceviceDateSucess(UsbData device, Map<Integer,BleData> bleDataMap) {
        super.onReceviceDateSucess(device,bleDataMap);
        if(bleDataMap!=null&&bleDataMap.size()>0){
            if(view == null) {
                view = OnMainPagerViewHelper.generatePressuserGrid(getContext(), container, bleDataMap);
                container.addView(view);
            }else{
                OnMainPagerViewHelper.updatePressuserGrid(bleDataMap);
            }
        }
    }
}
