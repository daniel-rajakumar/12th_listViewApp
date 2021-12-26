package com.example.orientationandlistviewapp;

public class SliderPreview {

    private final String url;
    private final boolean isImage;

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
