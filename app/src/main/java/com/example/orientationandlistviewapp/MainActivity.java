package com.example.orientationandlistviewapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ViewFlipper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

//TODO: fix the bug that happens when you remove the last element while an item is selected.

public class MainActivity extends AppCompatActivity {
    ListView listView;
    ArrayList<Product> list;
    Document document;
    SearchView editText;
    Button button;
    String name = "news";
    String url = "https://play.google.com/store/search?q=" + name + "&c=apps&hl=en_US&gl=US";
    Bundle savedInstanceState;
    boolean isLandSpace;
    RadioGroup radioGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.savedInstanceState = savedInstanceState;

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
        }
        editText = findViewById(R.id.editTextTextPersonName3);
        button = findViewById(R.id.button3);

        modifySearchBar();

        if (savedInstanceState == null){
            getWebsite();

        } else {
            listView.setAdapter(new CustomAdapter(this, this, R.layout.adapter_layout, list));
        }

    }

    void modifySearchBar(){
        EditText searchEditText = editText.findViewById(androidx.appcompat.R.id.search_src_text);
        ImageView searchIcon = editText.findViewById(androidx.appcompat.R.id.search_button);
        ImageView closeIcon = editText.findViewById(androidx.appcompat.R.id.search_close_btn);

        searchEditText.setTextColor(getResources().getColor(R.color.main_font));
        searchEditText.setHintTextColor(getResources().getColor(R.color.main_font));
        searchEditText.setTextCursorDrawable(null);
        searchEditText.setHintTextColor(getResources().getColor(R.color.main_font_tint));
        searchEditText.setImeOptions(EditorInfo.IME_ACTION_GO);

        searchIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_search_24));
        closeIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_round_close_24));
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
                    list.add(new Product(this, this, id, doc, doc2));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

        }
        while (list.size() < elements.size());
//        while (list.size() < 1);

        list.sort(Comparator.comparingInt(Product::getId));
        listView.setAdapter(new CustomAdapter(this, this, R.layout.adapter_layout, list));
    }


    void setToggleButton(){
        RadioGroup radioGroup = findViewById(R.id.radioGroup);

        updateRadioGroup(radioGroup);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            updateRadioGroup(radioGroup);
        });
    }

    void updateRadioGroup(RadioGroup radioGroup){
        ViewFlipper viewFlipper = findViewById(R.id.viewSwitcher);

        viewFlipper.setDisplayedChild(
                radioGroup.indexOfChild(
                        findViewById(
                            radioGroup.getCheckedRadioButtonId()
                        )
                )
        );

        for (int i = 0; i < radioGroup.getChildCount(); i++){
            radioGroup.getChildAt(i).setBackgroundTintList(this.getColorStateList(R.color.main_font_tint));
        }

        radioGroup.findViewById(radioGroup.getCheckedRadioButtonId()).setBackgroundTintList(this.getColorStateList(R.color.main_font));
    }





    void setListeners() throws IOException {
//        if (savedInstanceState != null && isLandSpace && (selectedProduct != null)){
//            textView_info.setMovementMethod(new ScrollingMovementMethod());
//            textView_info.setText(Html.fromHtml(selectedProduct.getInfo(), Html.FROM_HTML_MODE_COMPACT));
//            viewPager2.setAdapter(new SliderAdapter(selectedProduct.getSliderPreview(), viewPager2));
//        }
        if (savedInstanceState != null){
            updateSelectedAppInfo(this, list);
        }

        listView.setOnItemClickListener((parent, view, position, id) -> {
            if (Product.getSelectedIndex() != -1){
                getViewByPosition(Product.getSelectedIndex(), listView) .setBackground(getResources().getDrawable(R.drawable.custom_applist));
            }
            Product.setSelectedIndex(position);
            view.setBackground(getResources().getDrawable(R.drawable.custom_applist_selected));
            updateSelectedAppInfo(this, list);
        });
    }




    public static void updateSelectedAppInfo(MainActivity mainActivity, ArrayList<Product> list){
        TextView textView_info = mainActivity.findViewById(R.id.textView2);
        TextView textView_rating = mainActivity.findViewById(R.id.textView3);
        TextView textView_by = mainActivity.findViewById(R.id.textView6);
        ViewPager2 viewPager2 = mainActivity.findViewById(R.id.viewPager);
        ListView listView_comments = mainActivity.findViewById(R.id.listview_comment);

        if (Product.getSelectedIndex() == -1)  {
            textView_by.setText("By: ");
            textView_rating.setText(": Rating");
            if (mainActivity.isLandSpace){
                textView_info.setText("");
                viewPager2.setAdapter(null);
                listView_comments.setAdapter(null);
            }
            return;
        }

        Product selectedProduct = list.get(Product.getSelectedIndex());

        String author = selectedProduct.getAuthor();
        if (author.length() > 35) author = author.substring(0, 32) + "...";
        textView_by.setText("By: " + author);
        textView_rating.setText(selectedProduct.getRating() + " :Rating");


        if (mainActivity.isLandSpace) {
            textView_info.setMovementMethod(new ScrollingMovementMethod());
            textView_info.setText(Html.fromHtml(selectedProduct.getInfo(), Html.FROM_HTML_MODE_COMPACT));
            viewPager2.setAdapter(new SliderAdapter(selectedProduct.getSliderPreview(), viewPager2));
            listView_comments.setAdapter(new CustomAdapter_customer(mainActivity, mainActivity, R.layout.adapter_layout_comments, selectedProduct.getUserReviews()));
        }
    }






    public View getViewByPosition(int position, ListView listView) {
        int firstListItemPosition = listView.getFirstVisiblePosition();
        int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (position < firstListItemPosition || position > lastListItemPosition ) {
            return listView.getAdapter().getView(position, null, listView);
        } else {
            int childIndex = position - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    void go() {
        button.setOnClickListener(v -> {
            name = editText.getQuery().toString();
            url = "https://play.google.com/store/search?q=" + name + "&c=apps&hl=en_US&gl=US";
            try {
                getWebsite();
                updateList();
                Product.setSelectedIndex(-1);
                updateSelectedAppInfo(this, list);
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
    }
}

