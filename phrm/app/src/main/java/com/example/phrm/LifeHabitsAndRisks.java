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

public class LifeHabitsAndRisks extends AppCompatActivity {

    EditText description;
    TextView start_date_habit,end_date_habit;
    Button btn_save_habit;
    private Spinner habit_type;
    String familymemberid;
    private DatePickerDialog.OnDateSetListener mDateSetListener,mDateSetListener1;
    String stringdate;
    DocumentReference f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_life_habits_and_risks);

        Bundle extras = getIntent().getExtras();
        familymemberid = extras.getString("key");

        Date date = new Date();
        final Date newDate = new Date(date.getTime());
        SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yyyy");
        stringdate = dt.format(newDate);

        habit_type = findViewById(R.id.habit_type);
        start_date_habit = findViewById(R.id.start_date_habit);
        end_date_habit = findViewById(R.id.end_date_habit);
        description = findViewById(R.id.habit_description);
        btn_save_habit = findViewById(R.id.btn_habit_save);

        ArrayAdapter<String> myAdapter = new ArrayAdapter<>(LifeHabitsAndRisks.this,android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.life_habits_and_risks));
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
                        LifeHabitsAndRisks.this,
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
                        LifeHabitsAndRisks.this,
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

        f = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("familyMembers").document(familymemberid);

        btn_save_habit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LifeHabitAndRiskModel mymodel = new LifeHabitAndRiskModel(habit_type.getSelectedItem().toString(),start_date_habit.getText().toString(),end_date_habit.getText().toString(),description.getText().toString(),stringdate);
                CollectionReference drug_details_collections = f.collection("Life Habits And Risks");
                drug_details_collections.add(mymodel);
                Toast.makeText(LifeHabitsAndRisks.this,"Data Uploaded Successfully", Toast.LENGTH_SHORT).show();

                Intent i =new Intent(LifeHabitsAndRisks.this,LifeHabitsAndRisksList.class);
                i.putExtra("key",familymemberid);
                startActivity(i);

            }
        });
    }
}

class  LifeHabitAndRiskModel
{
    private String habit_type,start_date,end_date,description;
    String timestamp;

    public LifeHabitAndRiskModel(String habit_type, String start_date, String end_date, String description, String timestamp) {
        this.habit_type = habit_type;
        this.start_date = start_date;
        this.end_date = end_date;
        this.description = description;
        this.timestamp = timestamp;
    }

    public LifeHabitAndRiskModel() {
    }

    public String getHabit_type() {
        return habit_type;
    }

    public void setHabit_type(String habit_type) {
        this.habit_type = habit_type;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
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
