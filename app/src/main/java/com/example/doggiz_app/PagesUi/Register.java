package com.example.doggiz_app.PagesUi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doggiz_app.Models.Upload;
import com.example.doggiz_app.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;


public class Register extends AppCompatActivity {

    public static EditText     mFullName, mEmail, mPassword;
    private       Button       continueBtn;
    private       TextView     mLoginBtn;
    private       FirebaseAuth fAuth;
    private       ImageView    ivUser;


    private  static final int PICK_IMAGE = 1;
    private StorageReference  storageReference;
    private DatabaseReference databaseReference;
    private Uri               mImageUri;
    private StorageTask       uploadTask;
    public  static String     email,password,userName, photoName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        mFullName    = findViewById(R.id.editTextFullName);
        mEmail       = findViewById(R.id.editTextEmail);
        mPassword    = findViewById(R.id.regEditTextPassword);
        continueBtn  = findViewById(R.id.continueBtn);
        mLoginBtn    = findViewById(R.id.textOfLogin);
        ivUser       = findViewById(R.id.profileImage);

        fAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        databaseReference = FirebaseDatabase.getInstance().getReference("uploads");

//        if(fAuth.getCurrentUser() != null){
//            startActivity((new Intent(getApplicationContext(),MainActivity.class)));
//            finish();
//        }

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email    = mEmail.getText().toString().trim();
                password = mPassword.getText().toString().trim();
                userName = mFullName.getText().toString().trim();


                if(TextUtils.isEmpty(userName)){
                    mFullName.setError("Please insert your name");
                    mFullName.requestFocus();
                    return;
                }

                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Email is Required!");
                    mEmail.requestFocus();
                    return;
                }

                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    mEmail.setError("Please provide valid email!");
                    mEmail.requestFocus();
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    mPassword.setError("Password is Required!");
                    mPassword.requestFocus();
                    return;
                }

                if(password.length() < 6){
                    mPassword.setError("Password Must be more then 6 Characters");
                    mPassword.requestFocus();
                    return;
                }

                if(uploadTask != null && uploadTask.isInProgress()){
                    Toast.makeText(Register.this, "already Uploaded ", Toast.LENGTH_SHORT).show();
                }else
                    photoName = uploadFile();

                startActivity(new Intent(Register.this,RegisterPart2.class));


                //register the user in Firebase

//                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if(task.isSuccessful()){
//                            User user = new User(userName, email, phone, work, profession, address, instegram);
//                            user.setImageName(photoName);
//                            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if(task.isSuccessful()){
//                                        Toast.makeText(Register.this, "User Created!", Toast.LENGTH_LONG).show();
//                                        progressBar.setVisibility(View.GONE);
//                                        startActivity(new Intent(getApplicationContext(),LogIn.class));
//                                    }else{
//                                        Toast.makeText(Register.this, "Error ! " + task.getException().getMessage(),Toast.LENGTH_SHORT).show();
//                                        progressBar.setVisibility(View.GONE);
//                                    }
//                                }
//                            });
//
//
//                        }else{
//                            Toast.makeText(Register.this, "Error ! " + task.getException().getMessage(),Toast.LENGTH_SHORT).show();
//                            progressBar.setVisibility(View.GONE);
//                        }
//                    }
//                });
            }
        });

        ivUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this,LogIn.class));
            }
        });

//        mPhoto.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openFileChooser();
//
//
//            }
//        });

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

    private String uploadFile(){
        if(mImageUri != null){
            String imageName = System.currentTimeMillis()+"."+getFileExtension(mImageUri);
            StorageReference fileRefrence = storageReference.child(imageName);

            uploadTask = fileRefrence.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(Register.this, "upload finish",Toast.LENGTH_SHORT).show();
                    Upload upload = new Upload("profile_" + mEmail.getText().toString().trim(), taskSnapshot.getMetadata().getPath());
                    String uploadId = databaseReference.push().getKey();
                    databaseReference.child(uploadId).setValue(upload);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Register.this, e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
            return imageName;
        }else{
            Toast.makeText(this, "You didn't choose photo", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE && data != null && data.getData() != null){

            mImageUri = data.getData();
            ivUser.setImageURI(mImageUri);
        }
    }

//    private void uploadImage(){
//        storageReference = FirebaseStorage.getInstance().getReference();
//
//        storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                Toast.makeText(PersonProfile.this, "All Good", Toast.LENGTH_SHORT).show();
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//
//            }
//        });
//    }
}