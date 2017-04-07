package com.android.willchen.gobang.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.willchen.gobang.R;
import com.android.willchen.gobang.bean.Bluetooth;

import java.util.List;

/**
 * Time:2017.3.28 23:10
 * Created By:ThatNight
 */

public class BluetoothDevicesAdapter extends BaseAdapter {

    private List<Bluetooth> mBluetooths;
    private Context mContext;
    private LayoutInflater mLayoutInflater;


    public BluetoothDevicesAdapter(List<Bluetooth> bluetooths, Context context) {
        mBluetooths = bluetooths;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mBluetooths.size();
    }

    @Override
    public Object getItem(int position) {
        return mBluetooths.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = mLayoutInflater.inflate(R.layout.item_bluetooth, null);
            viewHolder.mName = (TextView) view.findViewById(R.id.tv_item_bluetooth_name);
            viewHolder.mAdress = (TextView) view.findViewById(R.id.tv_item_bluetooth_adress);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.mName.setText(mBluetooths.get(position).getName());
        viewHolder.mAdress.setText(mBluetooths.get(position).getAdress());
        return view;
    }

    public void setDevices(List<Bluetooth> bluetooths) {
        mBluetooths = bluetooths;
    }

    class ViewHolder {
        TextView mName;
        TextView mAdress;
    }
}
