package com.sri.bhai;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.sri.bhai.shared.SharedPrefManager;


public class getuser_data extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        Log.e("phone","phone"+SharedPrefManager.getInstance(getApplicationContext()).getphone());
        Intent i=new Intent(getApplicationContext(),conditions.class);
        startActivity(i);
    }
}
