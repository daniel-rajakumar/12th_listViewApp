package com.example.orientationandlistviewapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View adapterLayout = layoutInflater.inflate(xmlResource, null);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View adapterLayout = layoutInflater.inflate(xmlResource, null);

        TextView textView = adapterLayout.findViewById(R.id.textView_comment);
        TextView textView2 = adapterLayout.findViewById(R.id.textView_name);
        ImageView imageView = adapterLayout.findViewById(R.id.imageView_comment);
        RatingBar ratingBar = adapterLayout.findViewById(R.id.ratingBar_comment);
        Product.UserReviews product = list.get(position);

        textView2.setText(product.getName());
        textView.setText(product.getComment());
        ratingBar.setRating(Integer.parseInt(product.getStars()));

        try {
            String imageUrl = product.getProfileLink();
            InputStream inputStream = (InputStream) (new URL(imageUrl).getContent());
            imageView.setImageDrawable(Drawable.createFromStream(inputStream, null));
        } catch (IOException e) {
            e.printStackTrace();
        }


        return adapterLayout;
    }


}


