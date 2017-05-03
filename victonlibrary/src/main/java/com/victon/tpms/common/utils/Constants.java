package com.victon.tpms.common.utils;

/**
 * Created by Bob on 2015/4/17.
 */
public class Constants {

    public static final String FIRST_CONFIG = "first_config";

    public static final String USED ="AT+USED=1";
    public static final String UN_USED ="AT+USED=0";

    public static final String RSSI_ON ="AT+RSSI=ON";
    public static final String RSSI_OFF ="AT+RSSI=OFF";

    public static final boolean IS_TEST = false;

    public static final int LEFT_F = 1;
    public static final int RIGHT_F = 2;
    public static final int LEFT_B = 3;
    public static final int RIGHT_B = 4;

    public static final String TEMP_DW = "temp_danwei";
    public static final String PRESSUER_DW = "pressuer_danwei";
    public static final boolean SINGLE_BLE = true;
    public static final String DAY_NIGHT = "dayornight";

    public static final String LANDORPORT = "land_or_port";
    public static final String DEFIED = "竖屏";
    public static final String LAST_ID = "LAST_ID";

    public static final int MYSQL_DEVICE_ID = 10;
    public static final String MY_CAR_DEVICE = "my_car_device";

    public static final String PRESSUER_DW_NUM = "PRESSUER_DW_NUM";
    public static final String TEMP_DW_NUM = "TEMP_DW_NUM";

    public static final String HIGH_PRESS = "high_press";
    public static final String HIGH_TEMP = "high_temp";
    public static int deviceId = 1001;

    public static final String LoW_PRESS = "low_press";

    public static final String PAIRED_LEFT_FROM = "FFF50300000003";
    public static final String PAIRED_RIGHT_FROM = "FFF50300001013";
    public static final String PAIRED_LEFT_BACK = "FFF50300002023";
    public static final String PAIRED_RIGHT_BACK = "FFF50300003033";
    public static final String SLEEP_MODE = "FFF50300050008";
    public static final String CANCEL_PAIRED = "FFF5030000FF02";

    public static final String HIGH_PRESS_VALUES = "FFF50300010008";
    public static final String LOW_PRESS_VALUES = "FFF50300020008";
    public static final String TEMP_VALUES = "FFF50300030008";


    public static float getLowPressValue(){
        return Float.valueOf(SharedPreferences.getInstance().getString(Constants.LoW_PRESS,"1.7"));
    }
    public static float getHighPressValue(){
        return Float.valueOf(SharedPreferences.getInstance().getString(Constants.HIGH_PRESS,"3.2"));
    }
    public static int getHighTempValue(){
        return Integer.valueOf(SharedPreferences.getInstance().getString(Constants.HIGH_TEMP,"65"));
    }

    public static float getLowPressKpaValue(){
        return getLowPressValue()*102f;
    }
    public static float getHighPressKpaValue(){
        return getHighPressValue()*102f;
    }
    public static int getHighTempFValue(){
        return (int)(getHighTempValue()*1.80f)+32;
    }
    public static float getLowPressPsiValue(){
        return getLowPressValue()*14.5f;
    }
    public static float getHighPressPsiValue(){
        return getHighPressValue()*14.5f;
    }
    public static int getLowPressProgress(){
        if(getLowPressValue()==1.7f){
            return 0;
        }
        return (int)((getLowPressValue()-1.6)*10);
    }
    public static int getHighPressProgress(){
        if(getHighPressValue()==2.7f) return 0;
        if(getHighPressValue()==4.0f) return 13;
        return (int)((getHighPressValue()-2.7)*10);
    }
    public static int getLowTempProgress(){
        return getHighTempValue()-50;
    }
}
