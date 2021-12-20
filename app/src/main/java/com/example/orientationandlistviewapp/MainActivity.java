package com.example.orientationandlistviewapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    ArrayList<Product> list;
    Document document;
    EditText editText;
    Button button;
    String name = "samsung";
    String url = "https://play.google.com/store/search?q=" + name + "&c=apps&hl=en_US&gl=US";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            ready();
            set();
            go();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    void ready() throws IOException, InterruptedException {
        listView = findViewById(R.id.listView);
        list = new ArrayList<>();
        editText = findViewById(R.id.editTextTextPersonName3);
        button = findViewById(R.id.button3);
        getWebsite();
    }

    private void getWebsite() throws InterruptedException {
        Thread thread = new Thread(() -> {
            try {
                document = Jsoup.connect(url).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();
        while (thread.isAlive()) ;
    }


    void updateList() {
        list.clear();
        Elements elementsName = document.select("div.WsMG1c");
        Elements elementsRating = document.select("div.pf5lIe");
        Elements elementsAuthor = document.select("div.KoLSrc");
        Elements elementsImage = document.select("img.T75of.QNCnCf");

        for (int i = 0; i < elementsName.size(); i++) {
            int finalI = i;
            Thread thread = new Thread(() -> {
                try {
                    String url = elementsImage.get(finalI * 3).attr("data-src");
                    InputStream inputStream = (InputStream) (new URL(url).getContent());

//                    String name = elementsName.get(finalI).text();
                    Log.d("98", "~~~~~~~~~~~~~~~~~\n " + finalI + "  \n" + elementsRating + "");
//                    Log.d("size", elementsRating.size() + "" + elementsName.size());
                    String name = elementsRating.get(finalI).select("div").attr("aria-label");


//                    Log.d("label", elementsRating.select("div").attr("aria-label") + "");
//                    Log.d("label", elementsRating.select("div").get(finalI).attr("aria-label") + "");
//                    Log.d("label", elementsRating.get(finalI).select("div").attr("aria-label") + "");



                    String author = elementsAuthor.get(finalI * 2).text();
                    double rating = 1;
                    Drawable image = Drawable.createFromStream(inputStream, null);
                    list.add(new Product(name, author, rating, image));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            thread.start();
            while (thread.isAlive()) ;
        }
        listView.setAdapter(new CustomAdapter(this, R.layout.adapter_layout, list));
    }

    void set() throws IOException {
        updateList();
    }

    void go() {
        button.setOnClickListener(v -> {
            name = editText.getText().toString();
            Log.d("114", name);
            url = "https://play.google.com/store/search?q=" + name + "&c=apps&hl=en_US&gl=US";
            try {
                getWebsite();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            updateList();
        });
    }

    class CustomAdapter extends ArrayAdapter<Product> {
        List<Product> list;
        Context context;
        int xmlResource;

        public CustomAdapter(@NonNull Context context, int resource, @NonNull List<Product> objects) {
            super(context, resource, objects);
            this.context = context;
            this.xmlResource = resource;
            this.list = objects;
        }

        void update() {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View adapterLayout = layoutInflater.inflate(xmlResource, null);
            Button button = adapterLayout.findViewById(R.id.button);
            TextView textView = adapterLayout.findViewById(R.id.textView);
            ImageView imageView = adapterLayout.findViewById(R.id.imageView);

            for (Product p : list) {
                textView.setText(p.getName());
            }
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
            TextView textView1 = findViewById(R.id.textView6);
            LinearLayout linearLayout = adapterLayout.findViewById(R.id.linearLayout);
            Product product = list.get(position);
//            button.setText(product.getName());
            button.setText("remove");
            textView.setText(product.getName());
            imageView.setImageDrawable(product.getImage());

            linearLayout.setOnClickListener(v -> {
                for (int i = 0; i < parent.getChildCount(); i++){
                    parent.getChildAt(i).setBackgroundColor(Color.WHITE);
                }
                v.setBackgroundColor(Color.GREEN);
                textView1.setText("By: " + product.getAuthor());
            });


            button.setOnClickListener(view -> {
                list.remove(position);
                notifyDataSetChanged();
            });

            return adapterLayout;
        }
    }
}

