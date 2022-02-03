package com.example.doggiz_app.Models;

public class MedicalCard {

    public String ownerEmail, dogName, description, treatment;

    public MedicalCard(){

    }

    public MedicalCard(String ownerEmail, String dogName, String description, String treatment){
        this.ownerEmail  = ownerEmail;
        this.dogName     = dogName;
        this.description = description;
        this.treatment   = treatment;

    }
}
