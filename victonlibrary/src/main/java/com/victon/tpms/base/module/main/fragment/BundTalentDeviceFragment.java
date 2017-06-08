package com.victon.tpms.base.module.main.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.victon.tpms.R;
import com.victon.tpms.base.VictonBaseApplication;
import com.victon.tpms.base.db.dao.DeviceDao;
import com.victon.tpms.base.db.entity.Device;
import com.victon.tpms.base.module.config.ConfigTablentDevice;
import com.victon.tpms.base.widget.LoadingDialog;
import com.victon.tpms.base.widget.NotifyDialog;
import com.victon.tpms.common.usb.UsbComService;
import com.victon.tpms.common.usb.UsbData;
import com.victon.tpms.common.utils.Constants;
import com.victon.tpms.common.utils.DigitalTrans;
import com.victon.tpms.common.utils.Logger;
import com.victon.tpms.common.utils.SoundPlayUtils;
import com.victon.tpms.common.view.frame.BaseBleConnetFragment;
import com.victon.tpms.entity.ManageDevice;

/**
 * Created by Administrator on 2016/6/6.
 */
public class BundTalentDeviceFragment extends BaseBleConnetFragment implements View.OnClickListener {

    private final static String TAG = BundTalentDeviceFragment.class.getSimpleName();

