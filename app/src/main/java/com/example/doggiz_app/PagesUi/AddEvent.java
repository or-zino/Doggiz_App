package com.example.doggiz_app.PagesUi;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doggiz_app.Models.DateEvent;
import com.example.doggiz_app.Backend.MainActivity;
import com.example.doggiz_app.Models.DBevent;
import com.example.doggiz_app.R;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Calendar;

public class AddEvent extends AppCompatActivity {

    ImageView ivDate;
    EditText etTitle, etDesc;
    TextView tvDate;
    Button addButton;
    public int pYear,pMonth,pDay;
    DatePickerDialog.OnDateSetListener setListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        ivDate    = findViewById(R.id.calendarImageView);
        tvDate    = findViewById(R.id.EventDateText2);
        etDesc    = findViewById(R.id.eventDescriptionText);
        etTitle   = findViewById(R.id.eventTitleText);
        addButton = findViewById(R.id.eventAddNewBtn);

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        DBevent dbEvent = new DBevent();

        ivDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        AddEvent.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        month = month +1;
                        String date = day + "/" + month + "/" + year;
                        tvDate.setText(date);
                        tvDate.setVisibility(View.VISIBLE);
                        pYear = year; pMonth = month; pDay = day;
                    }
                },year,month,day);
                datePickerDialog.show();
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email   = LogIn.email;
                String dogName = MyDog.dogInUse;
                String title   = etTitle.getText().toString().trim();
                String description = etDesc.getText().toString().trim();
                DateEvent dateEvent = new DateEvent(String.valueOf(pYear), String.valueOf(pMonth), String.valueOf(pDay), title, description, dogName, email);

                dbEvent.add(dateEvent).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(AddEvent.this, "Record is insert",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                });


            }
        });
    }
}