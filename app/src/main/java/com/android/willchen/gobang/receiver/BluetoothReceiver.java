package com.android.willchen.gobang.receiver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.android.willchen.gobang.bean.Bluetooth;

import java.util.List;

/**
 * Time:2017.3.28 22:36
 * Created By:ThatNight
 */

public class BluetoothReceiver extends BroadcastReceiver {

    private List<BluetoothDevice> mDevices;
    private List<Bluetooth> mBluetoothList;
    public OnReceiverListener mOnReceiverListener;

    public BluetoothReceiver(List<BluetoothDevice> devices, List<Bluetooth> bluetooths, OnReceiverListener onReceiverListener) {
        mOnReceiverListener = onReceiverListener;
        mDevices = devices;
        mBluetoothList = bluetooths;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            String temp = " (在线中)";
            Bluetooth bluetooth = new Bluetooth(device.getName(), device.getAddress());
            if (!mDevices.contains(device)) {
                bluetooth.setName(bluetooth.getName() + temp);
                mBluetoothList.add(bluetooth);
                mDevices.add(device);
            } else {
                for (int i = 0; i < mBluetoothList.size(); i++) {
                    if (bluetooth.getAdress().equals(mBluetoothList.get(i).getAdress())) {
                        Bluetooth bt = new Bluetooth(mBluetoothList.get(i).getName() + temp, mBluetoothList.get(i).getAdress());
                        mBluetoothList.set(i, bt);
                    }
                }
            }
        } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            mOnReceiverListener.showText();
        }

        if (mOnReceiverListener != null) {
            mOnReceiverListener.setBluetoothList(mBluetoothList, mDevices);
        }
    }

    public interface OnReceiverListener {
        void setBluetoothList(List<Bluetooth> bluetooths, List<BluetoothDevice> devices);
        void showText();
    }
}
