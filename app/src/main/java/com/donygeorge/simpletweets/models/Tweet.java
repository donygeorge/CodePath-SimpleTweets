package com.donygeorge.simpletweets.models;

import com.donygeorge.simpletweets.MyDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONArray;
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
    public int replyCount;

    @Column
    public int retweetCount;

    @Column
    public int favoriteCount;

    @Column
    public boolean retweeted;

    @Column
    public boolean favorited;

    @Column
    @ForeignKey(saveForeignKeyModel = true)
    public User user;

    @Column
    @ForeignKey(saveForeignKeyModel = true)
    public Media media;

    final private static String EXTENDED_ENTITIES_KEY = "extended_entities";
    final private static String MEDIA_KEY = "media";

    public static Tweet fromJSON(JSONObject object) throws JSONException {
        Tweet tweet = new Tweet();

        tweet.body = object.getString("text");
        tweet.uid = object.getLong("id");
        tweet.createdAt = object.getString("created_at");
        tweet.user = User.fromJSON(object.getJSONObject("user"));
        tweet.favoriteCount = object.optInt("favorite_count", 0);
        tweet.replyCount = object.optInt("reply_count", 0);
        tweet.retweetCount = object.optInt("retweet_count", 0);
        tweet.retweeted = object.getBoolean("retweeted");
        tweet.favorited = object.getBoolean("favorited");

        if (object.has(EXTENDED_ENTITIES_KEY)) {
            JSONObject entities = object.getJSONObject(EXTENDED_ENTITIES_KEY);
            JSONArray mediaArray = entities.optJSONArray(MEDIA_KEY);
            if (mediaArray != null && mediaArray.length() > 0) {
                tweet.media = Media.fromJSON(mediaArray.getJSONObject(0));
            }
        }

        return tweet;
    }

    public boolean hasMedia() {
        return media != null;
    }

    public Media.Type mediaType() {
        if (!hasMedia()) {
            return Media.Type.MEDIA_TYPE_NONE;
        }
        return media.mediaType;
    }
}
