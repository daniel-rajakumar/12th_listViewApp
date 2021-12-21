package com.example.orientationandlistviewapp;

import android.graphics.drawable.Drawable;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

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
    private String info;


    Product(int id, Document doc) throws IOException {
        try {
            // id
            this.id = id;

            // doc
            this.doc = doc;
            Elements elements = doc.select("main.LXrl4c");

            String line = "";

            for (int i = 0; i < 1_000; i++) line += "~~~~~~~~~~~~~~~~~~~~~~~\n";
            line += "\n";
            Log.d("product\n",
                    id + " " + line +
                     elements.select("div.DWPxHb span div")
            );

            // info
            this.info = elements.select("div.DWPxHb span div").text();
//
            // name
            this.name = elements.select("h1.AHFaub").text();

            // author
            this.author = elements.select("a.hrTbp.R8zArc").get(0).text();

            // rating
            this.rating = elements.select(".pf5lIe div").attr("aria-label").replaceAll("[a-z]|[A-Z]", "").trim();
            this.rating = (this.rating.equals("")) ? "N/A": this.rating;

            //image
            String imageUrl = elements.select(".xSyT2c img").attr("src");
            InputStream inputStream = (InputStream) (new URL(imageUrl).getContent());
            this.image = Drawable.createFromStream(inputStream, null);

        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public String getInfo() {
        return info;
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
