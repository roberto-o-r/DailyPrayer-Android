package com.isscroberto.dailyprayerandroid.prayerdetail;

import com.isscroberto.dailyprayerandroid.data.source.PrayerLocalDataSource;

/**
 * Created by roberto.orozco on 23/09/2017.
 */

public class PrayerDetailPresenter implements PrayerDetailContract.Presenter{

    private final PrayerLocalDataSource mPrayerLocalDataSource;
    private PrayerDetailContract.View mView;

    public PrayerDetailPresenter(PrayerLocalDataSource prayerLocalDataSource) {
        mPrayerLocalDataSource = prayerLocalDataSource;
    }

    @Override
    public void deletePrayer(String id) {
        mPrayerLocalDataSource.delete(id);
    }

    @Override
    public void takeView(PrayerDetailContract.View view) {
        this.mView = view;
    }

    @Override
    public void dropView() {
        this.mView = null;
    }
}
