package com.example.doggiz_app.Models;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BDMedical {

    private DatabaseReference databaseReference;

    public BDMedical(){

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference(MedicalCard.class.getSimpleName());
    }
    public Task<Void> add(MedicalCard medicalCard){
        return databaseReference.push().setValue(medicalCard);
    }

}
