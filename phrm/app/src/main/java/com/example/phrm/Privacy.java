package com.example.phrm;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Privacy extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);

        TextView textView;

        textView = findViewById(R.id.p_text);
        textView.setText("Divya Shah, Nihar Sodha and Ramya Surati built the PHRM app as a Free app. This SERVICE is provided by Divya Shah, Nihar Sodha and Ramya Surati at no cost and is intended for use as is.\n" +
                "This page is used to inform visitors regarding my policies with the collection, use, and disclosure of Personal Information if anyone decided to use my Service.\n" +
                "If you choose to use our Service, then you agree to the collection and use of information in relation to this policy. The Personal Information that we collect is used for providing and improving the Service. We will not use or share your information with anyone except as described in this Privacy Policy.\n" +
                "The terms used in this Privacy Policy have the same meanings as in our Terms and Conditions, which is accessible at PHRM unless otherwise defined in this Privacy Policy.");
    }
}
