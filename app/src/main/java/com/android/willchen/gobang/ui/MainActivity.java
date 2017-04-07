package com.android.willchen.gobang.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.android.willchen.gobang.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @InjectView(R.id.btn_main_ai)
    Button mBtnMainAi;
    @InjectView(R.id.btn_main_ble)
    Button mBtnMainBle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        ButterKnife.inject(this);

    }

    @OnClick({R.id.btn_main_ai, R.id.btn_main_ble})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_main_ai:
                Intent aiIntent=new Intent(MainActivity.this, AiActivity.class);
                startActivity(aiIntent);
                break;
            case R.id.btn_main_ble:
                Intent bleIntent=new Intent(MainActivity.this,BleConnectActivity.class);
                startActivity(bleIntent);
                break;
        }
    }
}
