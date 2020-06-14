package com.example.phrm;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EmergencyContactsDetails extends AppCompatActivity {

    EditText name,contact;
    private Spinner contact_type;
    Button edit,delete;
    private String familymemberid,contactid;
    DocumentReference contactDocument;

    String[] contactType = {
            "Select Relative's/Doctor's Contact",
            "Relative",
            "Doctor"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contacts_details);

        Bundle extras = getIntent().getExtras();
        familymemberid = extras.getString("familymemberid");
        contactid = extras.getString("contact_key");

        contact_type = findViewById(R.id.contact_type1);
        name = findViewById(R.id.contact_name1);
        contact = findViewById(R.id.contact_number1);
        edit = findViewById(R.id.btn_edit_contact1);
        delete = findViewById(R.id.btn_delete_contact1);

        ArrayAdapter<String> myAdapter = new ArrayAdapter<>(EmergencyContactsDetails.this,android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.contact_type));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        contact_type.setAdapter(myAdapter);

        contactDocument = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("familyMembers").document(familymemberid)
                .collection("EmergencyContacts").document(contactid);

        contactDocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot doc = task.getResult();
                EmergencyContactModel document = doc.toObject(EmergencyContactModel.class);
                contact_type.setSelection(getPos(document.getContact_type(),contactType));
                name.setText(document.getName());
                contact.setText(document.getContact());
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Map<String, Object> map = new HashMap<>();
                map.put("contact_type",contact_type.getSelectedItem().toString());
                map.put("name",name.getText().toString());
                map.put("contact",contact.getText().toString());

                contactDocument.update(map)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(EmergencyContactsDetails.this,"Data Updated Successfully",Toast.LENGTH_SHORT).show();
                                Intent i =new Intent(EmergencyContactsDetails.this,EmergencyContactsList.class);
                                i.putExtra("key",familymemberid);
                                startActivity(i);
                            }
                        });

            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contactDocument.delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(EmergencyContactsDetails.this,"Data Deleted Successfully",Toast.LENGTH_SHORT).show();
                                Intent i =new Intent(EmergencyContactsDetails.this,EmergencyContactsList.class);
                                i.putExtra("key",familymemberid);
                                startActivity(i);
                            }
                        });
            }
        });
    }

    public int getPos(String str,String[] array)
    {
        int i;
        for (i=0;i<=array.length;i++)
        {
            if(array[i].equals(str))
            {
                return i;
            }
        }
        return 0;
    }
}
