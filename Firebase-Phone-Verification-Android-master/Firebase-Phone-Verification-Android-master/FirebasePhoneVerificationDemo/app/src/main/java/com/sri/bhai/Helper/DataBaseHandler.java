package com.sri.bhai.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.sri.bhai.model.ContactItem;

import java.util.ArrayList;

public class DataBaseHandler extends SQLiteOpenHelper {

    public String ContactsTable="contacts";
    public String Contacts_Id="id";
    public String Contact_name="name";
    public String Contact_number="number";
    public String Contact_checked="checked";

    private  final int DB_Version=1;
    public DataBaseHandler(Context context,String DBName,int DBVersion){
        super(context, DBName,null,DBVersion);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("Create Table " + ContactsTable + "(" + Contacts_Id + " INTEGER PRIMARY KEY AUTOINCREMENT, " + Contact_name + " Text, " + Contact_checked + " Text, " + Contact_number + " Text UNIQUE);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public void addContact(String name,String phone,String checked){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(Contact_name,name);
        cv.put(Contact_number,phone);
        cv.put(Contact_checked,checked);
        db.insertWithOnConflict(ContactsTable, null, cv,SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public ArrayList<ContactItem> fetchAllContact(){
        ArrayList<ContactItem> booksList = new ArrayList<ContactItem>();
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor c= db.query(ContactsTable, null, null, null, null, null, null);
        c.moveToFirst();
        while (!c.isAfterLast()){
            ContactItem book = new ContactItem();
            //book.set(c.getInt(0));
            book.setName(c.getString(1));
            book.setPhone(c.getString(2));
            book.setChecked(c.getString(3));
            booksList.add(book);
            c.moveToNext();
        }
        db.close();
        return booksList;
    }
    public void trunk() {
        SQLiteDatabase db = this.getReadableDatabase();

        try {

            db.execSQL("DROP TABLE " + ContactsTable);
            db.execSQL("Create Table " + ContactsTable + "(" + Contacts_Id + " INTEGER PRIMARY KEY AUTOINCREMENT, " + Contact_name + " Text, " + Contact_checked + " Text, " + Contact_number + " Text UNIQUE);");
        } catch (SQLiteException e) {
            e.printStackTrace();
        }

    }
    public Cursor all_phones() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + ContactsTable + " WHERE  1;";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }
    public int getcount() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + ContactsTable + " WHERE  1;";
        Cursor c = db.rawQuery(sql, null);
        return c.getCount();
    }
}
