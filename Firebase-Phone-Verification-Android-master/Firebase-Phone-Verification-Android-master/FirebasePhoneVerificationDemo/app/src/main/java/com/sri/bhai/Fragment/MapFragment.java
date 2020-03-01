package com.sri.bhai.Fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.sri.bhai.DisplayActivity;
import com.sri.bhai.Helper.Common;
import com.sri.bhai.Helper.DataBaseHandler;
import com.sri.bhai.R;
import com.sri.bhai.Service.APIService;
import com.sri.bhai.Service.TrackerService;
import com.sri.bhai.model.ContactItem;
import com.sri.bhai.model.MyResponse;
import com.sri.bhai.model.Notification;
import com.sri.bhai.model.Sender;
import com.sri.bhai.model.User;
import com.sri.bhai.shared.SharedPrefManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.SEND_SMS;


public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = DisplayActivity.class.getSimpleName();
    private HashMap<String, Marker> mMarkers = new HashMap<>();
    private GoogleMap mMap;
    User user1;
    List<User> list = new ArrayList<>();
    double lat,lon;
    public long idx = 0;
    FloatingActionButton settingss,call,location;
    private FusedLocationProviderClient client;
    Button alert;
    public long id;
    public static final int REQUEST_CALL_CONTACTS = 1;
    private static View view;
    double cu_lat,cu_long;
    private SettingsClient mSettingsClient;
    private LocationSettingsRequest mLocationSettingsRequest;
    GoogleApiClient mGoogleApiClient;
    private static final int REQUEST_CHECK_SETTINGS = 214;
    private static final int REQUEST_ENABLE_GPS = 516;
    //public List<String> tokens=new Str;
    private List<String> tokens = new ArrayList<>();
    APIService mService;
    DataBaseHandler db;
    DatabaseReference dataBaseProfile;
    private List<ContactItem> contactItems;
    private SupportMapFragment fragment;;
    RelativeLayout mapon;
    Fragment map;
    FrameLayout map_view;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;
    //changing base url for server purposes
    public static final String BASE_URL = "http://bhai.org.in/bhai/";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //View view = null;
        client = LocationServices.getFusedLocationProviderClient(getActivity());
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.map_view, container, false);
        } catch (InflateException e) {

        }
        if(!SharedPrefManager.getInstance(getContext()).isalert()){
            displayalert();
            SharedPrefManager.getInstance(getContext()).alert(true);
        }


        setHasOptionsMenu(true);
        db=new DataBaseHandler(getContext(),"DB_Name",1);
        mService = Common.getFCMService();
        mGoogleApiClient = new GoogleApiClient
                .Builder(getContext())
                .enableAutoManage(getActivity(), 34992, this)
                .addApi(LocationServices.API)
                .addOnConnectionFailedListener(this)
                .build();
        settingsrequest();
        Log.e("Token",""+ FirebaseInstanceId.getInstance().getToken());
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        fragment = new SupportMapFragment();
        transaction.add(R.id.map, fragment);
        transaction.commit();
        mapon=view.findViewById(R.id.onmap);
        map_view=(FrameLayout) view.findViewById(R.id.map_view);
        settingss=(FloatingActionButton) view.findViewById(R.id.settings);
        call=(FloatingActionButton)view.findViewById(R.id.call);
        location=(FloatingActionButton)view.findViewById(R.id.location);
        //notification = view.findViewById(R.id.notification);
        alert=(Button)view.findViewById(R.id.alert);
        contactItems = new ArrayList<>();
        settingss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getContext(), SEND_SMS) == PackageManager.PERMISSION_GRANTED) {

                    boolean connected = false;
                    ConnectivityManager connectivityManager = (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                    if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                        connected = true;
                    }
                    else
                        connected = false;
                    if(!connected){

                        client.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {

                                if(location!=null)
                                {
                                    cu_lat = location.getLatitude();
                                    cu_long = location.getLongitude();
                                }

                            }
                        });}
                    String phone = "";
                    db.addContact("BHAI", "+919502243567", "true");
                    Cursor cursor1 =   db.all_phones();
                    SmsManager smsManager = SmsManager.getDefault();
                    String uri = "http://maps.google.com/maps?daddr=" + cu_lat + "," + cu_long + " (" + "I AM HERE" + ")";
                    String a = SharedPrefManager.getInstance(getContext()).getmsg() + "\n" + uri;
                    if (cursor1.moveToFirst()) {
                        do {
                            //Log.e("phone",cursor1.getString(3));
                            phone = cursor1.getString(3);
                            smsManager.sendTextMessage(phone, null, a, null, null);
                        } while (cursor1.moveToNext());
                    }
                    if(cursor1.moveToFirst()) {
                        Toast.makeText(getContext(), "Message Sent", Toast.LENGTH_LONG).show();
                    }else  Toast.makeText(getContext(), "Please select minimum one trusted contact.", Toast.LENGTH_LONG).show();

                    Log.e("phone", phone);
                }
                else
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{SEND_SMS}, 1);
                }

            }
        });


        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + SharedPrefManager.getInstance(getContext()).getpolice()));
                if (ContextCompat.checkSelfPermission(getContext(), CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    startActivity(callIntent);
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{CALL_PHONE}, 1);
                    }
                }
                //Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + SharedPrefManager.getInstance(getContext()).getpolice()));// Initiates the Intent
                //startActivity(intent);

            }
        });
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mylocation();

            }
        });
        alert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String id = FirebaseAuth.getInstance().getCurrentUser().getUid();


