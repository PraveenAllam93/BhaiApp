package com.sri.bhai.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sri.bhai.Main2Activity;
import com.sri.bhai.R;
import com.sri.bhai.shared.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;


public class notification extends Fragment {
    DatabaseReference db;
    private List<sent> alertDetails = new ArrayList<>();
    private static int REQUEST_OVERLAY_PERMISSION=12;
    rvAdapter rvAdapter;
    TextView textView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments

        final View v = inflater.inflate(R.layout.activity_notification, container, false);
        //getActivity().setTitle("SETTINGS");
        ((Main2Activity) getActivity()).getSupportActionBar().setTitle("Sent Alerts");

        //canDrawOverlays(getContext());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Settings.ACTION_PRIVACY_SETTINGS
            if (!Settings.canDrawOverlays(getContext())) {
                // ask for setting
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getActivity().getPackageName()));
                startActivityForResult(intent, REQUEST_OVERLAY_PERMISSION);
            }
        }
        db = FirebaseDatabase.getInstance().getReference("Notifications").child("Sent");
        final RecyclerView rv = (RecyclerView)v.findViewById(R.id.rv);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        rv.setHasFixedSize(true);
        rv.setItemViewCacheSize(150);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rvAdapter = new rvAdapter(getContext(),alertDetails);
        rv.setAdapter(rvAdapter);

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String phone = SharedPrefManager.getInstance(getContext()).getphone();
                alertDetails.clear();
                //else {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //if(!snapshot.getKey().equals("Location")) {

                    String uid = snapshot.child("mobileNumber").getValue(String.class);
                    if (uid.equals(phone)) {

                        String dt = snapshot.child("Date").getValue(String.class);

                        double cu_lat = snapshot.child("cu_lat").getValue(double.class);
                        double cu_long = snapshot.child("cu_long").getValue(double.class);
                        String loc = "Alert Generated on: "+dt+"."+ "\n"+ "Location:"+cu_lat+","+cu_long;
                        sent alerts =new sent(loc);
                        alertDetails.add(alerts);
                    }

                }

                if (alertDetails.isEmpty()) {

                    Toast.makeText(getContext(),"No Alerts.",Toast.LENGTH_LONG).show();
                }

                rvAdapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        rv.setLayoutManager(new LinearLayoutManager(getContext()));


        return v;
    }
}
