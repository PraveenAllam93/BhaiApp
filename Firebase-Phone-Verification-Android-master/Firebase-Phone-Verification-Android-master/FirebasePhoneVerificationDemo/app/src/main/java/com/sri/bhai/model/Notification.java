package com.sri.bhai.model;

public class Notification {

    public String body;
    public String title;
    public String lat;
    public String longe;


    public Notification(){

    }

    public Notification(String body, String title,String lat,String longe) {
        this.body = body;
        this.title = title;
        this.lat = lat;
        this.longe = longe;
    }
    public String getlat() {
        return lat;
    }
    public String getlonge() {
        return longe;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
    public void setlat(String lat) {
        this.lat = lat;
    }
    public void setlonge(String longe) {
        this.longe = longe;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
