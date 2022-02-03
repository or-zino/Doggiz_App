package com.example.doggiz_app.PagesUi;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.doggiz_app.Backend.MapBack;
import com.example.doggiz_app.R;


public class Map extends Fragment {

    ImageView ivVet, ivPark;
    public static String mapChoose = "";



    public Map() {

    }

    public static Map newInstance(String param1, String param2) {
        Map fragment = new Map();
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

        View v = inflater.inflate(R.layout.fragment_map, container, false);
        ivVet  = v.findViewById(R.id.vetMapImage);
        ivPark = v.findViewById(R.id.parkMapImage);

        ivVet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapChoose = "veterinarians";
                Intent intent = new Intent(getActivity(), MapBack.class);
                startActivity(intent);
            }
        });

        ivPark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapChoose = "dog park";
                Intent intent = new Intent(getActivity(),MapBack.class);
                startActivity(intent);
            }
        });

        return v;
    }
}