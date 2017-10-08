package com.donygeorge.simpletweets.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.donygeorge.simpletweets.R;
import com.donygeorge.simpletweets.TwitterApplication;
import com.donygeorge.simpletweets.TwitterClient;
import com.donygeorge.simpletweets.fragments.TweetsListFragment;
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

import static com.donygeorge.simpletweets.helpers.Constants.SHOW_FOLLOWERS_KEY;
import static com.donygeorge.simpletweets.helpers.Constants.USER_KEY;

public class ProfileActivity extends AppCompatActivity implements TweetsListFragment.TweetSelectedListener {

    private TwitterClient mClient;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.ivProfileImage)
    CircleImageView ivProfileImage;
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvScreenName)
    TextView tvScreenName;
    @BindView(R.id.tvTagline)
    TextView tvTagline;
    @BindView(R.id.tvFollowers)
    TextView tvFollowers;
    @BindView(R.id.tvFollowing)
    TextView tvFollowing;
    @BindView(R.id.ivVerified)
    ImageView ivVerified;

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
        tvScreenName.setText("@" + user.screenName);
        tvTagline.setText(user.tagline);
        tvFollowers.setText(user.followersCount + " Followers");
        tvFollowing.setText(user.followingCount + " Following");
        ivVerified.setVisibility(user.verified ? View.VISIBLE : View.INVISIBLE);

        Glide.with(this)
                .load(user.profileImageUrl)
                .into(ivProfileImage);

        setupFollowClickListeners(tvFollowers, user, true);
        setupFollowClickListeners(tvFollowing, user, false);
    }

    private void setupFollowClickListeners(TextView textView, final User user, final boolean showFollowers) {
        final Context context = this;
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, FollowActivity.class);
                i.putExtra(USER_KEY, Parcels.wrap(user));
                i.putExtra(SHOW_FOLLOWERS_KEY, showFollowers);
                startActivity(i);
            }
        });
    }

    @Override
    public void onUserSelected(User user) {
        Intent i = new Intent(this, ProfileActivity.class);
        i.putExtra(USER_KEY, Parcels.wrap(user));
        startActivity(i);
    }
}
