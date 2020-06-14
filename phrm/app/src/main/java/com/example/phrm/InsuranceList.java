package com.example.phrm;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class InsuranceList extends AppCompatActivity {

    private ImageView add_insaurance;
    private String familymemberid;
    private ListView insaurancelist;
    FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    ArrayList<String> listItem;
    ArrayList<String> keys;
    DocumentReference insauranceDocument;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insurance_list);

        Bundle extras = getIntent().getExtras();
        familymemberid = extras.getString("key");

        add_insaurance = findViewById(R.id.add_insurance);
        insaurancelist = findViewById(R.id.listview_insaurance);

        add_insaurance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(InsuranceList.this, insurance_details.class);
                i.putExtra("key",familymemberid);
                startActivity(i);
            }
        });

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        listItem =new ArrayList();
        insauranceDocument = db.collection("users").document(firebaseAuth.getCurrentUser().getUid()).collection("familyMembers").document(familymemberid);
        keys=new ArrayList();

        insauranceDocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("tag", "DocumentSnapshot data: " + document.getData());
                        insauranceDocument.collection("InsuranceDetails").get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                Log.d("tag", document.getId() + " => " + document.getData());
                                                Insurancemodel a = document.toObject(Insurancemodel.class);
                                                keys.add(document.getId());

                                                listItem.add("\nCompany Name : " + a.getComapany_name() + "\nPolicy No : "+ a.getPolicy_no() +  "\nStart Date : " + a.getIns_start_date() + "\nEnd Date : " + a.getIns_expiry_date() + "\n") ;
                                                Log.d("list item Size",Integer.toString(listItem.size()));
                                            }
                                            if(!listItem.isEmpty()){
                                                final ArrayAdapter<String> adapter = new ArrayAdapter<>(InsuranceList.this, android.R.layout.simple_list_item_1,android.R.id.text1 ,listItem);
                                                insaurancelist.setAdapter(adapter);
                                                insaurancelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                    @Override
                                                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                                                        // TODO Auto-generated method stub
                                                        String insurance_key = keys.get(position);
                                                        Toast.makeText(InsuranceList.this,insurance_key,Toast.LENGTH_SHORT).show();
                                                        Intent i = new Intent(InsuranceList.this,InsuranceDetails.class);
                                                        i.putExtra("familymemberid",familymemberid);
                                                        i.putExtra("insurance_key",insurance_key);
                                                        startActivity(i);
                                                    }
                                                });
                                            }


                                        } else {
                                            Log.d("tag", "Error getting documents: ", task.getException());
                                        }
                                    }
                                });

                    } else {
                        Log.d("tag", "No such document");
                    }
                } else {
                    Log.d("tag", "get failed with ", task.getException());
                }
            }
        });
    }
}
