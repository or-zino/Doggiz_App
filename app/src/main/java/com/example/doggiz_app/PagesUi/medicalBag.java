package com.example.doggiz_app.PagesUi;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.doggiz_app.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

public class medicalBag extends Fragment {

    private View medicCard;
    LinearLayout linearLayout;
    ImageView addMed, iconTre, deleteMedical;
    TextView title, description;

    private static final String MED= "MedicalCard";



    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    public medicalBag() {
        // Required empty public constructor
    }

    public static medicalBag newInstance(String param1, String param2) {
        medicalBag fragment = new medicalBag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        FirebaseDatabase database;
        DatabaseReference medRef;
        StorageReference storageReference;
        String email = LogIn.email;


        database = FirebaseDatabase.getInstance();
        medRef = database.getReference(MED);

        View v        = inflater.inflate(R.layout.fragment_medical_bag, container, false);
        addMed        = v.findViewById(R.id.addMedicalImageView);
        deleteMedical = v.findViewById(R.id.removeMedicalImageView);

        medRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                linearLayout = v.findViewById(R.id.medicalBagLinear);
                linearLayout.removeAllViews();

                for(DataSnapshot ds : snapshot.getChildren()){
                    if(ds.child("ownerEmail").getValue().equals(email)) {
                        if(ds.child("dogName").getValue().equals(MyDog.dogInUse)){

                            medicCard = getLayoutInflater().inflate(R.layout.item_info, null);
                            linearLayout = v.findViewById(R.id.medicalBagLinear);

                            title = medicCard.findViewById(R.id.itemInfoTitle);
                            title.setText(ds.child("treatment").getValue().toString());

                            description = medicCard.findViewById(R.id.itemInfoDesc);
                            description.setText(ds.child("description").getValue().toString());

                            iconTre = medicCard.findViewById(R.id.itemInfoImage);
                            if(ds.child("treatment").getValue().equals("Vaccine")) {
                                iconTre.setBackgroundResource(R.drawable.medshotcolor);
                            }else
                            if(ds.child("treatment").getValue().equals("Medical pills")){
                                iconTre.setBackgroundResource(R.drawable.medpillscolor);
                            }else
                                if(ds.child("treatment").getValue().equals("Treatment")) {
                                    iconTre.setBackgroundResource(R.drawable.medtreatmentcolor);
                                }
                            linearLayout.addView(medicCard);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        addMed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),AddingMedicalBag.class);
                startActivity(intent);

            }
        });

        deleteMedical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),RemoveMedicalBag.class);
                startActivity(intent);
            }
        });








        return v;
    }
}