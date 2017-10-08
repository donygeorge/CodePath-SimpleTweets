package com.donygeorge.simpletweets;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.donygeorge.simpletweets.models.Tweet;
import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.api.BaseApi;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/scribejava/scribejava/tree/master/scribejava-apis/src/main/java/com/github/scribejava/apis
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final BaseApi REST_API_INSTANCE = TwitterApi.instance();
	public static final String REST_URL = "https://api.twitter.com/1.1";
	public static final String REST_CONSUMER_KEY = "SPAQpSwhoRX9lq485NW0kIj85";
	public static final String REST_CONSUMER_SECRET = "Mqom0vjvgjAOXmQxcBq1vL4Ru3kPwPlJPz4IzjdU45fPHsqLGP";

	// Landing page to indicate the OAuth flow worked in case Chrome for Android 25+ blocks navigation back to the app.
	public static final String FALLBACK_URL = "https://codepath.github.io/android-rest-client-template/success.html";

	// See https://developer.chrome.com/multidevice/android/intents
	public static final String REST_CALLBACK_URL_TEMPLATE = "intent://%s#Intent;action=android.intent.action.VIEW;scheme=%s;package=%s;S.browser_fallback_url=%s;end";

	public TwitterClient(Context context) {
		super(context, REST_API_INSTANCE,
				REST_URL,
				REST_CONSUMER_KEY,
				REST_CONSUMER_SECRET,
				String.format(REST_CALLBACK_URL_TEMPLATE, context.getString(R.string.intent_host),
						context.getString(R.string.intent_scheme), context.getPackageName(), FALLBACK_URL));
	}

	private static RequestParams getTimelineParams(long maxId) {
        RequestParams params = new RequestParams();
        params.put("count", 25);
        if (maxId >= 0) {
            params.put("max_id", maxId);
        }
        params.put("include_entities", true);
        return params;
    }

	public void getHomeTimeline(long maxId, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
        RequestParams params = getTimelineParams(maxId);
		client.get(apiUrl, params, handler);
	}

	public void getMentionsTimeline(long maxId, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/mentions_timeline.json");
        RequestParams params = getTimelineParams(maxId);
		client.get(apiUrl, params, handler);
	}

    public void getUserTimeline(String screenName, long maxId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/user_timeline.json");
        RequestParams params = getTimelineParams(maxId);
        params.put("screen_name", screenName);
        client.get(apiUrl, params, handler);
    }

    public void getSearchTimeline(String search, long maxId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("search/tweets.json");
        RequestParams params = getTimelineParams(maxId);
        params.put("q", search);
        client.get(apiUrl, params, handler);
    }

    public void getUserInfo(AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("account/verify_credentials.json");
        client.get(apiUrl, null, handler);
    }

    private static RequestParams getUserParams(long uid, long cursor) {
        RequestParams params = new RequestParams();
        params.put("id", uid);
        if (cursor >= 0) {
            params.put("cursor", cursor);
        }
        return params;
    }

    public void getFollowers(long uid, long cursor, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("followers/list.json");
        RequestParams params = getUserParams(uid, cursor);
        client.get(apiUrl, params, handler);
    }

    public void getFriends(long uid, long cursor, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("followers/list.json");
        RequestParams params = getUserParams(uid, cursor);
        client.get(apiUrl, params, handler);
    }

    public void refetchTweet(Tweet tweet, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/show/" + tweet.uid + ".json");
        RequestParams params = new RequestParams();
        params.put("id", tweet.uid);
        params.put("include_entities", true);
        client.get(apiUrl, params, handler);
    }

    public void postTweet(String text, long inReplyTo, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/update.json");
        // Can specify query string params directly or through RequestParams.
        RequestParams params = new RequestParams();
        params.put("status", text);
        if (inReplyTo >= 0) {
            params.put("in_reply_to_status_id", inReplyTo);
        }
        client.post(apiUrl, params, handler);
    }

    public void favoriteTweet(Tweet tweet, boolean shouldFavorite, AsyncHttpResponseHandler handler) {
        String url = shouldFavorite ? "favorites/create.json" : "favorites/destroy.json";
        String apiUrl = getApiUrl(url);
        RequestParams params = new RequestParams();
        params.put("id", tweet.uid);
        client.post(apiUrl, params, handler);
    }

    public void retweet(Tweet tweet, boolean shouldRetweet, AsyncHttpResponseHandler handler) {
        String url = shouldRetweet ? "statuses/retweet/" : "statuses/retweet/";
        url = url + tweet.uid + ".json";
        String apiUrl = getApiUrl(url);
        RequestParams params = new RequestParams();
        params.put("id", tweet.uid);
        client.post(apiUrl, params, handler);
    }
}
