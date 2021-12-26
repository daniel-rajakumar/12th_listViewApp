package com.example.orientationandlistviewapp;

import android.app.Activity;
import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

class CustomAdapter extends ArrayAdapter<Product> {
    private final MainActivity mainActivity;
    List<Product> list;
    Context context;
    int xmlResource;

    public CustomAdapter(MainActivity mainActivity, @NonNull Context context, int resource, @NonNull List<Product> objects) {
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

        Button button = adapterLayout.findViewById(R.id.button);
        TextView textView = adapterLayout.findViewById(R.id.textView);
        ImageView imageView = adapterLayout.findViewById(R.id.imageView);
        TextView _textView = null;
        LinearLayout linearLayout = adapterLayout.findViewById(R.id.linearLayout);

        if (mainActivity.isLandSpace) {
            _textView = mainActivity.findViewById(R.id.textView2);
            _textView.setMovementMethod(new ScrollingMovementMethod());
        }

        Product product = list.get(position);
        textView.setText(product.getName());
        imageView.setImageDrawable(product.getImage());

        if (Product.getSelectedIndex() == position){
                linearLayout.setBackgroundResource(R.drawable.custom_applist_selected);
        }

        button.setOnClickListener(view -> {
            list.remove(position);
            MainActivity.updateSelectedAppInfo(mainActivity, (ArrayList<Product>) list);
            notifyDataSetChanged();
        });

        return adapterLayout;
    }


}


