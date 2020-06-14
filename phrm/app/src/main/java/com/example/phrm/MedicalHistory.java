package com.example.phrm;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
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

public class MedicalHistory extends AppCompatActivity {

    private TextView startDate,endDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener,mDateSetListener1;
    Spinner medical_history_type;
    private String familymemberid;
    EditText description;
    Button save;
    DocumentReference f;
    String stringdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_history);

        Bundle extras = getIntent().getExtras();
        familymemberid = extras.getString("key");

        Date date = new Date();
        final Date newDate = new Date(date.getTime());
        SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yyyy");
        stringdate = dt.format(newDate);

        startDate = findViewById(R.id.medical_history_start);
        endDate = findViewById(R.id.medical_history_end);
        medical_history_type = findViewById(R.id.medical_history_type);
        description = findViewById(R.id.medical_history_description);
        save = findViewById(R.id.btn_save_medical_history);

        ArrayAdapter<String> myAdapter = new ArrayAdapter<>(MedicalHistory.this,android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.medical_history_type));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        medical_history_type.setAdapter(myAdapter);

        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        MedicalHistory.this,
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
                startDate.setText(date);
            }
        };

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        MedicalHistory.this,
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
                endDate.setText(date);
            }
        };

        f = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("familyMembers").document(familymemberid);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MedicalHistoryModel medicalHistoryModel = new MedicalHistoryModel(medical_history_type.getSelectedItem().toString(),startDate.getText().toString(),endDate.getText().toString(),description.getText().toString(),stringdate);
                CollectionReference medicalHistory = f.collection("MedicalHistory");
                medicalHistory.add(medicalHistoryModel);
                Toast.makeText(MedicalHistory.this,"Data Uploaded Successfully",Toast.LENGTH_SHORT).show();
                Intent i =new Intent(MedicalHistory.this,MedicalDocumentList.class);
                i.putExtra("key",familymemberid);
                startActivity(i);

            }
        });

    }
}

class  MedicalHistoryModel
{
    private String medicalHistoryType,startDate,endDate,description;
    private  String timestamp;

    public MedicalHistoryModel() {
    }

    public MedicalHistoryModel(String medicalHistoryType, String startDate, String endDate, String description, String timestamp) {
        this.medicalHistoryType = medicalHistoryType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
        this.timestamp = timestamp;
    }

    public String getMedicalHistoryType() {
        return medicalHistoryType;
    }

    public void setMedicalHistoryType(String medicalHistoryType) {
        this.medicalHistoryType = medicalHistoryType;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
