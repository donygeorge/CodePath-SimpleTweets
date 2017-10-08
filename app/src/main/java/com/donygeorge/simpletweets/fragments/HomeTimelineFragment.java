package com.donygeorge.simpletweets.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.donygeorge.simpletweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

public class HomeTimelineFragment extends TweetsListFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        loadFromDB();
        populateTimeline(-1);

        fabCompose.setVisibility(View.VISIBLE);
        fabCompose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                composeTweet(null, null);
            }
        });

        return v;
    }

    private void loadFromDB() {
        List<Tweet> tweets = SQLite.select().from(Tweet.class).queryList();
        addTweets(tweets);
    }

    void getTimeline(long maxId, JsonHttpResponseHandler handler) {
        getTwitterClient().getHomeTimeline(maxId, handler);
    }

    boolean shouldSaveTweets() {
        return true;
    }

    boolean shouldInsertTweets() {
        return true;
    }
}
