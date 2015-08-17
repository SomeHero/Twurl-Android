package com.example.twurl;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.twurl.app.AppController;
import com.example.twurl.model.Article;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class ArticleListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Article> articleItems;
    private int imageWidth;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public ArticleListAdapter(Activity activity, List<Article> articleItems, int imageWidth) {
        this.activity = activity;
        this.articleItems = articleItems;
        this.imageWidth = imageWidth;
    }

    @Override
    public int getCount() {
        return articleItems.size();
    }

    @Override
    public Object getItem(int position) {
        return articleItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null) {
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.article_item, null);
        }

        if (imageLoader == null) {
            imageLoader = AppController.getInstance().getImageLoader();
        }

        NetworkImageView articleImage = (NetworkImageView) convertView.findViewById(R.id.articleImage);
        articleImage.setMaxWidth(imageWidth);
        Log.v("getView","setting max width to:" + imageWidth);
        TextView headline = (TextView) convertView.findViewById(R.id.articleHeadline);
        NetworkImageView tweetIcon = (NetworkImageView) convertView.findViewById(R.id.tweetIcon);
        TextView twitterUser = (TextView) convertView.findViewById(R.id.twitterUserHandle);
        TextView tweetText = (TextView) convertView.findViewById(R.id.twitterText);
        TextView tweetTime = (TextView) convertView.findViewById(R.id.tweetTime);

        final Article mArticle = articleItems.get(position);
        articleImage.setImageUrl(mArticle.getHeadlineImageUrl(), imageLoader);
        headline.setText(mArticle.getHeadline());
        try {
            tweetIcon.setImageUrl(mArticle.getProfileImage(), imageLoader);
        } catch (Exception e) {
            Log.e("chatAdapter","error: " + e.getMessage());
            Toast networkToast = Toast.makeText(activity, activity.getString(R.string.network_failed), Toast.LENGTH_SHORT);
            networkToast.show();
        }
        twitterUser.setText(mArticle.getTwitterUsername() + ' ' + mArticle.getHandle());
        tweetText.setText(mArticle.getOriginalTweet());
        tweetTime.setText(getTimeDifference(mArticle.getCreatedAt()));
        return convertView;
    }

    public static Date parseTwitterDate(String dateString)
    {
        final String TWITTER = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        SimpleDateFormat sf = new SimpleDateFormat(TWITTER, Locale.ENGLISH);
        sf.setLenient(true);
        Date date = null;

        try {
            date = sf.parse(dateString);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date;
    }

    public String getTimeDifference(String dateString) {
        long twitterDate = parseTwitterDate(dateString).getTime();
        long currentDate = System.currentTimeMillis();
        long diffInMils = currentDate - twitterDate;
        long diffInSecs = TimeUnit.MILLISECONDS.toSeconds(diffInMils);
        long mins = TimeUnit.MILLISECONDS.toMinutes(diffInMils);
        long hours = TimeUnit.MILLISECONDS.toHours(diffInMils);
        long days = TimeUnit.MILLISECONDS.toDays(diffInMils);

        if (diffInSecs < 5) return "Just Now";
        else if (diffInSecs < 60) return diffInSecs + " seconds ago";
        else if (diffInSecs < 120) return "A minute ago";
        else if (mins < 60) return mins + " minutes ago";
        else if (mins < 120) return "An hour ago";
        else if (hours < 24) return hours + " hours ago";
        else if (hours < 48) return "Yesterday";
        else if (days < 7) return days + " days ago";
        else if (days < 14) return "Last week";
        else if (days < 31) return (days / 7) + " weeks ago";
        else if (days < 62) return "Last month";
        else if (days < 365.25) return (days / 31) + " months ago";
        else if (days < 731) return "Last year";
        else return (days / 365) + " years ago";
    }

}