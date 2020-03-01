package com.sri.bhai;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.sri.bhai.Fragment.MapFragment;
import com.sri.bhai.Fragment.about_us;
import com.sri.bhai.Fragment.notification;
import com.sri.bhai.Fragment.selectcontact_new;
import com.sri.bhai.Fragment.settings;
import com.sri.bhai.Service.TrackerService;
import com.sri.bhai.model.User;
import com.sri.bhai.shared.SharedPrefManager;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ImageView pic,edit;
    TextView name,phone,email;
    private static final String TAG = DisplayActivity.class.getSimpleName();
    private HashMap<String, Marker> mMarkers = new HashMap<>();
    private GoogleMap mMap;
    User user1;
    List<User> list = new ArrayList<>();
    double lat,lon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        Log.e("name",""+SharedPrefManager.getInstance(getApplicationContext()).getname());
        Log.e("phone",""+SharedPrefManager.getInstance(getApplicationContext()).getemail());
        Log.e("pic",""+SharedPrefManager.getInstance(getApplicationContext()).getimage());
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        name = (TextView) headerView.findViewById(R.id.name);
        phone = (TextView) headerView.findViewById(R.id.phone);
        pic = (ImageView) headerView.findViewById(R.id.imageView);
        edit = (ImageView) headerView.findViewById(R.id.edit);
        name.setText(SharedPrefManager.getInstance(getApplicationContext()).getname());
        phone.setText(SharedPrefManager.getInstance(getApplicationContext()).getphone());
       // token=Math.random();

        Glide.with(getApplicationContext())
                .load(SharedPrefManager.getInstance(getApplicationContext()).getimage())
                .apply(RequestOptions.skipMemoryCacheOf(true))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .into(pic);
        //Picasso.with(getApplicationContext()).invalidate(SharedPrefManager.getInstance(getApplicationContext()).getimage());
        //Picasso.with(getApplicationContext()).load(SharedPrefManager.getInstance(getApplicationContext()).getimage()).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE);
       // Picasso.with(getApplicationContext()).load(SharedPrefManager.getInstance(getApplicationContext()).getimage()).into(pic);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),edit_profile.class);
                startActivity(i);
            }
        });
        displaySelectedScreen(R.id.nav_home);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

        back();

    }
    public void back(){
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
    public void logout(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit");
        builder.setMessage("Are You Sure?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("user");
                String mobileNumber= SharedPrefManager.getInstance(getApplicationContext()).getphone();
                rootRef.child(mobileNumber).child("online").setValue("0");
                stopService(new Intent(getApplicationContext(), TrackerService.class));
                FirebaseAuth.getInstance().signOut();
                SharedPreferences preferences =getSharedPreferences("FCMSharedPref", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.commit();
                Intent i=new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main2, menu);
        /*
        MenuItem item = menu.findItem(R.id.switch_Id);
        item.setActionView(R.layout.show_protected_switch);
        SwitchCompat mySwitch = item.getActionView().findViewById(R.id.switchForActionBar);
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    startService(new Intent(getApplicationContext(), TrackerService.class));
                    Toast.makeText(getApplicationContext(),"true",Toast.LENGTH_LONG).show();
                }else{
                    stopService(new Intent(getApplicationContext(), TrackerService.class));
                    Toast.makeText(getApplicationContext(),"else",Toast.LENGTH_LONG).show();

                }
            }
        });
        */
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.switch_Id) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void displaySelectedScreen(int itemId) {

        //creating fragment object
        Fragment fragment = null;

        //initializing the fragment object which is selected
        switch (itemId) {
            case R.id.nav_home:
                fragment = new MapFragment();
                break;
            case R.id.nav_gallery:
                fragment = new settings();
                break;

            case R.id.alerts:
                fragment = new notification();
                break;
            case R.id.nav_slideshow:
                fragment = new selectcontact_new();
                break;
            case R.id.nav_tools:
                fragment = new about_us();
                break;
            case R.id.nav_share:
                logout();

                break;
            default:
                fragment = new MapFragment();
                break;
        }

        //replacing the fragment
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        displaySelectedScreen(item.getItemId());

        return true;
    }





}
