package com.isscroberto.dailyprayerandroid.prayer;

import android.annotation.SuppressLint;

import com.isscroberto.dailyprayerandroid.data.models.BingResponse;
import com.isscroberto.dailyprayerandroid.data.models.Item;
import com.isscroberto.dailyprayerandroid.data.models.Prayer;
import com.isscroberto.dailyprayerandroid.data.models.RssResponse;
import com.isscroberto.dailyprayerandroid.data.source.ImageRemoteDataSource;
import com.isscroberto.dailyprayerandroid.data.source.PrayerLocalDataSource;
import com.isscroberto.dailyprayerandroid.data.source.PrayerRemoteDataSource;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.annotation.Nonnull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by roberto.orozco on 11/09/2017.
 */

public class PrayerPresenter implements PrayerContract.Presenter {

    private final PrayerRemoteDataSource mPrayerDataSource;
    private final PrayerLocalDataSource mPrayerLocalDataSource;
    private final ImageRemoteDataSource mImageDataSource;
    private PrayerContract.View mView;

    public PrayerPresenter(PrayerRemoteDataSource prayerDataSource, PrayerLocalDataSource prayerLocalDataSource, ImageRemoteDataSource imageDataSource) {
        mPrayerDataSource = prayerDataSource;
        mPrayerLocalDataSource = prayerLocalDataSource;
        mImageDataSource = imageDataSource;
    }

    @Override
    public void reload() {
        loadPrayer();
        loadImage();
    }

    @Override
    public void loadPrayer() {
        mView.setLoadingIndicator(true);
        mPrayerDataSource.get(new Callback<RssResponse>() {
            @Override
            public void onResponse(@Nonnull Call<RssResponse> call, @Nonnull Response<RssResponse> response) {
                // Verify that response is not empty.
                if(response.body() != null) {
                        Item prayer = response.body().getChannel().getItem();

                    // Create prayer id based on the date.
                    @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    df.setTimeZone(TimeZone.getTimeZone("gmt"));
                    String id = df.format(new Date());

                    // Add year to prayer title.
                    prayer.setTitle(prayer.getTitle() + ", " + id.substring(0, id.indexOf("-")));

                    // Verify if prayer is saved.
                    prayer.setFav(false);
                    if (mPrayerLocalDataSource.get(id) != null) {
                        prayer.setFav(true);
                    }
                    if(mView != null) mView.showPrayer(prayer);
                } else {
                    mView.showError();
                }

                if(mView != null) mView.setLoadingIndicator(false);

            }

            @Override
            public void onFailure(@Nonnull Call<RssResponse> call, @Nonnull Throwable t) {
                if(mView != null) {
                    mView.showError();
                    mView.logError(t.getMessage());
                    mView.setLoadingIndicator(false);
                }
            }
        });
    }

    @Override
    public void loadImage() {
        mImageDataSource.get(new Callback<BingResponse>() {
            @Override
            public void onResponse(@Nonnull Call<BingResponse> call, @Nonnull Response<BingResponse> response) {
                // Verify response.
                if(response.body() != null) {
                    if (!response.body().getImages().isEmpty() && mView != null) {
                        mView.showImage("http://www.bing.com/" + response.body().getImages().get(0).getUrl());
                    }
                }
            }

            @Override
            public void onFailure(@Nonnull Call<BingResponse> call, @Nonnull Throwable t) {
                // Don't do nothing.
            }
        });
    }

    @Override
    public void savePrayer(Prayer prayer) {
        mPrayerLocalDataSource.put(prayer);
    }

    @Override
    public void deletePrayer(String id) {
        mPrayerLocalDataSource.delete(id);
    }

    @Override
    public void takeView(PrayerContract.View prayerView) {
        this.mView = prayerView;
    }

    @Override
    public void dropView() {
        this.mView = null;
    }
}
