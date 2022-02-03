package com.example.doggiz_app.PagesUi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doggiz_app.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;


public class PersonProfile extends AppCompatActivity {

    private static final String USER = "Users";
    private static final String UPLOADS = "uploads/";


    private Uri imageUri;
    private StorageReference storageReference;
    private TextView tvOccupation, tvName, tvWork, tvAddress;
    private TextView tvEmail, tvPhone, tvInstegram;
    private ImageView imUser, imLogOut;
    private String imageName;
    public  String email;
    private Button myDogBtn, addDogBtn, editProfileBtn;
    private FirebaseDatabase database;
    private DatabaseReference userRef, dogRef;
    private static final String DOG = "Dog";
    int counter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_profile);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        email = LogIn.email;

        tvName         = findViewById(R.id.nameTextView);
        tvEmail        = findViewById(R.id.emailTextview);
        tvPhone        = findViewById(R.id.phoneText);
        tvWork         = findViewById(R.id.workTextView);
        tvOccupation   = findViewById(R.id.occupationTextView);
        tvAddress      = findViewById(R.id.addressText);
        tvInstegram    = findViewById(R.id.instegramText);
        myDogBtn       = findViewById(R.id.myDogBtn);
        addDogBtn      = findViewById(R.id.AddNewDogBtn);
        imUser         = findViewById(R.id.personImageView);
        editProfileBtn = findViewById(R.id.editProfileBtn);
        imLogOut       = findViewById(R.id.logoutImage);

        database = FirebaseDatabase.getInstance();
        userRef = database.getReference(USER);
        dogRef  = database.getReference(DOG);

        String x = userRef.getKey();

        imLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PersonProfile.this,LogIn.class));
                Toast.makeText(PersonProfile.this, "Loged Out", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        });

        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PersonProfile.this,EditProfile.class));
            }
        });


        addDogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PersonProfile.this,AddingDog.class));
            }
        });

        myDogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PersonProfile.this,MyDog.class));
            }
        });

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()){
                    if(ds.child("email").getValue().equals(email)) {
                        tvName.setText(ds.child("fullName").getValue(String.class));
                        tvEmail.setText(ds.child("email").getValue(String.class));
                        tvPhone.setText(ds.child("phone").getValue(String.class));
                        tvWork.setText(ds.child("workingPlace").getValue(String.class));
                        tvOccupation.setText(ds.child("profession").getValue(String.class));
                        tvAddress.setText(ds.child("address").getValue(String.class));
                        tvInstegram.setText(ds.child("instegram").getValue(String.class));
                        imageName = ds.child("imageName").getValue(String.class);

                    }

                }
                storageReference = FirebaseStorage.getInstance().getReference().child(UPLOADS + imageName);
                try {
                File imgFile =File.createTempFile("profile", ".jpg");
                storageReference.getFile(imgFile).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                        Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getPath());
                        imUser.setImageBitmap(bitmap);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                        public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PersonProfile.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                    }
                });
                } catch (IOException e) {
                e.printStackTrace();
             }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void selectImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,100);
    }

    private void uploadImage(){
        storageReference = FirebaseStorage.getInstance().getReference();

        storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(PersonProfile.this, "All Good", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 100 && data != null && data.getData() != null){

            imageUri = data.getData();
            imUser.setImageURI(imageUri);
        }
    }



}