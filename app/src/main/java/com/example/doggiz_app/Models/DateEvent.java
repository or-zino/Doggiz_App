package com.example.doggiz_app.Models;

public class DateEvent {

    public String year, month, day, titleEvent, desOfEvent, dogName, ownerEmail;

    public DateEvent(){

    }

    public DateEvent(String year, String month, String day,String titleEvent, String desOfEvent, String dogName, String ownerEmail){
        this.year       = year;
        this.month      = month;
        this.day        = day;
        this.titleEvent = titleEvent;
        this.desOfEvent = desOfEvent;
        this.dogName    = dogName;
        this.ownerEmail = ownerEmail;
    }
}
