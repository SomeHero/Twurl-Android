package com.example.twurl.model;

/**
 * Created by mikekunze on 8/2/15.
 */
public class Article {
    private String headline, url, headlineImageUrl, handle, profileImage, originalTweet, twitterUsername, createdAt;
    private int headlineImageWidth, headlineImageHeight, id, tweetId;

    public Article() {

    }

    public Article(String headline, String url, String headlineImageUrl, String handle, String profileImage, String originalTweet,
                   String twitterUsername, String createdAt, int headline_image_width, int headline_image_height, int id, int tweetId) {
        this.headline = headline;
        this.url = url;
        this.headlineImageUrl = headlineImageUrl;
        this.handle = handle;
        this.profileImage = profileImage;
        this.originalTweet = originalTweet;
        this.twitterUsername = twitterUsername;
        this.createdAt = createdAt;
        this.headlineImageHeight = headline_image_height;
        this.headlineImageWidth = headline_image_width;
        this.id = id;
        this.tweetId = tweetId;

    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHeadlineImageUrl() {
        return headlineImageUrl;
    }

    public void setHeadlineImageUrl(String headline_image_url) {
        this.headlineImageUrl = headline_image_url;
    }

    public int getHeadlineImageWidth() {
        return headlineImageWidth;
    }

    public void setHeadlineImageWidth(int headlineImageWidth) {
        this.headlineImageWidth = headlineImageWidth;
    }

    public int getHeadlineImageHeight() {
        return headlineImageHeight;
    }

    public void setHeadlineImageHeight(int headlineImageHeight) {
        this.headlineImageHeight = headlineImageHeight;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTweetId() {
        return tweetId;
    }

    public void setTweetId(int tweetId) {
        this.tweetId = tweetId;
    }

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public String getOriginalTweet() {
        return originalTweet;
    }

    public void setOriginalTweet(String originalTweet) {
        this.originalTweet = originalTweet;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getTwitterUsername() {
        return twitterUsername;
    }

    public void setTwitterUsername(String twitterUsername) {
        this.twitterUsername = twitterUsername;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
