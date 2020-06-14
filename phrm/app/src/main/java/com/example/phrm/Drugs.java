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

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Drugs extends AppCompatActivity {

    EditText drug_name,prescriber,dosing,reason;
    TextView start_date_drug;
    Button btn_save_drug;
    String familymemberid;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    DocumentReference f;
    String stringdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drugs);

        Bundle extras = getIntent().getExtras();
        familymemberid = extras.getString("key");

        drug_name = findViewById(R.id.drug_name);
        prescriber = findViewById(R.id.prescriber);
        start_date_drug = findViewById(R.id.start_date_drug);
        dosing = findViewById(R.id.dosing);
        reason = findViewById(R.id.reason);
        btn_save_drug = findViewById(R.id.btn_drug_save);

        Date date = new Date();
        final Date newDate = new Date(date.getTime());
        SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yyyy");
        stringdate = dt.format(newDate);

        start_date_drug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        Drugs.this,
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

        f = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("familyMembers").document(familymemberid);

        btn_save_drug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Drugmodel drugmodel = new Drugmodel(drug_name.getText().toString(),prescriber.getText().toString(),start_date_drug.getText().toString(),dosing.getText().toString(),reason.getText().toString(),stringdate);
                CollectionReference drug_details_collections = f.collection("Drug Details");
                drug_details_collections.add(drugmodel);
                Toast.makeText(Drugs.this,"Data Uploaded Successfully", Toast.LENGTH_SHORT).show();

                Intent i =new Intent(Drugs.this,DrugsList.class);
                i.putExtra("key",familymemberid);
                startActivity(i);

            }
        });
    }
}

class Drugmodel{

    private String drug_name,prescriber,start_date_drug,dosing,reason;
    String tm;

    public Drugmodel(String drug_name, String prescriber, String start_date_drug, String dosing, String reason, String tm) {
        this.drug_name = drug_name;
        this.prescriber = prescriber;
        this.start_date_drug = start_date_drug;
        this.dosing = dosing;
        this.reason = reason;
        this.tm = tm;
    }

    public Drugmodel() {
    }

    public String getDrug_name() {
        return drug_name;
    }

    public void setDrug_name(String drug_name) {
        this.drug_name = drug_name;
    }

    public String getPrescriber() {
        return prescriber;
    }

    public void setPrescriber(String prescriber) {
        this.prescriber = prescriber;
    }

    public String getStart_date_drug() {
        return start_date_drug;
    }

    public void setStart_date_drug(String start_date_drug) {
        this.start_date_drug = start_date_drug;
    }

    public String getDosing() {
        return dosing;
    }

    public void setDosing(String dosing) {
        this.dosing = dosing;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getTimestamp() {
        return tm;
    }

    public void setTimestamp(String tm) {
        this.tm = tm;
    }
}