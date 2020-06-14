package com.example.phrm;


import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class TimeLine extends Fragment {

    FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private String familymemberid;
    public ArrayList<TimeLineItem> timeLineItems = new ArrayList<TimeLineItem>();
    public ArrayList<String> t_title,t_date,t_name,t_description;

    ListView timeline_list;

    int counter = 0;



    DocumentReference allergies,appointments,bloodreport,bodymeasurement,drugs,emergency_contact,immunization,insurance,lifehabits,medicaldocument,medicalhistory,personalinfolist;
    public TimeLine() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_time_line, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        timeline_list = getView().findViewById(R.id.list_timeline);
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        t_title = new ArrayList();
        t_date = new ArrayList<>();
        t_name = new ArrayList<>();
        t_description = new ArrayList<>();

        Bundle extras =getActivity().getIntent().getExtras();
        familymemberid = extras.getString("key");

       fetchdata();
    }

    public void fetchdata(){
        allergies = db.collection("users").document(firebaseAuth.getCurrentUser().getUid()).collection("familyMembers").document(familymemberid);
        allergies.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("tag", "DocumentSnapshot data: " + document.getData());
                        allergies.collection("Allergies").get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                                    @RequiresApi(api = Build.VERSION_CODES.O)
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {

                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                Log.d("timeline", document.getId() + " => " + document.getData());
                                                AllergyModel a = document.toObject(AllergyModel.class);
                                                TimeLineItem timeLineItem= new TimeLineItem("Allergy",a.getPersonal_allergy(),a.getSymptoms(), a.getTimestamp());
                                                timeLineItems.add(timeLineItem);
                                                t_title.add(timeLineItems.get(counter).getTitle());
                                                t_date.add(timeLineItems.get(counter).getTimestamp());
                                                t_name.add(timeLineItems.get(counter).getSubtitle1());
                                                t_description.add(timeLineItems.get(counter).getSubtitle2());
                                                TimelineCustomAdapter timelineCustomAdapter  = new TimelineCustomAdapter();
                                                timeline_list.setAdapter(timelineCustomAdapter);
                                                Log.d("timeline",timeLineItems.get(0).getSubtitle1());
                                                Log.d("timeline size 1",timeLineItems.size()+"");
                                                counter++;
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

        appointments = db.collection("users").document(firebaseAuth.getCurrentUser().getUid()).collection("familyMembers").document(familymemberid);
        appointments.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
           @Override
           public void onComplete(@NonNull Task<DocumentSnapshot> task) {
               if (task.isSuccessful()) {
                   DocumentSnapshot document = task.getResult();
                   if (document.exists()) {
                       Log.d("tag", "DocumentSnapshot data: " + document.getData());
                       appointments.collection("Appointments&Visits").get()
                               .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                                   @Override
                                   public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                       if (task.isSuccessful()) {

                                           for (QueryDocumentSnapshot document : task.getResult()) {
                                               Log.d("tag", document.getId() + " => " + document.getData());
                                               AppointmentsAndVisits a = document.toObject(AppointmentsAndVisits.class);
                                               TimeLineItem timeLineItem= new TimeLineItem("Appointment",a.getDoctor_name(),a.getSubject_matter(),a.getTimestamp());
                                               timeLineItems.add(timeLineItem);
                                               t_title.add(timeLineItems.get(counter).getTitle());
                                               t_date.add(timeLineItems.get(counter).getTimestamp());
                                               t_name.add(timeLineItems.get(counter).getSubtitle1());
                                               t_description.add(timeLineItems.get(counter).getSubtitle2());
                                               TimelineCustomAdapter timelineCustomAdapter  = new TimelineCustomAdapter();
                                               timeline_list.setAdapter(timelineCustomAdapter);
                                               Log.d("timeline",timeLineItems.get(0).getSubtitle1());
                                               Log.d("timeline size 1",timeLineItems.size()+"");
                                               counter++;
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
        bloodreport = db.collection("users").document(firebaseAuth.getCurrentUser().getUid()).collection("familyMembers").document(familymemberid);
        bloodreport.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("tag", "DocumentSnapshot data: " + document.getData());
                        appointments.collection("Blood Report & Other Tests").get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {

                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                Log.d("tag", document.getId() + " => " + document.getData());
                                                BloodReportAndOtherTestModel b = document.toObject(BloodReportAndOtherTestModel.class);
                                                TimeLineItem timeLineItem= new TimeLineItem("Blood Report",b.getTest_type(),b.getDescription(),b.getTimestamp());
                                                timeLineItems.add(timeLineItem);
                                                t_title.add(timeLineItems.get(counter).getTitle());
                                                t_date.add(timeLineItems.get(counter).getTimestamp());
                                                t_name.add(timeLineItems.get(counter).getSubtitle1());
                                                t_description.add(timeLineItems.get(counter).getSubtitle2());
                                                TimelineCustomAdapter timelineCustomAdapter  = new TimelineCustomAdapter();
                                                timeline_list.setAdapter(timelineCustomAdapter);
                                                Log.d("timeline",timeLineItems.get(0).getSubtitle1());
                                                Log.d("timeline size 1",timeLineItems.size()+"");
                                                counter++;
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
        bodymeasurement = db.collection("users").document(firebaseAuth.getCurrentUser().getUid()).collection("familyMembers").document(familymemberid);
        bodymeasurement.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("tag", "DocumentSnapshot data: " + document.getData());
                        appointments.collection("BodyMeasurements").get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {

                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                Log.d("tag", document.getId() + " => " + document.getData());
                                                BodyMeasurementModel b = document.toObject(BodyMeasurementModel.class);
                                                TimeLineItem timeLineItem= new TimeLineItem("BodyMeasurements",b.getHeight(),b.getWeight(),b.getTimestamp());
                                                timeLineItems.add(timeLineItem);
                                                t_title.add(timeLineItems.get(counter).getTitle());
                                                t_date.add(timeLineItems.get(counter).getTimestamp());
                                                t_name.add(timeLineItems.get(counter).getSubtitle1());
                                                t_description.add(timeLineItems.get(counter).getSubtitle2());
                                                TimelineCustomAdapter timelineCustomAdapter  = new TimelineCustomAdapter();
                                                timeline_list.setAdapter(timelineCustomAdapter);
                                                Log.d("timeline",timeLineItems.get(0).getSubtitle1());
                                                Log.d("timeline size 1",timeLineItems.size()+"");
                                                counter++;
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
        drugs = db.collection("users").document(firebaseAuth.getCurrentUser().getUid()).collection("familyMembers").document(familymemberid);
        drugs.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("tag", "DocumentSnapshot data: " + document.getData());
                        appointments.collection("Drug Details").get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {

                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                Log.d("tag", document.getId() + " => " + document.getData());
                                                Drugmodel d = document.toObject(Drugmodel.class);
                                                TimeLineItem timeLineItem= new TimeLineItem("BodyMeasurements",d.getDrug_name(),d.getReason(),d.getTimestamp());
                                                timeLineItems.add(timeLineItem);
                                                t_title.add(timeLineItems.get(counter).getTitle());
                                                t_date.add(timeLineItems.get(counter).getTimestamp());
                                                t_name.add(timeLineItems.get(counter).getSubtitle1());
                                                t_description.add(timeLineItems.get(counter).getSubtitle2());
                                                TimelineCustomAdapter timelineCustomAdapter  = new TimelineCustomAdapter();
                                                timeline_list.setAdapter(timelineCustomAdapter);
                                                Log.d("timeline",timeLineItems.get(0).getSubtitle1());
                                                Log.d("timeline size 1",timeLineItems.size()+"");
                                                counter++;
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
        emergency_contact = db.collection("users").document(firebaseAuth.getCurrentUser().getUid()).collection("familyMembers").document(familymemberid);
        emergency_contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("tag", "DocumentSnapshot data: " + document.getData());
                        appointments.collection("EmergencyContacts").get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {

                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                Log.d("tag", document.getId() + " => " + document.getData());
                                                EmergencyContactModel e = document.toObject(EmergencyContactModel.class);
                                                TimeLineItem timeLineItem= new TimeLineItem("Emergency Contact",e.getName(),e.getContact_type(),e.getTimestamp());
                                                timeLineItems.add(timeLineItem);
                                                t_title.add(timeLineItems.get(counter).getTitle());
                                                t_date.add(timeLineItems.get(counter).getTimestamp());
                                                t_name.add(timeLineItems.get(counter).getSubtitle1());
                                                t_description.add(timeLineItems.get(counter).getSubtitle2());
                                                TimelineCustomAdapter timelineCustomAdapter  = new TimelineCustomAdapter();
                                                timeline_list.setAdapter(timelineCustomAdapter);
                                                Log.d("timeline",timeLineItems.get(0).getSubtitle1());
                                                Log.d("timeline size 1",timeLineItems.size()+"");
                                                counter++;
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
        immunization = db.collection("users").document(firebaseAuth.getCurrentUser().getUid()).collection("familyMembers").document(familymemberid);
        immunization.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("tag", "DocumentSnapshot data: " + document.getData());
                        appointments.collection("Immunizations").get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {

                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                Log.d("tag", document.getId() + " => " + document.getData());
                                                ImmunizationModel i = document.toObject(ImmunizationModel.class);
                                                TimeLineItem timeLineItem= new TimeLineItem("Immunization",i.getVaccine_name(),i.getTarget_disease(),i.getTimestamp());
                                                timeLineItems.add(timeLineItem);
                                                t_title.add(timeLineItems.get(counter).getTitle());
                                                t_date.add(timeLineItems.get(counter).getTimestamp());
                                                t_name.add(timeLineItems.get(counter).getSubtitle1());
                                                t_description.add(timeLineItems.get(counter).getSubtitle2());
                                                TimelineCustomAdapter timelineCustomAdapter  = new TimelineCustomAdapter();
                                                timeline_list.setAdapter(timelineCustomAdapter);
                                                Log.d("timeline",timeLineItems.get(0).getSubtitle1());
                                                Log.d("timeline size 1",timeLineItems.size()+"");
                                                counter++;
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
        insurance = db.collection("users").document(firebaseAuth.getCurrentUser().getUid()).collection("familyMembers").document(familymemberid);
        insurance.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("tag", "DocumentSnapshot data: " + document.getData());
                        appointments.collection("InsuranceDetails").get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {

                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                Log.d("tag", document.getId() + " => " + document.getData());
                                                Insurancemodel i = document.toObject(Insurancemodel.class);
                                                TimeLineItem timeLineItem= new TimeLineItem("Insurance",i.getComapany_name(),i.getShort_description(),i.getTimestamp());
                                                timeLineItems.add(timeLineItem);
                                                t_title.add(timeLineItems.get(counter).getTitle());
                                                t_date.add(timeLineItems.get(counter).getTimestamp());
                                                t_name.add(timeLineItems.get(counter).getSubtitle1());
                                                t_description.add(timeLineItems.get(counter).getSubtitle2());
                                                TimelineCustomAdapter timelineCustomAdapter  = new TimelineCustomAdapter();
                                                timeline_list.setAdapter(timelineCustomAdapter);
                                                Log.d("timeline",timeLineItems.get(0).getSubtitle1());
                                                Log.d("timeline size 1",timeLineItems.size()+"");
                                                counter++;
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
        lifehabits = db.collection("users").document(firebaseAuth.getCurrentUser().getUid()).collection("familyMembers").document(familymemberid);
        lifehabits.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("tag", "DocumentSnapshot data: " + document.getData());
                        appointments.collection("Life Habits And Risks").get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {

                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                Log.d("tag", document.getId() + " => " + document.getData());
                                                LifeHabitAndRiskModel l = document.toObject(LifeHabitAndRiskModel.class);
                                                TimeLineItem timeLineItem= new TimeLineItem("Life Habits And Risks",l.getHabit_type(),l.getDescription(),l.getTimestamp());
                                                timeLineItems.add(timeLineItem);
                                                t_title.add(timeLineItems.get(counter).getTitle());
                                                t_date.add(timeLineItems.get(counter).getTimestamp());
                                                t_name.add(timeLineItems.get(counter).getSubtitle1());
                                                t_description.add(timeLineItems.get(counter).getSubtitle2());
                                                TimelineCustomAdapter timelineCustomAdapter  = new TimelineCustomAdapter();
                                                timeline_list.setAdapter(timelineCustomAdapter);
                                                Log.d("timeline",timeLineItems.get(0).getSubtitle1());
                                                Log.d("timeline size 1",timeLineItems.size()+"");
                                                counter++;
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
        medicaldocument = db.collection("users").document(firebaseAuth.getCurrentUser().getUid()).collection("familyMembers").document(familymemberid);
        medicaldocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("tag", "DocumentSnapshot data: " + document.getData());
                        appointments.collection("Medical Documents").get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {

                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                Log.d("tag", document.getId() + " => " + document.getData());
                                                Medicalmodel m = document.toObject(Medicalmodel.class);
                                                TimeLineItem timeLineItem= new TimeLineItem("Medical Document",m.getMedical_documents(),m.getShort_description(),m.getTimestamp());
                                                timeLineItems.add(timeLineItem);
                                                t_title.add(timeLineItems.get(counter).getTitle());
                                                t_date.add(timeLineItems.get(counter).getTimestamp());
                                                t_name.add(timeLineItems.get(counter).getSubtitle1());
                                                t_description.add(timeLineItems.get(counter).getSubtitle2());
                                                TimelineCustomAdapter timelineCustomAdapter  = new TimelineCustomAdapter();
                                                timeline_list.setAdapter(timelineCustomAdapter);
                                                Log.d("timeline",timeLineItems.get(0).getSubtitle1());
                                                Log.d("timeline size 1",timeLineItems.size()+"");
                                                counter++;
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
        medicalhistory = db.collection("users").document(firebaseAuth.getCurrentUser().getUid()).collection("familyMembers").document(familymemberid);
        medicalhistory.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("tag", "DocumentSnapshot data: " + document.getData());
                        appointments.collection("MedicalHistory").get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {

                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                Log.d("tag", document.getId() + " => " + document.getData());
                                                MedicalHistoryModel m = document.toObject(MedicalHistoryModel.class);
                                                TimeLineItem timeLineItem= new TimeLineItem("Medical History",m.getMedicalHistoryType(),m.getDescription(),m.getTimestamp());
                                                timeLineItems.add(timeLineItem);
                                                t_title.add(timeLineItems.get(counter).getTitle());
                                                t_date.add(timeLineItems.get(counter).getTimestamp());
                                                t_name.add(timeLineItems.get(counter).getSubtitle1());
                                                t_description.add(timeLineItems.get(counter).getSubtitle2());
                                                TimelineCustomAdapter timelineCustomAdapter  = new TimelineCustomAdapter();
                                                timeline_list.setAdapter(timelineCustomAdapter);
                                                Log.d("timeline",timeLineItems.get(0).getSubtitle1());
                                                Log.d("timeline size 1",timeLineItems.size()+"");
                                                counter++;
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
        personalinfolist = db.collection("users").document(firebaseAuth.getCurrentUser().getUid()).collection("familyMembers").document(familymemberid);
        personalinfolist.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("tag", "DocumentSnapshot data: " + document.getData());
                        appointments.collection("PersonalInfo").get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {

                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                Log.d("tag", document.getId() + " => " + document.getData());
                                                PersonalInfo p = document.toObject(PersonalInfo.class);
                                                TimeLineItem timeLineItem= new TimeLineItem("PersonalInfo",p.getEmail(),p.getMobile(),p.getTimestamp());
                                                timeLineItems.add(timeLineItem);
                                                t_title.add(timeLineItems.get(counter).getTitle());
                                                t_date.add(timeLineItems.get(counter).getTimestamp());
                                                t_name.add(timeLineItems.get(counter).getSubtitle1());
                                                t_description.add(timeLineItems.get(counter).getSubtitle2());
                                                TimelineCustomAdapter timelineCustomAdapter  = new TimelineCustomAdapter();
                                                timeline_list.setAdapter(timelineCustomAdapter);
                                                Log.d("timeline",timeLineItems.get(0).getSubtitle1());
                                                Log.d("timeline size 1",timeLineItems.size()+"");
                                                counter++;
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

    class TimelineCustomAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return t_date.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = getLayoutInflater().inflate(R.layout.timeline_layout, null);

            TextView date = view.findViewById(R.id.t_date);
            TextView title = view.findViewById(R.id.t_title);
            TextView name = view.findViewById(R.id.t_name);
            TextView description = view.findViewById(R.id.t_description);

            date.setText(t_date.get(position));
            title.setText(t_title.get(position));
            name.setText(t_name.get(position));
            description.setText(t_description.get(position));
            Log.d("adapter custom",t_date.get(position));

//                date.setText(timeLineItems.get(position).getTimestamp());
//                title.setText(timeLineItems.get(position).getTitle());
//                name.setText(timeLineItems.get(position).getSubtitle1());
//                description.setText(timeLineItems.get(position).getSubtitle2());
//                Log.d("adapter custom",t_date.get(position));

//            date.setText("04 June");
//            title.setText("Divya");
//            name.setText("ABCDEFG");
//            description.setText("12316548");

            return view;
        }
    }
}
