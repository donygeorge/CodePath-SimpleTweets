package com.donygeorge.simpletweets.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

import static com.donygeorge.simpletweets.helpers.Constants.SEARCH_KEY;

public class SearchFragment extends TweetsListFragment {

    private String mQuery;

    public static SearchFragment newInstance(String query) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(SEARCH_KEY, query);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        mQuery = getArguments().getString(SEARCH_KEY);
        populateTimeline(-1);

        return v;
    }

    void getTimeline(long maxId, final JsonHttpResponseHandler handler) {
        getTwitterClient().getSearchTimeline(mQuery, maxId, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray results = response.getJSONArray("statuses");
                    handler.onSuccess(statusCode, headers, results);
                } catch (JSONException e) {
                    handler.onFailure(statusCode, headers, e, (JSONObject)null);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                handler.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                handler.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                handler.onFailure(statusCode, headers, responseString, throwable);
            }

        });
    }

}
