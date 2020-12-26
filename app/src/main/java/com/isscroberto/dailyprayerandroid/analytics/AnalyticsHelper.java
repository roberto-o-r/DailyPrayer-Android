package com.isscroberto.dailyprayerandroid.analytics;

import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

public class AnalyticsHelper {

    public static void LogEvent(FirebaseAnalytics firebaseAnalytics, EventType eventType, Bundle params){
        firebaseAnalytics.logEvent("custom_" + eventType.toString().toLowerCase(), params);
    }

}