    private final int maxLenght = -100;
    private TextView tv_note_left_from;
    private TextView tv_note_right_from;
    private TextView tv_note_left_back;
    private TextView tv_note_right_back;
    private Button topleft_next;
    private Button topright_next;
    private Button bottomleft_next;
    private Button bottomright_next;
    public static int leftF = 0;
    public static int rightF = 1;
    public static int leftB = 2;
    public static int rightB =3;
    public static int none =5;
    private int state;
    private DeviceDao deviceDaoUtils;
    private Device deviceDao;
    private LoadingDialog loadDialog;
    private ManageDevice manageDevice;
    private boolean isFirst = true;

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(NotifyDialog.ACTION_BTN_STATE);
        intentFilter.addAction(NotifyDialog.ACTION_BTN_NEXT);
        return intentFilter;
    }

    public static BundTalentDeviceFragment newInstance(ManageDevice device) {
        BundTalentDeviceFragment fragment = new BundTalentDeviceFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("BLE_DEVICE", device);
        fragment.setArguments(bundle);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frg_bund, container, false);
        manageDevice = (ManageDevice) getArguments().getSerializable("BLE_DEVICE");
        getActivity().registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        super.setManageDevice(manageDevice);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initUI();
        initIsCofigBle();
        loadDialog = new LoadingDialog(getActivity());
    }
    @Override
    protected void initData() {
        deviceDaoUtils = VictonBaseApplication.getDeviceDao();
        deviceDao = new Device();
    }

    @Override
    protected void initRunnable() {

    }

    @Override
    protected void initConfig() {
    }

    private void initIsCofigBle() {
//        if(manageDevice.getLeftFDevice()==null||manageDevice.getLeftFDevice().equals("null")) {
            topleft_next.setText("点击绑定");
            topleft_next.setBackgroundResource(R.mipmap.b_btn);
//        }
//        else {
//            topleft_next.setText(R.string.unbund);
//            topleft_next.setBackgroundResource(R.mipmap.unbund);
//        }
//        if(manageDevice.getRightFDevice()==null||manageDevice.getRightFDevice().equals("null")) {
            topright_next.setText("点击绑定");
            topright_next.setBackgroundResource(R.mipmap.b_btn);
//        }
//        else {
//            topright_next.setText(R.string.unbund);
//            topright_next.setBackgroundResource(R.mipmap.unbund);
//        }
//        if(manageDevice.getLeftBDevice()==null||manageDevice.getLeftBDevice().equals("null")) {
            bottomleft_next.setText("点击绑定");
            bottomleft_next.setBackgroundResource(R.mipmap.b_btn);
//        }
//        else {
//            bottomleft_next.setText(R.string.unbund);
//            bottomleft_next.setBackgroundResource(R.mipmap.unbund);
//        }
//        if(manageDevice.getRightBDevice()==null||manageDevice.getRightBDevice().equals("null")) {
            bottomright_next.setText("点击绑定");
            bottomright_next.setBackgroundResource(R.mipmap.b_btn);
//        }
//        else {
//            bottomright_next.setText(R.string.unbund);
//            bottomright_next.setBackgroundResource(R.mipmap.unbund);
//        }
    }
    private void initUI() {
        ImageView back = (ImageView) getView().findViewById(R.id.back);
        ImageView setting = (ImageView)getView().findViewById(R.id.img_set);
        setting.setVisibility(View.GONE);
        back.setOnClickListener(this);
        tv_note_left_from = (TextView)getView(). findViewById(R.id.tv_note_left_from);
        tv_note_right_from = (TextView) getView().findViewById(R.id.tv_note_right_from);
        tv_note_left_back = (TextView) getView().findViewById(R.id.tv_note_left_back);
        tv_note_right_back = (TextView) getView().findViewById(R.id.tv_note_right_back);

//        topleft_ok = (Button) getView().findViewById(R.id.ll_topleft).findViewById(R.id.btn_ok);
        topleft_next = (Button) getView().findViewById(R.id.ll_topleft).findViewById(R.id.btn_next);

//        topright_ok = (Button) getView().findViewById(R.id.ll_topright).findViewById(R.id.btn_ok);
        topright_next = (Button) getView().findViewById(R.id.ll_topright).findViewById(R.id.btn_next);

//        bottomleft_ok = (Button)getView(). findViewById(R.id.ll_bottomleft).findViewById(R.id.btn_ok);
        bottomleft_next = (Button)getView(). findViewById(R.id.ll_bottomleft).findViewById(R.id.btn_next);

//        bottomright_ok = (Button)getView(). findViewById(R.id.ll_bottomright).findViewById(R.id.btn_ok);
        bottomright_next = (Button)getView(). findViewById(R.id.ll_bottomright).findViewById(R.id.btn_next);

        topleft_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendButtonEvent(topleft_next,leftF);
            }
        });
        topright_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendButtonEvent(topright_next,rightF);
            }
        });
        bottomleft_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendButtonEvent(bottomleft_next,leftB);
            }
        });
        bottomright_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendButtonEvent(bottomright_next,rightB);
            }
        });
    }
    private void sendButtonEvent(Button tv,int event){
        if(tv.getText().toString().equals(getResources().getString(R.string.unbund))) {
//            showNotifyDialog(event);
        }else if(tv.getText().toString().equals(getResources().getString(R.string.unbund_success))) {
            bundDevice(event);
//            tv.setText(getResources().getString(R.string.unbund));
//            tv.setBackgroundResource(R.mipmap.unbund);
//            tv.setVisibility(View.VISIBLE);
        }
    }
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==0) {
                Intent it =new Intent(getActivity(),NotifyDialog.class);
                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                it.putExtra(ConfigTablentDevice.PAIRED_OK,"重试");
                it.putExtra(ConfigTablentDevice.NONE_NEXT,true);
                startActivity(it);
                isFirst = true;
            }
        }
    };

    private void unBundDevice(Button tvNext,int state) {
        if(UsbComService.receviceUsb==null) return;
        tvNext.setText("点击绑定");
        tvNext.setBackgroundResource(R.mipmap.b_btn);
        Logger.e(TAG,"正在解绑。。。"+state);
        //保存蓝牙mac地址
        deviceDaoUtils.update(state,Constants.MY_CAR_DEVICE,null);
        switch (state) {
            case 0:
                manageDevice.setLeftFDevice(null);
                UsbComService.receviceUsb.sendData(DigitalTrans.hex2byte(Constants.CANCEL_PAIRED));
                break;
            case 1:
                manageDevice.setRightFDevice(null);
                UsbComService.receviceUsb.sendData(DigitalTrans.hex2byte(Constants.CANCEL_PAIRED));
                break;
            case 2:
                manageDevice.setLeftBDevice(null);
                UsbComService.receviceUsb.sendData(DigitalTrans.hex2byte(Constants.CANCEL_PAIRED));
                break;
            case 3:
                manageDevice.setRightBDevice(null);
                UsbComService.receviceUsb.sendData(DigitalTrans.hex2byte(Constants.CANCEL_PAIRED));
                break;
        }

    }
    private void bundDevice(int states) {
        if(UsbComService.receviceUsb==null) return;
        showDialog("开始配对模块。。。",false);
        state = states;
        switch (states) {
            case 0:
                UsbComService.receviceUsb.sendData(DigitalTrans.hex2byte(Constants.PAIRED_LEFT_FROM));
//                VictonBaseApplication.getInstance().usbService.receviceUsb.sendData(DigitalTrans.hex2byte(Constants.PAIRED_LEFT_FROM));
                break;
            case 1:
                UsbComService.receviceUsb.sendData(DigitalTrans.hex2byte(Constants.PAIRED_RIGHT_FROM));
//                VictonBaseApplication.getInstance().usbService.receviceUsb.sendData(DigitalTrans.hex2byte(Constants.PAIRED_RIGHT_FROM));
                break;
            case 2:
                UsbComService.receviceUsb.sendData(DigitalTrans.hex2byte(Constants.PAIRED_LEFT_BACK));
//                VictonBaseApplication.getInstance().usbService.receviceUsb.sendData(DigitalTrans.hex2byte(Constants.PAIRED_LEFT_BACK));
                break;
            case 3:
                UsbComService.receviceUsb.sendData(DigitalTrans.hex2byte(Constants.PAIRED_RIGHT_BACK));
//                VictonBaseApplication.getInstance().usbService.receviceUsb.sendData(DigitalTrans.hex2byte(Constants.PAIRED_RIGHT_BACK));
                break;
        }
        isFirst = true;
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            String action = intent.getAction();
           if (NotifyDialog.ACTION_BTN_STATE.equals(action)) {
               showDialog("正在配对模块。。。",false);
               isFirst = true;
               UsbComService.receviceUsb.sendData(DigitalTrans.hex2byte(Constants.CANCEL_PAIRED));
               bundDevice(state);
            }else if (NotifyDialog.ACTION_BTN_NEXT.equals(action)) {
               Logger.e(TAG,"完成"+state);
               UsbComService.receviceUsb.sendData(DigitalTrans.hex2byte(Constants.CANCEL_PAIRED));
               isFirst = true;
            }
        }
    };

    private void bleIsFind(UsbData device) {
        byte data = device.getData()[0];
        byte status = (byte) (data&0xF0);
        switch (status){
            case (byte) 0x00:
                paresData(data,device);
                break;
            case (byte) 0x10:
                paresData(data,device);
                break;
            case (byte) 0x20:
                paresData(data,device);
                break;
            case (byte) 0x30:
                paresData(data,device);
                break;
        }
    }
    private void paresData( byte status,UsbData device) {
        switch (status&(byte) 0x0F){
            case (byte) 0x00://进入配对
                showDialog("进入配对。。。",false);
                break;
            case (byte) 0x01://配对成功
                onSuccess(device);
                Logger.e(TAG,"配对成功！");
                break;
            case (byte) 0x02://配对失败或超时退出
                mHandler.sendEmptyMessage(0);
                break;
        }
    }
    private void onSuccess(UsbData device) {
        if(!isFirst) return;
        isFirst = false;
        switch (device.getData()[0]&(byte) 0xF0)  {
            case  (byte) 0x00:
                setUsedToTrue(device,topleft_next,tv_note_left_from,"左前轮");
                break;
            case (byte) 0x10:
                setUsedToTrue(device,topright_next,tv_note_right_from,"右前轮");
                break;
            case (byte) 0x20:
                setUsedToTrue(device,bottomleft_next,tv_note_left_back,"左后轮");
                break;
            case (byte) 0x30:
                setUsedToTrue(device,bottomright_next,tv_note_right_back,"右后轮");
                break;
        }
    }
    private void setUsedToTrue(UsbData device,Button currentBtn ,TextView currentTv,String str) {
        Intent it =new Intent(getActivity(),NotifyDialog.class);
        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        it.putExtra(ConfigTablentDevice.PAIRED_OK,"完成");
        it.putExtra(ConfigTablentDevice.NONE_NEXT,true);
        startActivity(it);
        loadDialog.stopCount(true);
//        currentBtn.setText(getResources().getString(R.string.unbund));
//        currentBtn.setBackgroundResource(R.mipmap.unbund);
//        currentBtn.setVisibility(View.VISIBLE);
        //保存数据到本地
        deviceDaoUtils.update(state, Constants.MY_CAR_DEVICE,device.getTireType(true));
        currentTv.setText(str+"：\n"+device.getTireType(true));
        switch (state) {
            case 0:
                manageDevice.setLeftFDevice(device.getTireType(true));
                break;
            case 1:
                manageDevice.setRightFDevice(device.getTireType(true));
                break;
            case 2:
                manageDevice.setLeftBDevice(device.getTireType(true));
                break;
            case 3:
                manageDevice.setRightBDevice(device.getTireType(true));
                break;
        }
        state = none;
        SoundPlayUtils.play(2);
    }
    private void showDialog(String str,boolean isConnect) {
        if(!loadDialog.isShowing()) {
            loadDialog.setText(str);
            loadDialog.show();
            loadDialog.setCountNum(120);
            loadDialog.startCount(new LoadingDialog.OnListenerCallBack() {
                @Override
                public void onListenerCount() {
                    mHandler.sendEmptyMessage(0);
                }
            });
        }else{
            loadDialog.reStartCount(str,120);
        }
    }
    private void showDialog(String str) {
        if(!loadDialog.isShowing()) {
            loadDialog.setText(str);
            loadDialog.show();
        }else{
            loadDialog.stopCount(true);
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            getActivity().unregisterReceiver(mGattUpdateReceiver);
        }catch (IllegalArgumentException e) {
            Logger.e("mHomeKeyEventReceiver:"+e.toString());
        }
    }

    @Override
    public void broadcastUpdate(String action, UsbData gatt) {
        bleIsFind(gatt);
        Logger.d(TAG,"收到数据类型："+gatt.getTireType());
//        if(!isBundBle(gatt)) {
//            bleIsFind(gatt);
//            Logger.d(TAG,"收到数据类型："+gatt.getTireType());
//        }else{
//            Logger.d(TAG,"已配对数据类型："+gatt.getTireType());
//        }
    }
    private boolean isBundBle(UsbData device) {
        if(device.getTireType(true).equals(manageDevice.getLeftFDevice()))
            return true;
        if(device.getTireType(true).equals(manageDevice.getRightFDevice()))
            return true;
        if(device.getTireType(true).equals(manageDevice.getLeftBDevice()))
            return true;
        if(device.getTireType(true).equals(manageDevice.getRightBDevice()))
            return true;
        return false;
    }
    /**
     *
     */
    private void showNotifyDialog(final int state) {
//        App.getInstance().speak(preStr+"与"+curStr+"进行对调，请选择确定或者取消");
//        new AlertDialog.Builder(getActivity()).setTitle("系统提示")//设置对话框标题
//                .setMessage("请确定传感器已经损坏或者无法正常工作情况下，才能使用解绑功能，否则解绑成功之后，无法进行绑定，" +
//                        "如果确定请点击确定按钮进行解绑，否则请选择取消")//设置显示的内容
//                .setPositiveButton("确定",new DialogInterface.OnClickListener() {//添加确定按钮
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
//                        // TODO Auto-generated method stub
//                        dialog.dismiss();
                        switch (state) {
                            case 0:
                                unBundDevice(topleft_next,leftF);
                                break;
                            case 1:
                                unBundDevice(topright_next,rightF);
                                break;
                            case 2:
                                unBundDevice(bottomleft_next,leftB);
                                break;
                            case 3:
                                unBundDevice(bottomright_next,rightB);
                                break;
                        }
//                    }
//                }).setNegativeButton("取消",new DialogInterface.OnClickListener() {//添加返回按钮
//            @Override
//            public void onClick(DialogInterface dialog, int which) {//响应事件
//                // TODO Auto-generated method stub
//                dialog.dismiss();
//            }
//        }).show();//在按键响应事件中显示此对话框
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.back){
            getFragmentManager().popBackStack();
        }
    }
}
