package com.example.doggiz_app.Models;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DBevent {

    private DatabaseReference databaseReference;

    public DBevent(){

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference(DateEvent.class.getSimpleName());
    }

    public Task<Void> add(DateEvent dateEvent){
        return databaseReference.push().setValue(dateEvent);
    }
}
