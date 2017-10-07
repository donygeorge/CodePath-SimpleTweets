package com.donygeorge.simpletweets.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.donygeorge.simpletweets.R;
import com.donygeorge.simpletweets.TwitterApplication;
import com.donygeorge.simpletweets.TwitterClient;
import com.donygeorge.simpletweets.fragments.UserTimelineFragment;
import com.donygeorge.simpletweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

import static com.donygeorge.simpletweets.helpers.Constants.SCREEN_NAME_KEY;

public class ProfileActivity extends AppCompatActivity {

    private TwitterClient mClient;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        String screenName = getIntent().getStringExtra(SCREEN_NAME_KEY);
        UserTimelineFragment fragment = UserTimelineFragment.newInstance(screenName);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.flContainer, fragment);
        transaction.commit();

        mClient = TwitterApplication.getRestClient();
        mClient.getUserInfo(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    User user = User.fromJSON(response);
                    getSupportActionBar().setTitle("@" + user.screenName);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
