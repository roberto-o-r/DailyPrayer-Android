package com.isscroberto.dailyprayerandroid.prayerdetail;

import com.isscroberto.dailyprayerandroid.data.source.PrayerLocalDataSource;

/**
 * Created by roberto.orozco on 23/09/2017.
 */

public class PrayerDetailPresenter implements PrayerDetailContract.Presenter {

    private final PrayerLocalDataSource mPrayerLocalDataSource;
    private final PrayerDetailContract.View mView;

    public PrayerDetailPresenter(PrayerLocalDataSource prayerLocalDataSource, PrayerDetailContract.View view) {
        mPrayerLocalDataSource = prayerLocalDataSource;
        mView = view;

        view.setPresenter(this);
    }

    @Override
    public void start() {

    }

    @Override
    public void deletePrayer(String id) {
        mPrayerLocalDataSource.delete(id);
    }
}
