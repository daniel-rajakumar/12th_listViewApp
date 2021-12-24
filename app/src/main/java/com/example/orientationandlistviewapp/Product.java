package com.example.orientationandlistviewapp;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class Product implements Parcelable {
    private int id;
    private Document doc, doc2;
    private String name;
    private String author;
    private String rating;
    private Drawable image;
    private String info;
    private String videoLink;
    private ArrayList<String> imageLinks;
    private ArrayList<SliderPreview> sliderPreview;
    private ArrayList<UserReviews> userReviews;



    Product(int id, Document doc, Document doc2) throws IOException {
        try {
            // id
            this.id = id;

            // doc
            this.doc = doc;
            this.doc2 = doc2;
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
//                    doc2.body().getElementsByTag("script")
//            );

            //comments
            userReviews = new ArrayList<>();
            Elements elements2 = doc2.body().getElementsByTag("script");

//            Log.d("doc", "size" + elements2.size());
            for (int i = 0; i < elements2.size(); i++){
                String str = elements2.get(i).toString();
                if (str.contains("https://play-lh.googleusercontent.com/a-/")){
                    String[] arr = str.split("\\[\"");
                    for (String a: arr){
                        if (a.endsWith(",null,null,")){
                            String[] pattern0 = a.split("\",\\[null,[0-9]+,null,\\[null,null,");
                            String[] pattern1 = pattern0[1].split("]]],[0-9]+,null,");
                            String[] pattern2 = pattern1[1].split(",\\[[0-9]+,[0-9]+\\],[0-9]+,null,null,");

                            String userName =  pattern0[0];
                            String link =  pattern1[0].replace("\"", "").split("\\\\u")[0];
                            String comment = pattern2[0].replace("\"", "");

                            userReviews.add(new UserReviews(userName, link, comment));
//                            String comment = arr2[5].substring(arr2[5].indexOf("\""), arr2[5].lastIndexOf("\""));
//                            Log.d("doc", i + " -> " + Arrays.toString(arr2));
//                            Log.d("doc",
//                                    ".\nname: " + userName + "\n" +
//                                         "link: " + link + "\n" +
//                                         "comment: " + comment + "\n."
//                            );
//
//                            String[] patter2 = pattern[1].split("]]],[0-9]+,null,");
//                            Log.d("doc", patter2[1].split(",\\[[0-9]+,[0-9]+\\],[0-9]+,null,null,")[0]);

                        }
                    }
                }
            }












            // video link
            this.sliderPreview = new ArrayList<>();
            videoLink = doc.select(".MMZjL.lgooh").attr("data-trailer-url");




            // image links
            imageLinks = new ArrayList<>();
            Elements links = doc.select("img.T75of.DYfLw");


            for (Element element: links){
                String link1 = element.attr("src");
                if (link1.startsWith("data")) link1 = "";
                String link2 = element.attr("data-src");
                String link = link1 + link2;
                imageLinks.add(link);
            }

            if (!videoLink.equals("")) {
                imageLinks.remove(0);
            }





            // set images
            for (String url: imageLinks){
                sliderPreview.add(new SliderPreview(url, true));
            }

            // set video
            if (!videoLink.equals("")){
                sliderPreview.add(0, new SliderPreview(videoLink, false));
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

    public String getVideoLink() {
        return videoLink;
    }

    public ArrayList<String> getImageLinks() {
        return imageLinks;
    }

    public ArrayList<SliderPreview> getSliderPreview() {
        return sliderPreview;
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



    class UserReviews {
        String name;
        String profileLink;
        String comment;

        UserReviews(String name, String profileLink, String comment){
            this.name = name;
            this.profileLink = profileLink;
            this.comment = comment;
        }

        public String getName() {
            return name;
        }

        public String getComment() {
            return comment;
        }

        public String getProfileLink() {
            return profileLink;
        }
    }
}
