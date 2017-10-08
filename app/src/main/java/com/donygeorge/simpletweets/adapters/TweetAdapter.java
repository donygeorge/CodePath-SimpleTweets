package com.donygeorge.simpletweets.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.donygeorge.simpletweets.R;
import com.donygeorge.simpletweets.helpers.DateHelper;
import com.donygeorge.simpletweets.helpers.PatternEditableBuilder;
import com.donygeorge.simpletweets.models.Media;
import com.donygeorge.simpletweets.models.Tweet;

import java.util.List;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.BaseViewHolder> {

    private List<Tweet> mTweets;
    private Context mContext;
    private TweetAdapterListener mListener;

    public interface TweetAdapterListener {
        public abstract void onItemPhotoSelected(View view, int position);
        public abstract void onItemFavoriteSelected(View view, int position);
        public abstract void onItemRetweetSelected(View view, int position);
        public abstract void onItemReplySelected(View view, int position);
        public abstract void onHashtagClicked(String hashtag);
        public abstract void onMentionClicked(String mention);
    }

    public TweetAdapter(List<Tweet> tweets, TweetAdapterListener listener)
    {
        mTweets = tweets;
        mListener = listener;
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
                // TODO: Support video playback
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
        setupClickableSpan(holder.tvBody);
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
                // TODO: Support video playback
            default:
        }

        int retweetId = tweet.retweeted ? R.drawable.ic_retweet_active : R.drawable.ic_retweet;
        int favoriteId = tweet.favorited ? R.drawable.ic_favorite_active : R.drawable.ic_favorite;
        holder.ivRetweet.setBackground(mContext.getResources().getDrawable(retweetId));
        holder.ivFavorite.setBackground(mContext.getResources().getDrawable(favoriteId));
    }

    private void setupClickableSpan(TextView textView) {
        PatternEditableBuilder builder = new PatternEditableBuilder();
        builder.addPattern(Pattern.compile("\\@(\\w+)"), R.color.colorPrimary,
                new PatternEditableBuilder.SpannableClickedListener() {
                    @Override
                    public void onSpanClicked(String text) {
                        mListener.onMentionClicked(text);
                    }
                }).into(textView);

        builder.addPattern(Pattern.compile("\\#(\\w+)"), R.color.colorPrimary,
                new PatternEditableBuilder.SpannableClickedListener() {
                    @Override
                    public void onSpanClicked(String text) {
                        mListener.onHashtagClicked(text);
                    }
                }).into(textView);
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

    public class BaseViewHolder extends RecyclerView.ViewHolder {

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
        @BindView(R.id.ivFavorite)
        ImageView ivFavorite;
        @BindView(R.id.ivReply)
        ImageView ivReply;
        @BindView(R.id.ivRetweet)
        ImageView ivRetweet;

        public BaseViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            ivProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        int position = getAdapterPosition();
                        mListener.onItemPhotoSelected(v, position);
                    }
                }
            });

            ivFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        int position = getAdapterPosition();
                        mListener.onItemFavoriteSelected(v, position);
                    }
                }
            });

            ivReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        int position = getAdapterPosition();
                        mListener.onItemReplySelected(v, position);
                    }
                }
            });

            ivRetweet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        int position = getAdapterPosition();
                        mListener.onItemRetweetSelected(v, position);
                    }
                }
            });
        }
    }

    public class ImageViewHolder extends BaseViewHolder {

        @BindView(R.id.ivTweetImage)
        ImageView ivTweetImage;

        public ImageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class VideoViewHolder extends BaseViewHolder {

        @BindView(R.id.vvTweetVideo)
        VideoView vvTweetVideo;

        public VideoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
