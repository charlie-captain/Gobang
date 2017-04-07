package com.android.willchen.gobang.socket;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.widget.Toast;

import com.android.willchen.gobang.ui.BleGameActivity;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Time:2017.4.5 13:46
 * Created By:ThatNight
 */

public class BleSocketThread extends Thread {
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket mThisSocket;
    private Activity mBleConnectActivity;
    private String mAdress;
    private boolean isConnecting;

    public BleSocketThread(BluetoothAdapter bluetoothAdapter, BluetoothSocket thisSocket, Activity bleConnectActivity, String adress) {
        mBluetoothAdapter = bluetoothAdapter;
        mThisSocket = thisSocket;
        mBleConnectActivity = bleConnectActivity;
        mAdress = adress;
        isConnecting = true;
    }

    public void run() {
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        DataInputStream dis = null;
        try {
            mThisSocket.connect();
            if (!mBluetoothAdapter.isEnabled()) {
                mBluetoothAdapter.enable();
            }
            mBleConnectActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mBleConnectActivity, "正在等待对方回应...", Toast.LENGTH_SHORT).show();
                }
            });
            while (isConnecting) {
                dis = new DataInputStream(mThisSocket.getInputStream());
                String result = dis.readUTF();
                if ("accept".equals(result)) {
                    SocketManager.addBleSocketHm(mAdress, mThisSocket);
                    Intent intent = new Intent(mBleConnectActivity, BleGameActivity.class);
                    intent.putExtra("adress", mAdress);
                    intent.putExtra("isStart", true);
                    mBleConnectActivity.startActivity(intent);
                    break;
                } else {
                    mBleConnectActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mBleConnectActivity, "对方不接受挑战!", Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void cancel() {
        isConnecting = false;
        try {
            if (mThisSocket != null) {
                mThisSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
