package com.sri.bhai;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.sri.bhai.shared.SharedPrefManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;
import java.util.regex.Pattern;

public class create_profile extends AppCompatActivity {
    EditText name,email,dob,address;
    String gender="Male";
    RadioGroup genderRadioGroup;
    ImageView profile,profile_pic;
    Button send;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;
    FirebaseStorage storage;
    StorageReference storageReference;
    boolean choose_pic=false;
    private int mYear, mMonth, mDay, mHour, mMinute;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Intent i=getIntent();
        //if(i.getStringExtra("login").contentEquals("true")){
          //  getdata();
        //}
        //i.getStringExtra("login")
        name=(EditText)findViewById(R.id.name);
        email=(EditText)findViewById(R.id.email);
        dob=(EditText)findViewById(R.id.dob);
        address=(EditText)findViewById(R.id.address);
        genderRadioGroup=(RadioGroup)findViewById(R.id.genderRadioGroup);
        genderRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                RadioButton radioButton = (RadioButton) findViewById(checkedId);
                gender=radioButton.getText().toString();
                //Toast.makeText(getBaseContext(), radioButton.getText(), Toast.LENGTH_SHORT).show();
            }
        }
        );
        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date();
            }
        });

        profile=(ImageView)findViewById(R.id.profile);
        profile_pic=(ImageView)findViewById(R.id.profile_pic);
        send=(Button)findViewById(R.id.login);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });
    }
    public void date(){
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        dob.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if ( id == android.R.id.home ) {
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

        return super.onOptionsItemSelected(item);
    }
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                profile_pic.setImageBitmap(bitmap);
                choose_pic=true;
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }else{
            choose_pic=false;
        }
    }
    private void uploadImage() {

        if(filePath == null && !choose_pic) {
            Toast.makeText(create_profile.this, "Select Your Pic", Toast.LENGTH_SHORT).show();

        }else if(name.getText().toString().trim().contentEquals("") || email.getText().toString().trim().contentEquals("") || dob.getText().toString().trim().contentEquals("") || address.getText().toString().trim().contentEquals("")){
            Toast.makeText(create_profile.this, "Fill All Fields", Toast.LENGTH_SHORT).show();

        }
        else if(!isValidEmailId(email.getText().toString().trim())){
            Toast.makeText(create_profile.this, "INVALID EMAIL", Toast.LENGTH_SHORT).show();

        }
        else{
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Uri downloadUri = taskSnapshot.getDownloadUrl();
                            String mobileNumber= SharedPrefManager.getInstance(getApplicationContext()).getphone();
                            final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("user");
                            rootRef.child(mobileNumber).child("Name").setValue(name.getText().toString());
                            rootRef.child(mobileNumber).child("Email").setValue(email.getText().toString());
                            rootRef.child(mobileNumber).child("gender").setValue(gender);
                            rootRef.child(mobileNumber).child("dob").setValue(dob.getText().toString());
                            rootRef.child(mobileNumber).child("address").setValue(address.getText().toString());
                            rootRef.child(mobileNumber).child("Image").setValue(downloadUri.toString());
                            rootRef.child(mobileNumber).child("online").setValue("1");
                            progressDialog.dismiss();
                            SharedPrefManager.getInstance(getApplicationContext()).saveName(name.getText().toString());
                            SharedPrefManager.getInstance(getApplicationContext()).saveEmail(email.getText().toString());
                            SharedPrefManager.getInstance(getApplicationContext()).savePic(downloadUri.toString());
                            SharedPrefManager.getInstance(getApplicationContext()).savegender(gender);
                            SharedPrefManager.getInstance(getApplicationContext()).savedob(dob.getText().toString());
                            SharedPrefManager.getInstance(getApplicationContext()).saveaddress(address.getText().toString());
                            SharedPrefManager.getInstance(getApplicationContext()).saveonline(true);
                            Toast.makeText(create_profile.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            Intent i=new Intent(getApplicationContext(),conditions.class);
                            startActivity(i);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(create_profile.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }
    private boolean isValidEmailId(String email){

        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }
    public void getdata(){

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
                        //SharedPrefManager.getInstance(getApplicationContext()).savePhone(mobileNumber);
                        Log.e("name",SharedPrefManager.getInstance(getApplicationContext()).getname());
                        Log.e("email",SharedPrefManager.getInstance(getApplicationContext()).getemail());
                        Log.e("image",SharedPrefManager.getInstance(getApplicationContext()).getimage());
                        Log.e("gender",SharedPrefManager.getInstance(getApplicationContext()).getgender());
                        Log.e("dob",SharedPrefManager.getInstance(getApplicationContext()).getdob());
                        Log.e("address",SharedPrefManager.getInstance(getApplicationContext()).getaddress());
                        Intent i=new Intent(getApplicationContext(),conditions.class);
                        //i.putExtra("login","yes");
                        startActivity(i);
                        //Intent i=new Intent(getApplicationContext(),getuser_data.class);
                        //startActivity(i);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
