package com.example.orientationandlistviewapp;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class SliderViewHolder extends RecyclerView.ViewHolder {
    private ImageView imageView;

    SliderViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.imageSlide);
    }

    void setImage(SliderImage sliderImage) throws IOException {
        String imageUrl = sliderImage.getUrl();
        InputStream inputStream = (InputStream) (new URL(imageUrl).getContent());
        imageView.setImageDrawable(Drawable.createFromStream(inputStream, null));
    }
}
