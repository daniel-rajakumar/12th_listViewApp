package com.example.orientationandlistviewapp;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ListView;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class Product implements Parcelable {
    private int id;
    private String name;
    private String author;
    private String rating;
    private Drawable image;
    private String info;
    private ArrayList<SliderPreview> sliderPreview;
    private ArrayList<UserReviews> userReviews;
    private static int selectedIndex = -1;



    Product(int id, Document appPage, Document appFullReviewPage) throws IOException {

        try {
            // id
            this.id = id;

            // doc
            Elements elements = appPage.select("main.LXrl4c");

            // info
            this.info = elements.select("div.DWPxHb span div").toString();

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

            // userReview
            userReviews = new ArrayList<>();
            Elements elements_review = appFullReviewPage.body().getElementsByTag("script");

            for (int i = 0; i < elements_review.size(); i++){
                String line = elements_review.get(i).toString();
                if (line.contains("https://play-lh.googleusercontent.com/a-/")){
                    String[] array = line.split("\\[\"");
                    for (String str: array){
                        if (str.endsWith(",null,null,")){
                            String[] pattern0 = str.split("\",\\[null,[0-9]+,null,\\[null,null,");
                            String[] pattern1 = pattern0[1].split("]]],[0-9]+,");
                            String[] pattern2 = pattern1[1].split(",\\[[0-9]+,[0-9]+\\],[0-9]+,null,null,");
//

                            int stars = Integer.parseInt(pattern0[1].charAt(pattern0[1].indexOf("]]],") + 4) + "");
                            String userName =  pattern0[0];
                            String link =  pattern1[0].replace("\"", "").split("\\\\u")[0];
                            String comment = pattern2[0].substring(pattern2[0].indexOf("\"") + 1, pattern2[0].length() - 1);


                            userReviews.add(new UserReviews(userName, stars, link, comment));
                        }
                    }
                }
            }

            // video link
            this.sliderPreview = new ArrayList<>();
            String videoLink = appPage.select(".MMZjL.lgooh").attr("data-trailer-url");



            // image links
            ArrayList<String> imageLinks = new ArrayList<>();
            Elements links = appPage.select("img.T75of.DYfLw");


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

    public String getRating() {
        return rating;
    }

    public Drawable getImage() {
        return image;
    }

    public ArrayList<SliderPreview> getSliderPreview() {
        return sliderPreview;
    }

    public ArrayList<UserReviews> getUserReviews() {
        return userReviews;
    }

    public static int getSelectedIndex() {
        return selectedIndex;
    }

    public static void setSelectedIndex(int selectedIndex) {
        Product.selectedIndex = selectedIndex;
    }


    class UserReviews {
        String name;
        int stars;
        String profileLink;
        String comment;

        UserReviews(String name, int stars, String profileLink, String comment){
            this.name = name;
            this.stars = stars;
            this.profileLink = profileLink;
            this.comment = comment;
        }

        public String getName() {
            return name;
        }

        public String getComment() {
            return comment;
        }

        public int getStars() {
            return stars;
        }

        public String getProfileLink() {
            return profileLink;
        }
    }






    /*
     * Parcelable related methods
     */

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(author);
        dest.writeString(rating);
        dest.writeString(info);
    }

    protected Product(Parcel in) {
        id = in.readInt();
        name = in.readString();
        author = in.readString();
        rating = in.readString();
        info = in.readString();
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
}
