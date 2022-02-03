package com.example.doggiz_app.PagesUi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.doggiz_app.Backend.MainActivity;
import com.example.doggiz_app.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

public class RemoveMedicalBag extends AppCompatActivity {

    private View medicCard;
    LinearLayout linearLayout;
    ImageView iconTre;
    TextView title, description;
    private static final String MED= "MedicalCard";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_medical_bag);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        FirebaseDatabase database;
        DatabaseReference medRef;
        StorageReference storageReference;
        String email = LogIn.email;

        database = FirebaseDatabase.getInstance();
        medRef = database.getReference(MED);


        medRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                linearLayout = findViewById(R.id.medicalRemoveBagLinear);
                linearLayout.removeAllViews();

                for(DataSnapshot ds : snapshot.getChildren()){
                    if(ds.child("ownerEmail").getValue().equals(email)) {
                        if(ds.child("dogName").getValue().equals(MyDog.dogInUse)){

                            medicCard = getLayoutInflater().inflate(R.layout.item_info, null);
                            linearLayout = findViewById(R.id.medicalRemoveBagLinear);

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

                            medicCard.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    AlertDialog dlg = new AlertDialog.Builder(RemoveMedicalBag.this)
                                            .setTitle("Delete Medical Bag")
                                            .setMessage("Are you sure you want to delete this Medical Bag")
                                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    ds.getRef().removeValue();
                                                    finish();
                                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                                    dialog.dismiss();
                                                }
                                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            })
                                            .create();
                                    dlg.show();
//                                        ds.getRef().removeValue();
//                                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
//                                    Toast.makeText(RemoveMedicalBag.this,"medical bag has been remove",Toast.LENGTH_SHORT).show();
                                    }
                            });
                            linearLayout.addView(medicCard);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}