package com.isscroberto.dailyprayerandroid;

import com.github.stkent.amplify.feedback.DefaultEmailFeedbackCollector;
import com.github.stkent.amplify.feedback.GooglePlayStoreFeedbackCollector;
import com.github.stkent.amplify.tracking.Amplify;
import com.isscroberto.dailyprayerandroid.di.DaggerAppComponent;

import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;
import io.realm.Realm;

public class DailyPrayerAndroid extends DaggerApplication {
    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize Realm.
        Realm.init(this);

        // Feedback.
        Amplify.initSharedInstance(this)
                .setPositiveFeedbackCollectors(new GooglePlayStoreFeedbackCollector())
                .setCriticalFeedbackCollectors(new DefaultEmailFeedbackCollector(getString(R.string.my_email)))
                .applyAllDefaultRules();
                //.setAlwaysShow(BuildConfig.DEBUG);


    }

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        return DaggerAppComponent.builder().application(this).build();
    }
}
