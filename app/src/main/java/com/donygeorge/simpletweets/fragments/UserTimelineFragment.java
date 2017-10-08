package com.donygeorge.simpletweets.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.donygeorge.simpletweets.helpers.MyJsonHttpResponseHandler;

import static com.donygeorge.simpletweets.helpers.Constants.SCREEN_NAME_KEY;

public class UserTimelineFragment extends TweetsListFragment {

    public static UserTimelineFragment newInstance(String screenName) {
        UserTimelineFragment fragment = new UserTimelineFragment();
        Bundle args = new Bundle();
        args.putString(SCREEN_NAME_KEY, screenName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        populateTimeline(-1);

        return v;
    }

    void getTimeline(long maxId, MyJsonHttpResponseHandler handler) {
        String screenName = getArguments().getString(SCREEN_NAME_KEY);
        getTwitterClient().getUserTimeline(screenName, maxId, handler);
    }

}
