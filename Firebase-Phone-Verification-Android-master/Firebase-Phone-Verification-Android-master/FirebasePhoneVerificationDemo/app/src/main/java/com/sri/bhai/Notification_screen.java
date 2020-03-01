package com.sri.bhai;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

public class Notification_screen extends AppCompatActivity {
    int pStatus = 0;
    private Handler handler = new Handler();
    TextView tv;
    Button Direction;
    double destinationLatitude,destinationLongitude;
    String name,phone,pic;
    ImageView profile;
    TextView names,phones;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_screen);
        names=(TextView)findViewById(R.id.name);
        phones=(TextView)findViewById(R.id.phone);
        profile=(ImageView)findViewById(R.id.pic);
        Intent i=getIntent();
        destinationLatitude=Double.parseDouble(i.getStringExtra("lat"));
        destinationLongitude=Double.parseDouble(i.getStringExtra("longe"));
        names.setText(i.getStringExtra("name"));
        phones.setText(i.getStringExtra("phone"));
       // Uri uri = getIntent().getExtras().getParcelable("pic");
        pic=i.getStringExtra("pic");
        Log.e("pic",pic);
        Log.e("lat",""+destinationLatitude);
        Log.e("longe",""+destinationLongitude);
        final MediaPlayer mPlayer = MediaPlayer.create(Notification_screen.this, R.raw.alert);
        AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);;
        audio.setStreamVolume(AudioManager.STREAM_MUSIC,audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC),0);

        Glide.with(getApplicationContext())
                .load(pic.toString())
                .apply(RequestOptions.skipMemoryCacheOf(true))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .into(profile);
        Direction=(Button)findViewById(R.id.dir);
        Direction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayer.stop();
                String uri = "http://maps.google.com/maps?daddr=" + destinationLatitude + "," + destinationLongitude + " (" + "Where the Location is at" + ")";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            }
        });
        Resources res = getResources();
        Drawable drawable = res.getDrawable(R.drawable.circular);
        final ProgressBar mProgress = (ProgressBar) findViewById(R.id.circularProgressbar);
        mProgress.setProgress(0);   // Main Progress
        mProgress.setSecondaryProgress(100); // Secondary Progress
        mProgress.setMax(100); // Maximum Progress
        mProgress.setProgressDrawable(drawable);
        tv = (TextView) findViewById(R.id.tv);
        new Thread(new Runnable() {

            @Override
            public void run() {
                mPlayer.start();
                // TODO Auto-generated method stub
                while (pStatus < 100) {

                    pStatus += 1;

                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            mProgress.setProgress(pStatus);
                            tv.setText(pStatus + "");

                        }
                    });
                    try {

                        Thread.sleep(100); //thread will take approx 3 seconds to finish
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                mPlayer.stop();
            }
        }).start();
    }
}
