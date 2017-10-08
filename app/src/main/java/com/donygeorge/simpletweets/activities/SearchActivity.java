package com.donygeorge.simpletweets.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.donygeorge.simpletweets.R;
import com.donygeorge.simpletweets.fragments.SearchFragment;
import com.donygeorge.simpletweets.fragments.TweetsListFragment;
import com.donygeorge.simpletweets.models.User;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.donygeorge.simpletweets.helpers.Constants.SEARCH_KEY;
import static com.donygeorge.simpletweets.helpers.Constants.USER_KEY;

public class SearchActivity extends AppCompatActivity implements TweetsListFragment.TweetSelectedListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        String query = getIntent().getStringExtra(SEARCH_KEY);
        getSupportActionBar().setTitle("Results for '" + query + "'");

        SearchFragment fragment = SearchFragment.newInstance(query);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.flContainer, fragment);
        transaction.commit();
    }

    @Override
    public void onUserSelected(User user) {
        Intent i = new Intent(this, ProfileActivity.class);
        i.putExtra(USER_KEY, Parcels.wrap(user));
        startActivity(i);
    }

    @Override
    public void onHashtagSelected(String hashtag) {
        Intent i = new Intent(this, SearchActivity.class);
        i.putExtra(SEARCH_KEY, hashtag);
        startActivity(i);
    }
}
