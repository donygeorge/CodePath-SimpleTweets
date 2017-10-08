package com.donygeorge.simpletweets.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.donygeorge.simpletweets.R;
import com.donygeorge.simpletweets.TwitterApplication;
import com.donygeorge.simpletweets.TwitterClient;
import com.donygeorge.simpletweets.adapters.UserAdapter;
import com.donygeorge.simpletweets.helpers.DividerItemDecoration;
import com.donygeorge.simpletweets.helpers.EndlessRecyclerViewScrollListener;
import com.donygeorge.simpletweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

import static com.donygeorge.simpletweets.helpers.Constants.SHOW_FOLLOWERS_KEY;
import static com.donygeorge.simpletweets.helpers.Constants.USER_KEY;
import static com.raizlabs.android.dbflow.config.FlowManager.getContext;

public class FollowActivity extends AppCompatActivity implements UserAdapter.UserAdapterListener {

    private TwitterClient mClient;
    private UserAdapter mAdapter;
    private ArrayList<User> mUsers;
    private EndlessRecyclerViewScrollListener mScrollListener;
    private LinearLayoutManager mLayoutManager;
    private long mCursor = -1;
    private boolean mShowFollowers;
    private User mUser;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rvUsers)
    RecyclerView rvUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        mClient = TwitterApplication.getRestClient();

        mUser = (User) Parcels.unwrap(getIntent().getParcelableExtra(USER_KEY));
        mShowFollowers =  getIntent().getBooleanExtra(SHOW_FOLLOWERS_KEY, false);
        String title = mShowFollowers ? "Followers" : "Following";
        getSupportActionBar().setTitle(title);

        mUsers = new ArrayList<>();
        mAdapter = new UserAdapter(mUsers, this);
        mLayoutManager = new LinearLayoutManager(getContext());
        rvUsers.setLayoutManager(mLayoutManager);
        rvUsers.setAdapter(mAdapter);
        rvUsers.addItemDecoration(new DividerItemDecoration(getContext()));
        rvUsers.setItemAnimator(new DefaultItemAnimator());
        mScrollListener = new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                queryUsers();
            }
        };
        rvUsers.setOnScrollListener(mScrollListener);
        queryUsers();
    }

    private void queryUsers() {
        JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    JSONArray array = response.getJSONArray("users");
                    mCursor = response.getLong("next_cursor");
                    for (int i = 0; i < array.length(); i++) {
                        try {
                            User user = User.fromJSON(array.getJSONObject(i));
                            mUsers.add(user);
                            mAdapter.notifyItemInserted(mUsers.size() - 1);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                throwable.printStackTrace();
            }
        };
        if (mShowFollowers) {
            mClient.getFollowers(mUser.uid, mCursor, handler);
        } else {
            mClient.getFriends(mUser.uid, mCursor, handler);
        }
    }

    @Override
    public void onUserSelected(User user) {
        Intent i = new Intent(this, ProfileActivity.class);
        i.putExtra(USER_KEY, Parcels.wrap(user));
        startActivity(i);
    }
}
