package com.example.doggiz_app.PagesUi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doggiz_app.Models.User;
import com.example.doggiz_app.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class RegisterPart2 extends AppCompatActivity {

    private EditText mPhone, mAddress, mProfession, mInsta, mWork;
    private Button mRegisterBtn;
    private TextView mLoginBtn;
    private FirebaseAuth fAuth;


    private  static final int PICK_IMAGE = 1;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_part2);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        mPhone       = findViewById(R.id.editTextPhone);
        mAddress     = findViewById(R.id.editTextAddress);
        mProfession  = findViewById(R.id.editTextProfession);
        mInsta       = findViewById(R.id.editTextInsta);
        mWork        = findViewById(R.id.editTextWork);

        mRegisterBtn = findViewById(R.id.registerBtn);
        mLoginBtn    = findViewById(R.id.textOfLogin);

        fAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        databaseReference = FirebaseDatabase.getInstance().getReference("uploads");
//
//        if(fAuth.getCurrentUser() != null){
//            startActivity((new Intent(getApplicationContext(),MainActivity.class)));
//            finish();
//        }

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = mPhone.getText().toString().trim();
                String address = mAddress.getText().toString().trim();
                String profession = mProfession.getText().toString().trim();
                String instegram = mInsta.getText().toString().trim();
                String work      = mWork.getText().toString().trim();



                if(!TextUtils.isDigitsOnly(phone)){
                    mPhone.setError("Only Digits");
                    mPhone.requestFocus();
                    return;
                }


                //register the user in Firebase

                fAuth.createUserWithEmailAndPassword(Register.email,Register.password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            User user = new User(Register.userName, Register.email, phone, work, profession, address, instegram);
                            user.setImageName(Register.photoName);
                            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(RegisterPart2.this, "User Created!", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(getApplicationContext(),LogIn.class));
                                    }else{
                                        Toast.makeText(RegisterPart2.this, "Error ! " + task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }else{
                            Toast.makeText(RegisterPart2.this, "Error ! " + task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterPart2.this,LogIn.class));
            }
        });
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