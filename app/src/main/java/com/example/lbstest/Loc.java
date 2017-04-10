package com.example.lbstest;

import cn.bmob.v3.BmobObject;

/**
 * Created by yf on 2017/4/7.
 */

public class Loc extends BmobObject{
    public String latitude;
    public String longgitude;
    public String update;
    public String addStreet;
    public String locType;

    public void setLocType(String locType) {
        this.locType = locType;
    }

    public String getLocType() {
        return locType;
    }

    public void setUpdate(String update) {
        this.update = update;
    }

    public void setAddStreet(String addStreet) {
        this.addStreet = addStreet;
    }

    public String getAddStreet() {
        return addStreet;
    }

    public String getUpdate() {
        return update;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longgitude) {
        this.longgitude = longgitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLonggitude() {
        return longgitude;
    }
}
