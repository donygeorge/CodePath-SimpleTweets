package com.donygeorge.simpletweets.fragments;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.donygeorge.simpletweets.R;
import com.donygeorge.simpletweets.TwitterApplication;
import com.donygeorge.simpletweets.TwitterClient;
import com.donygeorge.simpletweets.adapters.TweetAdapter;
import com.donygeorge.simpletweets.helpers.DividerItemDecoration;
import com.donygeorge.simpletweets.helpers.EndlessRecyclerViewScrollListener;
import com.donygeorge.simpletweets.models.Tweet;
import com.donygeorge.simpletweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class TweetsListFragment extends Fragment implements TweetAdapter.TweetAdapterListener {

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

    public interface TweetSelectedListener {
        public abstract void onUserSelected(User user);
    }

    public TweetsListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_tweets_list, container, false);
        ButterKnife.bind(this, v);

        mClient = TwitterApplication.getRestClient();
        mTweets = new ArrayList<>();
        mAdapter = new TweetAdapter(mTweets, this);
        mLayoutManager = new LinearLayoutManager(getContext());
        rvTweets.setLayoutManager(mLayoutManager);
        rvTweets.setAdapter(mAdapter);
        rvTweets.addItemDecoration(new DividerItemDecoration(getContext()));
        rvTweets.setItemAnimator(new DefaultItemAnimator());
        mScrollListener = new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                populateTimeline(totalItemsCount);
            }
        };
        rvTweets.setOnScrollListener(mScrollListener);

        srlTweets.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateTimeline(-1);
            }
        });

        fabCompose.setVisibility(View.INVISIBLE);

        return v;
    }

    TwitterClient getTwitterClient() {
        return mClient;
    }

    @Override
    public void onItemPhotoSelected(View view, int position) {
        Tweet tweet = mTweets.get(position);
        ((TweetSelectedListener)getActivity()).onUserSelected(tweet.user);
    }

    void addTweets(List<Tweet> tweets) {
        for (Tweet tweet : tweets) {
            mTweets.add(tweet);
            mAdapter.notifyItemInserted(mTweets.size() - 1);
        }
    }

    void insertTweet(Tweet tweet) {
        mTweets.add(0, tweet);
        mAdapter.notifyItemInserted(0);
        mLayoutManager.scrollToPosition(0);
    }

    void populateTimeline(final int totalItemsCount) {
        long maxId = -1;
        if (totalItemsCount > 0) {
            Tweet tweet = mTweets.get(totalItemsCount - 1);
            maxId = tweet.uid;
        }
        getTimeline(maxId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // Clear existing items (if required)
                if (totalItemsCount < 0) {
                    mAdapter.clear();
                }
                for (int i = 0; i < response.length(); i++) {
                    try {
                        Tweet tweet = Tweet.fromJSON(response.getJSONObject(i));
                        if (shouldSaveTweets()) {
                            tweet.save();
                        }
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

    boolean shouldSaveTweets() {
        return false;
    }

    abstract void getTimeline(long maxId, JsonHttpResponseHandler handler);
}
