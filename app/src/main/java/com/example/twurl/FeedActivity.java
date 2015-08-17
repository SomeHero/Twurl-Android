package com.example.twurl;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.twurl.app.AppController;
import com.example.twurl.model.Article;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class FeedActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    //public MixpanelAPI mixpanel;


    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_feed);
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onNavigationDrawerItemSelected(String category, int categoryID) {
        Log.v("onNav", "category: " + category + " categoryID: " + categoryID);
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(category, categoryID))
                .commit();
    }

    public void onSectionAttached(String category) {
        mTitle = category;
    }


    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        /*actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);*/
        TextView textView = (TextView) findViewById(R.id.categoryTitle);
        if (textView != null) {
            textView.setText(mTitle);
        } else {
            Log.v("restoreActionBar", "htat shit be null");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            //getMenuInflater().inflate(R.menu.feed, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_CATEGORY = "section_number";

        private ListView listView;
        private ArticleListAdapter articleListAdapter;
        private List<Article> articles = new ArrayList<Article>();
        private static final String ARG_CATEGORY_ID = "category_id";
        private int categoryID;
        private String category;
        private int lastTweet;
        private ProgressDialog progressDialog;
        private SwipeRefreshLayout swipeLayout;

        Parcelable listviewState;
        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(String category, int categoryID) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putString(ARG_CATEGORY, category);
            fragment.setArguments(args);
            fragment.categoryID = categoryID;
            fragment.category = category;
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_feed, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((FeedActivity) activity).onSectionAttached(
                    getArguments().getString(ARG_CATEGORY));
        }

        @Override
        public void onResume() {
            super.onResume();
            initArticleListView();
            initSwipeToRefresh();
        }

        public void initSwipeToRefresh() {
            swipeLayout = (SwipeRefreshLayout) getView().findViewById(R.id.swipeLayout);
            swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    swipeToRefresh();
                }
            });
        }

        public void swipeToRefresh() {
            RequestQueue queue = Volley.newRequestQueue(getActivity());
            String url ="http://www.twurl.net/api/v1/twurls";
            Article firstArticle = articles.get(0);
            final int firstArticleId = firstArticle.getId();
            if (categoryID != 26) {
                url += "?category_id=" + categoryID;
            }
            JsonArrayRequest articleReq = new JsonArrayRequest(url,
                    new Response.Listener<JSONArray>() {


                        @Override
                        public void onResponse(JSONArray response) {
                            Log.v("onResponseSwipe", "response: " + response.toString());

                            for (int i = response.length(); i > 0; i --) {
                                try {
                                    JSONObject jsonObject = response.getJSONObject(i - 1);
                                    if (jsonObject.getInt("id") > firstArticleId) {
                                        Log.v("swipeToRefresh","adding article");
                                        Article article = new Article();
                                        article.setHeadline(jsonObject.getString("headline"));
                                        article.setHeadlineImageUrl(jsonObject.getString("headline_image_url"));
                                        article.setUrl(jsonObject.getString("url"));
                                        article.setHeadlineImageHeight(jsonObject.getInt("headline_image_height"));
                                        article.setHeadlineImageWidth(jsonObject.getInt("headline_image_width"));
                                        article.setId(jsonObject.getInt("id"));
                                        JSONObject influencerObject = jsonObject.getJSONObject("influencer");
                                        article.setHandle(influencerObject.getString("handle"));
                                        article.setOriginalTweet(jsonObject.getString("original_tweet"));
                                        article.setCreatedAt(jsonObject.getString("created_at"));
                                        article.setProfileImage(influencerObject.getString("profile_image_url"));
                                        article.setTwitterUsername(influencerObject.getString("twitter_username"));
                                        articles.add(0, article);
                                    } else {
                                        Log.v("swipeToRefresh","already seen tweet! id = " + jsonObject.getInt("id") + " " + firstArticleId);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            swipeLayout.setRefreshing(false);
                            articleListAdapter.notifyDataSetChanged();
                            listView.smoothScrollToPosition(0);
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.v("onErrorResponse", "Error: " + error.getMessage());
                    swipeLayout.setRefreshing(false);
                }
            });

            AppController.getInstance().addToRequestQueue(articleReq);
        }

        @Override
        public void onPause() {
            super.onPause();
            listviewState = listView.onSaveInstanceState();
        }

        public int getDisplayWidth() {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            return displayMetrics.widthPixels;
        }

        public void initArticleListView() {
            listView = (ListView) getView().findViewById(R.id.articleList);
            articleListAdapter = new ArticleListAdapter(getActivity(), articles, getDisplayWidth());
            listView.setAdapter(articleListAdapter);
            if (listviewState != null) {
                listView.onRestoreInstanceState(listviewState);
            }
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent showArticleIntent = new Intent(getActivity(), ViewArticle.class);
                    Article article = articles.get(position);
                    viewArticleEvent(article);
                    showArticleIntent.putExtra("headline", article.getHeadline());
                    showArticleIntent.putExtra("url", article.getUrl());
                    showArticleIntent.putExtra("imageUrl", article.getHeadlineImageUrl());
                    startActivity(showArticleIntent);
                    getActivity().overridePendingTransition(R.transition.pull_in_from_bottom, R.transition.putsh_out_top);
                }
            });
            listView.setOnScrollListener(new EndlessScrollListener() {
                @Override
                public void onLoadMore(int page, int totalItemsCount) {
                    Log.v("onLoadMore", "getting called with id of item: " + totalItemsCount);
                    getArticles(articles.get(totalItemsCount - 1).getId());
                }
            });
            getArticles(-1);
        }

        private void viewArticleEvent(Article article) {
            Tracker t = ((AppController) getActivity().getApplication()).getTracker();

            t.send(new HitBuilders.EventBuilder()
                    .setCategory(getString(R.string.article_category_id))
                    .setAction(article.getUrl())
                    .setLabel(getString(R.string.article_label_id))
                    .build());

        }

        private void hideProgressDialog() {
            if (progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null;
            }
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            hideProgressDialog();
        }

        public void getArticles(int lastTwurlId) {
            RequestQueue queue = Volley.newRequestQueue(getActivity());
            String url ="http://www.twurl.net/api/v1/twurls";
            if (categoryID != 26) {
                url += "?category_id=" + categoryID;
                if (lastTwurlId != -1) {
                    url += "&last_twurl_id=" + lastTwurlId;
                }
            } else if(lastTwurlId != -1) {
                url += "?last_twurl_id=" + lastTwurlId;
            }
            JsonArrayRequest articleReq = new JsonArrayRequest(url,
                    new Response.Listener<JSONArray>() {


                        @Override
                        public void onResponse(JSONArray response) {
                            Log.v("onResponse", "response: " + response.toString());

                            for (int i = 0; i < response.length(); i ++) {
                                try {
                                    JSONObject jsonObject = response.getJSONObject(i);
                                    Article article = new Article();
                                    article.setHeadline(jsonObject.getString("headline"));
                                    article.setHeadlineImageUrl(jsonObject.getString("headline_image_url"));
                                    article.setUrl(jsonObject.getString("url"));
                                    article.setHeadlineImageHeight(jsonObject.getInt("headline_image_height"));
                                    article.setHeadlineImageWidth(jsonObject.getInt("headline_image_width"));
                                    article.setId(jsonObject.getInt("id"));
                                    JSONObject influencerObject = jsonObject.getJSONObject("influencer");
                                    article.setHandle(influencerObject.getString("handle"));
                                    article.setOriginalTweet(jsonObject.getString("original_tweet"));
                                    article.setCreatedAt(jsonObject.getString("created_at"));
                                    article.setProfileImage(influencerObject.getString("profile_image_url"));
                                    article.setTwitterUsername(influencerObject.getString("twitter_username"));
                                    articles.add(article);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            articleListAdapter.notifyDataSetChanged();
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            VolleyLog.v("onErrorResponse", "Error: " + error.getMessage());

                }
            });

            try {
                AppController.getInstance().addToRequestQueue(articleReq);
            } catch (Exception e) {
                Log.e("addToRequestQueue","error: " + e.getMessage());
                showNetworkToast();
            }
        }

        public void showNetworkToast() {
            Toast networkToast = Toast.makeText(getActivity(), getString(R.string.network_failed), Toast.LENGTH_SHORT);
            networkToast.show();
        }
    }



}
