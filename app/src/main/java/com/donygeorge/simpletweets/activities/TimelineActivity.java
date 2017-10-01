package com.donygeorge.simpletweets.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.donygeorge.simpletweets.R;
import com.donygeorge.simpletweets.TwitterApplication;
import com.donygeorge.simpletweets.TwitterClient;
import com.donygeorge.simpletweets.adapters.TweetAdapter;
import com.donygeorge.simpletweets.fragments.ComposeFragment;
import com.donygeorge.simpletweets.helpers.EndlessRecyclerViewScrollListener;
import com.donygeorge.simpletweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity implements ComposeFragment.ComposeFragmentListener {

    private TwitterClient mClient;
    private TweetAdapter mAdapter;
    private ArrayList<Tweet> mTweets;
    private EndlessRecyclerViewScrollListener mScrollListener;
    private LinearLayoutManager mLayoutManager;

    @BindView(R.id.rvTweets)
    RecyclerView rvTweets;
    @BindView(R.id.srlTweets)
    SwipeRefreshLayout srlTweets;
    @BindView(R.id.fabCompose)
    FloatingActionButton fabCompose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ButterKnife.bind(this);

        mClient = TwitterApplication.getRestClient();
        mTweets = new ArrayList<>();
        mAdapter = new TweetAdapter(mTweets);
        mLayoutManager = new LinearLayoutManager(this);
        rvTweets.setLayoutManager(mLayoutManager);
        rvTweets.setAdapter(mAdapter);
        mScrollListener = new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                populateTimeline(totalItemsCount);
            }
        };
        rvTweets.setOnScrollListener(mScrollListener);

        fabCompose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                composeTweet(null);
            }
        });

        srlTweets.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateTimeline(-1);
            }
        });

        // Handle intent
        Intent intent = getIntent();
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(intent.getAction()) && type != null) {
            if ("text/plain".equals(type)) {
                // Make sure to check whether returned data will be null.
                String titleOfPage = intent.getStringExtra(Intent.EXTRA_SUBJECT);
                String urlOfPage = intent.getStringExtra(Intent.EXTRA_TEXT);
                composeTweet(titleOfPage + " " + urlOfPage);
            }
        }

        loadFromDB();
        populateTimeline(-1);
    }

    private void loadFromDB() {
        List<Tweet> tweets = SQLite.select().from(Tweet.class).queryList();
        for (Tweet tweet : tweets) {
            mTweets.add(tweet);
            mAdapter.notifyItemInserted(mTweets.size() - 1);
        }
    }

    private void populateTimeline(final int totalItemsCount) {
        long maxId = -1;
        if (totalItemsCount > 0) {
            Tweet tweet = mTweets.get(totalItemsCount - 1);
            maxId = tweet.uid;
        }
        mClient.getHomeTimeline(maxId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // Clear existing items (if required)
                if (totalItemsCount < 0) {
                    mAdapter.clear();
                }
                for (int i = 0; i < response.length(); i++) {
                    try {
                        Tweet tweet = Tweet.fromJSON(response.getJSONObject(i));
                        tweet.save();
                        mTweets.add(tweet);
                        mAdapter.notifyItemInserted(mTweets.size() - 1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                // Should be harmless even if this wasn't triggered by a push to refresh
                srlTweets.setRefreshing(false);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                srlTweets.setRefreshing(false);
            }
        });
    }

    private void composeTweet(String text) {
        FragmentManager fm = getSupportFragmentManager();
        ComposeFragment composeDialogFragment = ComposeFragment.newInstance(text);
        composeDialogFragment.show(fm, "fragment_compose");
    }

    @Override
    public void postTweet(String text) {
        mClient.postTweet(text, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject object) {
                try {
                    Tweet tweet = Tweet.fromJSON(object);
                    mTweets.add(0, tweet);
                    mAdapter.notifyItemInserted(0);
                    mLayoutManager.scrollToPosition(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject object) {
                // TODO: Handle error
                super.onFailure(statusCode, headers, throwable, object);
            }
        });
    }
}