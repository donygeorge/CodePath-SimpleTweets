package com.donygeorge.simpletweets.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.donygeorge.simpletweets.R;
import com.donygeorge.simpletweets.TwitterApplication;
import com.donygeorge.simpletweets.TwitterClient;
import com.donygeorge.simpletweets.fragments.UserTimelineFragment;
import com.donygeorge.simpletweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.donygeorge.simpletweets.helpers.Constants.USER_KEY;

public class ProfileActivity extends AppCompatActivity {

    private TwitterClient mClient;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.ivProfileImage)
    CircleImageView ivProfileImage;
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvTagline)
    TextView tvTagline;
    @BindView(R.id.tvFollowers)
    TextView tvFollowers;
    @BindView(R.id.tvFollowing)
    TextView tvFollowing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        User user = (User)Parcels.unwrap(getIntent().getParcelableExtra(USER_KEY));
        String screenName = (user != null) ? user.screenName : null;
        UserTimelineFragment fragment = UserTimelineFragment.newInstance(screenName);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.flContainer, fragment);
        transaction.commit();

        if (user == null) {
            mClient = TwitterApplication.getRestClient();
            mClient.getUserInfo(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        User user = User.fromJSON(response);
                        populateUserHeadline(user);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                }
            });
        } else {
            populateUserHeadline(user);
        }
    }

    private void populateUserHeadline(User user) {
        getSupportActionBar().setTitle("@" + user.screenName);
        tvName.setText(user.name);
        tvTagline.setText(user.tagline);
        tvFollowers.setText(user.followers + " Followers");
        tvFollowing.setText(user.following + " Following");

        Glide.with(this)
                .load(user.profileImageUrl)
                .into(ivProfileImage);
    }
}
