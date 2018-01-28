package com.isscroberto.dailyprayerandroid.prayer;

import com.isscroberto.dailyprayerandroid.BasePresenter;
import com.isscroberto.dailyprayerandroid.BaseView;
import com.isscroberto.dailyprayerandroid.data.models.Item;
import com.isscroberto.dailyprayerandroid.data.models.Prayer;

/**
 * Created by roberto.orozco on 11/09/2017.
 */

public interface PrayerContract {

    interface View extends BaseView<Presenter> {
        void showPrayer(Item prayer);
        void showError();
        void showImage(String url);
        void setLoadingIndicator(boolean active);
    }

    interface Presenter extends BasePresenter {
        void loadPrayer();
        void loadImage();
        void savePrayer(Prayer prayer);
        void deletePrayer(String id);
    }

}
