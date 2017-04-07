package com.android.willchen.gobang.socket;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;

import com.android.willchen.gobang.view.PeopleGameView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Time:2017.4.5 13:24
 * Created By:ThatNight
 */

public class ConnectedThread extends Thread {

    private final BluetoothSocket mSocket;
    private PeopleGameView mGameView;
    private boolean isMe = false;

    public ConnectedThread(BluetoothSocket socket, PeopleGameView gameView, boolean isStart) {
        mSocket = socket;
        mGameView = gameView;
        isMe = isStart;
    }

    @Override
    public void run() {
        while (true) {
            if (mSocket != null) {
                DataInputStream dis = null;
                String data = null;
                try {
                    dis = new DataInputStream(mSocket.getInputStream());
                    data = dis.readUTF();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (data != null) {
                    final String finalData = data;
                    mGameView.post(new Runnable() {
                        @Override
                        public void run() {
                            mGameView.getCommand(finalData);
                        }
                    });
                } else {
                    Activity activity = (Activity) mGameView.getContext();
                    activity.finish();
                    break;
                }
            }
        }
    }

    public void write(byte[] bytes) {
        DataOutputStream dos = null;
        try {
            dos = new DataOutputStream(mSocket.getOutputStream());
            String temp = new String(bytes, "utf-8");
            dos.writeUTF(temp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cancel() {
        try {
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}