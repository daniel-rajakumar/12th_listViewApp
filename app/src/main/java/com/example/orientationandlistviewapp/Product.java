package com.example.orientationandlistviewapp;

import android.graphics.drawable.Drawable;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class Product {
    private int id;
    private Document doc;
    private String name;
    private String author;
    private String rating;
    private Drawable image;

    Product(int id, Document doc, String name, String author, String rating, String imageUrl) throws IOException {
        this.id = id;
        this.doc = doc;
        this.name = name;
        this.author = author;
        this.rating = (rating.equals("")) ? "N/A": rating;

        InputStream inputStream = (InputStream) (new URL(imageUrl).getContent());
        this.image = Drawable.createFromStream(inputStream, null);
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public int getId() {
        return id;
    }


    public Document getDoc() {
        return doc;
    }

    public String getRating() {
        return rating;
    }

    public Drawable getImage() {
        return image;
    }

}
