package com.victon.tpms.base.module.main.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.victon.tpms.R;
import com.victon.tpms.base.VictonBaseApplication;
import com.victon.tpms.entity.ManageDevice;
import com.victon.tpms.entity.MyBluetoothDevice;
import com.victon.tpms.base.db.dao.DeviceDao;
import com.victon.tpms.common.utils.Constants;
import com.victon.tpms.common.utils.ToastUtil;

/**
 * Created by Administrator on 2016/6/6.
 */
public class ChangeTablentDeviceFragment extends Fragment implements View.OnClickListener{
    private TextView topleftAdjust;
    private TextView toprightAdjust;
    private TextView bottomleftAdjust;
    private TextView bottomrightAdjust;
    private String curStrAddr;
    private String preStrAddr;
    private String preStr;
    private String curStr;
    private DeviceDao deviceDaoUtils;
    private TextView imgTopleft;
    private TextView imgTopright;
    private TextView imgBottomleft;
    private TextView imgBottomright;
    private ManageDevice manageDevice;

    public static ChangeTablentDeviceFragment newInstance(ManageDevice device) {
        ChangeTablentDeviceFragment fragment = new ChangeTablentDeviceFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("BLE_DEVICE", device);
        fragment.setArguments(bundle);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frg_change_position, container, false);
//        getActivity().registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        manageDevice = (ManageDevice) getArguments().getSerializable("BLE_DEVICE");
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initUI();
        deviceDaoUtils = VictonBaseApplication.getDeviceDao();
    }
    private void initUI() {
        imgTopleft = (TextView)getView(). findViewById(R.id.img_topleft);
        imgTopright = (TextView) getView().findViewById(R.id.img_topright);
        imgBottomleft = (TextView)getView(). findViewById(R.id.img_bottomleft);
        imgBottomright = (TextView)getView(). findViewById(R.id.img_bottomright);
        topleftAdjust = (TextView)getView().findViewById(R.id.topleft_adjust);
        toprightAdjust = (TextView) getView().findViewById(R.id.topright_adjust);
        bottomleftAdjust = (TextView) getView().findViewById(R.id.bottomleft_adjust);
        bottomrightAdjust = (TextView) getView().findViewById(R.id.bottomright_adjust);
        bottomleftAdjust.setOnClickListener(this);
        bottomrightAdjust.setOnClickListener(this);
        topleftAdjust.setOnClickListener(this);
        toprightAdjust.setOnClickListener(this);

    }
    private void initData(MyBluetoothDevice device, TextView btn) {
        if(device==null) {
            btn.setText(getResources().getString(R.string.erronofind));
            btn.setEnabled(false);
        }else if(!device.isSuccessComm()) {
            btn.setText(getResources().getString(R.string.erroinfo));
            btn.setEnabled(false);
        }
    }
    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.topleft_adjust) {
            if (topleftAdjust.getText().equals(getResources().getString(R.string.adjust))) {
                topleftAdjust.setVisibility(View.GONE);
                toprightAdjust.setText(getResources().getString(R.string.adjustTo));
                bottomleftAdjust.setText(getResources().getString(R.string.adjustTo));
                bottomrightAdjust.setText(getResources().getString(R.string.adjustTo));
                preStr = "左前轮"/*+ manageDevice.getLeftFDevice()*/;
                preStrAddr = manageDevice.getLeftFDevice();
                //App.getInstance().speak("您选择了调换左前轮");
            } else {
                reset();
                curStr = "左前轮"/*+manageDevice.getLeftFDevice()*/;
                curStrAddr = manageDevice.getLeftFDevice();
//                    ToastUtil.show(preStr + "与" + curStr + "对调");
                showDialog(preStr, curStr, preStrAddr, curStrAddr);
            }


        } else if (i == R.id.topright_adjust) {
            if (toprightAdjust.getText().equals(getResources().getString(R.string.adjust))) {
                toprightAdjust.setVisibility(View.GONE);
                topleftAdjust.setText(getResources().getString(R.string.adjustTo));
                bottomleftAdjust.setText(getResources().getString(R.string.adjustTo));
                bottomrightAdjust.setText(getResources().getString(R.string.adjustTo));
                preStr = "右前轮"/*+manageDevice.getRightFDevice()*/;
                preStrAddr = manageDevice.getRightFDevice();
            } else {
                reset();
                curStr = "右前轮"/*+manageDevice.getRightFDevice()*/;
                curStrAddr = manageDevice.getRightFDevice();
//                    ToastUtil.show(preStr + "与" + curStr + "对调");
                showDialog(preStr, curStr, preStrAddr, curStrAddr);
            }

        } else if (i == R.id.bottomleft_adjust) {
            if (bottomleftAdjust.getText().equals(getResources().getString(R.string.adjust))) {
                bottomleftAdjust.setVisibility(View.GONE);
                topleftAdjust.setText(getResources().getString(R.string.adjustTo));
                toprightAdjust.setText(getResources().getString(R.string.adjustTo));
                bottomrightAdjust.setText(getResources().getString(R.string.adjustTo));
                preStr = "左后轮"/*+manageDevice.getLeftBDevice()*/;
                preStrAddr = manageDevice.getLeftBDevice();
            } else {
                reset();
                curStr = "左后轮"/*+manageDevice.getLeftBDevice()*/;
                curStrAddr = manageDevice.getLeftBDevice();
//                    ToastUtil.show(preStr + "与" + curStr + "对调");
                showDialog(preStr, curStr, preStrAddr, curStrAddr);
            }

        } else if (i == R.id.bottomright_adjust) {
            if (bottomrightAdjust.getText().equals(getResources().getString(R.string.adjust))) {
                bottomrightAdjust.setVisibility(View.GONE);
                topleftAdjust.setText(getResources().getString(R.string.adjustTo));
                toprightAdjust.setText(getResources().getString(R.string.adjustTo));
                bottomleftAdjust.setText(getResources().getString(R.string.adjustTo));
                preStr = "右后轮"/*+manageDevice.getRightBDevice()*/;
                preStrAddr = manageDevice.getRightBDevice();
            } else {
                reset();
                curStr = "右后轮"/*+manageDevice.getRightBDevice()*/;
                curStrAddr = manageDevice.getRightBDevice();
//                    ToastUtil.show(preStr + "与" + curStr + "对调");
                showDialog(preStr, curStr, preStrAddr, curStrAddr);
            }

        }
    }
    /**
     *
     * @param preStr 被调换的位置
     * @param curStr
     */
    private void showDialog(final String preStr, final String curStr,final String preStrAddr,final String curStrAddr)
    {
//        App.getInstance().speak(preStr+"与"+curStr+"进行对调，请选择确定或者取消");
        new AlertDialog.Builder(getActivity()).setTitle("系统提示")//设置对话框标题
                .setMessage(preStr+"  与  "+curStr+"  进行对调？")//设置显示的内容
                .setPositiveButton("确定",new DialogInterface.OnClickListener() {//添加确定按钮
                    @Override
                    public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                        // TODO Auto-generated method stub
//                         finish();
                        dialog.dismiss();
                        if(preStr.contains("左前轮")) {
                            manageDevice.setLeftFDevice(curStrAddr);
                            deviceDaoUtils.update(Constants.LEFT_F,Constants.MY_CAR_DEVICE,curStrAddr);
                            imgTopleft.setText(curStr);
                        }else if(preStr.contains("右前轮")) {
                            manageDevice.setRightFDevice(curStrAddr);
                            deviceDaoUtils.update(Constants.RIGHT_F,Constants.MY_CAR_DEVICE,curStrAddr);
                            imgTopright.setText(curStr);
                        }else if(preStr.contains("左后轮")) {
                            manageDevice.setLeftBDevice(curStrAddr);
                            deviceDaoUtils.update(Constants.LEFT_B,Constants.MY_CAR_DEVICE,curStrAddr);
                            imgBottomleft.setText(curStr);
                        }else if(preStr.contains("右后轮")) {
                            manageDevice.setRightBDevice(curStrAddr);
                            deviceDaoUtils.update(Constants.RIGHT_B,Constants.MY_CAR_DEVICE,curStrAddr);
                            imgBottomright.setText(curStr);
                        }
                        if(curStr.contains("左前轮")) {
                            manageDevice.setLeftFDevice(preStrAddr);
                            deviceDaoUtils.update(Constants.LEFT_F,Constants.MY_CAR_DEVICE,preStrAddr);
                            imgTopleft.setText(preStr);
                        }else if(curStr.contains("右前轮"))
                        {
                            manageDevice.setRightFDevice(preStrAddr);
                            deviceDaoUtils.update(Constants.RIGHT_F,Constants.MY_CAR_DEVICE,preStrAddr);
                            imgTopright.setText(preStr);
                        }else if(curStr.contains("左后轮")) {
                            manageDevice.setLeftBDevice(preStrAddr);
                            deviceDaoUtils.update(Constants.LEFT_B,Constants.MY_CAR_DEVICE,preStrAddr);
                            imgBottomleft.setText(preStr);
                        }else if(curStr.contains("右后轮")) {
                            manageDevice.setRightBDevice(preStrAddr);
                            deviceDaoUtils.update(Constants.RIGHT_B,Constants.MY_CAR_DEVICE,preStrAddr);
                            imgBottomright.setText(preStr);
                        }
                        ToastUtil.show(preStr + "与" + curStr + "已经对调");
                    }
                }).setNegativeButton("不用了",new DialogInterface.OnClickListener() {//添加返回按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {//响应事件
                // TODO Auto-generated method stub
//                    Log.i("alertdialog"," 请保存数据！");
//                    finish();
                dialog.dismiss();
            }
        }).show();//在按键响应事件中显示此对话框
    }
    private void showEditUI() {
        reset();
    }

    private void dissmEditUI() {
        topleftAdjust.setVisibility(View.GONE);
        toprightAdjust.setVisibility(View.GONE);
        bottomleftAdjust.setVisibility(View.GONE);
        bottomrightAdjust.setVisibility(View.GONE);
    }

    private void reset() {
        bottomleftAdjust.setText(getResources().getString(R.string.adjust));
        topleftAdjust.setText(getResources().getString(R.string.adjust));
        toprightAdjust.setText(getResources().getString(R.string.adjust));
        bottomrightAdjust.setText(getResources().getString(R.string.adjust));
        topleftAdjust.setVisibility(View.VISIBLE);
        toprightAdjust.setVisibility(View.VISIBLE);
        bottomleftAdjust.setVisibility(View.VISIBLE);
        bottomrightAdjust.setVisibility(View.VISIBLE);
    }
}
