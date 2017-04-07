package com.android.willchen.gobang.ui;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.ListPopupWindow;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.android.willchen.gobang.R;
import com.android.willchen.gobang.socket.ConnectedThread;
import com.android.willchen.gobang.socket.SocketManager;
import com.android.willchen.gobang.view.PeopleGameView;

import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class BleGameActivity extends Activity implements PeopleGameView.onBluetoothListener {

    @InjectView(R.id.tv_pgv_win)
    TextView mIsWin;
    @InjectView(R.id.view_pgv)
    PeopleGameView mPeopleGameView;
    @InjectView(R.id.btn_pgv_return)
    Button mBtnPgvReturn;
    @InjectView(R.id.btn_pgv_again)
    Button mBtnPgvAgain;
    @InjectView(R.id.btn_pgv_msg)
    Button mBtnPgvMsg;
    private ConnectedThread mConnectedThread;
    private BluetoothSocket mSocket;
    private ListPopupWindow mWindowCompat;
    private List<String> mMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_ble_game);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        ButterKnife.inject(this);
        init();
    }

    //初始化
    private void init() {
        String[] message = getResources().getStringArray(R.array.array_message);
        mMessage = Arrays.asList(message);

        mPeopleGameView.setTvWin(mIsWin);
        mPeopleGameView.setCallBack(this);

        //获取信息
        Intent intent = getIntent();
        String adress = intent.getStringExtra("adress");
        boolean isStart = intent.getBooleanExtra("isStart", false);
        if (adress != null) {
            mSocket = SocketManager.getmBleSocketHm(adress);
            manageClientSocket(isStart);
            mPeopleGameView.setAdress(adress);
            mPeopleGameView.setIsStart(isStart);
        }
        popupWindow();
    }

    //开启连接线程
    public void manageClientSocket(boolean isStart) {
        mConnectedThread = new ConnectedThread(mSocket, mPeopleGameView, isStart);
        mConnectedThread.start();
    }

    //发送信息
    @Override
    public void onCommand(String temp) {
        mConnectedThread.write(temp.getBytes());
    }

    @OnClick({R.id.btn_pgv_return, R.id.btn_pgv_again, R.id.btn_pgv_msg})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_pgv_msg:
                mWindowCompat.show();
                break;
            case R.id.btn_pgv_return:
                String command = "return";
                mConnectedThread.write(command.getBytes());
                mPeopleGameView.returnUp();
                break;
            case R.id.btn_pgv_again:
                String restartCommand = "restart";
                mConnectedThread.write(restartCommand.getBytes());
                mPeopleGameView.restartGame();
                break;
        }
    }

    //弹出快捷回复窗口
    private void popupWindow() {
        mWindowCompat = new ListPopupWindow(this);
        mWindowCompat.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mMessage));
        mWindowCompat.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        mWindowCompat.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mWindowCompat.setAnchorView(mBtnPgvMsg);
        mWindowCompat.setModal(true);
        mWindowCompat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String msg = "msg;" + mMessage.get(position);
                mConnectedThread.write(msg.getBytes());
                mWindowCompat.dismiss();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mConnectedThread.cancel();
    }
}
