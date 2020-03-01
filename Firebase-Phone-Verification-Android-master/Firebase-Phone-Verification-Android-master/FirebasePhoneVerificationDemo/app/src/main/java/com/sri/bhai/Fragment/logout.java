package com.sri.bhai.Fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sri.bhai.R;
import com.sri.bhai.shared.SharedPrefManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class logout extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        View v=inflater.inflate(R.layout.about_us, container, false);
        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("user");
        String mobileNumber= SharedPrefManager.getInstance(getContext()).getphone();
        rootRef.child(mobileNumber).child("online").setValue("0");
        FirebaseAuth.getInstance().signOut();
        /*
        SharedPreferences preferences =getContext().getSharedPreferences("FCMSharedPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
        getContext().stopService(new Intent(getContext(), TrackerService.class));
        Intent i=new Intent(getContext(), MainActivity.class);
        startActivity(i);
        */
        //finish();
        return v;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Home");
    }
}
