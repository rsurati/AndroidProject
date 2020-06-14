package com.example.phrm;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class Help extends AppCompatActivity {

    private ListView help_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        help_list = findViewById(R.id.help_list);

        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("FAQ");
        arrayList.add("Tutorials");
        arrayList.add("Email Us at phrm.project@gmail.com");
        arrayList.add("User Guide");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, arrayList);
        help_list.setAdapter(arrayAdapter);
        help_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String clickedItem=(String) help_list.getItemAtPosition(position);
                Toast.makeText( Help.this,clickedItem,Toast.LENGTH_LONG).show();
            }
        });
    }
}
