package com.example.phrm;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class FavoritesFragment extends Fragment {

    private ListView relation_list;
    private FirebaseAuth firebaseAuth;
    DocumentReference user;
    FirebaseFirestore db1;
    ArrayList<String> relationList_doc;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorites,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        relation_list = getView().findViewById(R.id.member_relation_list);
        relationList_doc =new ArrayList();

        db1 = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user= db1.collection("users").document(firebaseAuth.getCurrentUser().getUid());
        user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("tag", "DocumentSnapshot data: " + document.getData());
                        UserProfile u= document.toObject(UserProfile.class);
                        user.collection("familyMembers").orderBy("timestamp").get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                Log.d("tag", document.getId() + " => " + document.getData());
                                                final FamilyMember f = document.toObject(FamilyMember.class);
                                                //listName.add(f.getFirst_name()+" "+f.getLast_name());
                                                user.collection("familyMembers").document(document.getId())
                                                        .collection("PersonalInfo").get()
                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                if (task.isSuccessful()) {
                                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                                        Log.d("tag relation", document.getId() + " => " + document.getData());
                                                                        PersonalInfo p = document.toObject(PersonalInfo.class);
                                                                        relationList_doc.add(f.getFirst_name() + " " + f.getLast_name() + "        Relation : " + p.getMySpinner2());
                                                                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1,android.R.id.text1 ,relationList_doc);
                                                                        relation_list.setAdapter(arrayAdapter);
                                                                        Log.d("list Name Size",(relationList_doc.size()+""));
                                                                    }
                                                                }
                                                            }
                                                        });
                                            }
                                            if(!relationList_doc.isEmpty()){
//                                                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1,android.R.id.text1 ,relationList_doc);
//                                                relation_list.setAdapter(arrayAdapter);
                                            }


                                        } else {
                                            Log.d("tag", "Error getting documents: ", task.getException());
                                        }
                                    }
                                });

                    } else {
                        Log.d("tag", "No such document");
                    }
                } else {
                    Log.d("tag", "get failed with ", task.getException());
                }
            }
        });
    }
}
