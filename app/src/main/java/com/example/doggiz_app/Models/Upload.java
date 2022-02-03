package com.example.doggiz_app.Models;

public class Upload {
    private String mName;
    private String mImageUrl;



    public Upload(){

    }

    public Upload(String name, String imageUrl){
     this.mName = name;
     this.mImageUrl = imageUrl;
    }

    public String getName(){
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String ImageUrl) {
        this.mImageUrl = ImageUrl;
    }
}