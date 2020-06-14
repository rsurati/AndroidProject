package com.example.phrm;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import java.util.HashMap;
import java.util.Map;

public class MedicalDocumentDetails extends AppCompatActivity {

    private EditText doc_date,short_description;
    private Spinner medical_docs;
    private ImageView imageView;
    private Button image,edit,btn_delete;

    private String familymemberid,medicaldocumentid;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private static final int PICK_IMAGE = 100;
    Map<String, String> updateMap;
    Uri imageUri;
    String imageUrl,deleteurl;

    DocumentReference medicaldocument;

    String[] medicaldocuments = {
      "Document Type",
      "X-RAY",
      "CT SCAN",
      "MRI",
      "Prescription"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_document_details);

        Bundle extras = getIntent().getExtras();
        familymemberid = extras.getString("familymemberid");
        medicaldocumentid = extras.getString("document_key");

        doc_date = findViewById(R.id.doc_date1);
        medical_docs = findViewById(R.id.medical_documents1);
        short_description = findViewById(R.id.short_description1);
        edit = findViewById(R.id.btn_edit_medical_doc);
        imageView = findViewById(R.id.img_medical_doc1);
        image = findViewById(R.id.btn_medical_doc1);
        btn_delete = findViewById(R.id.btn_delete_medical_doc);

        ArrayAdapter<String> myAdapter = new ArrayAdapter<>(MedicalDocumentDetails.this,android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.medical_documents));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        medical_docs.setAdapter(myAdapter);

        image.setOnClickListener(new  View.OnClickListener(){
            @Override
            public  void onClick(View v){
                openGallery();
            }
        });

        medicaldocument = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("familyMembers").document(familymemberid)
                .collection("Medical Documents").document(medicaldocumentid);

        medicaldocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot doc = task.getResult();
                Medicalmodel document = doc.toObject(Medicalmodel.class);
                medical_docs.setSelection(getPos(document.getMedical_documents()));
                Picasso.get().load(document.getImageUrl()).into(imageView);
                deleteurl = document.getImageUrl();
                doc_date.setText(document.getDoc_date());
                short_description.setText(document.getShort_description());

            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            final Map<String, Object> map = new HashMap<>();
                                            map.put("doc_date",doc_date.getText().toString());
                                            map.put("medical_documents",medical_docs.getSelectedItem().toString());
                                            map.put("short_description",short_description.getText().toString());
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
                                                            medicaldocument.update(map)
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            Toast.makeText(MedicalDocumentDetails.this,"Data Updated Successfully",Toast.LENGTH_SHORT).show();
                                                                            Intent i =new Intent(MedicalDocumentDetails.this,MedicalDocumentList.class);
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

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                medicaldocument.delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                StorageReference imgref = FirebaseStorage.getInstance().getReference().getStorage().getReferenceFromUrl(deleteurl);
                                imgref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(MedicalDocumentDetails.this,"Data Deleted Successfully",Toast.LENGTH_SHORT).show();
                                    }
                                });
                                Intent i =new Intent(MedicalDocumentDetails.this,MedicalDocumentList.class);
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

    public int getPos(String str)
    {
        int i;
        for (i=0;i<=medicaldocuments.length;i++)
        {
            if(medicaldocuments[i].equals(str))
            {
                return i;
            }
        }
        return 0;
    }
}
