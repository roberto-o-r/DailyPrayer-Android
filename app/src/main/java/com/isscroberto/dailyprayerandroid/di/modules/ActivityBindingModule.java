package com.isscroberto.dailyprayerandroid.di.modules;

/**
 * Created by roberto.orozco on 14/03/2018.
 */

import com.isscroberto.dailyprayerandroid.di.ActivityScoped;
import com.isscroberto.dailyprayerandroid.prayer.PrayerActivity;
import com.isscroberto.dailyprayerandroid.prayerdetail.PrayerDetailActivity;
import com.isscroberto.dailyprayerandroid.prayerssaved.PrayersSavedActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBindingModule {
    @ActivityScoped
    @ContributesAndroidInjector(modules = {
            PrayerModule.class,
            PrayerDataSourceModule.class})
    abstract PrayerActivity prayerActivity();

    @ActivityScoped
    @ContributesAndroidInjector(modules = {
            PrayerDetailModule.class,
            PrayerDataSourceModule.class})
    abstract PrayerDetailActivity prayerDetailActivity();

    @ActivityScoped
    @ContributesAndroidInjector(modules = {
            PrayersSavedModule.class,
            PrayerDataSourceModule.class})
    abstract PrayersSavedActivity prayersSavedActivity();
}
