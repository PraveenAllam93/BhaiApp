package com.sri.bhai.model;

public class ContactItem {


       String id, name, phone,checked;
       byte[] contactImage = null;

    public ContactItem(){

    }
    public ContactItem(String id,String name, String phone,String checked) {
        this.name = name;
        this.id = id;
        this.phone = phone;
        this.checked = checked;

    }
    public  String getId() {
        return id;
    }
    public  String getChecked() {
        return checked;
    }
    public void setId(String id) {
        this.id = id;
    }

    public  String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setChecked(String Check) {
        this.checked = Check;
    }


    public  String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public byte[] getContactImage() {
        return contactImage;
    }

    public void setContactImage(byte[] contactImage) {
        this.contactImage = contactImage;
    }
}
