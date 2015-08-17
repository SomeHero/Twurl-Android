package com.example.twurl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;


public class SplashActivity extends Activity {

    private final int SPLASH_DISPLAY_LENGTH = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startFeedActivity();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    public void startFeedActivity() {
        Intent mainIntent = new Intent(this, FeedActivity.class);
        startActivity(mainIntent);
        finish();
    }

}
