package com.example.orientationandlistviewapp;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.io.IOException;
import java.util.ArrayList;

public class SliderAdapter extends RecyclerView.Adapter<SliderViewHolder> {

    private ArrayList<SliderImage> sliderImages;
    private ViewPager2 viewPager2;

    SliderAdapter(ArrayList<SliderImage> sliderImages, ViewPager2 viewPager2) {
        this.sliderImages = sliderImages;
        this.viewPager2 = viewPager2;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SliderViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.slide_image_container,
                parent,
                false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        try {
            holder.setImage(sliderImages.get(position));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return sliderImages.size();
    }

}
