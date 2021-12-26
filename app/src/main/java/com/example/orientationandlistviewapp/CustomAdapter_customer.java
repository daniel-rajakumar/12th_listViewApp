package com.example.orientationandlistviewapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

class CustomAdapter_customer extends ArrayAdapter<Product.UserReviews> {
    private final MainActivity mainActivity;
    List<Product.UserReviews> list;
    Context context;
    int xmlResource;

    public CustomAdapter_customer(MainActivity mainActivity, @NonNull Context context, int resource, @NonNull List<Product.UserReviews> objects) {
        super(context, resource, objects);
        this.mainActivity = mainActivity;
        this.context = context;
        this.xmlResource = resource;
        this.list = objects;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View adapterLayout = layoutInflater.inflate(xmlResource, null);

        TextView textView_comment = adapterLayout.findViewById(R.id.textView_review_comment);
        TextView textView_name = adapterLayout.findViewById(R.id.textView_review_name);
        ImageView imageView_pfp = adapterLayout.findViewById(R.id.imageView_review_pfp);
        RatingBar ratingBar_rating = adapterLayout.findViewById(R.id.ratingBar_review_rating);
        Product.UserReviews product = list.get(position);

        textView_name.setText(product.getName());
        textView_comment.setText(product.getComment());
        ratingBar_rating.setRating(product.getStars());

        try {
            String imageUrl = product.getProfileLink();
            InputStream inputStream = (InputStream) (new URL(imageUrl).getContent());
            imageView_pfp.setImageDrawable(Drawable.createFromStream(inputStream, null));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return adapterLayout;
    }


}


