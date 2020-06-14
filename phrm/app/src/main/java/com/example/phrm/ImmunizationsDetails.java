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

public class ImmunizationsDetails extends AppCompatActivity {

    Button edit,delete;
    TextView immunizationDate;
    EditText vaccine_name,target_disease,administration_type,body_site,lot_number,exp_date;
    private DatePickerDialog.OnDateSetListener mDateSetListener,mDateSetListener1;
    String familymemberid,immunizationid;

    DocumentReference immunizationDocument;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_immunizations_details);

        Bundle extras = getIntent().getExtras();
        familymemberid = extras.getString("familymemberid");
        immunizationid = extras.getString("immunization_key");

        immunizationDate = findViewById(R.id.immunization_date1);
        vaccine_name = findViewById(R.id.vaccine_name1);
        target_disease = findViewById(R.id.target_disease1);
        administration_type = findViewById(R.id.administration_type1);
        body_site = findViewById(R.id.body_site1);
        lot_number = findViewById(R.id.lot_number1);
        exp_date = findViewById(R.id.exp_date1);
        edit = findViewById(R.id.btn_edit_immunizations1);
        delete = findViewById(R.id.btn_delete_immunizations1);

        immunizationDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        ImmunizationsDetails.this,
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
                immunizationDate.setText(date);
            }
        };

        exp_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        ImmunizationsDetails.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener1,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener1 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                // Log.d(TAG, "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year);

                String date = month + "/" + day + "/" + year;
                exp_date.setText(date);
            }
        };

        immunizationDocument = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("familyMembers").document(familymemberid)
                .collection("Immunizations").document(immunizationid);

        immunizationDocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot doc = task.getResult();
                ImmunizationModel document = doc.toObject(ImmunizationModel.class);
                vaccine_name.setText(document.getVaccine_name());
                immunizationDate.setText(document.getImmunizationDate());
                target_disease.setText(document.getTarget_disease());
                administration_type.setText(document.getAdministration_type());
                body_site.setText(document.getBody_site());
                lot_number.setText(document.getLot_number());
                exp_date.setText(document.getExp_date());
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Map<String, Object> map = new HashMap<>();
                map.put("vaccine_name",vaccine_name.getText().toString());
                map.put("immunizationDate",immunizationDate.getText().toString());
                map.put("target_disease",target_disease.getText().toString());
                map.put("administration_type",administration_type.getText().toString());
                map.put("body_site",body_site.getText().toString());
                map.put("exp_date",exp_date.getText().toString());

                immunizationDocument.update(map)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(ImmunizationsDetails.this,"Data Updated Successfully",Toast.LENGTH_SHORT).show();
                                Intent i =new Intent(ImmunizationsDetails.this,ImmunizationsList.class);
                                i.putExtra("key",familymemberid);
                                startActivity(i);
                            }
                        });

            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                immunizationDocument.delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(ImmunizationsDetails.this,"Data Deleted Successfully",Toast.LENGTH_SHORT).show();
                                Intent i =new Intent(ImmunizationsDetails.this,ImmunizationsList.class);
                                i.putExtra("key",familymemberid);
                                startActivity(i);
                            }
                        });
            }
        });

    }
}
