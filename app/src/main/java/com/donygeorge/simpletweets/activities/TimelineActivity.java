package com.donygeorge.simpletweets.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.donygeorge.simpletweets.R;
import com.donygeorge.simpletweets.fragments.TweetsPagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TimelineActivity extends AppCompatActivity {

    @BindView(R.id.vpPager)
    ViewPager vpPager;
    @BindView(R.id.tlTabs)
    TabLayout tlTabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ButterKnife.bind(this);

        vpPager.setAdapter(new TweetsPagerAdapter(getSupportFragmentManager(), TimelineActivity.this));
        tlTabs.setupWithViewPager(vpPager);

        // TODO: Fix this
        /*
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
        */
    }
}