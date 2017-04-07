package com.android.willchen.gobang.socket;

import android.bluetooth.BluetoothSocket;

import java.util.HashMap;

/**
 * Time:2017.4.5 13:39
 * Created By:ThatNight
 */

public class SocketManager {
    public static HashMap<String, BleServerSocketThread> mServerHm = new HashMap<>();
    public static HashMap<String, BleSocketThread> mSocketHm = new HashMap<>();
    public static HashMap<String, BluetoothSocket> mBleSocketHm = new HashMap<>();

    public static BleServerSocketThread getmServerHm(String adress) {
        return mServerHm.get(adress);
    }

    public static BleSocketThread getmSocketHm(String adress) {
        return mSocketHm.get(adress);
    }

    public static BluetoothSocket getmBleSocketHm(String adress) {
        return mBleSocketHm.get(adress);
    }

    public static void addServerHm(String adress, BleServerSocketThread bleServerSocketThread) {
        mServerHm.put(adress, bleServerSocketThread);
    }

    public static void addSocketHm(String adress, BleSocketThread bleSocketThread) {
        mSocketHm.put(adress, bleSocketThread);
    }

    public static void addBleSocketHm(String adress, BluetoothSocket bluetoothSocket) {
        mBleSocketHm.put(adress, bluetoothSocket);
    }


}
