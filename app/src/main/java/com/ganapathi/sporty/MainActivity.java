package com.ganapathi.sporty;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    TextView title, titledesc;
    ImageView infopic;
    YouTubePlayerView youTubePlayerView;
    SwipeRefreshLayout swipeContainer;
    String videoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        youTubePlayerView = findViewById(R.id.youtube_player_view);
        getLifecycle().addObserver(youTubePlayerView);

        title = findViewById(R.id.title);
        titledesc = findViewById(R.id.titledesc);
        infopic = findViewById(R.id.infopic);

        swipeContainer = findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDetailsPage();
            }
        });


        getDetailsPage();

    }

    public void getDetailsPage(){
        swipeContainer.setRefreshing(true);
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(MainActivity.this, MainActivity.this.getResources().getString(R.string.hitCommonURL), null, new JsonHttpResponseHandler() {

            @Override
            public void onStart() {}

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                swipeContainer.setRefreshing(false);
                try {
                    title.setText(response.getString("title"));
                    titledesc.setText(response.getString("description"));
                    if(response.has("illustrations")) {
                        JSONArray details = response.getJSONArray("illustrations");
                        if(details.length() > 0){
                            JSONObject data = details.getJSONObject(0);
                            Picasso.get().load(data.getString("imageUrl")).into(new Target() {
                                @Override
                                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                    infopic.setImageBitmap(bitmap);
                                }
                                @Override
                                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                                }
                                @Override
                                public void onPrepareLoad(Drawable placeHolderDrawable) {
                                }
                            });
                        }
                    }
                    if(response.has("equipments")) {
                        JSONArray details = response.getJSONArray("equipments");
                        if(details.length() > 0) {
                            JSONObject data = details.getJSONObject(0);

                        }
                    }
                    videoId = response.getJSONObject("video").getString("url");
                    youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                        @Override
                        public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                            videoId = videoId.replace("https://www.youtube.com/watch?v=", "");
                            youTubePlayer.loadVideo(videoId, 0);
                        }
                    });

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), ""+ e, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                swipeContainer.setRefreshing(false);
                Toast.makeText(getApplicationContext(),statusCode + "/" + throwable, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                swipeContainer.setRefreshing(false);
                Toast.makeText(getApplicationContext(),statusCode + "/" + throwable, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                swipeContainer.setRefreshing(false);
                Toast.makeText(getApplicationContext(),statusCode + "/" + throwable, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void toolBackOnclick(View view){
        finish();
    }
}
