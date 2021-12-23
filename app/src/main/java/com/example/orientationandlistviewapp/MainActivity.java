package com.example.orientationandlistviewapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    ArrayList list;
    Document document;
    EditText editText;
    Button button;
    String name = "news";
    String url = "https://play.google.com/store/search?q=" + name + "&c=apps&hl=en_US&gl=US";
    Bundle savedInstanceState;
    boolean isLandSpace;
    TextView textView_by, textView_rating, textView_info;
    int clickedItemID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.savedInstanceState = savedInstanceState;

        isLandSpace = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

        try {
            ready();
            go();
            setListeners();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }


    void ready() throws IOException, InterruptedException {
        listView = findViewById(R.id.listView);
        if (savedInstanceState == null){
            list = new ArrayList<>();
        } else {
            list = (ArrayList) savedInstanceState.get("list");
        }
        editText = findViewById(R.id.editTextTextPersonName3);
        button = findViewById(R.id.button3);
        textView_rating = findViewById(R.id.textView6);
        textView_by = findViewById(R.id.textView3);

        if (isLandSpace) {
            textView_info = findViewById(R.id.textView2);
        }

        if (savedInstanceState == null){
            getWebsite();

        } else {
            textView_by.setText(savedInstanceState.get("by").toString());
            textView_rating.setText(savedInstanceState.get("rating").toString());
            listView.setAdapter(new CustomAdapter(this, this, R.layout.adapter_layout, list));
        }

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
        int count = 0;
        for (Element element: elements){
            int id = ++count;
            String url = String.format("https://play.google.com%s&hl=en_US&gl=US", element.select(".wXUyZd a").attr("href"));
            new Thread(() -> {
                try {
                    Document doc = Jsoup.connect(url).get();
                    list.add(new Product(id, doc));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
        while (list.size() < elements.size());
        list.sort(Comparator.comparingInt(Product::getId));
        listView.setAdapter(new CustomAdapter(this, this, R.layout.adapter_layout, list));
    }


    void setListeners() throws IOException {
        if (savedInstanceState != null && isLandSpace){
            Product product = (Product) list.get((Integer) savedInstanceState.get("position"));
            textView_info.setMovementMethod(new ScrollingMovementMethod());
            textView_info.setText(Html.fromHtml(product.getInfo(), Html.FROM_HTML_MODE_COMPACT));
        }

        listView.setOnItemClickListener((parent, view, position, id) -> {
            clickedItemID = position;
            Product product = (Product) list.get(position);
            String author = product.getAuthor();

            if (author.length() > 35) author = author.substring(0, 32) + "...";

            TextView textView1 = findViewById(R.id.textView6);
            TextView textView2 = findViewById(R.id.textView3);

            textView1.setText("By: " + author);
            textView2.setText(product.getRating() + " :Rating");

            if (isLandSpace) {
                textView_info.setMovementMethod(new ScrollingMovementMethod());
                textView_info.setText(Html.fromHtml(product.getInfo(), Html.FROM_HTML_MODE_COMPACT));
            }
        });
    }





    void resetVariables(){
        textView_by.setText("By: ");
        textView_rating.setText(": Rating");
        textView_info.setText("");
    }



    void go() {
        button.setOnClickListener(v -> {
            name = editText.getText().toString();
            url = "https://play.google.com/store/search?q=" + name + "&c=apps&hl=en_US&gl=US";
            try {
                getWebsite();
                updateList();
                resetVariables();
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("list", (ArrayList<? extends Parcelable>) list);
        outState.putString("by",  textView_by.getText().toString());
        outState.putString("rating",  textView_rating.getText().toString());
        outState.putInt("position", clickedItemID);
    }
}

