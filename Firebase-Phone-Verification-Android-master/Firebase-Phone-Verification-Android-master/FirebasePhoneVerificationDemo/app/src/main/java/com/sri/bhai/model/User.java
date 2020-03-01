package com.sri.bhai.model;

/**
 * Created by Liew on 4/30/2018.
 */

public class User {

    private String name;
    private String email;
    private String token;
    private String lattitude;
    private String longitude;
    private String image;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String name,String token, String email,String lattitude,String longitude,String image) {
        this.name = name;
        this.email = email;
        this.token = token;
        this.lattitude = lattitude;
        this.longitude = longitude;
        this.image = image;
    }

    public String getname() {
        return name;
    }

}
