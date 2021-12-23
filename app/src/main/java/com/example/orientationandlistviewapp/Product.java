package com.example.orientationandlistviewapp;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class Product implements Parcelable {
    private int id;
    private Document doc;
    private String name;
    private String author;
    private String rating;
    private Drawable image;
    private String info;
    private ArrayList<String> imageLink;



    Product(int id, Document doc) throws IOException {
        try {
            // id
            this.id = id;

            // doc
            this.doc = doc;
            Elements elements1 = doc.select("main.LXrl4c");

            // info
            this.info = elements1.select("div.DWPxHb span div").toString();

//            Log.d("info", info);
//
            // name
            this.name = elements1.select("h1.AHFaub").text();

            // author
            this.author = elements1.select("a.hrTbp.R8zArc").get(0).text();

            // rating
            this.rating = elements1.select(".pf5lIe div").attr("aria-label").replaceAll("[a-z]|[A-Z]", "").trim();
            this.rating = (this.rating.equals("")) ? "N/A": this.rating;

            //image
            String imageUrl = elements1.select(".xSyT2c img").attr("src");
            InputStream inputStream = (InputStream) (new URL(imageUrl).getContent());
            this.image = Drawable.createFromStream(inputStream, null);

            //url
//            Log.d("doc",
//                  ".\n" +
////                   doc.select("div.SgoUSc .Q4vdJd").select("img.T75of.DYfLw")
//                    doc.select("img.T75of.DYfLw")
//            );

            // image links
            imageLink = new ArrayList<>();
            Elements links = doc.select("img.T75of.DYfLw");

            for (Element element: links){
                String link1 = element.attr("src");
                if (link1.startsWith("data")) link1 = "";
                String link2 = element.attr("data-src");
                String link = link1 + link2;
                imageLink.add(link);
                Log.d("link", link);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

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

    public ArrayList<String> getImageLink() {
        return imageLink;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    protected Product(Parcel in) {
        id = in.readInt();
        name = in.readString();
        author = in.readString();
        rating = in.readString();
        info = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(author);
        dest.writeString(rating);
        dest.writeString(info);
    }
}
