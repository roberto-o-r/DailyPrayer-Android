package com.isscroberto.dailyprayerandroid.data.source.retrofit;

import com.isscroberto.dailyprayerandroid.data.models.BingResponse;
import com.isscroberto.dailyprayerandroid.data.models.RssResponse;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by roberto.orozco on 11/09/2017.
 */

public interface ImageApi {

    @GET("HPImageArchive.aspx?format=js&idx=0&n=1&mkt=en-US")
    Call<BingResponse> get();

}
