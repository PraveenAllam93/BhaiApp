package com.sri.bhai.Fragment;

public class notification_db {

    String name;
    String mobileNumber;
    double cu_lat;
    double cu_long;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public double getCu_lat() {
        return cu_lat;
    }

    public void setCu_lat(double cu_lat) {
        this.cu_lat = cu_lat;
    }

    public double getCu_long() {
        return cu_long;
    }

    public void setCu_long(double cu_long) {
        this.cu_long = cu_long;
    }

    public notification_db(String name, String mobileNumber, double cu_lat, double cu_long) {
        this.name = name;
        this.mobileNumber = mobileNumber;
        this.cu_lat = cu_lat;
        this.cu_long = cu_long;
    }

    public notification_db(){

    }
}

