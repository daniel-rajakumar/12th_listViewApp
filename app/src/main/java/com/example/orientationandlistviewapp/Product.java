package com.example.orientationandlistviewapp;

import android.graphics.drawable.Drawable;

public class Product {
    private String name;
    private String author;
    private double rating;
    private Drawable image;

    Product(String name, String author, double rating, Drawable image){
        this.name = name;
        this.author = author;
        this.rating = rating;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public double getRating() {
        return rating;
    }

    public Drawable getImage() {
        return image;
    }

}
