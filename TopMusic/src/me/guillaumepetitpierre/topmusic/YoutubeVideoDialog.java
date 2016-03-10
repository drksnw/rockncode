package me.guillaumepetitpierre.topmusic;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.google.android.youtube.player.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by darksnow on 2/8/16.
 */
public class YoutubeVideoDialog extends AppCompatActivity implements
        YouTubePlayer.OnInitializedListener{
    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        youTubePlayer.loadVideo(video);
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

    }

    private final String API_KEY = "AIzaSyDGH5bVNodScEQAIbAlo_xSnYMx9-bSJ5s";
    private String video = "dQw4w9WgXcQ";


    public Drawable drawableFromUrl(String url) throws IOException {
        Bitmap x;

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.connect();
        InputStream input = connection.getInputStream();

        x = BitmapFactory.decodeStream(input);
        return new BitmapDrawable(x);
    }

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        setContentView(R.layout.dialog_video);

        try{
            getSupportActionBar().setBackgroundDrawable(drawableFromUrl(intent.getStringExtra("banner")));
        } catch(Exception e){
            e.printStackTrace();
        }
        String yturl = intent.getStringExtra("yturl");
        String[] yolo = yturl.split("=");
        video = yolo[1];
        String artistNsong = "Artiste : "+intent.getStringExtra("artist")+"\nChanson : "+intent.getStringExtra("title");
        TextView ans = (TextView)findViewById(R.id.artistNsong);

        ans.setText(artistNsong);

        YouTubePlayerFragment youTubePlayerFragment = (YouTubePlayerFragment)getFragmentManager()
                .findFragmentById(R.id.youtubeplayerfragment);
        youTubePlayerFragment.initialize(API_KEY, this);


    }
}
