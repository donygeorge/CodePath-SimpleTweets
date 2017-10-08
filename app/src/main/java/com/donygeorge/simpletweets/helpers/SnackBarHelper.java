package com.donygeorge.simpletweets.helpers;

import android.support.design.widget.Snackbar;
import android.view.View;

public class SnackBarHelper {

    public static void displayError(View parentView, MyJsonHttpResponseHandler.FailureReason reason) {
        if (reason == MyJsonHttpResponseHandler.FailureReason.FAILURE_REASON_NETWORK) {
            displayNetworkError(parentView);
        } else {
            displayError(parentView, "Something went wrong");
        }
    }

    public static void displayNetworkError(View parentView) {
        displayError(parentView, "No internet connection");
    }

    public static void displayError(View parentView, String message) {
        Snackbar.make(parentView, message, Snackbar.LENGTH_LONG).show();
    }

}
