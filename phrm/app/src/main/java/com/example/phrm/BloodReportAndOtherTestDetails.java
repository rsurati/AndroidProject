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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class BloodReportAndOtherTestDetails extends AppCompatActivity {

    private TextView test_date;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    Spinner test_type;
    private String familymemberid,testid;
    EditText description;
    private ImageView imageView;
    Button image,edit,delete;

    private static final int PICK_IMAGE = 100;
    Map<String, String> updateMap;
    Uri imageUri;
    String imageUrl,deleteurl;

    DocumentReference testDocument;

    String[] type = {
            "Test Type",
            "Blood Test",
            "Cholesterol Test",
            "Hemoglobin Test",
            "Uric Acid",
            "Alcohol Content"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_report_and_other_test_details);

        Bundle extras = getIntent().getExtras();
        familymemberid = extras.getString("familymemberid");
        testid = extras.getString("test_key");

        test_type = findViewById(R.id.test_type1);
        test_date = findViewById(R.id.test_date1);
        imageView = findViewById(R.id.img_test1);
        image = findViewById(R.id.btn_img_test1);
        description = findViewById(R.id.test_description1);
        edit = findViewById(R.id.btn_edit_test1);
        delete = findViewById(R.id.btn_delete_test1);

        ArrayAdapter<String> myAdapter = new ArrayAdapter<>(BloodReportAndOtherTestDetails.this,android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.test_type));
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
                        BloodReportAndOtherTestDetails.this,
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

        testDocument = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("familyMembers").document(familymemberid)
                .collection("Blood Report & Other Tests").document(testid);

        testDocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot doc = task.getResult();
                BloodReportAndOtherTestModel document = doc.toObject(BloodReportAndOtherTestModel.class);
                test_type.setSelection(getPos(document.getTest_type(),type));
                Picasso.get().load(document.getImageUrl()).into(imageView);
                deleteurl = document.getImageUrl();
                test_date.setText(document.getDate());
                description.setText(document.getDescription());

            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        final Map<String, Object> map = new HashMap<>();
                                        map.put("test_type",test_type.getSelectedItem().toString());
                                        map.put("date",test_date.getText().toString());
                                        map.put("description",description.getText().toString());
                                        StorageReference imgref = FirebaseStorage.getInstance().getReference().getStorage().getReferenceFromUrl(deleteurl);
                                        imgref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                uploadFile();
                                                Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    public void run() {
                                                        imageUrl = updateMap.get("imageUrl");
                                                        map.put("imageUrl",imageUrl);
                                                        testDocument.update(map)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        Toast.makeText(BloodReportAndOtherTestDetails.this,"Data Updated Successfully",Toast.LENGTH_SHORT).show();
                                                                        Intent i =new Intent(BloodReportAndOtherTestDetails.this,BloodReportAndOtherTestList.class);
                                                                        i.putExtra("key",familymemberid);
                                                                        startActivity(i);
                                                                    }
                                                                });
                                                    }
                                                }, 30000);

                                            }
                                        });

                                    }
                                }

        );

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testDocument.delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                StorageReference imgref = FirebaseStorage.getInstance().getReference().getStorage().getReferenceFromUrl(deleteurl);
                                imgref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(BloodReportAndOtherTestDetails.this,"Data Deleted Successfully",Toast.LENGTH_SHORT).show();
                                    }
                                });
                                Intent i =new Intent(BloodReportAndOtherTestDetails.this,BloodReportAndOtherTestList.class);
                                i.putExtra("key",familymemberid);
                                startActivity(i);
                            }
                        });
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
