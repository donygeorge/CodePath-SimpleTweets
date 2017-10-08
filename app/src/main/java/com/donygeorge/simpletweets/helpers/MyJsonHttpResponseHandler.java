package com.donygeorge.simpletweets.helpers;

import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

import static com.donygeorge.simpletweets.helpers.MyJsonHttpResponseHandler.FailureReason.FAILURE_REASON_UNKNOWN;

public abstract class MyJsonHttpResponseHandler extends JsonHttpResponseHandler {

    final String TAG = "JsonHttpResponseHandler";
    public enum FailureReason {
        FAILURE_REASON_NETWORK,
        FAILURE_REASON_UNKNOWN
    };

    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        onSuccess(response);
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
        onSuccess(response);
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
        Log.w(TAG, "Failure due to: " + errorResponse);
        throwable.printStackTrace();
        onFailure(FAILURE_REASON_UNKNOWN);
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
        Log.w(TAG, "Failure due to: " + errorResponse);
        throwable.printStackTrace();
        onFailure(FAILURE_REASON_UNKNOWN);
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
        Log.w(TAG, "Failure due to: " + responseString);
        throwable.printStackTrace();
        onFailure(FAILURE_REASON_UNKNOWN);
    }

    public void onSuccess(JSONObject response) {
        Log.w(TAG, "onSuccess(JSONObject) was not overriden, but callback was received");
    }

    public void onSuccess(JSONArray response) {
        Log.w(TAG, "onSuccess(JSONArray) was not overriden, but callback was received");
    }

    public abstract void onFailure(FailureReason reason);
}
