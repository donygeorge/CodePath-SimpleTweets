package com.donygeorge.simpletweets.models;


import com.donygeorge.simpletweets.MyDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Table(database = MyDatabase.class)
@Parcel(analyze={User.class})
public class User {

    @Column
    @PrimaryKey
    public long uid;

    @Column
    public String name;

    @Column
    public String screenName;

    @Column
    public String tagline;

    @Column
    public int followers;

    @Column
    public int following;

    @Column
    public boolean verified;

    @Column
    public String profileImageUrl;

    public static User fromJSON(JSONObject object) throws JSONException {
        User user = new User();

        user.name = object.getString("name");
        user.uid = object.getLong("id");
        user.screenName = object.getString("screen_name");
        user.tagline = object.getString("description");
        user.followers = object.getInt("followers_count");
        user.following = object.getInt("friends_count");
        user.verified = object.optBoolean("verified", false);
        user.profileImageUrl = object.getString("profile_image_url");

        return user;
    }
}
