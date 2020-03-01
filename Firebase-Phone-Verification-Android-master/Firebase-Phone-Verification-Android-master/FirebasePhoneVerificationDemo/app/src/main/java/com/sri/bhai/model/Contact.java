package com.sri.bhai.model;

public class Contact {

    public static String name;
    public static String phone;
    public static String checked;


    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setChecked(String checked) {
        this.checked = checked;
    }



    public static String getName() {
        return name;
    }

    public static String getPhone() {
        return phone;
    }
    public static String getChecked() {
        return checked;
    }
}
