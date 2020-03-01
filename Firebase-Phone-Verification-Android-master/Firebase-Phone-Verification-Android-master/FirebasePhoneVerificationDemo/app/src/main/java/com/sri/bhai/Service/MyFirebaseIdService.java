package com.sri.bhai.Service;

import android.util.Log;

import com.sri.bhai.shared.SharedPrefManager;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh(){
        super.onTokenRefresh();
        String tokenRefreshed = FirebaseInstanceId.getInstance().getToken();
        Log.d("Token", "Refreshed token: " + tokenRefreshed);
        if(SharedPrefManager.getInstance(getApplicationContext()).getphone() != null) {
            updateTokenToFirebase(tokenRefreshed);
        }
    }

    private void updateTokenToFirebase(String tokenRefreshed){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference();
        //Token token = new Token(tokenRefreshed, false);
        // false because token send from client app
        SharedPrefManager.getInstance(getApplicationContext()).saveDeviceToken(tokenRefreshed);
        tokens.child(SharedPrefManager.getInstance(getApplicationContext()).getphone()).child("Token").setValue(tokenRefreshed);
    }
}
