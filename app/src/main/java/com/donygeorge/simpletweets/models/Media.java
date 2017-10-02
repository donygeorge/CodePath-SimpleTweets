package com.donygeorge.simpletweets.models;


import com.donygeorge.simpletweets.MyDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;


@Table(database = MyDatabase.class)
@Parcel(analyze={Media.class})
public class Media {

    public enum Type {
        MEDIA_TYPE_NONE,
        MEDIA_TYPE_PHOTO,
        MEDIA_TYPE_VIDEO,
        MEDIA_TYPE_GIF,
    }

    @Column
    @PrimaryKey
    public long uid;

    @Column
    public String url;

    @Column
    public Type mediaType;

    public static Media fromJSON(JSONObject object) throws JSONException {
        Media media = new Media();

        media.uid = object.getLong("id");
        String typeString = object.getString("type");
        Type type;
        switch (typeString) {
            case "photo":
                type = Type.MEDIA_TYPE_PHOTO;
                media.url = object.getString("media_url");
                break;
            case "video":
                type = Type.MEDIA_TYPE_VIDEO;
                media.url = getVideoURL(object);
                break;
            case "animated_gif":
                type = Type.MEDIA_TYPE_GIF;
                media.url = getVideoURL(object);
                break;
            default:
                return null;
        }
        if (media.url == null) {
            return null;
        }
        media.mediaType = type;

        return media;
    }

    private static String getVideoURL(JSONObject object) throws JSONException {
        JSONObject videoInfo = object.optJSONObject("video_info");
        if (videoInfo == null) return  null;

        JSONArray variants = videoInfo.optJSONArray("variants");
        if (variants != null && variants.length() > 0) {
            for (int i = 0; i < variants.length(); i ++) {
                JSONObject variant = variants.getJSONObject(i);
                if (variant.optString("content_type").equals("video/mp4")) {
                    return variant.optString("url");
                }
            }
        }
        return null;
    }
}
