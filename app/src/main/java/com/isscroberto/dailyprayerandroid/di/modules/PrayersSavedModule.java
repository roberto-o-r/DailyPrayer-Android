package com.isscroberto.dailyprayerandroid.di.modules;

import com.isscroberto.dailyprayerandroid.di.ActivityScoped;
import com.isscroberto.dailyprayerandroid.prayerssaved.PrayersSavedContract;
import com.isscroberto.dailyprayerandroid.prayerssaved.PrayersSavedPresenter;

import dagger.Binds;
import dagger.Module;

/**
 * Created by roberto.orozco on 23/03/2018.
 */

@Module
public abstract class PrayersSavedModule {

    @ActivityScoped
    @Binds
    abstract PrayersSavedContract.Presenter prayersSavedPresenter (PrayersSavedPresenter presenter);

}
