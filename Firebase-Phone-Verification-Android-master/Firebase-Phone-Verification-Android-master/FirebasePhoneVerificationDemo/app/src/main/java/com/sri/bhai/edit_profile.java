package com.sri.bhai;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.sri.bhai.shared.SharedPrefManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Calendar;
import java.util.UUID;
import java.util.regex.Pattern;

public class edit_profile extends AppCompatActivity {
    ImageView pic,edit;
    String gender;
    RadioGroup genderRadioGroup;
    EditText name,email,dob,address;
    Button save;
    boolean choose=false;
    private final int PICK_IMAGE_REQUEST = 78;
    private Uri filePath;
    FirebaseStorage storage;
    StorageReference storageReference;
    RadioButton Male,Female;
    private int mYear, mMonth, mDay, mHour, mMinute;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        pic=(ImageView)findViewById(R.id.imageView);
        edit=(ImageView)findViewById(R.id.edit);
        name=(EditText)findViewById(R.id.name);
        email=(EditText)findViewById(R.id.email);
        save=(Button)findViewById(R.id.save);
        dob=(EditText)findViewById(R.id.dob);
        address=(EditText)findViewById(R.id.address);
        genderRadioGroup=(RadioGroup)findViewById(R.id.genderRadioGroup);
        Male=(RadioButton)findViewById(R.id.maleRadioButton);
        Female=(RadioButton)findViewById(R.id.femaleRadioButton);
        name.setText(SharedPrefManager.getInstance(getApplicationContext()).getname());
        email.setText(SharedPrefManager.getInstance(getApplicationContext()).getemail());
        dob.setText(SharedPrefManager.getInstance(getApplicationContext()).getdob());
        address.setText(SharedPrefManager.getInstance(getApplicationContext()).getaddress());
        gender=SharedPrefManager.getInstance(getApplicationContext()).getgender();
        if(gender.contentEquals("Male")){
            Male.setChecked(true);
        }else{
            Female.setChecked(true);
        }
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

        name.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            }
        });
        email.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            }
        });
        Glide.with(getApplicationContext())
                .load(SharedPrefManager.getInstance(getApplicationContext()).getimage())
                .apply(RequestOptions.skipMemoryCacheOf(true))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .into(pic);
        //Picasso.get().load(SharedPrefManager.getInstance(getApplicationContext()).getimage()).into(pic);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
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
                pic.setImageBitmap(bitmap);
                choose=true;
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }else{
            choose=false;
        }
    }
    private void uploadImage() {

        if(filePath == null && !choose) {
            if(name.getText().toString().contentEquals("") || email.getText().toString().contentEquals("")){
                Toast.makeText(edit_profile.this, "Fill All Fields", Toast.LENGTH_SHORT).show();

            }
            else if(!isValidEmailId(email.getText().toString().trim()))
            {
                Toast.makeText(edit_profile.this, "INVALID EMAIL", Toast.LENGTH_SHORT).show();

            }else{

                String mobileNumber = SharedPrefManager.getInstance(getApplicationContext()).getphone();
                final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("user");
                rootRef.child(mobileNumber).child("Name").setValue(name.getText().toString());
                rootRef.child(mobileNumber).child("Email").setValue(email.getText().toString());
                rootRef.child(mobileNumber).child("gender").setValue(gender);
                rootRef.child(mobileNumber).child("dob").setValue(dob.getText().toString());
                rootRef.child(mobileNumber).child("address").setValue(email.getText().toString());
                SharedPrefManager.getInstance(getApplicationContext()).saveName(name.getText().toString());
                SharedPrefManager.getInstance(getApplicationContext()).saveEmail(email.getText().toString());
                SharedPrefManager.getInstance(getApplicationContext()).savegender(gender);
                SharedPrefManager.getInstance(getApplicationContext()).savedob(dob.getText().toString());
                SharedPrefManager.getInstance(getApplicationContext()).saveaddress(address.getText().toString());

                //rootRef.child(mobileNumber).child("Image").setValue(downloadUri.toString());

                Toast.makeText(edit_profile.this, "Updated", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(), Main2Activity.class);
                startActivity(i);
            }
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
                            rootRef.child(mobileNumber).child("Image").setValue(downloadUri.toString());
                            rootRef.child(mobileNumber).child("gender").setValue(gender);
                            rootRef.child(mobileNumber).child("dob").setValue(dob.getText().toString());
                            rootRef.child(mobileNumber).child("address").setValue(email.getText().toString());
                            progressDialog.dismiss();
                            SharedPrefManager.getInstance(getApplicationContext()).saveName(name.getText().toString());
                            SharedPrefManager.getInstance(getApplicationContext()).saveEmail(email.getText().toString());
                            SharedPrefManager.getInstance(getApplicationContext()).savePic(downloadUri.toString());
                            SharedPrefManager.getInstance(getApplicationContext()).savegender(gender);
                            SharedPrefManager.getInstance(getApplicationContext()).savedob(dob.getText().toString());
                            SharedPrefManager.getInstance(getApplicationContext()).saveaddress(address.getText().toString());
                            Toast.makeText(edit_profile.this, "Updated", Toast.LENGTH_SHORT).show();
                            Intent i=new Intent(getApplicationContext(),Main2Activity.class);
                            startActivity(i);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(edit_profile.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if ( id == android.R.id.home ) {
            Intent i=new Intent(getApplicationContext(),Main2Activity.class);
            startActivity(i);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
    private boolean isValidEmailId(String email){

        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }
}
