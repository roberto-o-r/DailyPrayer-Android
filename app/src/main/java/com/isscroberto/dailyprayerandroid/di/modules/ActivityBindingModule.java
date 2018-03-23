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

/**
 * We want Dagger.Android to create a Subcomponent which has a parent Component of whichever module ActivityBindingModule is on,
 * in our case that will be AppComponent. The beautiful part about this setup is that you never need to tell AppComponent that it is going to have all these subcomponents
 * nor do you need to tell these subcomponents that AppComponent exists.
 * We are also telling Dagger.Android that this generated SubComponent needs to include the specified modules and be aware of a scope annotation @ActivityScoped
 * When Dagger.Android annotation processor runs it will create the subcomponents for us.
 */
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
