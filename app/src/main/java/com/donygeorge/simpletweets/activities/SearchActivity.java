package com.donygeorge.simpletweets.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.donygeorge.simpletweets.R;
import com.donygeorge.simpletweets.TwitterClient;
import com.donygeorge.simpletweets.fragments.SearchFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.donygeorge.simpletweets.helpers.Constants.SEARCH_KEY;

public class SearchActivity extends AppCompatActivity {

    private TwitterClient mClient;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        String query = getIntent().getStringExtra(SEARCH_KEY);
        getSupportActionBar().setTitle("Query: " + query);

        SearchFragment fragment = SearchFragment.newInstance(query);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.flContainer, fragment);
        transaction.commit();
    }
}
