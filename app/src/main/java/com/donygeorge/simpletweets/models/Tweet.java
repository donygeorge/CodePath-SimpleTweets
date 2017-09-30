package com.donygeorge.simpletweets.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Tweet {

    public String body;
    public long uid;
    public String createdAt;
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
