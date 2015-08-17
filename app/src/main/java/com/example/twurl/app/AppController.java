package com.example.twurl.app;

/**
 * Created by mikekunze on 8/2/15.
 */

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.example.twurl.R;
import com.example.twurl.model.Article;
import com.example.twurl.util.LruBitmapCache;
import com.facebook.FacebookSdk;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import io.fabric.sdk.android.Fabric;


public class AppController extends Application {

    public static final String TAG = AppController.class.getSimpleName();

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private Article article;

    public static GoogleAnalytics analytics;
    public static Tracker tracker;

    private static AppController mInstance;

    public MixpanelAPI mixpanel;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        FacebookSdk.sdkInitialize(getApplicationContext());

        TwitterAuthConfig authConfig = new TwitterAuthConfig(getString(R.string.TWITTER_KEY), getString(R.string.TWITTER_SECRET));
        Fabric.with(this, new Twitter(authConfig));

        mixpanel = MixpanelAPI.getInstance(this, getString(R.string.mix_panel_project_token));
    }

    public AppController() {
        super();
    }

    public synchronized Tracker getTracker() {
        analytics = GoogleAnalytics.getInstance(this);
        analytics.setLocalDispatchPeriod(1800);

        tracker = analytics.newTracker(getString(R.string.google_analytics_tracker_id)); // Replace with actual tracker/property Id
        tracker.enableExceptionReporting(true);
        tracker.enableAdvertisingIdCollection(true);
        tracker.enableAutoActivityTracking(true);
        return tracker;
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            Log.v("getImageLoader", "creating ImageLoader");
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new LruBitmapCache());
        }
        return this.mImageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}