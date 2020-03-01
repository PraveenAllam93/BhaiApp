package com.sri.bhai.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.sri.bhai.Helper.NotificationHelper;
import com.sri.bhai.MainActivity;
import com.sri.bhai.Notification_screen;
import com.sri.bhai.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class MyFirebaseMessaging extends FirebaseMessagingService {

    String lat;
    String longe,name,phone,pic;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.e("message","msg"+remoteMessage.getData().get("message"));
        //startService(new Intent(getApplicationContext(), openservice.class));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            sendNotificationAPI26(remoteMessage);
            Intent i = new Intent(getApplicationContext(), Notification_screen.class);
            i.putExtra("lat", lat);
            i.putExtra("longe", longe);
            i.putExtra("name",name);
            i.putExtra("phone",phone);
            String uri = Uri.parse(pic)
                    .buildUpon()
                    .appendQueryParameter("pic", pic)
                    .build().toString();
            i.putExtra("pic",uri);
            try {
                startActivity(i);
            }catch (Exception e){
                Log.e("exception",e.toString());
            }
        }
        else
        {
            sendNotification(remoteMessage);
            Intent i = new Intent(getApplicationContext(), Notification_screen.class);
            i.putExtra("lat", lat);
            i.putExtra("longe", longe);
            i.putExtra("name",name);
            i.putExtra("phone",phone);
            //i.putExtra("pic",Uri.parse(pic));
            String uri = Uri.parse(pic)
                    .buildUpon()
                    .appendQueryParameter("pic", pic)
                    .build().toString();
            i.putExtra("pic",uri);
            startActivity(i);
        }


    }



    private void sendNotificationAPI26(RemoteMessage remoteMessage) {
        final MediaPlayer mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.alert);
        AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);;
        audio.setStreamVolume(AudioManager.STREAM_MUSIC,audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC),0);
        //mPlayer.start();
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //mPlayer.stop();
            }
        });

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        String title = remoteMessage.getData().get("message");
        String content;
//        Log.e("dataChat",notification.getTitle());
        try {
            String[] separated = title.split("#");
            //content=separated[0]; // this will contain "Fruit"
            title = separated[0];
            content = separated[1];
            lat = separated[2];
            longe = separated[3];
            name = separated[4];
            phone = separated[5];
            pic = separated[6];
            //pic = separated[5]+separated[6];
            Log.e("pic_url",pic);

        }catch (Exception e){
            String[] separated = title.split("#");
            title = "save me";
            content = "EMERGENCY HERE";

            lat = "17.745209";
            longe = "83.2297473";
            name = "none";
            phone = "none";
            pic = "none";
        }

        //Intent intent = new Intent(this, MainActivity.class);
        String uri = "http://maps.google.com/maps?daddr=" + lat + "," + longe + " (" + "Where the Location is at" + ")";
        Log.e("map_url",uri);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");
        //startActivity(intent);
        //intent.putExtra(Common.PHONE_TEXT, Common.currentUser.getPhone());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationHelper helper = new NotificationHelper(this);
        Notification.Builder builder = helper.getiDeliveryChannelNotification(title,content,pendingIntent,defaultSoundUri);

        //get random ID for notification to show all notifications
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            helper.getManager().notify(new Random().nextInt(), builder.build());
        }
        //Intent i=new Intent(getApplicationContext(), Notification_screen.class);
        //startActivity(i);
        //startActivity(new Intent(this, Notification_screen.class));


    }

    private void sendNotification(RemoteMessage remoteMessage) {
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        Log.e("message",remoteMessage.toString());
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(notification.getTitle())
                .setContentText(notification.getBody())
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager noti = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        noti.notify(0, builder.build());
        String title = remoteMessage.getData().get("message");
        String content = notification.getBody();
        Log.e("dataChat",notification.getTitle());
        String[] separated = title.split("#");
        title=separated[0]; // this will contain "Fruit"
        content=separated[1]; // this will contain "Fruit"
        String lat=separated[2];
        String longe=separated[3];
        name = separated[4];
        phone = separated[5];
        pic = separated[6];
        Log.e("pic_url",pic);
        Intent i=new Intent(getApplicationContext(), Notification_screen.class);
        i.putExtra("lat",lat);
        i.putExtra("longe",longe);
        i.putExtra("name",name);
        i.putExtra("phone",phone);
        i.putExtra("pic",Uri.parse(pic));
        startActivity(i);
    }
}
