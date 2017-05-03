package com.victon.tpms.base.module.setting;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.victon.tpms.R;
import com.victon.tpms.base.db.dao.DeviceDao;
import com.victon.tpms.base.db.entity.Device;
import com.victon.tpms.common.utils.Constants;
import com.victon.tpms.common.utils.Logger;
import com.victon.tpms.common.utils.SharedPreferences;
import com.victon.tpms.common.view.activity.BaseActionBarActivity;

import java.text.DecimalFormat;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by sid-fu on 2016/5/16.
 */
public class PersonTabletSetting extends BaseActionBarActivity implements View.OnClickListener{
    private static final String TAG = "ConfigDevice";
    private List<Device> articles;
    private PersonTabletSetting mContext;
    private TextView tvPreesure;
    private TextView tvTemp;
    private TextView tvLandPort;
    private Switch switch2;
    private TextView lowPressValue;
    private TextView highPressValue;
    private TextView highTempValue;
    private SeekBar lowPressSeekBar;
    private SeekBar highPressSeekBar;
    private SeekBar highTempSeekBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_person_config);
        mContext = PersonTabletSetting.this;
        ButterKnife.bind(this);
        articles = new DeviceDao(this).listByUserId(1);
        initUI();
        /*显示App icon左侧的back键*/
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

    }

    private void initUI() {
        tvPreesure = (TextView)findViewById(R.id.tv_preesure);
        tvTemp = (TextView)findViewById(R.id.tv_temp);
        tvLandPort = (TextView)findViewById(R.id.tv_land_port);

        switch2 = (Switch)findViewById(R.id.switch2);
        tvPreesure.setText(SharedPreferences.getInstance().getString(Constants.PRESSUER_DW, "Bar"));
        tvTemp.setText(SharedPreferences.getInstance().getString(Constants.TEMP_DW, "℃"));
        tvLandPort.setText(SharedPreferences.getInstance().getString(Constants.LANDORPORT,Constants.DEFIED));
        switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.getInstance().putBoolean(Constants.DAY_NIGHT, isChecked);
            }
        });
        switch2.setChecked(SharedPreferences.getInstance().getBoolean(Constants.DAY_NIGHT, false));
        tvPreesure.setOnClickListener(this);
        tvTemp.setOnClickListener(this);
        tvLandPort.setOnClickListener(this);
        initSeekBar();

    }



    public void choocesPre() {
        new AlertDialog.Builder(this)
                .setTitle("气压单位")
                .setSingleChoiceItems(R.array.pressure, SharedPreferences.getInstance().getInt(Constants.PRESSUER_DW_NUM,0), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String hoddy = getResources().getStringArray(R.array.pressure)[which];
                        tvPreesure.setText(hoddy);
                        SharedPreferences.getInstance().putInt(Constants.PRESSUER_DW_NUM, which);
                        SharedPreferences.getInstance().putString(Constants.PRESSUER_DW, hoddy);
                        dialog.dismiss();
                    }
                })
//                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        tvPreesure.setText(getResources().getStringArray(R.array.pressure)[which]);
//                    }
//                })
                .show();
    }
    public void choocesLandOrPort() {
        new AlertDialog.Builder(this)
                .setTitle("切换屏幕模式")
                .setSingleChoiceItems(R.array.land, 0, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String hoddy = getResources().getStringArray(R.array.land)[which];
                        tvLandPort.setText(hoddy);
                        SharedPreferences.getInstance().putString(Constants.LANDORPORT, hoddy);
                        dialog.dismiss();
                    }
                })
//                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        tvPreesure.setText(getResources().getStringArray(R.array.pressure)[which]);
//                    }
//                })
                .show();
    }
    public void choocesTemp() {
        new AlertDialog.Builder(this)
                .setTitle("温度单位")
                .setSingleChoiceItems(R.array.temp, SharedPreferences.getInstance().getInt(Constants.TEMP_DW_NUM,0), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String hoddy = getResources().getStringArray(R.array.temp)[which];
                        tvTemp.setText(hoddy);
                        SharedPreferences.getInstance().putInt(Constants.TEMP_DW_NUM, which);
                        SharedPreferences.getInstance().putString(Constants.TEMP_DW, hoddy);
                        dialog.dismiss();
                    }
                })
//                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        tvTemp.setText(getResources().getStringArray(R.array.temp)[which]);
//                    }
//                })
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.tv_preesure) {
            choocesPre();
        } else if (i == R.id.tv_temp) {
            choocesTemp();
        } else if (i == R.id.tv_land_port) {
            choocesLandOrPort();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private void initSeekBar() {
        lowPressValue = (TextView)findViewById(R.id.lowPressValue);
        highPressValue = (TextView)findViewById(R.id.highPressValue);
        highTempValue = (TextView)findViewById(R.id.highTempValue);
        lowPressSeekBar = (SeekBar)findViewById(R.id.lowPress_seekBar);
        highPressSeekBar = (SeekBar)findViewById(R.id.highPress_seekBar);
        highTempSeekBar = (SeekBar)findViewById(R.id.highTemp_seekBar);
        final DecimalFormat fnum = new DecimalFormat("##0.0");
        lowPressValue.setText(Constants.getLowPressValue()+"");
        highPressValue.setText(Constants.getHighPressValue()+"");
        highTempValue.setText(Constants.getHighTempValue()+"");
        lowPressSeekBar.setProgress(Constants.getLowPressProgress());
        Logger.e(Constants.getLowPressProgress()+"");
        highPressSeekBar.setProgress(Constants.getHighPressProgress());
        highTempSeekBar.setProgress(Constants.getLowTempProgress());
        lowPressSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                lowPressValue.setText(fnum.format(1.6+i*0.1f));
                SharedPreferences.getInstance().putString(Constants.LoW_PRESS,fnum.format(1.6+i*0.1f));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        highPressSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                highPressValue.setText(fnum.format(3.0+i*0.1f));
                SharedPreferences.getInstance().putString(Constants.HIGH_PRESS,fnum.format(3.0+i*0.1f));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        highTempSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                highTempValue.setText(60+i+"");
                SharedPreferences.getInstance().putString(Constants.HIGH_TEMP,60+i+"");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
