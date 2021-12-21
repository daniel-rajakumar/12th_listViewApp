package com.example.orientationandlistviewapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.Button;
import android.widget.EditText;
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
    TextView _textView;
    boolean isLandSpace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.savedInstanceState = savedInstanceState;

        isLandSpace = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

        try {
            if (savedInstanceState == null){
                ready();
//                set();
                go();
            } else {
                _ready();
//                _set();
                _go();

            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    void ready() throws IOException, InterruptedException {
        listView = findViewById(R.id.listView);
        list = new ArrayList<>();
        editText = findViewById(R.id.editTextTextPersonName3);
        button = findViewById(R.id.button3);

        if (isLandSpace) {
            _textView = findViewById(R.id.textView2);
        }

//        Log.d("list", list.toString());

        getWebsite();
    }

    void _ready() throws IOException, InterruptedException {
        listView = findViewById(R.id.listView);
        list = (ArrayList) savedInstanceState.get("list");
        editText = findViewById(R.id.editTextTextPersonName3);
        button = findViewById(R.id.button3);

        if (isLandSpace) {
            _textView = findViewById(R.id.textView2);
        }


//        Log.d("list", list.toString());

//        getWebsite();
//        updateList();

        listView.setAdapter(new CustomAdapter(this, this, R.layout.adapter_layout, list));

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
        int size = elements.size();
        while (list.size() < elements.size());
        list.sort(Comparator.comparingInt(Product::getId));
        listView.setAdapter(new CustomAdapter(this, this, R.layout.adapter_layout, list));
    }

    void _set() throws IOException {
        updateList();
    }


    void set() throws IOException {
        updateList();
    }


    void _go() {
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

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("list", (ArrayList<? extends Parcelable>) list);
    }
}

