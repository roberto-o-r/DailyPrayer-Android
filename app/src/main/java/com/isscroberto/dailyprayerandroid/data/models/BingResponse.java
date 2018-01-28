package com.isscroberto.dailyprayerandroid.data.models;

import java.util.ArrayList;

/**
 * Created by roberto.orozco on 11/09/2017.
 */

public class BingResponse {

    public BingResponse() {
        images = new ArrayList<Image>();
    }

    private ArrayList<Image> images;

    public ArrayList<Image> getImages() {
        return images;
    }

    public void setImages(ArrayList<Image> images) {
        images = images;
    }
}
