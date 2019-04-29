package com.isscroberto.dailyprayerandroid.prayerssaved;

import com.isscroberto.dailyprayerandroid.data.models.Prayer;
import com.isscroberto.dailyprayerandroid.data.source.PrayerLocalDataSource;

import javax.inject.Inject;

import io.realm.RealmResults;

/**
 * Created by roberto.orozco on 22/09/2017.
 */

public class PrayersSavedPresenter implements PrayersSavedContract.Presenter {

    private final PrayerLocalDataSource mPrayerLocalDataSource;
    private PrayersSavedContract.View mView;

    @Inject
    public PrayersSavedPresenter(PrayerLocalDataSource prayerLocalDataSource) {
        mPrayerLocalDataSource = prayerLocalDataSource;
    }

    @Override
    public void loadPrayers() {
        RealmResults<Prayer> prayers = mPrayerLocalDataSource.get();
        mView.showPrayers(prayers);
    }

    @Override
    public void takeView(PrayersSavedContract.View view) {
        this.mView = view;
        loadPrayers();
    }

    @Override
    public void dropView() {
        this.mView = null;
    }
}
