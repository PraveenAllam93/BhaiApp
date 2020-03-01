package com.sri.bhai;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.sri.bhai.shared.SharedPrefManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.phonenumberui.PhoneNumberActivity;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private String mobileNumber = "";
    private Button btnVerify;
    private static final int REQUEST_PHONE_VERIFICATION = 1080;
    //DatabaseReference databaseReference;
    FirebaseDatabase db;
    DatabaseReference user;
    ImageView mBellImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent in=new Intent();
        Bundle bundle = in.getExtras();
        if (bundle != null) {
            for (String key : bundle.keySet()) {
                Object value = bundle.get(key);
                Log.d("FCM", "Key: " + key + " Value: " + value);
            }
        }
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        mBellImageView = (ImageView) findViewById(R.id.image_view_bell);
        shake.setDuration(3000);
        mBellImageView.startAnimation(shake);
        db = FirebaseDatabase.getInstance();
        user = db.getReference("");
        Log.e("Token",""+FirebaseInstanceId.getInstance().getToken());

        //btnVerify = findViewById(R.id.btnVerify);
        if(SharedPrefManager.getInstance(getApplicationContext()).islogin()){
            Intent i=new Intent(getApplicationContext(),TrackerActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            overridePendingTransition (0, 0);
            //startActivity(i);
        }else {
            Intent intent = new Intent(MainActivity.this, PhoneNumberActivity.class);
            //Optionally you can add toolbar title
            intent.putExtra("TITLE", getResources().getString(R.string.app_name));
            //Optionally you can pass phone number to populate automatically.
            intent.putExtra("PHONE_NUMBER", "");
            startActivityForResult(intent, REQUEST_PHONE_VERIFICATION);
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_PHONE_VERIFICATION:
// If mobile number is verified successfully then you get your phone number to perform further operations.
                if (data != null && data.hasExtra("PHONE_NUMBER") && data.getStringExtra("PHONE_NUMBER") != null) {
                    String phoneNumber = data.getStringExtra("PHONE_NUMBER");
                    mobileNumber = phoneNumber;
                    SharedPrefManager.getInstance(getApplicationContext()).login(true);
                    final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("user");
                    rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            if (snapshot.hasChild(mobileNumber)) {
                                // run some code
                                rootRef.child(mobileNumber).child("Token").setValue(FirebaseInstanceId.getInstance().getToken());
                                rootRef.child(mobileNumber).child("online").setValue("1");
                                //rootRef.child(mobileNumber).child("online").setValue("1");

                                Log.i("Token",FirebaseInstanceId.getInstance().getToken());
                                //user.child(mobileNumber).child("Token").setValue(FirebaseInstanceId.getInstance().getToken());
                                SharedPrefManager.getInstance(getApplicationContext()).saveDeviceToken(FirebaseInstanceId.getInstance().getToken());
                                SharedPrefManager.getInstance(getApplicationContext()).savePhone(mobileNumber);
                                //getdata();
                                DatabaseReference ref= FirebaseDatabase.getInstance().getReference("user");
                                ref.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                                            String mobileNumber= SharedPrefManager.getInstance(getApplicationContext()).getphone();

                                            if(child.getKey().contentEquals(mobileNumber)) {

                                                HashMap<String, Object> value = (HashMap<String, Object>) child.getValue();
                                                SharedPrefManager.getInstance(getApplicationContext()).savePhone(mobileNumber);
                                                SharedPrefManager.getInstance(getApplicationContext()).saveName(value.get("Name").toString());
                                                SharedPrefManager.getInstance(getApplicationContext()).saveEmail(value.get("Email").toString());
                                                SharedPrefManager.getInstance(getApplicationContext()).savePic(value.get("Image").toString());
                                                SharedPrefManager.getInstance(getApplicationContext()).savegender(value.get("gender").toString());
                                                SharedPrefManager.getInstance(getApplicationContext()).savedob(value.get("dob").toString());
                                                SharedPrefManager.getInstance(getApplicationContext()).saveaddress(value.get("address").toString());
                                                SharedPrefManager.getInstance(getApplicationContext()).saveonline(true);

                                            }
                                        }

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                                Intent i=new Intent(getApplicationContext(),conditions.class);
                                i.putExtra("login","true");
                                startActivity(i);

                            }else{
                                rootRef.push().setValue(mobileNumber);
                                rootRef.child(mobileNumber).child("Token").setValue(FirebaseInstanceId.getInstance().getToken());
                                SharedPrefManager.getInstance(getApplicationContext()).saveDeviceToken(FirebaseInstanceId.getInstance().getToken());
                                SharedPrefManager.getInstance(getApplicationContext()).savePhone(mobileNumber);
                                Intent i=new Intent(getApplicationContext(),create_profile.class);
                                startActivity(i);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                } else {
                    // If mobile number is not verified successfully You can hendle according to your requirement.
                    Toast.makeText(MainActivity.this,getString(R.string.mobile_verification_fails),Toast.LENGTH_SHORT);
                }
                break;
        }
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit");
        builder.setMessage("Are You Sure?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                System.exit(0);
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }


}
