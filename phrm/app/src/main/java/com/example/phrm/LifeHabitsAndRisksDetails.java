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

public class LifeHabitsAndRisksDetails extends AppCompatActivity {

    EditText description;
    TextView start_date_habit,end_date_habit;
    Button btn_edit_habit,btn_delete_habit;
    private Spinner habit_type;
    String familymemberid,habitid;
    private DatePickerDialog.OnDateSetListener mDateSetListener,mDateSetListener1;
    DocumentReference habitDocument;

    String[] habitType = {
            "Habits And Risks",
            "Tobacco",
            "Drug",
            "Alcohol",
            "Hypertension",
            "Diabetes",
            "Stress"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_life_habits_and_risks_details);

        Bundle extras = getIntent().getExtras();
        familymemberid = extras.getString("familymemberid");
        habitid = extras.getString("habit_key");

        habit_type = findViewById(R.id.habit_type1);
        start_date_habit = findViewById(R.id.start_date_habit1);
        end_date_habit = findViewById(R.id.end_date_habit1);
        description = findViewById(R.id.habit_description1);
        btn_edit_habit = findViewById(R.id.btn_habit_edit1);
        btn_delete_habit = findViewById(R.id.btn_habit_delete1);

        ArrayAdapter<String> myAdapter = new ArrayAdapter<>(LifeHabitsAndRisksDetails.this,android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.life_habits_and_risks));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        habit_type.setAdapter(myAdapter);

        start_date_habit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        LifeHabitsAndRisksDetails.this,
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
                start_date_habit.setText(date);
            }
        };

        end_date_habit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        LifeHabitsAndRisksDetails.this,
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
                end_date_habit.setText(date);
            }
        };

        habitDocument = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("familyMembers").document(familymemberid)
                .collection("Life Habits And Risks").document(habitid);

        habitDocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot doc = task.getResult();
                LifeHabitAndRiskModel document = doc.toObject(LifeHabitAndRiskModel.class);
                habit_type.setSelection(getPos(document.getHabit_type(),habitType));
                start_date_habit.setText(document.getStart_date());
                end_date_habit.setText(document.getEnd_date());
                description.setText(document.getDescription());
            }
        });

        btn_edit_habit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Map<String, Object> map = new HashMap<>();
                map.put("habit_type",habit_type.getSelectedItem().toString());
                map.put("start_date",start_date_habit.getText().toString());
                map.put("end_date",end_date_habit.getText().toString());
                map.put("description",description.getText().toString());

                habitDocument.update(map)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(LifeHabitsAndRisksDetails.this,"Data Updated Successfully",Toast.LENGTH_SHORT).show();
                                Intent i =new Intent(LifeHabitsAndRisksDetails.this,LifeHabitsAndRisksList.class);
                                i.putExtra("key",familymemberid);
                                startActivity(i);
                            }
                        });

            }
        });

        btn_delete_habit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                habitDocument.delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(LifeHabitsAndRisksDetails.this,"Data Deleted Successfully",Toast.LENGTH_SHORT).show();
                                Intent i =new Intent(LifeHabitsAndRisksDetails.this,LifeHabitsAndRisksList.class);
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
