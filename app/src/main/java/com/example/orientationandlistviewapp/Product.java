package com.example.orientationandlistviewapp;

import android.graphics.drawable.Drawable;

public class Product {
    private String name;
    private double price;
    private Drawable image;

    Product(String name, double price, Drawable image){
        this.name = name;
        this.price = price;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public Drawable getImage() {
        return image;
    }

    @Override
    public String toString() {
        return "Product{" + "name='" + name + '\'' +
                ", price=" + price +
                ", image=" + image +
                '}';
    }
}
