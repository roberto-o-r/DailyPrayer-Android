package com.isscroberto.dailyprayerandroid.prayerssaved;

import com.isscroberto.dailyprayerandroid.BasePresenter;
import com.isscroberto.dailyprayerandroid.BaseView;
import com.isscroberto.dailyprayerandroid.data.models.Prayer;

import java.util.ArrayList;

import io.realm.RealmResults;

/**
 * Created by roberto.orozco on 22/09/2017.
 */

public interface PrayersSavedContract {

    interface View extends BaseView<Presenter> {
        void showPrayers(RealmResults<Prayer> prayers);
    }

    interface Presenter extends BasePresenter<View> {
        void loadPrayers();
    }

}
