package com.isscroberto.dailyprayerandroid.di.modules;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.isscroberto.dailyprayerandroid.di.ActivityScoped;
import com.isscroberto.dailyprayerandroid.prayer.PrayerActivity;
import com.isscroberto.dailyprayerandroid.prayer.PrayerContract;
import com.isscroberto.dailyprayerandroid.prayer.PrayerPresenter;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

/**
 * Created by roberto.orozco on 23/02/2018.
 */

@Module
public abstract class PrayerModule {

    @ActivityScoped
    @Binds abstract PrayerContract.Presenter prayerPresenter (PrayerPresenter presenter);

    @ActivityScoped
    @Provides
    static FirebaseAnalytics firebaseAnalytics(PrayerActivity activity) {
        return FirebaseAnalytics.getInstance(activity);
    }


}
