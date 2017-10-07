package com.donygeorge.simpletweets.fragments;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.donygeorge.simpletweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

public class HomeTimelineFragment extends TweetsListFragment implements ComposeFragment.ComposeFragmentListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        loadFromDB();
        populateTimeline(-1);

        fabCompose.setVisibility(View.VISIBLE);
        fabCompose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                composeTweet(null);
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

    @Override
    public void postTweet(String text) {
        getTwitterClient().postTweet(text, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject object) {
                try {
                    Tweet tweet = Tweet.fromJSON(object);
                    insertTweet(tweet);
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

    private void composeTweet(String text) {
        FragmentManager fm = getFragmentManager();
        ComposeFragment composeDialogFragment = ComposeFragment.newInstance(text);
        composeDialogFragment.setTargetFragment(this, 200);
        composeDialogFragment.show(fm, "fragment_compose");
    }
}
