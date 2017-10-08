package com.donygeorge.simpletweets.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.donygeorge.simpletweets.R;
import com.donygeorge.simpletweets.models.User;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.BaseViewHolder> {

    private List<User> mUsers;
    private Context mContext;
    private UserAdapterListener mListener;

    public interface UserAdapterListener {
        abstract public void onUserSelected(User user);
    }

    public UserAdapter(List<User> users, UserAdapterListener listener)
    {
        mUsers = users;
        mListener = listener;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View userView = inflater.inflate(R.layout.item_user, parent, false);
        BaseViewHolder viewHolder = new BaseViewHolder(userView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        final User user = mUsers.get(position);

        holder.tvName.setText(user.name);
        holder.tvScreenName.setText("@" + user.screenName);
        holder.tvTagline.setText(user.tagline);

        Glide.with(mContext)
                .load(user.profileImageUrl)
                .into(holder.ivProfileImage);

        holder.ivVerified.setVisibility(user.verified ? View.VISIBLE : View.INVISIBLE);
        int followingId = user.following ? R.drawable.ic_following_active : R.drawable.ic_following;
        holder.ivFollowing.setBackground(mContext.getResources().getDrawable(followingId));

        holder.ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onUserSelected(user);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }


    public class BaseViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ivProfileImage)
        CircleImageView ivProfileImage;
        @BindView(R.id.tvName)
        TextView tvName;
        @BindView(R.id.tvScreenName)
        TextView tvScreenName;
        @BindView(R.id.tvTagline)
        TextView tvTagline;
        @BindView(R.id.ivVerified)
        ImageView ivVerified;
        @BindView(R.id.ivFollowing)
        ImageView ivFollowing;

        public BaseViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            ivProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });

            ivFollowing.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }
    }
}
