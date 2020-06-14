package com.example.phrm;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EmergencyContacts extends AppCompatActivity {

    EditText name,contact;
    private Spinner contact_type;
    Button save;
    private String familymemberid;
    DocumentReference c;
    String stringdate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contacts);

        Bundle extras = getIntent().getExtras();
        familymemberid = extras.getString("key");

        contact_type = findViewById(R.id.contact_type);
        name = findViewById(R.id.contact_name);
        contact = findViewById(R.id.contact_number);

        save = findViewById(R.id.btn_save_contact);
        Date date = new Date();
        final Date newDate = new Date(date.getTime());
        SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yyyy");
        stringdate = dt.format(newDate);

        ArrayAdapter<String> myAdapter = new ArrayAdapter<>(EmergencyContacts.this,android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.contact_type));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        contact_type.setAdapter(myAdapter);

        c = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("familyMembers").document(familymemberid);


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EmergencyContactModel emergencyContactModel = new EmergencyContactModel(contact_type.getSelectedItem().toString(),name.getText().toString(),contact.getText().toString(),stringdate);
                CollectionReference emergencyContacts = c.collection("EmergencyContacts");
                emergencyContacts.add(emergencyContactModel);
                Toast.makeText(EmergencyContacts.this,"Data Uploaded Successfully",Toast.LENGTH_SHORT).show();
                Intent i =new Intent(EmergencyContacts.this,EmergencyContactsList.class);
                i.putExtra("key",familymemberid);
                startActivity(i);

            }
        });
    }
}

class EmergencyContactModel
{
    String contact_type;
    String name;
    String contact;
    private String timestamp;

    public EmergencyContactModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getContact_type() {
        return contact_type;
    }

    public void setContact_type(String contact_type) {
        this.contact_type = contact_type;
    }

    public EmergencyContactModel(String contact_type, String name, String contact, String timestamp) {
        this.contact_type = contact_type;
        this.name = name;
        this.contact = contact;
        this.timestamp = timestamp;
    }
}