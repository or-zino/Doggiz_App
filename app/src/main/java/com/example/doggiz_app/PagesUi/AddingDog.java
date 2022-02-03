package com.example.doggiz_app.PagesUi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.doggiz_app.Models.DBdog;
import com.example.doggiz_app.Models.Dog;
import com.example.doggiz_app.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;

public class AddingDog extends AppCompatActivity {

    String[] breeds = {"Akita", "Alaskan Malamute", "American Stafford","shire terrier","American water spaniel","Australian cattle","Australian shepherd","Australian terrier","basenji","bassethound","beagle","bearded collie","Bedlington terrier","Bernesemountain","bichon frise","bloodhound,border collie","border terrier","borzoi","Boston terrier","bouvier desFlandres","boxer","briard","Brittany","Brussels griffon","bull terrier","bulldog","bullmastiff","cairn terrier","Canaan dog","Chesapeake Bay retriever","Chihuahua","Chinese crested","Chinese shar-pei","chow chow","Clumber spaniel","cocker spaniel","collie","curly-coated retriever","dachshund","Dalmatian","Dobermanpinscher","English cocker spaniel","Englishsetter","English springer spaniel","English toy spaniel","Eskimo dog","Finnishspitz","flat-coated retriever","fox terrier","foxhound","French bulldog","Germanshepherd","German shorthaired pointer","German wirehaired pointer","golden retriever","Gordon setter","Great Dane","greyhound","Irish setter","Irish waterspaniel","Irish wolfhound","Jack Russell terrier","Japanese spaniel","keeshond","Kerryblueterrier","komondor","kuvasz","Labrador retriever","Lakelandterrier","Lhasa apso","Maltese","Manchester terrier","mastiff","Mexicanhairless","Newfoundland","Norwegian elkhound","Norwich terrier","otterhound","papillon","Pekingese","pointer","Pomeranian","poodle","pug","puli","Rhodesinridgeback","Rottweiler","Saint Bernard","saluki","Samoyed","schipperke","schnauzer","Scottishdeerhound","Scottish terrier","Sealyham terrier","Shetland sheepdog","shih tzu","Siberian husky","silky terrier","Skyeterrier","Staffordshire bull terrier","soft-coated wheaten terrier","Sussexspaniel","spitz","Tibetan terrier","vizsla","Weimaraner","Welsh terrier","WestHighland white terrier" ,"whippet","Yorkshire terrier"};
    ArrayAdapter<String> adapterItems;
    private AutoCompleteTextView etBreed;
    private EditText etDogName, etVet, etDogDate;
    private Button addDogBtn;
    private FirebaseAuth fAuth;
    private ImageView ivDog, dateImage;
    private ProgressBar progressBar;
    private DatePickerDialog.OnDateSetListener onDateSetListener;

    private  static final int PICK_IMAGE = 1;
    private static StorageReference storageReference;
    private static DatabaseReference databaseReference;
    private static Uri mImageUri;
    private static StorageTask uploadTask;
    private static String photoDogName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_dog);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        etDogName   = findViewById(R.id.dogNameTextview);
        etVet       = findViewById(R.id.vetText);
        etDogDate   = findViewById(R.id.dogDateText);
        etBreed     = findViewById(R.id.auto_complete_txt);
        addDogBtn   = findViewById(R.id.addDogBtn);
        progressBar = findViewById(R.id.addDogProgressBar);
        ivDog       = findViewById(R.id.dogImageView);
        dateImage   = findViewById(R.id.addDateDog);

        fAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("dogs");
        databaseReference = FirebaseDatabase.getInstance().getReference("dogs");
        DBdog dbdog = new DBdog();


        dateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(AddingDog.this,
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
                etDogDate.setText(date);
            }
        };
        adapterItems = new ArrayAdapter<String>(this,R.layout.list_item,breeds);
        etBreed.setAdapter(adapterItems);
        etBreed.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
            }
        });


        addDogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String dogName = etDogName.getText().toString().trim();
                String breed = etBreed.getText().toString().trim();
                String vet = etVet.getText().toString().trim();
                String dogDate = etDogDate.getText().toString().trim();

                if(TextUtils.isEmpty(dogName)){
                    etDogName.setError("Please insert your name");
                    etDogName.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    return;
                }


                Dog dog = new Dog(dogName, breed, vet, dogDate, LogIn.email);
                if(photoDogName == null)
                    photoDogName = "empty.jpg";
                dog.setImageName(photoDogName);

                dbdog.add(dog).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(AddingDog.this, "Record is insert",Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        uploadFile();
                        startActivity(new Intent(AddingDog.this,PersonProfile.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddingDog.this, "1Error! "+e.getMessage(),Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });

            }
        });

        ivDog.setOnClickListener(new View.OnClickListener() {
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
                    Toast.makeText(AddingDog.this, "upload finish", Toast.LENGTH_SHORT).show();
                }

            });
          }
        }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode == PICK_IMAGE && data != null && data.getData() != null){
            mImageUri = data.getData();
            ivDog.setImageURI(mImageUri);
            String[] temp = mImageUri.getPath().split("/",20);
            photoDogName = temp[temp.length-1] + Timestamp.now().getSeconds();
        }
    }
}
