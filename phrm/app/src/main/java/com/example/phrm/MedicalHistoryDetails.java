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

public class MedicalHistoryDetails extends AppCompatActivity {

    private TextView startDate,endDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener,mDateSetListener1;
    Spinner medical_history_type;
    private String familymemberid,medicalhistoryid;
    EditText description;
    Button edit,delete;
    DocumentReference medicalHistory;

    String[] medicalHistoryType = {
            "Medical History Type",
            "Hereditary Medical History",
            "Surgical History",
            "Work Accidents"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_history_details);

        Bundle extras = getIntent().getExtras();
        familymemberid = extras.getString("familymemberid");
        medicalhistoryid = extras.getString("medicalHistory_key");

        startDate = findViewById(R.id.medical_history_start1);
        endDate = findViewById(R.id.medical_history_end1);
        medical_history_type = findViewById(R.id.medical_history_type1);
        description = findViewById(R.id.medical_history_description1);
        edit = findViewById(R.id.btn_save_medical_history1);
        delete = findViewById(R.id.btn_delete_medical_history1);

        ArrayAdapter<String> myAdapter = new ArrayAdapter<>(MedicalHistoryDetails.this,android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.medical_history_type));
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
                        MedicalHistoryDetails.this,
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
                        MedicalHistoryDetails.this,
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

        medicalHistory = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("familyMembers").document(familymemberid)
                .collection("MedicalHistory").document(medicalhistoryid);

        medicalHistory.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot doc = task.getResult();
                MedicalHistoryModel document = doc.toObject(MedicalHistoryModel.class);
                medical_history_type.setSelection(getPos(document.getMedicalHistoryType(),medicalHistoryType));
                startDate.setText(document.getStartDate());
                endDate.setText(document.getEndDate());
                description.setText(document.getDescription());
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Map<String, Object> map = new HashMap<>();
                map.put("medicalHistoryType",medical_history_type.getSelectedItem().toString());
                map.put("startDate",startDate.getText().toString());
                map.put("endDate",endDate.getText().toString());
                map.put("description",description.getText().toString());

                medicalHistory.update(map)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(MedicalHistoryDetails.this,"Data Updated Successfully",Toast.LENGTH_SHORT).show();
                                Intent i =new Intent(MedicalHistoryDetails.this,MedicalHistoryList.class);
                                i.putExtra("key",familymemberid);
                                startActivity(i);
                            }
                        });

            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                medicalHistory.delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(MedicalHistoryDetails.this,"Data Deleted Successfully",Toast.LENGTH_SHORT).show();
                                Intent i =new Intent(MedicalHistoryDetails.this,MedicalHistoryList.class);
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
