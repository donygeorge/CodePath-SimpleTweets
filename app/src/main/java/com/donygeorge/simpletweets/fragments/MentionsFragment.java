package com.donygeorge.simpletweets.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.donygeorge.simpletweets.helpers.MyJsonHttpResponseHandler;

public class MentionsFragment extends TweetsListFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        populateTimeline(-1);

        return v;
    }

    void getTimeline(long maxId, MyJsonHttpResponseHandler handler) {
        getTwitterClient().getMentionsTimeline(maxId, handler);
    }

}