//
                //dp.child()
                Log.e("Token Size", "size" + tokens.size());
                String tkn = "";   String pname=SharedPrefManager.getInstance(getContext()).getname();
                String pphone=SharedPrefManager.getInstance(getContext()).getphone();
                notification_db ndb = new notification_db(pname,pphone,cu_lat,cu_long);
                DatabaseReference dbs = FirebaseDatabase.getInstance().getReference("Notifications").child("Sent");//
                dbs.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            //String s = snapshot.child("count").getValue(String.class);
                            idx = dataSnapshot.getChildrenCount();
                     // }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();

                Random r = new Random();
                int x = r.nextInt();
                String id =currentUser.getUid()+ x + "" + idx ;
                Date dt = new Date();
                SimpleDateFormat ft = new SimpleDateFormat("E YYYY.MM.dd 'at' hh:mm:ss a");
                String date = ft.format(dt);

                DatabaseReference dp = FirebaseDatabase.getInstance().getReference("Notifications").child("Sent").child(id);//
                dp.setValue(ndb);
                dp.child("Date").setValue(date);
//                DatabaseReference dps = FirebaseDatabase.getInstance().getReference("Notifications").child("Sent").child(count);//

                for (int i = 0; i < tokens.size(); i++) {
                    //sendNotification(tokens.get(i).toString());
                    tkn += tokens.get(i).toString() + "#";
                }
                String name;
                String phone;
                String pic;
                try {
                    name = SharedPrefManager.getInstance(getContext()).getname();
                    phone = SharedPrefManager.getInstance(getContext()).getphone();
                    pic = SharedPrefManager.getInstance(getContext()).getimage();
                } catch (Exception e) {
                    name = "none";
                    phone = "none";
                    pic = "none";

                }
                Log.e("token", tkn);
                if (!tkn.contentEquals("")) {
                    //edited by pls
                    //edited by pls
                    new AsyncLogin().execute(tkn, String.valueOf(cu_lat), String.valueOf(cu_long), name, phone, pic);
                    //edited bypls
                    if (ContextCompat.checkSelfPermission(getContext(), SEND_SMS) == PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


                        db.addContact("BHAI", "+919502243567", "true");

                        SmsManager smsManager = SmsManager.getDefault();
                        String pno = "";
                        String uri = "http://maps.google.com/maps?daddr=" + cu_lat + "," + cu_long + " (" + "I AM HERE" + ")";
                        String a = SharedPrefManager.getInstance(getContext()).getmsg() + "\n" + uri;
                        Cursor cursor1 = db.all_phones();
                        if (cursor1.moveToFirst()) {
                            do {
                                Log.e("phone", cursor1.getString(3));
                                pno = cursor1.getString(3) + ";";
                                smsManager.sendTextMessage(pno, null, a, null, null);
                            } while (cursor1.moveToNext());
                        }
                        if (cursor1.moveToFirst()) {
                            Toast.makeText(getContext(), "Message Sent", Toast.LENGTH_LONG).show();
                        } else
                            Toast.makeText(getContext(), "Please select minimum one trusted contact.", Toast.LENGTH_LONG).show();
                        Log.e("phone", pno);
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{SEND_SMS}, 1);
                    }

                    //editd by pls
                } else {
                    boolean connected = false;
                    ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                    if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                        //we are connected to a network
                        connected = true;
                    } else
                        connected = false;
                    if (!connected) {

                        client.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {

                                if (location != null) {
                                    Toast.makeText(getContext(), "No One is Online/Your'e Offline and hence messages are send to your trusted contacts.", Toast.LENGTH_LONG).show();
                                    cu_lat = location.getLatitude();
                                    cu_long = location.getLongitude();
                                }

                            }
                        });
                    }

                                    }

            }

        });

        fragment.getMapAsync(this);
        Log.e("Phone Number", SharedPrefManager.getInstance(getContext()).getphone());
        //changes done by pls

        return view;
    }





    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        inflater.inflate(R.menu.main2, menu);
        MenuItem item = menu.findItem(R.id.switch_Id);
        item.setActionView(R.layout.show_protected_switch);
        SwitchCompat mySwitch = item.getActionView().findViewById(R.id.switchForActionBar);
        mySwitch.setChecked(SharedPrefManager.getInstance(getContext()).isonline());
        if(SharedPrefManager.getInstance(getContext()).isonline()){
            map_view.setVisibility(View.VISIBLE);
            mapon.setVisibility(View.VISIBLE);
            subscribeToUpdates();
            getContext().startService(new Intent(getContext(), TrackerService.class));
            //Toast.makeText(getContext(),"true",Toast.LENGTH_LONG).show();

        }else{
            map_view.setVisibility(View.GONE);
            mapon.setVisibility(View.GONE);
            getContext().stopService(new Intent(getContext(), TrackerService.class));

        }
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("user");
                    String mobileNumber= SharedPrefManager.getInstance(getContext()).getphone();
                    rootRef.child(mobileNumber).child("online").setValue("1");
                    SharedPrefManager.getInstance(getContext()).saveonline(true);
                    map_view.setVisibility(View.VISIBLE);
                    mapon.setVisibility(View.VISIBLE);
                    subscribeToUpdates();
                    getContext().startService(new Intent(getContext(), TrackerService.class));
                    setMarker();
                    //Toast.makeText(getContext(),"true",Toast.LENGTH_LONG).show();
                }else{
                    final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("user");
                    String mobileNumber= SharedPrefManager.getInstance(getContext()).getphone();
                    rootRef.child(mobileNumber).child("online").setValue("0");
                    SharedPrefManager.getInstance(getContext()).saveonline(false);
                    map_view.setVisibility(View.GONE);
                    mapon.setVisibility(View.GONE);
                    getContext().stopService(new Intent(getContext(), TrackerService.class));
                    //Toast.makeText(getContext(),"else",Toast.LENGTH_LONG).show();

                }
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    protected void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                CALL_PHONE)) {
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{CALL_PHONE},
                    REQUEST_CALL_CONTACTS);
        }

    }


    @Override
    public void onMapReady(GoogleMap map) {
        locationChecker(mGoogleApiClient, getActivity());
        mMap = map;
        mMap.setMaxZoomPreference(30);
        subscribeToUpdates();
        settingsrequest();
    }
    private void loginToFirebase() {
        String email = getString(R.string.firebase_email);
        String password = getString(R.string.firebase_password);
        FirebaseAuth.getInstance().signInWithEmailAndPassword(
                email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    subscribeToUpdates();
                    Log.d(TAG, "firebase auth success");
                } else {
                    Log.d(TAG, "firebase auth failed");
                }
            }
        });
    }

    private void subscribeToUpdates() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_path));

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                getCurrent(dataSnapshot);
                setMarker();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                // Log.e("String",dataSnapshot.getKey());
                Log.e("on data change",dataSnapshot.getKey());
                getCurrent(dataSnapshot);
                setMarker();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d(TAG, "Failed to read value.", error.toException());
            }
        });

    }
    private void getCurrent(DataSnapshot dataSnapshot){
        String key = dataSnapshot.getKey();
        //keys.add(key);
        //Log.e("key",""+key);
        try {
            if (dataSnapshot.getKey().contentEquals(SharedPrefManager.getInstance(getContext()).getphone())) {
                HashMap<String, Object> value = (HashMap<String, Object>) dataSnapshot.getValue();
                try {
                    cu_lat = Double.parseDouble(value.get("Lattitude").toString());
                    cu_long = Double.parseDouble(value.get("Longitude").toString());
                    LatLng location = new LatLng(cu_lat, cu_long);
                    if (!mMarkers.containsKey(key)) {
                        mMarkers.put(key, mMap.addMarker(new MarkerOptions().title(key).snippet(value.get("Name").toString()).position(location)));
                    } else {
                        mMarkers.get(key).setPosition(location);
                    }
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    for (Marker marker : mMarkers.values()) {
                        builder.include(marker.getPosition());
                        marker.showInfoWindow();
                    }
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 300));
                    /*
                    if (!mMarkers.containsKey(key)) {
                        //mMarkers.put(key, mMap.addMarker(new MarkerOptions().title(key).position(location)));

                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(cu_lat, cu_long))
                                .title(key)
                                .snippet(value.get("Name").toString())
                        );
                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(new LatLng(cu_lat, cu_long))      // Sets the center of the map to location user
                                .zoom(17)                   // Sets the zoom
                                .bearing(90)                // Sets the orientation of the camera to east
                                .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                                .build();                   // Creates a CameraPosition from the builder
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }*/
                } catch (Exception e) {
                    Log.e("value", "novalue");
                }
            }
        }catch (Exception e){

        }
    }
    private void setMarker() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("user");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    tokens.clear();
                    for(DataSnapshot d : dataSnapshot.getChildren()) {
                        try {
                            HashMap<String, Object> value = (HashMap<String, Object>) d.getValue();
                            String key = d.getKey();
                            // Log.e("keys",key);
                            String name = "NO NAME";
                            //HashMap<String, Object> value = (HashMap<String, Object>) dataSnapshot.getValue();
                            double lat = Double.parseDouble(value.get("Lattitude").toString());
                            double lng = Double.parseDouble(value.get("Longitude").toString());
                            int radius = Integer.parseInt(SharedPrefManager.getInstance(getContext()).getdist());
                            //Log.e("online",key+"=online:"+value.get("online").toString());
                            //Log.e("RADIUS","radius::"+radius);
                            float[] dist = new float[1];
                            Location.distanceBetween(cu_lat, cu_long, lat, lng, dist);
                            name = value.get("Name").toString();

                            //Log.e("phone value",name+"radius::"+dist[0]);

                            if (dist[0] / radius < 1 && dist[0] / radius != 0 ) {
                                try {
                                    name = value.get("Name").toString();
                                    Log.e("phone value",name+"radius::"+dist[0]);
                                    if (key != SharedPrefManager.getInstance(getContext()).getphone() && value.get("online").toString().contentEquals("1")) {

                                        tokens.add(value.get("Token").toString());
                                    }
                                } catch (Exception E) {
                                    name = "NO NAME";
                                    Log.e("exception", E.toString());
                                }
                                LatLng location = new LatLng(lat, lng);
                                if(value.get("online").toString().contentEquals("1")) {

                                    if (!mMarkers.containsKey(key)) {
                                        //Log.e("if",key+"=online:"+value.get("online").toString());
                                        mMarkers.put(key, mMap.addMarker(new MarkerOptions().position(location)));
                                    } else {
                                        //Log.e("else",key+"=online:"+value.get("online").toString());
                                        mMarkers.get(key).setPosition(location);
                                        mMarkers.get(key).setVisible(true);
                                    }
                                }else{
                                    //Log.e("ifelse",key+"=online:"+value.get("online").toString());
                                    mMarkers.get(key).setVisible(false);

                                }
                                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                for (Marker marker : mMarkers.values()) {
                                    builder.include(marker.getPosition());
                                    //marker.showInfoWindow();
                                }
                                //mMarkers.clear();
                                // mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 300));
                            }





                        } catch (Exception e) {
                            Log.e("Exce", "No value");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    public void mylocation(){
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(cu_lat, cu_long))      // Sets the center of the map to location user
                .zoom(17)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        getFragmentManager().beginTransaction().remove(this).commit();
    }
    @Override
    public void onPause() {
        super.onPause();
        mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();
    }
    public void settingsrequest()
    {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true); //this is the key ingredient

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
// Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        loginToFirebase();
                        subscribeToUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        settingsrequest();//keep asking if imp or do whatever
                        break;
                }

                break;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    public static void locationChecker(GoogleApiClient mGoogleApiClient, final Activity activity) {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    activity, 1000);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }
    private void sendNotification(String Token) {

        String name;
        String phone;
        String pic;
        try{
            name=SharedPrefManager.getInstance(getContext()).getname();
            phone=SharedPrefManager.getInstance(getContext()).getphone();
            pic=SharedPrefManager.getInstance(getContext()).getimage();
        }catch (Exception e){
            name="none";
            phone="none";
            pic="none";

        }
        //Log.e("pic",pic);
        Notification notification = new Notification("PLEASE SAVE ME", "EMERGENCY HERE#"+String.valueOf(cu_lat)+"#"+String.valueOf(cu_long)+"#"+name+"#"+phone+"#"+pic,String.valueOf(cu_lat),String.valueOf(cu_long));
        Sender content = new Sender(Token, notification);

        mService.sendNotification(content).enqueue(new Callback<MyResponse>() {
            @Override

            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                //only run when get result
                if (response.code() == 200) {
                    if (response.body().success == 1) {
                        Toast.makeText(getContext(), "Send Successfully.", Toast.LENGTH_SHORT).show();
                        //finish();
                    } else {
                        //Toast.makeText(getContext(), "Failed to place order.", Toast.LENGTH_SHORT).show();

                    }
                }
            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {
                Log.e("ERROR", t.getMessage());
            }
        });

    }
    private class AsyncLogin extends AsyncTask<String, String ,String> {



        ProgressDialog pdLoading=new ProgressDialog(getContext());
        HttpURLConnection conn;
        URL url=null;
        @Override
        protected void onPreExecute() {
            pdLoading.setMessage("\t Loading...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }


        @Override
        protected String doInBackground(String... params) {
            try {
                url=new URL(BASE_URL+"notify.php");
            }catch (MalformedURLException e) {
                Log.e("error","Unable to find url");
                return "exception";
            }
            try {
                conn=(HttpURLConnection)url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");

                conn.setDoInput(true);
                conn.setDoOutput(true);

                //Appened Parameters to url

                Uri.Builder builder=new Uri.Builder()
                        .appendQueryParameter("tokens",params[0])
                        .appendQueryParameter("c_lat",params[1])
                        .appendQueryParameter("c_longe",params[2])
                        .appendQueryParameter("name",params[3])
                        .appendQueryParameter("phone",params[4])
                        .appendQueryParameter("pic",params[5])
                        ;
                String query = builder.build().getEncodedQuery();
                // Open connection for sending data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();



            } catch (IOException e1) {
                Log.e("error","Unable to connect to server");
                return "exception";
            }

            try {

                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Pass data to onPostExecute method
                    return(result.toString());

                }else{

                    return("unsuccessful");
                }

            } catch (IOException e) {
                Log.e("error","Unable to get data");
                return "exception";
            } finally {
                conn.disconnect();
            }

        }

        @Override
        protected void onPostExecute(String result) {

            //this method will be running on UI thread

            pdLoading.dismiss();
            Log.e("result",result.toString());
            Toast.makeText(getContext(),result.toString(),Toast.LENGTH_SHORT).show();

            if(result.equalsIgnoreCase("true"))
            {


                Toast.makeText(getContext(), "Update Successfully", Toast.LENGTH_LONG).show();

            }else if (result.equalsIgnoreCase("false")){

                // If username and password does not match display a error message
                Toast.makeText(getContext(), "Error in Registering,\t Please try again later", Toast.LENGTH_LONG).show();

            }
            else if(result.equalsIgnoreCase("already login")){

                Toast.makeText(getContext(), "You are already Login", Toast.LENGTH_LONG).show();

            }
            else if(result.equalsIgnoreCase("area")){

                Toast.makeText(getContext(), "Please choose an area", Toast.LENGTH_LONG).show();

            }
            else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {

                Toast.makeText(getContext(), "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();

            }
        }

    }
    public void displayalert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Alert!");
        builder.setMessage("Please Use Alert Button In Emergency Only . Don't Miss Use    - Thank you");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }


}