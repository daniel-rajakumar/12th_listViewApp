package com.example.orientationandlistviewapp;

public class Product {
    private String name;
    private double price;
    private int image;

    Product(String name, double price, int image){
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

    public int getImage() {
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
