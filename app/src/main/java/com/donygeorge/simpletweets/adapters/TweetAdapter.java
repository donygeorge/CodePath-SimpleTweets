package com.donygeorge.simpletweets.adapters;


import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.donygeorge.simpletweets.R;
import com.donygeorge.simpletweets.helpers.DateHelper;
import com.donygeorge.simpletweets.models.Media;
import com.donygeorge.simpletweets.models.Tweet;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.BaseViewHolder> {

    private List<Tweet> mTweets;
    private Context mContext;

    public TweetAdapter(List<Tweet> tweets) {
        mTweets = tweets;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);

        BaseViewHolder viewHolder;
        View tweetView;

        switch (Media.Type.values()[viewType]) {
            case MEDIA_TYPE_PHOTO:
                tweetView = inflater.inflate(R.layout.item_tweet_image, parent, false);
                viewHolder = new ImageViewHolder(tweetView);
                break;
            case MEDIA_TYPE_GIF:
            case MEDIA_TYPE_VIDEO:
                tweetView = inflater.inflate(R.layout.item_tweet_video, parent, false);
                viewHolder = new VideoViewHolder(tweetView);
                break;
            case MEDIA_TYPE_NONE:
                default:
                tweetView = inflater.inflate(R.layout.item_tweet, parent, false);
                viewHolder = new BaseViewHolder(tweetView);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        Tweet tweet = mTweets.get(position);

        holder.tvUserName.setText(tweet.user.name);
        holder.tvScreenName.setText("@" + tweet.user.screenName);
        holder.tvBody.setText(tweet.body);
        holder.tvRelativeTime.setText(DateHelper.getRelativeTimeAgo(tweet.createdAt));
        Glide.with(mContext)
                .load(tweet.user.profileImageUrl)
                .into(holder.ivProfileImage);

        Media.Type type = tweet.mediaType();
        switch (type) {
            case MEDIA_TYPE_PHOTO:
                ImageViewHolder imageViewHolder = (ImageViewHolder)holder;
                Glide.with(mContext)
                        .load(tweet.media.url)
                        .into(imageViewHolder.ivTweetImage);
                break;
            case MEDIA_TYPE_GIF:
            case MEDIA_TYPE_VIDEO:
                VideoViewHolder videoViewHolder = (VideoViewHolder)holder;
                videoViewHolder.vvTweetVideo.setMediaController(new MediaController(mContext));
                Uri uri = Uri.parse(tweet.media.url); //Declare your url here.
                videoViewHolder.vvTweetVideo.setVideoURI(uri);
                videoViewHolder.vvTweetVideo.start();
                break;
            default:
        }
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    @Override
    public int getItemViewType(int position) {
        Tweet tweet = mTweets.get(position);
        return tweet.mediaType().ordinal();
    }

    public void clear() {
        for (Tweet tweet : mTweets) {
            tweet.delete();
        }
        mTweets.clear();
        notifyDataSetChanged();
    }

    public static class BaseViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ivProfileImage)
        ImageView ivProfileImage;
        @BindView(R.id.tvUserName)
        TextView tvUserName;
        @BindView(R.id.tvScreenName)
        TextView tvScreenName;
        @BindView(R.id.tvRelativeTime)
        TextView tvRelativeTime;
        @BindView(R.id.tvBody)
        TextView tvBody;

        public BaseViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class ImageViewHolder extends BaseViewHolder {

        @BindView(R.id.ivTweetImage)
        ImageView ivTweetImage;

        public ImageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class VideoViewHolder extends BaseViewHolder {

        @BindView(R.id.vvTweetVideo)
        VideoView vvTweetVideo;

        public VideoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
