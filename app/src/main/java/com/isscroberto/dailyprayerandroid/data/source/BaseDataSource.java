package com.isscroberto.dailyprayerandroid.data.source;

import retrofit2.Callback;

/**
 * Created by roberto.orozco on 11/09/2017.
 */

public interface BaseDataSource<T> {

    void get(Callback<T> callback);
}
