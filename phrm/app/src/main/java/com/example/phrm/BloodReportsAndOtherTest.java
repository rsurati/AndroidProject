package com.example.phrm;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BloodReportsAndOtherTest extends AppCompatActivity {

    private TextView test_date;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    Spinner test_type;
    private String familymemberid;
    EditText description;
    Button save,image;
    DocumentReference f;
    String stringdate;

    ImageView imageView;
    private static final int PICK_IMAGE = 100;
    Uri imageUri;
    String imageUrl;
    Map<String, String> updateMap;

    FirebaseStorage storage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_reports_and_other_test);

        Bundle extras = getIntent().getExtras();
        familymemberid = extras.getString("key");

        Date date = new Date();
        final Date newDate = new Date(date.getTime());
        SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yyyy");
        stringdate = dt.format(newDate);

        test_type = findViewById(R.id.test_type);
        test_date = findViewById(R.id.test_date);
        imageView = findViewById(R.id.img_test);
        image = findViewById(R.id.btn_img_test);
        description = findViewById(R.id.test_description);
        save = findViewById(R.id.btn_save_test);

        ArrayAdapter<String> myAdapter = new ArrayAdapter<>(BloodReportsAndOtherTest.this,android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.test_type));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        test_type.setAdapter(myAdapter);

        image.setOnClickListener(new  View.OnClickListener(){
            @Override
            public  void onClick(View v){
                openGallery();
            }
        });

        test_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        BloodReportsAndOtherTest.this,
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
                String date = day + "/" + month + "/" + year;
                test_date.setText(date);
            }
        };

        f = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("familyMembers").document(familymemberid);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadFile();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        imageUrl = updateMap.get("imageUrl");
                        BloodReportAndOtherTestModel md = new BloodReportAndOtherTestModel(test_type.getSelectedItem().toString(),imageUrl,test_date.getText().toString(),description.getText().toString(),stringdate);
                        CollectionReference mytest = f.collection("Blood Report & Other Tests");
                        mytest.add(md);
                    }
                }, 30000);

                Toast.makeText(BloodReportsAndOtherTest.this,"Data Uploaded Successfully",Toast.LENGTH_SHORT).show();
                Intent i =new Intent(BloodReportsAndOtherTest.this,BloodReportAndOtherTestList.class);
                i.putExtra("key",familymemberid);
                startActivity(i);
            }
        });


    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery,PICK_IMAGE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE && data != null && data.getData() != null ){
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imageView.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void uploadFile() {
        //progressDialog();
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance("gs://phrm-22e27.appspot.com/");
        StorageReference storageReference = firebaseStorage.getReference();
        final StorageReference riversRef = storageReference.child("Users/"+ familymemberid+"/images/"+imageUri.getLastPathSegment());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        byte[] data = baos.toByteArray();
        final UploadTask uploadTask = riversRef.putBytes(data);

        riversRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (!task.isSuccessful()) {
                                    Log.i("problem", task.getException().toString());
                                }

                                return riversRef.getDownloadUrl();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    Uri downloadUri = task.getResult();
                                    imageUrl = downloadUri.toString();
                                    Log.d("seeThisUri", downloadUri.toString());


                                    updateMap = new HashMap();
                                    updateMap.put("imageUrl",imageUrl);

                                    //final DocumentReference f = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("familyMembers").document(familymemberid);
                                    //f.collection("PersonalInfo").add(updateMap);

                                }else {
                                    Log.i("wentWrong","downloadUri failure");
                                }
                            }
                        });
                    }
                });
    }
}

class BloodReportAndOtherTestModel
{
    private String test_type,imageUrl,date,description;
    private String timestamp;

    public BloodReportAndOtherTestModel(String test_type, String imageUrl, String date, String description,String timestamp) {
        this.test_type = test_type;
        this.imageUrl = imageUrl;
        this.date = date;
        this.description = description;
        this.timestamp = timestamp;
    }

    public BloodReportAndOtherTestModel() {
    }

    public String getTest_type() {
        return test_type;
    }

    public void setTest_type(String test_type) {
        this.test_type = test_type;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

