package com.example.orientationandlistviewapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {
    ListView listView, listView2;
    ArrayList<Product> list;
    Document document;
    EditText editText;
    Button button;
    String name = "news";
    String url = "https://play.google.com/store/search?q=" + name + "&c=apps&hl=en_US&gl=US";
    Bundle savedInstanceState;
    boolean isLandSpace;
    TextView textView_by, textView_rating, textView_info;
    int clickedItemID;
    Product selectedProduct;
    Switch toggleSwitch;
    ViewSwitcher viewSwitcher;
    ViewPager2 viewPager2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.savedInstanceState = savedInstanceState;


        Toolbar toolBar = findViewById(R.id.toolBar);
        setSupportActionBar(toolBar);






        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


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
            list = (ArrayList<Product>) savedInstanceState.get("list");
            selectedProduct = (Product) savedInstanceState.get("product");
        }
        editText = findViewById(R.id.editTextTextPersonName3);
        button = findViewById(R.id.button3);
        textView_rating = findViewById(R.id.textView3);
        textView_by = findViewById(R.id.textView6);

        if (isLandSpace) {
            textView_info = findViewById(R.id.textView2);
        }

        if (savedInstanceState == null){
            getWebsite();

        } else {
            if (selectedProduct != null){
                textView_by.setText(selectedProduct.getAuthor());
                textView_rating.setText(selectedProduct.getRating());
            }
//            Log.d("selected", selectedProduct + "");
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
//            if (count == 2) break;

            String url = String.format("https://play.google.com%s&hl=en_US&gl=US", element.select(".wXUyZd a").attr("href"));
            new Thread(() -> {
                try {
                    Document doc = Jsoup.connect(url).get();
                    Document doc2 = Jsoup.connect(url + "&showAllReviews=true").get();
                    list.add(new Product(id, doc, doc2));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

        }
        while (list.size() < elements.size());
//        while (list.size() < 1);

        list.sort(Comparator.comparingInt(Product::getId));
        listView.setAdapter(new CustomAdapter(this, this, R.layout.adapter_layout, list));

//        Button button2 = findViewById(R.id.button);
//        button2.setOnClickListener(v -> {
//            resetVariables();
//        });

    }


    void setToggleButton(){
        toggleSwitch = findViewById(R.id.switch2);
        viewSwitcher = findViewById(R.id.viewSwitcher);
        viewPager2 =  findViewById(R.id.viewPager);


        toggleSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                viewSwitcher.showNext();
            } else {
                viewSwitcher.showPrevious();
            }
        });

    }





    void setListeners() throws IOException {
        if (savedInstanceState != null && isLandSpace && (selectedProduct != null)){
            textView_info.setMovementMethod(new ScrollingMovementMethod());
            textView_info.setText(Html.fromHtml(selectedProduct.getInfo(), Html.FROM_HTML_MODE_COMPACT));
            viewPager2.setAdapter(new SliderAdapter(selectedProduct.getSliderPreview(), viewPager2));
        }

        listView.setOnItemClickListener((parent, view, position, id) -> {
            clickedItemID = position;
            selectedProduct = list.get(position);
            String author = selectedProduct.getAuthor();

            if (author.length() > 35) author = author.substring(0, 32) + "...";

            TextView textView1 = findViewById(R.id.textView6);
            TextView textView2 = findViewById(R.id.textView3);

            textView1.setText("By: " + author);
            textView2.setText(selectedProduct.getRating() + " :Rating");


            if (isLandSpace) {
                textView_info.setMovementMethod(new ScrollingMovementMethod());
                textView_info.setText(Html.fromHtml(selectedProduct.getInfo(), Html.FROM_HTML_MODE_COMPACT));
                viewPager2.setAdapter(new SliderAdapter(selectedProduct.getSliderPreview(), viewPager2));
            }
        });
    }




    void resetVariables(){
        textView_by.setText("By: ");
        textView_rating.setText(": Rating");
        if (isLandSpace){
            textView_info.setText("");
            viewPager2.setAdapter(null);
        }
        selectedProduct = null;
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

        if (isLandSpace) {
            setToggleButton();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("list", list);
        outState.putInt("position", clickedItemID);
        outState.putParcelable("product", selectedProduct);
    }

}

