package com.victon.tpms.base.widget;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.victon.tpms.R;
import com.victon.tpms.common.view.activity.BaseActivity;

/**
 * Created by Administrator on 2016/5/27.
 */
public class SystemDialog extends BaseActivity {
    Button button2;
    Button button3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_system_dialog);
        button2 = (Button)findViewById(R.id.button2);
        button3 = (Button)findViewById(R.id.button3);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
