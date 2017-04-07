package com.android.willchen.gobang.ui;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.willchen.gobang.R;
import com.android.willchen.gobang.adapter.BluetoothDevicesAdapter;
import com.android.willchen.gobang.bean.Bluetooth;
import com.android.willchen.gobang.config.ConfigData;
import com.android.willchen.gobang.receiver.BluetoothReceiver;
import com.android.willchen.gobang.socket.BleServerSocketThread;
import com.android.willchen.gobang.socket.BleSocketThread;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class BleConnectActivity extends Activity {


    @InjectView(R.id.btn_search)
    Button mBtnSearch;
    @InjectView(R.id.lv_bluetooth)
    ListView mLvBluetooth;
    private final static int REQUEST_BLUE = 123;
    private BluetoothDevicesAdapter mAdapter;
    private BluetoothReceiver mReceiver;
    private BluetoothAdapter mBluetoothAdapter;
    private String mName, mAdress;
    private List<Bluetooth> mBluetooths;
    private List<BluetoothDevice> mDevices;
    private BleServerSocketThread mBleServerSocketThread;
    private BleSocketThread mBleSocketThread;
    private BluetoothSocket mSocket;
    private BluetoothSocket mThisSocket;
    private BluetoothDevice mThisDevice;
    private boolean isAccept = false;
    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_bleconnect);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        ButterKnife.inject(this);
        init();
        initSocket();
    }

    private void init() {
        mBluetooths = new ArrayList<>();
        mDevices = new ArrayList<>();
        mActivity = this;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            openBluetooth();
        }
        mName = mBluetoothAdapter.getName();
        mAdress = mBluetoothAdapter.getAddress();

        mAdapter = new BluetoothDevicesAdapter(mBluetooths, this);
        mLvBluetooth.setAdapter(mAdapter);

        //注册广播
        mReceiver = new BluetoothReceiver(mDevices, mBluetooths, new BluetoothReceiver.OnReceiverListener() {
            @Override
            public void setBluetoothList(List<Bluetooth> bluetooths, List<BluetoothDevice> devices) {
                mBluetooths = bluetooths;
                mDevices = devices;
                mAdapter.setDevices(mBluetooths);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void showText() {
                Toast.makeText(BleConnectActivity.this, "搜索完成!", Toast.LENGTH_SHORT).show();
            }
        });

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter);

        mLvBluetooth.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                connectBluetooth(position);
            }
        });

    }


    //初始化Scocket
    private void initSocket() {
        mBleServerSocketThread = new BleServerSocketThread(mSocket, mName, mAdress, mActivity, mBluetoothAdapter, isAccept);
        mBleServerSocketThread.start();
    }

    // 开始连接对方
    private void connectBluetooth(int position) {
        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(mBluetooths.get(position).getAdress());
        try {
            if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                Method method = BluetoothDevice.class.getMethod("createBond");
                method.invoke(device);
            } else if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                mThisSocket = device.createRfcommSocketToServiceRecord(ConfigData.UUID);
                AlertDialog dialog = new AlertDialog.Builder(BleConnectActivity.this)
                        .setTitle("发起挑战")
                        .setMessage("确认挑战玩家: " + mBluetooths.get(position).getName())
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mBleSocketThread = new BleSocketThread(mBluetoothAdapter, mThisSocket, mActivity, mAdress);
                                mBleSocketThread.start();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();

            }
        } catch (Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(BleConnectActivity.this, "连接失败!", Toast.LENGTH_SHORT).show();
                }
            });
            e.printStackTrace();
        }
    }

    @OnClick(R.id.btn_search)
    public void onClick() {
        findBluetooth();
    }

    private void findBluetooth() {
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        mBluetooths.clear();
        mDevices.clear();
        Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
        if (devices.size() > 0) {
            for (BluetoothDevice device : devices) {
                if (!mDevices.contains(device)) {
                    mBluetooths.add(new Bluetooth(device.getName(), device.getAddress()));
                    mDevices.add(device);
                }
            }
        }
        mAdapter.setDevices(mBluetooths);
        mAdapter.notifyDataSetChanged();
        mBluetoothAdapter.startDiscovery();
    }

    private void openBluetooth() {
        Intent openIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        openIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 500);
        startActivity(openIntent);
    }


    @Override
    protected void onResume() {
        super.onResume();
        checkPermission();
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                return;
            }
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_BLUE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_BLUE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {

                }
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        if(mBleServerSocketThread!=null){
            mBleServerSocketThread.cancel();
        }
        if(mBleSocketThread!=null){
            mBleSocketThread.cancel();
        }
    }


}
