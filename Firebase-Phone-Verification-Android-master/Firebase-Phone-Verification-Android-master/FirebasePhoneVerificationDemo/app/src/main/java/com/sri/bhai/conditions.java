package com.sri.bhai;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

public class conditions extends AppCompatActivity {
    CheckBox check1,check2;
    Button next;
    boolean checkk1=true,checkk2=false;
    private static int REQUEST_OVERLAY_PERMISSION=12;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conditions);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        check1=(CheckBox)findViewById(R.id.check1);
        check2=(CheckBox)findViewById(R.id.check2);
        next=(Button)findViewById(R.id.next);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Settings.ACTION_PRIVACY_SETTINGS
            if(!Settings.canDrawOverlays(getApplicationContext())){
                // ask for setting
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_OVERLAY_PERMISSION);
            }
        }
        //onDisplayPopupPermission();
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkk2){
                    Intent i=new Intent(getApplicationContext(),TrackerActivity.class);
                    startActivity(i);

                }else{
                    Toast.makeText(getApplicationContext(),"Accept Terms & Conditions",Toast.LENGTH_LONG).show();
                }

            }
        });
        check1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    checkk1=true;
                }else{
                    checkk1=false;
                }
            }
        });
        check2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    checkk2=true;
                }else{
                    checkk2=false;
                }
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if ( id == android.R.id.home ) {
            Intent intent=new Intent(getApplicationContext(), create_profile.class);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_OVERLAY_PERMISSION) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(getApplicationContext())) {
                    // permission granted...
                }else{
                    // permission not granted...
                    Toast.makeText(getApplicationContext(),"Please Allow",Toast.LENGTH_LONG).show();

                }
            }
        }
    }
}
