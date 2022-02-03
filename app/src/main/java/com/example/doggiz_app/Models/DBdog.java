package com.example.doggiz_app.Models;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DBdog {

    private DatabaseReference databaseReference;

    public DBdog(){

        FirebaseDatabase db =FirebaseDatabase.getInstance();
        databaseReference = db.getReference(Dog.class.getSimpleName());
    }
    public Task<Void> add(Dog dog){
        return databaseReference.push().setValue(dog);
    }
}
