package com.isscroberto.dailyprayerandroid.prayerssaved;

import com.isscroberto.dailyprayerandroid.data.models.Prayer;
import com.isscroberto.dailyprayerandroid.data.source.ImageRemoteDataSource;
import com.isscroberto.dailyprayerandroid.data.source.PrayerLocalDataSource;
import com.isscroberto.dailyprayerandroid.data.source.PrayerRemoteDataSource;
import com.isscroberto.dailyprayerandroid.prayer.PrayerContract;

import io.realm.RealmResults;

/**
 * Created by roberto.orozco on 22/09/2017.
 */

public class PrayersSavedPresenter implements PrayersSavedContract.Presenter {

    private final PrayerLocalDataSource mPrayerLocalDataSource;
    private final PrayersSavedContract.View mView;

    public PrayersSavedPresenter(PrayerLocalDataSource prayerLocalDataSource, PrayersSavedContract.View view) {
        mPrayerLocalDataSource = prayerLocalDataSource;
        mView = view;

        view.setPresenter(this);
    }

    @Override
    public void start() {
        loadPrayers();
    }

    @Override
    public void loadPrayers() {
        RealmResults<Prayer> prayers = mPrayerLocalDataSource.get();
        mView.showPrayers(prayers);
    }
}
