package com.example.orientationandlistviewapp;

public class SliderPreview {

    private String url;
    private boolean isImage;

    SliderPreview(String url, boolean isImage){
        this.url = url;
        this.isImage = isImage;
    }

    public String getUrl() {
        return url;
    }

    public boolean isImage() {
        return isImage;
    }
}
