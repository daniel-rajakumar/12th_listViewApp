package com.example.orientationandlistviewapp;

import android.app.Activity;
import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

class CustomAdapterApp extends ArrayAdapter<Product> {
    private final MainActivity mainActivity;
    List<Product> list;
    Context context;
    int xmlResource;

    public CustomAdapterApp(MainActivity mainActivity, @NonNull Context context, int resource, @NonNull List<Product> objects) {
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

        Button button_remove = adapterLayout.findViewById(R.id.button_applist_remove);
        TextView textView_name = adapterLayout.findViewById(R.id.textView_applist_name);
        ImageView imageView_icon = adapterLayout.findViewById(R.id.imageView_applist_icon);
        TextView textView_info = adapterLayout.findViewById(R.id.textView_info);
        LinearLayout linearLayout = adapterLayout.findViewById(R.id.linearLayout);

        Product product = list.get(position);

        if (textView_info != null){
            textView_info.setMovementMethod(new ScrollingMovementMethod());
        }

        textView_name.setText(product.getName());
        imageView_icon.setImageDrawable(product.getImage());

        if (Product.getSelectedIndex() == position){
            linearLayout.setBackgroundResource(R.drawable.custom_applist_selected);
        }

        button_remove.setOnClickListener(view -> {
            View selectedView = mainActivity.getViewByPosition(position, mainActivity.listView_app);
            selectedView.startAnimation(AnimationUtils.loadAnimation(mainActivity, R.anim.remove_list));

            selectedView.postDelayed(() -> {
                list.remove(position);
                notifyDataSetChanged();

                if (Product.getSelectedIndex() == position) {
                    Product.setSelectedIndex(-1);

                } else if (Product.getSelectedIndex() > position) {
                    Product.setSelectedIndex(Product.getSelectedIndex() - 1);

                }
                MainActivity.updateSelectedAppInfo(mainActivity, (ArrayList<Product>) list);

            }, selectedView.getAnimation().getDuration());
        });

        return adapterLayout;
    }


}


