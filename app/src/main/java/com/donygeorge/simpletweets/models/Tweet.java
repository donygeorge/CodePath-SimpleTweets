package com.donygeorge.simpletweets.models;

import com.donygeorge.simpletweets.MyDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Table(database = MyDatabase.class)
@Parcel(analyze={Tweet.class})
public class Tweet extends BaseModel {

    @Column
    @PrimaryKey
    public long uid;

    @Column
    public String body;

    @Column
    public String createdAt;

    @Column
    @ForeignKey(saveForeignKeyModel = true)
    public User user;

    public static Tweet fromJSON(JSONObject object) throws JSONException {
        Tweet tweet = new Tweet();

        tweet.body = object.getString("text");
        tweet.uid = object.getLong("id");
        tweet.createdAt = object.getString("created_at");
        tweet.user = User.fromJSON(object.getJSONObject("user"));

        return tweet;
    }

}
