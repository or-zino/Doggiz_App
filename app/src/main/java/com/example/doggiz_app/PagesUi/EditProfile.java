package com.example.doggiz_app.PagesUi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class EditProfile extends AppCompatActivity {

    private static final String USERS = "Users";
    private static final String UPLOADS = "uploads/";

    private ImageView imProfile;
    private TextView phone, address, instegram;
    private FirebaseDatabase database;
    private DatabaseReference userRef, editProfile;
    private StorageReference storageReference;
    private Button saveBtn;
    private  static final int PICK_IMAGE = 1;
    private static Uri mImageUri;
    private static StorageTask uploadTask;
    public String email;
    public static String  imageName;
    public String userId;
    private static boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        flag = true;

        email       = LogIn.email;
        database    = FirebaseDatabase.getInstance();
        userRef     = database.getReference(USERS);
        editProfile = FirebaseDatabase.getInstance().getReference();

        phone     = findViewById(R.id.editPhoneText);
        address   = findViewById(R.id.editAddressText);
        instegram = findViewById(R.id.editInstegramText);
        imProfile = findViewById(R.id.editProfileImageView);
        saveBtn   = findViewById(R.id.editProfileBtn);

        imProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (LogIn.email.equals(ds.child("email").getValue().toString())) {
                        phone.setText(ds.child("phone").getValue().toString());
                        imageName = ds.child("imageName").getValue(String.class);
                        address.setText(ds.child("address").getValue().toString());
                        instegram.setText(ds.child("instegram").getValue().toString());
                        userId = ds.getKey();

                        storageReference = FirebaseStorage.getInstance().getReference().child(UPLOADS + imageName);
                        try {
                            File imgFile = File.createTempFile("profile", ".jpg");
                            storageReference.getFile(imgFile).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                                    Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getPath());
                                    imProfile.setImageBitmap(bitmap);
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

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(phone.getText().toString().isEmpty() ||  address.getText().toString().isEmpty() || instegram.getText().toString().isEmpty()){
                    Toast.makeText(EditProfile.this, "fields are empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                String newPhone          = phone.getText().toString();
                String newAddress        = address.getText().toString();
                String newInst           = instegram.getText().toString();

                editProfile.child(USERS).child(userId).child("phone").setValue(newPhone);
                editProfile.child(USERS).child(userId).child("address").setValue(newAddress);
                editProfile.child(USERS).child(userId).child("instegram").setValue(newInst);
                Toast.makeText(EditProfile.this, "Profile Updated", Toast.LENGTH_SHORT).show();

                //if(imageName != null) {
                   // flag = false;
                    storageReference = FirebaseStorage.getInstance().getReference("uploads");
                    uploadFile();
               // }
//                if(flag) {
//                    Intent intent = new Intent(EditProfile.this,PersonProfile.class);
//                    startActivity(intent);
//                }
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
            StorageReference fileRefrence = storageReference.child(imageName);

            uploadTask = fileRefrence.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    editProfile.child(USERS).child(userId).child("imageName").setValue(imageName);
                    Toast.makeText(EditProfile.this, "upload finish", Toast.LENGTH_SHORT).show();
                    //if(!flag){
                        Intent intent = new Intent(EditProfile.this, PersonProfile.class);
                        startActivity(intent);
                    //}
                }

            });
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode == PICK_IMAGE && data != null && data.getData() != null){
            mImageUri = data.getData();
            imProfile.setImageURI(mImageUri);
            String[] temp = mImageUri.getPath().split("/",20);
            imageName = temp[temp.length-1]+ Timestamp.now().getSeconds();
        }
    }
}