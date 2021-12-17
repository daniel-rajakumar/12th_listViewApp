package com.example.orientationandlistviewapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    ArrayList<Product> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        ready();
        set();
        go();
    }
    
    void ready(){ }

    void set(){
        listView = findViewById(R.id.listView);
        list = new ArrayList<>();




        list.add(new Product("one", 3, R.drawable.ic_launcher_background));
        list.add(new Product("two", 8, R.drawable.ic_launcher_foreground));

        listView.setAdapter(new CustomeAdapter(this, R.layout.adapter_layout, list));
    }
    
    void go(){
        
    }

    class CustomeAdapter extends ArrayAdapter<Product> {
        List<Product> list;
        Context context;
        int xmlResource;

        public CustomeAdapter(@NonNull Context context, int resource, @NonNull List<Product> objects) {
            super(context, resource, objects);
            this.context = context;
            this.xmlResource = resource;
            this.list = objects;
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
            Product product = list.get(position);
            button.setText(product.getName());
            textView.setText(product.getPrice() + "");
            imageView.setImageResource(product.getImage());

            return adapterLayout;
        }
    }
}

