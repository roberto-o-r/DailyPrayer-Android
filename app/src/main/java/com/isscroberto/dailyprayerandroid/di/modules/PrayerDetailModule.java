package com.isscroberto.dailyprayerandroid.di.modules;

import com.isscroberto.dailyprayerandroid.di.ActivityScoped;
import com.isscroberto.dailyprayerandroid.prayerdetail.PrayerDetailContract;
import com.isscroberto.dailyprayerandroid.prayerdetail.PrayerDetailPresenter;

import dagger.Binds;
import dagger.Module;

/**
 * Created by roberto.orozco on 23/03/2018.
 */

@Module
public abstract class PrayerDetailModule {

    @ActivityScoped
    @Binds
    abstract PrayerDetailContract.Presenter prayerDetailPresenter (PrayerDetailPresenter presenter);

}
