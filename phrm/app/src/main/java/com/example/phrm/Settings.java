package com.example.phrm;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class Settings extends AppCompatActivity {

    private ListView setting_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setting_list = findViewById(R.id.setting_list);

        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Language");
        arrayList.add("Storage");
        arrayList.add("Users Control");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, arrayList);
        setting_list.setAdapter(arrayAdapter);
        setting_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String clickedItem=(String) setting_list.getItemAtPosition(position);
                Toast.makeText( Settings.this,clickedItem,Toast.LENGTH_LONG).show();
            }
        });
    }
}
