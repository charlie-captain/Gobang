package com.android.willchen.gobang.bean;

/**
 * Time:2017.3.28 22:41
 * Created By:ThatNight
 */

public class Bluetooth {

    private String mName;
    private String mAdress;

    public Bluetooth(String name, String adress) {
        mName = name;
        mAdress = adress;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getAdress() {
        return mAdress;
    }

    public void setAdress(String adress) {
        mAdress = adress;
    }
}
