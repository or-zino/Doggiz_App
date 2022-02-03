package com.example.doggiz_app.PagesUi;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doggiz_app.Backend.MainActivity;
import com.example.doggiz_app.Models.BDMedical;
import com.example.doggiz_app.Models.MedicalCard;
import com.example.doggiz_app.R;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Calendar;

public class AddingMedicalBag extends AppCompatActivity {

    private EditText etDes;
    private TextView tvDate;
    private Button addBtn;
    private ImageView ivVac, ivMedPills, ivTreat, ivDate;
    private static String chooseTreat = "", date = "";
    private DatePickerDialog.OnDateSetListener onDateSetListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_medical_bag);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        ivVac      = findViewById(R.id.imageVaccine);
        ivMedPills = findViewById(R.id.imagePills);
        ivTreat    = findViewById(R.id.imageTreatment);
        etDes      = findViewById(R.id.medicalDescriptionText);
        addBtn     = findViewById(R.id.medicalAddBtn);
        tvDate     = findViewById(R.id.medicalDateText);
        ivDate     = findViewById(R.id.medicalDateImage);

        BDMedical dbmed = new BDMedical();

        ivDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(AddingMedicalBag.this,
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
                date = dayOfMonth +"/"+ (month+1) + "/" + year;
                tvDate.setText(date);
            }
        };

        ivVac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chooseTreat.equals("Vaccine")){
                    chooseTreat = "";
                    ivVac.setBackgroundResource(R.drawable.medshotcolor);

                }else{
                    chooseTreat = "Vaccine";
                    ivVac.setBackgroundResource(R.drawable.check);
                    ivMedPills.setBackgroundResource(R.drawable.medpillscolor);
                    ivTreat.setBackgroundResource(R.drawable.medtreatmentcolor);
                }

            }
        });

        ivMedPills.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chooseTreat.equals("Medical pills")){
                    chooseTreat = "";
                    ivMedPills.setBackgroundResource(R.drawable.medpillscolor);

                }else{
                    chooseTreat = "Medical pills";
                    ivVac.setBackgroundResource(R.drawable.medshotcolor);
                    ivMedPills.setBackgroundResource(R.drawable.check);
                    ivTreat.setBackgroundResource(R.drawable.medtreatmentcolor);
                }
            }
        });

        ivTreat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chooseTreat.equals("Treatment")){
                    chooseTreat = "";
                    ivTreat.setBackgroundResource(R.drawable.medtreatmentcolor);

                }else{
                    chooseTreat = "Treatment";
                    ivVac.setBackgroundResource(R.drawable.medshotcolor);
                    ivMedPills.setBackgroundResource(R.drawable.medpillscolor);
                    ivTreat.setBackgroundResource(R.drawable.check);
                }
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email   = LogIn.email;
                String dogName = MyDog.dogInUse;
                String description = date + ":" + etDes.getText().toString().trim();

                MedicalCard medicalCard = new MedicalCard(email,dogName,description,chooseTreat);
                dbmed.add(medicalCard).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(AddingMedicalBag.this, "Record is insert",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));

                    }
                });


            }
        });



    }
}