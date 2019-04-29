package com.isscroberto.dailyprayerandroid.data.source;

import com.isscroberto.dailyprayerandroid.data.models.BingResponse;
import com.isscroberto.dailyprayerandroid.data.source.retrofit.ImageApi;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by roberto.orozco on 11/09/2017.
 */

@Singleton
public class ImageRemoteDataSource implements BaseDataSource<BingResponse> {

    @Inject
    public ImageRemoteDataSource(){}

    @Override
    public void get(final Callback<BingResponse> callback) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://www.bing.com/").addConverterFactory(GsonConverterFactory.create()).build();
        ImageApi api = retrofit.create(ImageApi.class);
        Call<BingResponse> apiCall = api.get();
        apiCall.enqueue(callback);
    }

}
