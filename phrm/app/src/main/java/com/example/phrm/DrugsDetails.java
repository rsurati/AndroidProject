package com.example.phrm;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class DrugsDetails extends AppCompatActivity {

    EditText drug_name,prescriber,dosing,reason;
    TextView start_date_drug;
    Button edit,delete;
    String familymemberid,drugid;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    DocumentReference drugDocument;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drugs_details);

        Bundle extras = getIntent().getExtras();
        familymemberid = extras.getString("familymemberid");
        drugid = extras.getString("drug_key");

        drug_name = findViewById(R.id.drug_name1);
        prescriber = findViewById(R.id.prescriber1);
        start_date_drug = findViewById(R.id.start_date_drug1);
        dosing = findViewById(R.id.dosing1);
        reason = findViewById(R.id.reason1);
        edit = findViewById(R.id.btn_drug_edit1);
        delete = findViewById(R.id.btn_drug_delete1);

        start_date_drug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        DrugsDetails.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });



        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                // Log.d(TAG, "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year);

                String date = month + "/" + day + "/" + year;
                start_date_drug.setText(date);
            }
        };

        drugDocument = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("familyMembers").document(familymemberid)
                .collection("Drug Details").document(drugid);

        drugDocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot doc = task.getResult();
                Drugmodel document = doc.toObject(Drugmodel.class);
                drug_name.setText(document.getDrug_name());
                start_date_drug.setText(document.getStart_date_drug());
                prescriber.setText(document.getPrescriber());
                reason.setText(document.getReason());
                dosing.setText(document.getDosing());
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Map<String, Object> map = new HashMap<>();
                map.put("drug_name",drug_name.getText().toString());
                map.put("start_date_drug",start_date_drug.getText().toString());
                map.put("prescriber",prescriber.getText().toString());
                map.put("dosing",dosing.getText().toString());
                map.put("reason",reason.getText().toString());

                drugDocument.update(map)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(DrugsDetails.this,"Data Updated Successfully",Toast.LENGTH_SHORT).show();
                                Intent i =new Intent(DrugsDetails.this,DrugsList.class);
                                i.putExtra("key",familymemberid);
                                startActivity(i);
                            }
                        });

            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drugDocument.delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(DrugsDetails.this,"Data Deleted Successfully",Toast.LENGTH_SHORT).show();
                                Intent i =new Intent(DrugsDetails.this,DrugsList.class);
                                i.putExtra("key",familymemberid);
                                startActivity(i);
                            }
                        });
            }
        });
    }
}
