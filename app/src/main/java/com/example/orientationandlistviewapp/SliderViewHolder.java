package com.example.orientationandlistviewapp;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class SliderViewHolder extends RecyclerView.ViewHolder {
    private final ImageView imageView;
    private final YouTubePlayerView videoView;

    SliderViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.imageSlide);
        videoView = itemView.findViewById(R.id.videoSlide);
    }

    void setPreview(SliderPreview sliderPreview) throws IOException {
        if (sliderPreview.isImage()){
            String imageUrl = sliderPreview.getUrl();
            InputStream inputStream = (InputStream) (new URL(imageUrl).getContent());
            imageView.setImageDrawable(Drawable.createFromStream(inputStream, null));
            
        } else {
            videoView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                @Override
                public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                    String videoId = sliderPreview.getUrl().substring(30, 41);
                    youTubePlayer.cueVideo(videoId, 0);
                }
            });
        }
    }
}
