package com.example.phrm;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class About extends AppCompatActivity {

    private ListView about_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        about_list = findViewById(R.id.about_list);

        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Rate Our App");
        arrayList.add("Version");
        arrayList.add("Our Website");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, arrayList);
        about_list.setAdapter(arrayAdapter);
        about_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String clickedItem=(String) about_list.getItemAtPosition(position);
                Toast.makeText( About.this,clickedItem,Toast.LENGTH_LONG).show();
            }
        });
    }
}
