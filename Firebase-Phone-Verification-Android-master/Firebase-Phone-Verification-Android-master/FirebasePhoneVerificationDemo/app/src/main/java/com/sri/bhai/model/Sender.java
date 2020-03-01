package com.sri.bhai.model;

import com.google.gson.annotations.SerializedName;

public class  Sender {

    @SerializedName("to")
    public String to;
    @SerializedName("data")
    public Notification notification;

    public Sender(){

    }

    public Sender(String to, Notification notification) {
        this.to = to;
        this.notification = notification;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }
}
