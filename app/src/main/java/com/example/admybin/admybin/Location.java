package com.example.admybin.admybin;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Location{

    private int binID;
    private String binName;
    private String imei;
    private String type;
    private double latitude;
    private double longitude;
    private String location;
    private String address;
    private String binCode;

    public Location(){}

    public Location(int binID, String binName, String imei, String type, double latitude, double longitude, String location, String address,String binCode) {
        this.binID = binID;
        this.binName = binName;
        this.imei = imei;
        this.type = type;
        this.latitude = latitude;
        this.longitude = longitude;
        this.location = location;
        this.address = address;
        this.binCode=binCode;
    }




    public int getBinID() {
        return binID;
    }

    public void setBinID(int binID) {
        this.binID = binID;
    }

    public String getBinName() {
        return binName;
    }

    public void setBinName(String binName) {
        this.binName = binName;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBinCode() {
        return binCode;
    }

    public void setBinCode(String binCode) {
        this.binCode = binCode;
    }


}
