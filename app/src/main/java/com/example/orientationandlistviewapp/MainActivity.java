package com.example.orientationandlistviewapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
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
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    ArrayList<Product> list;
    Document document;
    String name = "samsung";
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



//        Log.d("test", url);
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    document = Jsoup.connect(url).get();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });

    }

    private void getWebsite() throws InterruptedException {
        Thread thread = new Thread(() -> {
            final StringBuilder builder = new StringBuilder();
            Document doc = null;

            try {
                doc = Jsoup.connect(url).get();
                String title = doc.title();
                Elements links = doc.select("WsMbG1c");

                builder.append(title).append("\n");

                for (Element link : links) {
                    builder.append(link);
//                    builder.append("\n").append(" : ").append(link.attr("href"))
//                            .append("\n").append("Text : ").append(link.text());
                }
            } catch (IOException e) {
                builder.append("Error : ").append(e.getMessage()).append("\n");
            }

            document = doc;
//            runOnUiThread(() -> {
//                document = finalDoc;
//                Log.d("jsoup", document.title());
//            });
        });
        thread.start();

        while (thread.isAlive());

        Log.d("jsoup", thread.isAlive() + " -> " + document.title());
//        Thread.sleep(10000);
//
//        Log.d("jsoup", document.title());


    }








    void set(){
//        Log.d("jsoup", document));
//        Elements elements = document.select("div.WsMbG1c");
//
//        for (Element e: elements){
//            Log.d("jsoup", e.toString());
//        }

        for (int i = 0; i < 10; i++){
            list.add(new Product(i+"", i*i, R.drawable.ic_launcher_background));
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
            textView.setText(product.getPrice() + "");
            imageView.setImageResource(product.getImage());


            button.setOnClickListener(view -> {
                list.remove(position);
                Log.d("jsoup", document.title());
                notifyDataSetChanged();
            });

            return adapterLayout;
        }
    }
}

