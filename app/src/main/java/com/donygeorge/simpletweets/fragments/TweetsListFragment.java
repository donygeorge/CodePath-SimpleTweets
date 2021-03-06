package com.donygeorge.simpletweets.fragments;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.donygeorge.simpletweets.R;
import com.donygeorge.simpletweets.TwitterApplication;
import com.donygeorge.simpletweets.TwitterClient;
import com.donygeorge.simpletweets.adapters.TweetAdapter;
import com.donygeorge.simpletweets.helpers.DividerItemDecoration;
import com.donygeorge.simpletweets.helpers.EndlessRecyclerViewScrollListener;
import com.donygeorge.simpletweets.helpers.MyJsonHttpResponseHandler;
import com.donygeorge.simpletweets.helpers.SnackBarHelper;
import com.donygeorge.simpletweets.models.Tweet;
import com.donygeorge.simpletweets.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class TweetsListFragment
        extends Fragment
        implements TweetAdapter.TweetAdapterListener, ComposeFragment.ComposeFragmentListener {

    private TwitterClient mClient;
    private TweetAdapter mAdapter;
    private ArrayList<Tweet> mTweets;
    private EndlessRecyclerViewScrollListener mScrollListener;
    private LinearLayoutManager mLayoutManager;

    @BindView(R.id.rvTweets)
    RecyclerView rvTweets;
    @BindView(R.id.srlTweets)
    SwipeRefreshLayout srlTweets;
    @BindView(R.id.fabCompose)
    FloatingActionButton fabCompose;

    public interface TweetSelectedListener {
        public abstract void onUserSelected(User user);
        public abstract void onHashtagSelected(String hashtag);
    }

    public TweetsListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_tweets_list, container, false);
        ButterKnife.bind(this, v);

        mClient = TwitterApplication.getRestClient();
        mTweets = new ArrayList<>();
        mAdapter = new TweetAdapter(mTweets, this);
        mLayoutManager = new LinearLayoutManager(getContext());
        rvTweets.setLayoutManager(mLayoutManager);
        rvTweets.setAdapter(mAdapter);
        rvTweets.addItemDecoration(new DividerItemDecoration(getContext()));
        rvTweets.setItemAnimator(new DefaultItemAnimator());
        mScrollListener = new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                populateTimeline(totalItemsCount);
            }
        };
        rvTweets.setOnScrollListener(mScrollListener);

        srlTweets.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateTimeline(-1);
            }
        });

        fabCompose.setVisibility(View.INVISIBLE);

        return v;
    }

    TwitterClient getTwitterClient() {
        return mClient;
    }

    @Override
    public void onItemPhotoSelected(View view, int position) {
        Tweet tweet = mTweets.get(position);
        ((TweetSelectedListener)getActivity()).onUserSelected(tweet.user);
    }

    @Override
    public void onHashtagClicked(String hashtag) {
        ((TweetSelectedListener)getActivity()).onHashtagSelected(hashtag);
    }

    @Override
    public void onMentionClicked(String mention) {
        if (mention.startsWith("@")) {
            mention = mention.substring(1);
        }
        mClient.getUserInfo(mention, new MyJsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {
                super.onSuccess(response);
                try {
                    User user = User.fromJSON(response);
                    ((TweetSelectedListener)getActivity()).onUserSelected(user);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(FailureReason reason) {
                SnackBarHelper.displayError(srlTweets, reason);
            }
        });
    }

    @Override
    public void onItemFavoriteSelected(View view, int position) {
        final Tweet tweet = mTweets.get(position);
        final boolean shouldFavorite = !tweet.favorited;
        mClient.favoriteTweet(tweet, shouldFavorite, new MyJsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {
                refetchTweet(tweet);
            }

            @Override
            public void onFailure(FailureReason reason) {
                SnackBarHelper.displayError(srlTweets, reason);
            }
        });
    }

    @Override
    public void onItemRetweetSelected(View view, int position) {
        final Tweet tweet = mTweets.get(position);
        final boolean shouldRetweet = !tweet.retweeted;
        mClient.retweet(tweet, shouldRetweet, new MyJsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {
                refetchTweet(tweet);
            }

            @Override
            public void onFailure(FailureReason reason) {
                SnackBarHelper.displayError(srlTweets, reason);
            }
        });
    }

    private void refetchTweet(Tweet tweet) {
        mClient.refetchTweet(tweet, new MyJsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    Tweet updatedTweet = Tweet.fromJSON(response);
                    if (shouldSaveTweets()) {
                        updatedTweet.save();
                    }
                    for (int i = 0; i < mTweets.size(); i++) {
                        if (updatedTweet.uid == mTweets.get(i).uid) {
                            mTweets.set(i, updatedTweet);
                            mAdapter.notifyItemChanged(i);
                            break;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(FailureReason reason) {
                SnackBarHelper.displayError(srlTweets, reason);
            }
        });
    }

    @Override
    public void onItemReplySelected(View view, int position) {
        Tweet tweet = mTweets.get(position);
        composeTweet(null, tweet);
    }

    void addTweets(List<Tweet> tweets) {
        for (Tweet tweet : tweets) {
            mTweets.add(tweet);
            mAdapter.notifyItemInserted(mTweets.size() - 1);
        }
    }

    void insertTweet(Tweet tweet) {
        mTweets.add(0, tweet);
        mAdapter.notifyItemInserted(0);
        mLayoutManager.scrollToPosition(0);
    }

    void populateTimeline(final int totalItemsCount) {
        long maxId = -1;
        if (totalItemsCount > 0) {
            Tweet tweet = mTweets.get(totalItemsCount - 1);
            maxId = tweet.uid - 1;
        }
        getTimeline(maxId, new MyJsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONArray response) {
                // Clear existing items (if required)
                if (totalItemsCount < 0) {
                    mAdapter.clear();
                }
                for (int i = 0; i < response.length(); i++) {
                    try {
                        Tweet tweet = Tweet.fromJSON(response.getJSONObject(i));
                        if (shouldSaveTweets()) {
                            tweet.save();
                        }
                        mTweets.add(tweet);
                        mAdapter.notifyItemInserted(mTweets.size() - 1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                // Should be harmless even if this wasn't triggered by a push to refresh
                srlTweets.setRefreshing(false);
            }

            @Override
            public void onFailure(FailureReason reason) {
                srlTweets.setRefreshing(false);
                SnackBarHelper.displayError(srlTweets, reason);
            }
        });
    }

    boolean shouldSaveTweets() {
        return false;
    }

    boolean shouldInsertTweets() {
        return false;
    }

    void composeTweet(String text, Tweet retweet) {
        FragmentManager fm = getFragmentManager();
        ComposeFragment composeDialogFragment = ComposeFragment.newInstance(text, retweet);
        composeDialogFragment.setTargetFragment(this, 200);
        composeDialogFragment.show(fm, "fragment_compose");
    }

    @Override
    public void postTweet(String text, final long inReplyTo) {
        getTwitterClient().postTweet(text, inReplyTo, new MyJsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject object) {
                try {
                    Tweet tweet = Tweet.fromJSON(object);
                    if (shouldInsertTweets()) {
                        insertTweet(tweet);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(FailureReason reason) {
                SnackBarHelper.displayError(srlTweets, reason);
            }
        });
    }

    abstract void getTimeline(long maxId, MyJsonHttpResponseHandler handler);
}
