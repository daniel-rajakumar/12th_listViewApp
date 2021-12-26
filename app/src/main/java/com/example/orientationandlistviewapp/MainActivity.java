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
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ViewFlipper;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity {
    private ListView listView_app;
    private ArrayList<Product> list_product;
    private Document document;
    private SearchView searchView;
    private Bundle savedInstanceState;
    private boolean isLandSpace, hasRotated;
    private float percentage = 0;
    private int numAppsToShow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.savedInstanceState = savedInstanceState;

        updatePermissions();

        isLandSpace = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        hasRotated = savedInstanceState != null;

        try {
            ready();
            go();
            setListeners();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    void updatePermissions(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }


    void ready() throws IOException, InterruptedException {
        listView_app = findViewById(R.id.listView_app);
        if (hasRotated){
            list_product = (ArrayList<Product>) savedInstanceState.get("list");
        } else {
            list_product = new ArrayList<>();
        }

        modifySearchBar();

        if (hasRotated) {
            listView_app.setAdapter(new CustomAdapterApp(this, this, R.layout.adapter_layout, list_product));
        }
    }

    void modifySearchBar(){
        searchView = findViewById(R.id.searchView);
        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        ImageView searchIcon = searchView.findViewById(androidx.appcompat.R.id.search_button);
        ImageView closeIcon = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);

        searchEditText.setTextColor(ContextCompat.getColor(this, R.color.main_font));
        searchEditText.setHintTextColor(ContextCompat.getColor(this, R.color.main_font));
        searchEditText.setTextCursorDrawable(null);
        searchEditText.setHintTextColor(ContextCompat.getColor(this, R.color.main_font_tint));
        searchEditText.setImeOptions(EditorInfo.IME_ACTION_GO);
        searchIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_search_24));
        closeIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_round_close_24));
    }


    private void connectWithPlayStoreAppLists(String appName) throws InterruptedException {
        Thread thread = new Thread(() -> {
            try {
                String url = "https://play.google.com/store/search?q=" + appName + "&c=apps&hl=en_US&gl=US";
                document = Jsoup.connect(url).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();
        while (thread.isAlive()) ;
    }

    void updateAppListView() throws IOException {
        list_product.clear();
        percentage = 0;
        Elements elements = document.select("div.Vpfmgd");
        numAppsToShow = elements.size();
        int count = 0;
        for (Element element: elements){
            int id = ++count;
            String url = "https://play.google.com" + element.select(".wXUyZd a").attr("href") + "&hl=en_US&gl=US";

            new Thread(() -> {
                try {
                    Document appPage = Jsoup.connect(url).get();
                    percentage++;
                    Document appFullReviewPage = Jsoup.connect(url + "&showAllReviews=true").get();
                    percentage++;
                    list_product.add(new Product(id, appPage, appFullReviewPage));

                } catch (HttpStatusException e) {
                    numAppsToShow--;

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }

        new Thread(() -> {
            Button button  = findViewById(R.id.button_search);
            while (list_product.size() < numAppsToShow) {
                runOnUiThread(() -> {
                    float percent = (percentage/(elements.size() * 2) * 100);
                    if (percent == 0){
                        button.setText("Searching...");
                    } else {
                        button.setText(percent + "%");
                    }
                });
            }
            runOnUiThread(() -> {
                list_product.sort(Comparator.comparingInt(Product::getId));
                listView_app.setAdapter(new CustomAdapterApp(this, this, R.layout.adapter_layout, list_product));
                button.setText("Search");
            });
        }).start();
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

        RadioButton button = radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());
        for (int i = 0; i < radioGroup.getChildCount(); i++){
            button.clearAnimation();
        }
        button.startAnimation(AnimationUtils.loadAnimation(this, R.anim.bounce_radio_button));

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
        if (hasRotated){
            updateSelectedAppInfo(this, list_product);
        }

        listView_app.setOnItemClickListener((parent, view, position, id) -> {
            if (Product.getSelectedIndex() != -1){
                getViewByPosition(Product.getSelectedIndex(), listView_app).setBackgroundResource(R.drawable.custom_applist);
            }
            if (position == Product.getSelectedIndex()) {
                Product.setSelectedIndex(-1);
                view.clearAnimation();
            } else {
                Product.setSelectedIndex(position);
                view.setBackgroundResource(R.drawable.custom_applist_selected);
            }

            updateSelectedAppInfo(this, list_product);
        });
    }




    public static void updateSelectedAppInfo(MainActivity mainActivity, ArrayList<Product> list){
        TextView textView_info = mainActivity.findViewById(R.id.textView_info);
        TextView textView_rating = mainActivity.findViewById(R.id.textView_rating);
        TextView textView_by = mainActivity.findViewById(R.id.textView_by);
        ViewPager2 viewPager = mainActivity.findViewById(R.id.viewPager);
        ListView listView_comments = mainActivity.findViewById(R.id.listview_comment);

        if (Product.getSelectedIndex() == -1)  {
            textView_by.setText("By: ");
            textView_rating.setText(" :Rating");
            if (mainActivity.isLandSpace){
                textView_info.setText("");
                viewPager.setAdapter(null);
                listView_comments.setAdapter(null);
            }
            return;
        }
        if (list.isEmpty()) return;


        Product selectedProduct = list.get(Product.getSelectedIndex());
        View selectedView = mainActivity.getViewByPosition(Product.getSelectedIndex(), mainActivity.listView_app);
        String author = selectedProduct.getAuthor();

        if (author.length() > 35) author = author.substring(0, 32) + "...";
        textView_by.setText("By: " + author);
        textView_rating.setText(selectedProduct.getRating() + " :Rating");

        if (mainActivity.isLandSpace) {
            textView_info.setMovementMethod(new ScrollingMovementMethod());
            textView_info.setText(Html.fromHtml(selectedProduct.getInfo(), Html.FROM_HTML_MODE_COMPACT));
            viewPager.setAdapter(new SliderAdapter(selectedProduct.getSliderPreview()));
            listView_comments.setAdapter(new CustomAdapterReviews(mainActivity, mainActivity, R.layout.adapter_layout_comments, selectedProduct.getUserReviews()));
        }

        selectedView.setAnimation(AnimationUtils.loadAnimation(mainActivity, R.anim.bounce_app_list));
    }



    public View getViewByPosition(int position, ListView listView) {
        int firstListItemPosition = listView.getFirstVisiblePosition();
        int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (position < firstListItemPosition || position > lastListItemPosition) {
            return listView.getAdapter().getView(position, null, listView);
        } else {
            int childIndex = position - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }




    void go() {
        Button button = findViewById(R.id.button_search);
        button.setOnClickListener(v -> {
            String appName = searchView.getQuery().toString();
            try {
                listView_app.setAdapter(null);
                connectWithPlayStoreAppLists(appName);
                updateAppListView();
                Product.setSelectedIndex(-1);
                updateSelectedAppInfo(this, list_product);
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
        outState.putParcelableArrayList("list", list_product);
    }
}

