package com.example.orientationandlistviewapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
    String name = "Samsung";
    String url = "https://play.google.com/store/search?q="  + name + "&c=apps&hl=en_US&gl=US";

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

        getWebsite();
    }

//    Elements links = doc.select("WsMbG1c");
    private void getWebsite() throws InterruptedException {
        Thread thread = new Thread(() -> {
            try {
                document = Jsoup.connect(url).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();
        while (thread.isAlive());
    }








    void set() throws IOException {
        Elements elements = document.select("div.WsMG1c");


//        Drawable drawable;

        for (int i = 0; i < elements.size(); i++){
            int finalI = i;
            Thread thread = new Thread(() -> {
                try {
                    Elements test = document.select("img.T75of.QNCnCf");
                    Element elements1 = document.select("img.T75of.QNCnCf").get(finalI * 3);
//                    System.out.println("dani: " + elements1.attr("data-src"));
                    System.out.println("dani1: " + elements1.attr("data-src"));
                    String url = elements1.attr("data-src");
                    URL myUrl = new URL(url);
                    InputStream inputStream = (InputStream) myUrl.getContent();
                    Drawable drawable = Drawable.createFromStream(inputStream, null);
                    list.add(new Product(elements.get(finalI).text(), 1, drawable));
                } catch (IOException ee) {
                    ee.printStackTrace();
                }
            });
            thread.start();
            while (thread.isAlive());
        }


        listView.setAdapter(new CustomAdapter(this, R.layout.adapter_layout, list));
    }
    
    void go(){
        
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
//            button.setText(product.getName());
            button.setText("remove: ");
            textView.setText(product.getName());
            imageView.setImageDrawable(product.getImage());
//            imageView.setImageResource(product.getImage());


            button.setOnClickListener(view -> {
                list.remove(position);
                notifyDataSetChanged();
            });

            return adapterLayout;
        }
    }
}

