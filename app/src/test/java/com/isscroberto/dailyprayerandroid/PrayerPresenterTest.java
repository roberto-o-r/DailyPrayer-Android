package com.isscroberto.dailyprayerandroid;

import com.isscroberto.dailyprayerandroid.data.models.BingResponse;
import com.isscroberto.dailyprayerandroid.data.models.Channel;
import com.isscroberto.dailyprayerandroid.data.models.Image;
import com.isscroberto.dailyprayerandroid.data.models.Item;
import com.isscroberto.dailyprayerandroid.data.models.Prayer;
import com.isscroberto.dailyprayerandroid.data.models.RssResponse;
import com.isscroberto.dailyprayerandroid.data.source.ImageRemoteDataSource;
import com.isscroberto.dailyprayerandroid.data.source.PrayerLocalDataSource;
import com.isscroberto.dailyprayerandroid.data.source.PrayerRemoteDataSource;
import com.isscroberto.dailyprayerandroid.prayer.PrayerContract;
import com.isscroberto.dailyprayerandroid.prayer.PrayerPresenter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.mockito.Mockito.verify;

public class PrayerPresenterTest {

    private static RssResponse PRAYER;
    private static BingResponse IMAGE;

    @Mock
    private PrayerRemoteDataSource mPrayerDataSource;
    @Mock
    private PrayerLocalDataSource mPrayerLocalDataSource;
    @Mock
    private ImageRemoteDataSource mImageRemoteDataSource;
    @Mock
    private PrayerContract.View mPrayerView;

    @Captor
    private ArgumentCaptor<Callback<RssResponse>> mRssResponseCallbackCaptor;
    @Captor
    private ArgumentCaptor<Callback<BingResponse>> mBingResponseCallbackCaptor;

    private PrayerPresenter mPrayerPresenter;

    @Before
    public void setupPrayerPresenter() {
        // Inject the mocks in the test.
        MockitoAnnotations.initMocks(this);

        // Get a reference to the presenter.
        mPrayerPresenter = new PrayerPresenter(mPrayerDataSource, mPrayerLocalDataSource, mImageRemoteDataSource);
        mPrayerPresenter.takeView(mPrayerView);

        // Prepare response with mock prayer.
        PRAYER = new RssResponse();
        PRAYER.setChannel(new Channel());
        PRAYER.getChannel().setItem(new Item());
        PRAYER.getChannel().getItem().setDescription("Prayer Description");
        PRAYER.getChannel().getItem().setFav(false);
        PRAYER.getChannel().getItem().setGuid("GUID");
        PRAYER.getChannel().getItem().setTitle("Prayer Title");

        // Prepare response with mock image.
        IMAGE = new BingResponse();
        IMAGE.setImages(new ArrayList<Image>());
        IMAGE.getImages().add(new Image());
        IMAGE.getImages().get(0).setUrl("image.jpg");
    }

    @Test
    public void loadPrayerAndLoadIntoView() {
        Call<RssResponse> mockedCall = Mockito.mock(Call.class);

        // When presenter starts load prayer is called on takeView.

        // Then progress indicator is shown and prayer is requested from the remote repository.
        verify(mPrayerView).setLoadingIndicator(true);

        // Capture callback and invoke with mock prayer.
        verify(mPrayerDataSource).get(mRssResponseCallbackCaptor.capture());
        mRssResponseCallbackCaptor.getValue().onResponse(mockedCall, Response.success(PRAYER));

        // Then prayer gets loaded on view and loading indicator gets hidden.
        verify(mPrayerView).showPrayer(PRAYER.getChannel().getItem());
        verify(mPrayerView).setLoadingIndicator(true);

    }

    @Test
    public void loadImageAndLoadIntoView() {
        Call mockedCall = Mockito.mock(Call.class);

        // When presenter starts load image is called on takeView.

        // Capture callback and invoke with mock image.
        verify(mImageRemoteDataSource).get(mBingResponseCallbackCaptor.capture());
        mBingResponseCallbackCaptor.getValue().onResponse(mockedCall, Response.success(IMAGE));

        // Then image gets loaded on view.
        verify(mPrayerView).showImage("http://www.bing.com/" + IMAGE.getImages().get(0).getUrl());

    }

    @Test
    public void clickOnSave_SavePrayerLocally() {
        // Given a daily prayer.
        Prayer prayer = new Prayer();
        prayer.setTitle("Prayer Title");
        prayer.setTitle("Prayer Description");
        prayer.setId("prayerid");

        // When click on save.
        mPrayerPresenter.savePrayer(prayer);

        // Then prayer is saved locally.
        verify(mPrayerLocalDataSource).put(prayer);
    }

    @Test
    public void clickOnDelete_DeletePrayerLocally() {
        // Given a daily prayer.
        Prayer prayer = new Prayer();
        prayer.setTitle("Prayer Title");
        prayer.setTitle("Prayer Description");
        prayer.setId("prayerid");

        // When click on delete.
        mPrayerPresenter.deletePrayer(prayer.getId());

        // Then prayer is deleted locally.
        verify(mPrayerLocalDataSource).delete(prayer.getId());
    }

}
