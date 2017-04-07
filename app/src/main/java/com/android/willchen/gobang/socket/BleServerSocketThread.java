package com.android.willchen.gobang.socket;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.android.willchen.gobang.config.ConfigData;
import com.android.willchen.gobang.ui.BleGameActivity;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Time:2017.4.5 13:44
 * Created By:ThatNight
 */

public class BleServerSocketThread extends Thread {

    private BluetoothServerSocket mServerSocket;
    private BluetoothSocket mSocket;
    private String mName, mAdress;
    private Activity mBleConnectActivity;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean isAccept = false;
    private boolean isConnecting = true;

    public BleServerSocketThread(BluetoothSocket socket, String name, String adress, Activity bleConnectActivity, BluetoothAdapter bluetoothAdapter, boolean isAccept) {
        mSocket = socket;
        mName = name;
        mAdress = adress;
        mBleConnectActivity = bleConnectActivity;
        mBluetoothAdapter = bluetoothAdapter;
        this.isAccept = isAccept;
        isConnecting = true;
    }

    public void run() {
        DataOutputStream dos = null;
        try {
            mServerSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(mName, ConfigData.UUID);
            while (isConnecting) {
                isAccept = false;
                mSocket = mServerSocket.accept();
                if (mBluetoothAdapter.isDiscovering()) {
                    mBluetoothAdapter.cancelDiscovery();
                }
                mBleConnectActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog aDialog = new AlertDialog.Builder(mBleConnectActivity)
                                .setTitle("消息")
                                .setMessage("是否接受挑战?")
                                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        isAccept = true;
                                        Toast.makeText(mBleConnectActivity, "连接成功!", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        isAccept = false;
                                    }
                                })
                                .show();
                    }
                });
                while (true) {
                    if (isAccept) {
                        String result = "accept";
                        dos = new DataOutputStream(mSocket.getOutputStream());
                        dos.writeUTF(result);
                        SocketManager.addBleSocketHm(mAdress, mSocket);
                        Intent intent = new Intent(mBleConnectActivity, BleGameActivity.class);
                        intent.putExtra("adress", mAdress);
                        intent.putExtra("isStart", false);
                        mBleConnectActivity.startActivity(intent);
                        break;
                    } else {

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void cancel() {
        isConnecting = false;
        try {
            if (mServerSocket != null) {
                mServerSocket.close();
            }
            if (mSocket != null) {
                mSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
