package com.donygeorge.simpletweets.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.donygeorge.simpletweets.R;
import com.donygeorge.simpletweets.fragments.TweetsListFragment;
import com.donygeorge.simpletweets.fragments.TweetsPagerAdapter;
import com.donygeorge.simpletweets.models.User;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.donygeorge.simpletweets.helpers.Constants.USER_KEY;

public class TimelineActivity extends AppCompatActivity implements TweetsListFragment.TweetSelectedListener {

    @BindView(R.id.vpPager)
    ViewPager vpPager;
    @BindView(R.id.tlTabs)
    TabLayout tlTabs;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ButterKnife.bind(this);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    public void onProfileView(MenuItem item) {
        Intent i = new Intent(this, ProfileActivity.class);
        startActivity(i);
    }

    @Override
    public void onUserSelected(User user) {
        Intent i = new Intent(this, ProfileActivity.class);
        i.putExtra(USER_KEY, Parcels.wrap(user));
        startActivity(i);
    }
}