package com.sri.bhai;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.sri.bhai.shared.SharedPrefManager;

public class start_settings extends AppCompatActivity {
    ImageView edit;
    EditText number;
    TextView seek;
    Button save;
    private static int REQUEST_OVERLAY_PERMISSION=12;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_settings);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(!Settings.canDrawOverlays(this)){
                // ask for setting
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_OVERLAY_PERMISSION);
            }
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        SeekBar sk = (SeekBar) findViewById(R.id.seekBar);
        edit=(ImageView) findViewById(R.id.edit);
        number=(EditText) findViewById(R.id.number);
        seek=(TextView) findViewById(R.id.textView4);
        save=(Button)findViewById(R.id.alert);
        sk.setProgress(Integer.parseInt(SharedPrefManager.getInstance(getApplicationContext()).getdist()));
        seek.setText(SharedPrefManager.getInstance(getApplicationContext()).getdist()+"Mtr.");
        number.setText(SharedPrefManager.getInstance(getApplicationContext()).getpolice());
        Log.e("police",""+Integer.parseInt(SharedPrefManager.getInstance(getApplicationContext()).getdist()));
        Log.e("number",""+Integer.parseInt(SharedPrefManager.getInstance(getApplicationContext()).getpolice()));
        final AlertDialog.Builder builder = new AlertDialog.Builder(start_settings.this,R.style.Theme_AppCompat_Light_Dialog_Alert);
        builder.setTitle("Emergency Number");
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText input = new EditText(getApplicationContext());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        number.setText(input.getText().toString());
                        SharedPrefManager.getInstance(getApplicationContext()).savepolice(input.getText().toString());
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        sk.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                seek.setText(String.valueOf(i)+"Mtr.");
                SharedPrefManager.getInstance(getApplicationContext()).savedist(String.valueOf(i));

            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),TrackerActivity.class);
                startActivity(i);
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if ( id == android.R.id.home ) {
            Intent intent=new Intent(getApplicationContext(), conditions.class);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_OVERLAY_PERMISSION) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    // permission granted...
                }else{
                    // permission not granted...
                }
            }
        }
    }
}
