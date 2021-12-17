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
        final StringBuffer sb = new StringBuffer("Product{");
        sb.append("name='").append(name).append('\'');
        sb.append(", price=").append(price);
        sb.append(", image=").append(image);
        sb.append('}');
        return sb.toString();
    }
}
