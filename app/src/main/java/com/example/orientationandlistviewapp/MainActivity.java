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
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    ArrayList<Product> list;
    Document document;
    EditText editText;
    Button button;
    String name = "news";
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

    void updateList() throws IOException {
        list.clear();
        Elements elements = document.select("div.Vpfmgd");
//        Elements elementsName = elements.select("div.WsMG1c.nnK0zc");
//        Elements elementsRating = document.select("div.pf5lIe");
//        Elements elementsAuthor = document.select("div.KoLSrc");
//        Elements elementsImage = elements.get(0).select(".T75of.QNCnCf").get(0).attr("data-src");


//        Log.d("updateList\n",
//                elements.get(0)
//                        + "");


//        Log.d("size", elements.size() + "");
        int count = 0;
        for (Element element: elements){
            int id = ++count;
//            String name = element.select(".WsMG1c.nnK0zc").text();
//            String author = element.select(".KoLSrc").get(1).text();
//            String rating = element.select(".pf5lIe div").attr("aria-label").replaceAll("[a-z]|[A-Z]", "").trim();
//            String image = element.select(".T75of.QNCnCf").get(0).attr("data-src");
            String url = String.format("https://play.google.com%s&hl=en_US&gl=US", element.select(".wXUyZd a").attr("href"));
//            Log.d("url", id + " " + url);
            new Thread(() -> {
                try {
//                    Log.d("count", id + " -> \t start...  " + url);
                    Document doc = Jsoup.connect(url).get();
//                    Log.d("count", id + " -> \t ...done   " + url);
                    list.add(new Product(id, doc));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
        while (list.size() < elements.size());
        list.sort(Comparator.comparingInt(Product::getId));
        listView.setAdapter(new CustomAdapter(this, R.layout.adapter_layout, list));
    }

    void set() throws IOException {
//        updateList();
    }

    void go() {
        button.setOnClickListener(v -> {
            button.setText("Loading");
            name = editText.getText().toString();
            url = "https://play.google.com/store/search?q=" + name + "&c=apps&hl=en_US&gl=US";
            try {
                getWebsite();
                updateList();
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
            button.setText("Done");
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
            TextView textView2 = findViewById(R.id.textView3);
            LinearLayout linearLayout = adapterLayout.findViewById(R.id.linearLayout);
            Product product = list.get(position);
//            button.setText(product.getName());
            textView.setText(product.getName());
            imageView.setImageDrawable(product.getImage());

            linearLayout.setOnClickListener(v -> {
                for (int i = 0; i < parent.getChildCount(); i++){
                    parent.getChildAt(i).setBackgroundColor(Color.WHITE);
                }
                v.setBackgroundColor(Color.GREEN);
                String author = product.getAuthor();
                if (author.length() > 35) author = author.substring(0, 32) + "...";
                textView1.setText("By: " + author);
                textView2.setText("Rating: " + product.getRating());
            });


            button.setOnClickListener(view -> {
                list.remove(position);
                notifyDataSetChanged();
            });

            return adapterLayout;
        }
    }
}

