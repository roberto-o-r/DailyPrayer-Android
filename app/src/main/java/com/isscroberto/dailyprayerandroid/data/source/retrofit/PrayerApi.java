package com.isscroberto.dailyprayerandroid.data.source.retrofit;

import com.isscroberto.dailyprayerandroid.data.models.RssResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

public interface PrayerApi {

    @GET("en/daily-prayer-rss-feed")
    Call<RssResponse> get();

}
