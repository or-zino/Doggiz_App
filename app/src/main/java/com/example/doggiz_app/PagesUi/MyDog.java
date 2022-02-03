package com.example.doggiz_app.PagesUi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doggiz_app.Backend.MainActivity;
import com.example.doggiz_app.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class MyDog extends AppCompatActivity {

    private static final String DOG = "Dog";
    private static final String USERS = "Users";
    private static final String UPLOADS = "dogs/";
    public static String dogInUse;

    private ImageView imgDog[] = new ImageView[9];
    private ImageView imgDeleteDog[] = new ImageView[9];
    private LinearLayout linearLayout;
    private View view;
    private TextView dogName, owenerName, breed, vetName, dateOfBirth;
    private View[] views = new View[9];
    private FirebaseDatabase database;
    private DatabaseReference dogRef, userRef;
    private StorageReference storageReference;

    public String email;
    public String owner;
    public String imagesName[] = new String[9];
    private int  index = 0, index2 = 0,index3 = 0, counter = 0;
    private Bitmap bitmap[] = new Bitmap[9];
    private File imgFile[] = new File[9];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_dog);
        email = LogIn.email;

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        database    = FirebaseDatabase.getInstance();
        userRef     = database.getReference(USERS);
        dogRef      = database.getReference(DOG);

        showDogs();


    }

    private void showDogs(){

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (ds.child("email").getValue().equals(email)) {
                        owner = ds.child("fullName").getValue().toString();
                        dogRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds2 : snapshot.getChildren()) {
                                        if (ds2.child("userEmail").getValue().equals(email)) {

                                            counter++;
                                            linearLayout = findViewById(R.id.myDogsLayout);
                                            view = getLayoutInflater().inflate(R.layout.item_image, null);

                                            breed = view.findViewById(R.id.breedTextView);
                                            breed.setText(ds2.child("breed").getValue().toString());
                                            vetName = view.findViewById(R.id.vetTextView);
                                            vetName.setText(ds2.child("vet").getValue().toString());
                                            dateOfBirth = view.findViewById(R.id.dateTextView);
                                            dateOfBirth.setText(ds2.child("dateOfBirth").getValue().toString());
                                            dogName = view.findViewById(R.id.dogName);
                                            dogName.setText(ds2.child("dogName").getValue().toString());
                                            owenerName = view.findViewById(R.id.ownerName);
                                            owenerName.setText(owner);
                                            views[index] = view;
                                            index++;
                                            linearLayout.addView(view);

                                        }
                                    }


                                dogRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot snapshot) {
                                        index = 0;
                                        for (DataSnapshot ds2 : snapshot.getChildren()) {
                                            if (ds2.child("userEmail").getValue().equals(email)) {

                                                imgDog[index] = views[index].findViewById(R.id.dogImg);
                                                imgDeleteDog[index] = views[index].findViewById(R.id.deleteImage);
                                                imgDeleteDog[index].setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        //
                                                        AlertDialog dlg = new AlertDialog.Builder(MyDog.this)
                                                                .setTitle("Delete Dog")
                                                                .setMessage("Are you sure you want to delete this dog")
                                                                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        ds2.getRef().removeValue();
                                                                        finish();
                                                                        startActivity(new Intent(MyDog.this,MyDog.class));
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
                                                    }
                                                });
                                                imagesName[index] = ds2.child("imageName").getValue().toString();
                                                    storageReference = FirebaseStorage.getInstance().getReference().child(UPLOADS + imagesName[index]);
                                                    try {
                                                        imgFile[index] = File.createTempFile("profile", ".jpg");
                                                        storageReference.getFile(imgFile[index]).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                                                                try {
                                                                    Thread.sleep(500);
                                                                } catch (InterruptedException e) {
                                                                    e.printStackTrace();
                                                                }
                                                                bitmap[index2] = BitmapFactory.decodeFile(imgFile[index2].getPath());
                                                                imgDog[index2].setImageBitmap(bitmap[index2]);
                                                                views[index2].findViewById(R.id.dogName).setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {
                                                                        dogName = v.findViewById(R.id.dogName);
                                                                        dogInUse = dogName.getText().toString();
                                                                        startActivity(new Intent(MyDog.this, MainActivity.class));
                                                                    }
                                                                });
                                                                index2++;
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(MyDog.this, "213Error Occurred", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });


                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                    index++;

                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }

                                });


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }


            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }});




    }


    private void alert(){
        AlertDialog dlg = new AlertDialog.Builder(MyDog.this)
                .setTitle("Delete Dog")
                .setMessage("Delete this dog")
                .setPositiveButton("Approve", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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
    }
}