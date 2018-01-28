package com.isscroberto.dailyprayerandroid.prayerdetail;

import com.isscroberto.dailyprayerandroid.BasePresenter;
import com.isscroberto.dailyprayerandroid.BaseView;

/**
 * Created by roberto.orozco on 23/09/2017.
 */

public interface PrayerDetailContract {
    interface View extends BaseView<Presenter> {
    }

    interface Presenter extends BasePresenter {
        void deletePrayer(String id);
    }
}
