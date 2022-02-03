package com.example.doggiz_app.PagesUi;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.doggiz_app.Backend.MainActivity;
import com.example.doggiz_app.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import org.naishadhparmar.zcustomcalendar.CustomCalendar;
import org.naishadhparmar.zcustomcalendar.OnDateSelectedListener;
import org.naishadhparmar.zcustomcalendar.Property;

import java.util.Calendar;
import java.util.HashMap;


public class Events extends Fragment {

    public int preDay = 0, currentDay, dayWithEvent;
    private static final String EVENT= "DateEvent";
    private View eventCard;
    public LinearLayout eventsLinear;
    TextView title, description, dogName;
    ImageView deleteImage;

    public Events() {
    }

    public static Events newInstance(String param1, String param2) {
        Events fragment = new Events();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_events, container, false);


        CustomCalendar customCalendar;
        TextView dateTitle, eventDes;
        FloatingActionButton addBtn;


        FirebaseDatabase database;
        DatabaseReference eventRef;
        StorageReference storageReference;
        String email = LogIn.email;

        database = FirebaseDatabase.getInstance();
        eventRef = database.getReference(EVENT);

        customCalendar = v.findViewById(R.id.calendar);
        dateTitle      = v.findViewById(R.id.dateText);
        eventsLinear   = v.findViewById(R.id.LinearEventDesc);
        addBtn         = v.findViewById(R.id.addEventBtn);

        HashMap<Object, Property> descHashMap = new HashMap<>();
        HashMap<Object, Property> pre = new HashMap<>();

        Property defaultProperty = new Property();

        defaultProperty.layoutResource = R.layout.default_view;

        defaultProperty.dateTextViewResource = R.id.text_view;
        descHashMap.put("default", defaultProperty);


        Property currentProperty = new Property();
        currentProperty.layoutResource = R.layout.current_view;
        currentProperty.dateTextViewResource = R.id.text_view;
        descHashMap.put("current", currentProperty);

        Property presentProperty = new Property();
        presentProperty.layoutResource = R.layout.present_view;
        presentProperty.dateTextViewResource = R.id.text_view;
        descHashMap.put("present", presentProperty);

        Property absentProperty = new Property();
        absentProperty.layoutResource = R.layout.absent_view;
        absentProperty.dateTextViewResource = R.id.text_view;
        descHashMap.put("absent",absentProperty);


        customCalendar.setMapDescToProp(descHashMap);

        HashMap<Integer,Object> dateHashMap = new HashMap<>();
        Calendar calendar = Calendar.getInstance();





        dateHashMap.put(calendar.get(Calendar.DAY_OF_MONTH),"current");
        currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot ds : snapshot.getChildren()){
                    if(ds.child("ownerEmail").getValue().equals(email)) {
                        if (ds.child("dogName").getValue().equals(MyDog.dogInUse)) {
                            dayWithEvent = Integer.parseInt(ds.child("day").getValue().toString());
                            dateHashMap.put(dayWithEvent, "absent");
                            customCalendar.setDate(calendar,dateHashMap);
                        }
                    }
                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        customCalendar.setDate(calendar,dateHashMap);

        customCalendar.setOnDateSelectedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(View view, Calendar selectedDate, Object desc) {

                eventsLinear = v.findViewById(R.id.LinearEventDesc);
                eventsLinear.removeAllViews();
                eventsLinear.setVisibility(View.VISIBLE);

                if(currentDay == preDay)
                    dateHashMap.put(currentDay, "current");
                else {
                    eventRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                if (ds.child("ownerEmail").getValue().equals(email)) {
                                    if (ds.child("dogName").getValue().equals(MyDog.dogInUse)) {
                                        if(ds.child("day").getValue().equals(String.valueOf(selectedDate.get(Calendar.DAY_OF_MONTH)))) {
                                            if(ds.child("day").getValue().equals(String.valueOf(selectedDate.get(Calendar.DAY_OF_MONTH))))
                                            dayWithEvent = Integer.parseInt(ds.child("day").getValue().toString());
                                        }

                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    if (preDay == dayWithEvent) {
                        dateHashMap.put(dayWithEvent, "absent");
                    }else
                        dateHashMap.put(preDay, "default");
                }
                String sDate = selectedDate.get(Calendar.DAY_OF_MONTH) + "/" + (selectedDate.get(Calendar.MONTH) + 1) + "/" + selectedDate.get(Calendar.YEAR);
                dateTitle.setVisibility(View.VISIBLE);
                dateTitle.setText(sDate);
                dateHashMap.put(selectedDate.get(Calendar.DAY_OF_MONTH),"present");
                if(preDay == selectedDate.get(Calendar.DAY_OF_MONTH)){
                    dateHashMap.put(preDay,"default");
                    eventsLinear.setVisibility(View.GONE);
                    dateTitle.setVisibility(View.GONE);
                    dateHashMap.put(currentDay,"current");
                }
                customCalendar.setDate(calendar,dateHashMap);
                preDay = selectedDate.get(Calendar.DAY_OF_MONTH);






                eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds : snapshot.getChildren()){
                            if(ds.child("ownerEmail").getValue().equals(email)) {
                                if(ds.child("dogName").getValue().equals(MyDog.dogInUse)){
                                    if(ds.child("year").getValue().equals(String.valueOf(selectedDate.get(Calendar.YEAR))) &&
                                       ds.child("month").getValue().equals(String.valueOf(selectedDate.get(Calendar.MONTH)+ 1)) &&
                                       ds.child("day").getValue().equals(String.valueOf(selectedDate.get(Calendar.DAY_OF_MONTH)))){

                                        eventCard = getLayoutInflater().inflate(R.layout.item_add, null);
                                        eventsLinear = v.findViewById(R.id.LinearEventDesc);

                                        title = eventCard.findViewById(R.id.itemAddTitle);
                                        title.setText(ds.child("titleEvent").getValue().toString());

                                        description = eventCard.findViewById(R.id.itemAddDesc);
                                        description.setText(ds.child("desOfEvent").getValue().toString());

                                        dogName = eventCard.findViewById(R.id.itemAddName);
                                        dogName.setText(ds.child("dogName").getValue().toString());

                                        deleteImage = eventCard.findViewById(R.id.itemAddDelete);



                                        deleteImage.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                AlertDialog dlg = new AlertDialog.Builder( v.getContext())
                                                        .setTitle("Delete Event")
                                                        .setMessage("Are you sure you want to delete this dog")
                                                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                ds.getRef().removeValue();
                                                                eventsLinear.setVisibility(View.GONE);
                                                                startActivity(new Intent(getActivity(), MainActivity.class));
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
                                        customCalendar.setDate(calendar,dateHashMap);
                                        eventsLinear.addView(eventCard);
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),AddEvent.class);
                startActivity(intent);
            }
        });



        return v;
    }
}