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

public class InsuranceDetails extends AppCompatActivity {

    private String familymemberid,insuranceid;
    private DatePickerDialog.OnDateSetListener mDateSetListener,mDateSetListener2;

    EditText policy_no,company_name,policy_amt,premium_amt,short_description;
    TextView start_date,end_date;

    Button edit_ins_details,delete_ins_details;

    DocumentReference insuranceDocument;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insurance_details2);

        Bundle extras = getIntent().getExtras();
        familymemberid = extras.getString("familymemberid");
        insuranceid = extras.getString("insurance_key");

        policy_no = findViewById(R.id.policy_no1);
        company_name = findViewById(R.id.company_name1);
        policy_amt = findViewById(R.id.policy_amount1);
        premium_amt = findViewById(R.id.premium_amount1);
        short_description = findViewById(R.id.short_description1);
        start_date = findViewById(R.id.start_date1);
        end_date = findViewById(R.id.end_date1);
        edit_ins_details = findViewById(R.id.btn_edit_insurance1);
        delete_ins_details = findViewById(R.id.btn_delete_insurance1);

        start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        InsuranceDetails.this,
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
                start_date.setText(date);
            }
        };


        end_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        InsuranceDetails.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener2,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });



        mDateSetListener2 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                // Log.d(TAG, "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year);

                String date = month + "/" + day + "/" + year;
                end_date.setText(date);
            }
        };

        insuranceDocument = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("familyMembers").document(familymemberid)
                .collection("InsuranceDetails").document(insuranceid);

        insuranceDocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot doc = task.getResult();
                Insurancemodel document = doc.toObject(Insurancemodel.class);
                policy_no.setText(document.getPolicy_no());
                company_name.setText(document.getComapany_name());
                start_date.setText(document.getIns_start_date());
                end_date.setText(document.getIns_expiry_date());
                policy_amt.setText(document.getPolicy_amount());
                premium_amt.setText(document.getPremium_amount());
                short_description.setText(document.getShort_description());
            }
        });

        edit_ins_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Map<String, Object> map = new HashMap<>();
                map.put("comapany_name",company_name.getText().toString());
                map.put("policy_no",policy_no.getText().toString());
                map.put("ins_start_date",start_date.getText().toString());
                map.put("ins_expiry_date",end_date.getText().toString());
                map.put("policy_amount",policy_amt.getText().toString());
                map.put("premium_amount",premium_amt.getText().toString());
                map.put("short_description",end_date.getText().toString());

                insuranceDocument.update(map)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(InsuranceDetails.this,"Data Updated Successfully",Toast.LENGTH_SHORT).show();
                                Intent i =new Intent(InsuranceDetails.this,InsuranceList.class);
                                i.putExtra("key",familymemberid);
                                startActivity(i);
                            }
                        });

            }
        });

        delete_ins_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insuranceDocument.delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(InsuranceDetails.this,"Data Deleted Successfully",Toast.LENGTH_SHORT).show();
                                Intent i =new Intent(InsuranceDetails.this,InsuranceList.class);
                                i.putExtra("key",familymemberid);
                                startActivity(i);
                            }
                        });
            }
        });

    }
}
