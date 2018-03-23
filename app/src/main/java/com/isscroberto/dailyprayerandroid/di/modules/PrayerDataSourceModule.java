package com.isscroberto.dailyprayerandroid.di.modules;

import com.isscroberto.dailyprayerandroid.data.source.BaseDataSource;
import com.isscroberto.dailyprayerandroid.data.source.ImageRemoteDataSource;
import com.isscroberto.dailyprayerandroid.data.source.PrayerLocalDataSource;
import com.isscroberto.dailyprayerandroid.data.source.PrayerRemoteDataSource;
import com.isscroberto.dailyprayerandroid.di.Local;
import com.isscroberto.dailyprayerandroid.di.Remote;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;

/**
 * Created by roberto.orozco on 21/03/2018.
 */

@Module
public abstract class PrayerDataSourceModule {

    @Singleton
    @Binds
    @Remote
    abstract BaseDataSource prayerRemoteDataSource (PrayerRemoteDataSource prayerRemoteDataSource);

    @Singleton
    @Binds
    @Local
    abstract PrayerLocalDataSource prayerLocalDataSource (PrayerLocalDataSource prayerLocalDataSource);

    @Singleton
    @Binds
    @Remote
    abstract ImageRemoteDataSource imageRemoteDataSource (ImageRemoteDataSource imageRemoteDataSource);

}
