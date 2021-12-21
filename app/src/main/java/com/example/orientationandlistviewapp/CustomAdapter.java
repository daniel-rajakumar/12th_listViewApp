package com.example.orientationandlistviewapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.Html;
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

        TextView textView1 = mainActivity.findViewById(R.id.textView6);
        TextView textView2 = mainActivity.findViewById(R.id.textView3);
        textView1.setText("By: ");
        textView2.setText(" :Rating");
        
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//            return super.getView(position, convertView, parent);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View adapterLayout = layoutInflater.inflate(xmlResource, null);

        Button button = adapterLayout.findViewById(R.id.button);
        TextView textView = adapterLayout.findViewById(R.id.textView);
        ImageView imageView = adapterLayout.findViewById(R.id.imageView);
        TextView textView1 = mainActivity.findViewById(R.id.textView6);
        TextView textView2 = mainActivity.findViewById(R.id.textView3);
        TextView _textView = null;

        if (mainActivity.isLandSpace) {
            _textView = mainActivity.findViewById(R.id.textView2);
            _textView.setMovementMethod(new ScrollingMovementMethod());
        }
        LinearLayout linearLayout = adapterLayout.findViewById(R.id.linearLayout);
        Product product = list.get(position);
//            button.setText(product.getName());
        textView.setText(product.getName());
        imageView.setImageDrawable(product.getImage());

//            for (int i = 0; i < parent.getChildCount(); i++){
//                Log.d("BG",  i + " -> " + parent.getChildAt(i).getBackground().toString());
//            }

        TextView final_textView = _textView;
        linearLayout.setOnClickListener(v -> {
            for (int i = 0; i < parent.getChildCount(); i++) {
                parent.getChildAt(i).setBackgroundColor(Color.WHITE);
            }
            v.setBackgroundColor(Color.rgb(200, 200, 200));
            String author = product.getAuthor();
            if (author.length() > 35) author = author.substring(0, 32) + "...";
            textView1.setText("By: " + author);
            textView2.setText(product.getRating() + " :Rating");

            if (mainActivity.isLandSpace) {
                final_textView.setText(Html.fromHtml(product.getInfo(), Html.FROM_HTML_MODE_COMPACT));
            }
        });


        button.setOnClickListener(view -> {
            list.remove(position);
            notifyDataSetChanged();
        });

        return adapterLayout;
    }


}
