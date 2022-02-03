package com.example.doggiz_app.PagesUi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doggiz_app.Backend.MainActivity;
import com.example.doggiz_app.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

public class EditDogInfo extends AppCompatActivity {

    private static final String DOG = "Dog";
    private static final String USERS = "Users";
    private static final String UPLOADS = "dogs/";
    private static final String MEDICAL = "MedicalCard";
    private static final String EVENT = "DateEvent";

    private ImageView imDog, dateImage;
    private TextView dogName, breed, vetName, dateOfBirth;
    private FirebaseDatabase database;
    private DatabaseReference dogRef, editDog, medicalRef, eventRef;
    private StorageReference storageReference;
    private Button saveBtn;
    private DatePickerDialog.OnDateSetListener onDateSetListener;
    private  static final int PICK_IMAGE = 1;
    private static Uri mImageUri;
    private static StorageTask uploadTask;
    private static String photoDogName;

    public String email;
    public String imageName;
    private static String dogId;
    private static boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_dog_info);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        flag = true;

        email      = LogIn.email;
        database   = FirebaseDatabase.getInstance();
        dogRef     = database.getReference(DOG);
        medicalRef = database.getReference(MEDICAL);
        eventRef   = database.getReference(EVENT);
        editDog    = FirebaseDatabase.getInstance().getReference();

        dogName     = findViewById(R.id.editDogName);
        breed       = findViewById(R.id.editBreedText);
        vetName     = findViewById(R.id.editVetText);
        dateOfBirth = findViewById(R.id.editDogDateText);
        imDog       = findViewById(R.id.editDogImageView);
        saveBtn     = findViewById(R.id.editDogBtn);
        dateImage   = findViewById(R.id.editDateDog);



        dogRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        if (MyDog.dogInUse.equals(ds.child("dogName").getValue().toString())) {
                            dogName.setText(ds.child("dogName").getValue().toString());
                            imageName = ds.child("imageName").getValue(String.class);
                            breed.setText(ds.child("breed").getValue().toString());
                            vetName.setText(ds.child("vet").getValue().toString());
                            dateOfBirth.setText(ds.child("dateOfBirth").getValue().toString());
                            dogId = ds.getKey();

                            storageReference = FirebaseStorage.getInstance().getReference().child(UPLOADS + imageName);
                            try {
                                File imgFile = File.createTempFile("profile", ".jpg");
                                storageReference.getFile(imgFile).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                                        Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getPath());
                                        imDog.setImageBitmap(bitmap);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                    }
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        dateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(EditDogInfo.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        onDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String date = dayOfMonth +"/"+ (month+1) + "/" + year;
                dateOfBirth.setText(date);
            }
        };

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dogName.getText().toString().isEmpty() ||  breed.getText().toString().isEmpty() || vetName.getText().toString().isEmpty() || dateOfBirth.getText().toString().isEmpty()){
                    Toast.makeText(EditDogInfo.this, "fields are empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                String newName          = dogName.getText().toString();
                String newBreed         = breed.getText().toString();
                String newVet           = vetName.getText().toString();
                String newDateOfBirth   = dateOfBirth.getText().toString();

                editDog.child(DOG).child(dogId).child("dogName").setValue(newName);
                editDog.child(DOG).child(dogId).child("dateOfBirth").setValue(newDateOfBirth);
                editDog.child(DOG).child(dogId).child("breed").setValue(newBreed);
                editDog.child(DOG).child(dogId).child("vet").setValue(newVet);
                if(photoDogName != null) {
                    flag = false;
                    storageReference = FirebaseStorage.getInstance().getReference("dogs");
                    uploadFile();
                }
                eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            if (ds.child("ownerEmail").getValue().equals(email)) {
                                if(ds.child("dogName").getValue().equals(MyDog.dogInUse)){
                                    editDog.child(EVENT).child(ds.getKey()).child("dogName").setValue(newName);

                                }
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                medicalRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            if (ds.child("ownerEmail").getValue().equals(email)) {
                                if(ds.child("dogName").getValue().equals(MyDog.dogInUse)){
                                    editDog.child(MEDICAL).child(ds.getKey()).child("dogName").setValue(newName);

                                }
                            }
                        }
                        MyDog.dogInUse = newName;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                if(flag) {
                    Intent intent = new Intent(EditDogInfo.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });

        imDog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });


    }


    private void openFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE);
    }

    private String getFileExtension(Uri uri){
        ContentResolver cr =getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    private void uploadFile(){
        if(mImageUri != null){
            String imageName = photoDogName;
            StorageReference fileRefrence = storageReference.child(imageName);

            uploadTask = fileRefrence.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    editDog.child(DOG).child(dogId).child("imageName").setValue(photoDogName);
                    Toast.makeText(EditDogInfo.this, "upload finish", Toast.LENGTH_SHORT).show();
                    if(!flag){
                        Intent intent = new Intent(EditDogInfo.this, MainActivity.class);
                        startActivity(intent);
                    }
                }

            });
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode == PICK_IMAGE && data != null && data.getData() != null){
            mImageUri = data.getData();
            imDog.setImageURI(mImageUri);
            String[] temp = mImageUri.getPath().split("/",20);
            photoDogName = temp[temp.length-1]+ Timestamp.now().getSeconds();
        }
    }
}