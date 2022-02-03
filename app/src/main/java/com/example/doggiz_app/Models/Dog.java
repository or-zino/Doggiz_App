package com.example.doggiz_app.Models;

public class Dog {

    public String dogName, breed, vet, dateOfBirth, imageName, userEmail;

    public Dog(){

    }


    public Dog(String dogName, String breed, String vet, String dateOfBirth, String userEmail){
        this.dogName = dogName;
        this.breed = breed;
        this.vet = vet;
        this.dateOfBirth = dateOfBirth;
        this.userEmail = userEmail;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}
