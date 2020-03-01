package com.sri.bhai.Fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.sri.bhai.Helper.ContactAdapter;
import com.sri.bhai.Helper.DataBaseHandler;
import com.sri.bhai.Main2Activity;
import com.sri.bhai.R;
import com.sri.bhai.model.ContactItem;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import mx.com.quiin.contactpicker.Contact;
import mx.com.quiin.contactpicker.SimpleContact;
import mx.com.quiin.contactpicker.interfaces.ContactSelectionListener;
import mx.com.quiin.contactpicker.ui.ContactPickerActivity;
import static android.app.Activity.RESULT_OK;

public class selectcontact_new extends Fragment implements ContactSelectionListener {
    private static final int READ_CONTACT_REQUEST = 1;
    private static final int CONTACT_PICKER_REQUEST = 2;

    private List<Contact> mContacts = new ArrayList<>();
    private ListView listView;
    private TextView mTextView;
    DataBaseHandler db;
    private List<ContactItem> contactItems;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.selectnew_contact, container, false);
        //getActivity().setTitle("SELECT CONTACT");
        ((Main2Activity)getActivity()).getSupportActionBar().setTitle("SELECT CONTACT");

        listView = (ListView) v.findViewById(R.id.contact_list);

        //mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);

        mTextView = (TextView) v.findViewById(R.id.textView);
        db=new DataBaseHandler(getContext(),"DB_Name",1);

        Button btnOpenPicker = (Button) v.findViewById(R.id.btnOpenPicker);
        Button btnCLR = (Button) v.findViewById(R.id.btnClr);
        btnCLR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.trunk();
                Toast.makeText(getContext(),"Contacts Cleared",Toast.LENGTH_LONG).show();
                setRecyclerView();
            }
        });
        setRecyclerView();
        btnOpenPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(db.getcount() >= 10 )
                {
                    Toast.makeText(getContext(),"10 Contacts Already Selected. \n Click on Clear To select New Contacts.",Toast.LENGTH_LONG).show();

                }else{
                    launchContactPicker(v);
                }

            }
        });
        return v;
    }


    public void launchContactPicker(View view) {
        int permissionCheck = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS);
        if(permissionCheck == PackageManager.PERMISSION_GRANTED){
            Intent contactPicker = new Intent(getContext(), ContactPickerActivity.class);
            startActivityForResult(contactPicker, CONTACT_PICKER_REQUEST);
        }else{
            ActivityCompat.requestPermissions(getActivity(),
                    new String[] {Manifest.permission.READ_CONTACTS},
                    READ_CONTACT_REQUEST);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case CONTACT_PICKER_REQUEST:
                //contacts were selected
                if(resultCode == RESULT_OK){
                    if(data != null){
                        TreeSet<SimpleContact> selectedContacts = (TreeSet<SimpleContact>) data.getSerializableExtra(ContactPickerActivity.CP_SELECTED_CONTACTS);
                        if(selectedContacts != null) {
                            if(selectedContacts.size() + db.getcount() <= 10) {
                                contactItems = new ArrayList<>();
                                //db.trunk();
                                int i = 0;
                                for (SimpleContact selectedContact : selectedContacts) {
                                    List<String> list = new ArrayList<>();
                                    list.add(selectedContact.getCommunication());

                                    ContactItem item = new ContactItem();
                                    //item.setId(id);
                                    item.setName("" + i);
                                    item.setPhone("+" + i);
                                    Log.e("Name", selectedContact.getDisplayName());
                                    Log.e("Phone", selectedContact.getCommunication());
                                    contactItems.add(item);
                                    db.addContact(selectedContact.getDisplayName(), selectedContact.getCommunication(), "flase");
                                    Log.e("item", item.toString());
                                    mContacts.add(new Contact(selectedContact.getDisplayName(), list));
                                    item = null;
                                    i++;
                                }
                            }else{
                                int bal=10-db.getcount();
                                Toast.makeText(getContext(),"Please Select only  "+ bal +"  Contacts",Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                    setRecyclerView();
                }
                break;
            default:
                super.onActivityResult(requestCode,resultCode,data);
        }
    }
    private void setRecyclerView() {
       // ContactAdapter adapter = new ContactAdapter(getContext(),mContacts, this, null, null);
        contactItems=db.fetchAllContact();
        ContactAdapter adapter = new ContactAdapter(getContext(), contactItems);

        listView.setAdapter(adapter);
        if(contactItems.size() > 0){
            mTextView.setText("MODIFY CONTACTS");
            mTextView.setVisibility(View.INVISIBLE);
        }else{
            mTextView.setVisibility(View.VISIBLE);
            mTextView.setText("CHOOSE CONTACTS");
        }
        //listView.setItemAnimator(new DefaultItemAnimator());
        //mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        //mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //toggleViews();
    }

    private void toggleViews() {

        mTextView.setText("Contacts selected: ");
    }


    @Override
    public void onContactSelected(Contact contact, String communication) {

    }

    @Override
    public void onContactDeselected(Contact contact, String communication) {

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case READ_CONTACT_REQUEST:
                if(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    launchContactPicker(getView());
                   // Intent contactPicker = new Intent(getContext(), ContactPickerActivity.class);
                    //startActivityForResult(contactPicker, CONTACT_PICKER_REQUEST);
                }
        }
    }
}
