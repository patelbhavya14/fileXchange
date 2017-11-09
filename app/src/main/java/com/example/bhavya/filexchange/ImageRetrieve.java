package com.example.bhavya.filexchange;

import android.graphics.Bitmap;

/**
 * Created by bhaVYa on 03/09/17.
 */

public class ImageRetrieve {
    public String tag;
    public Bitmap bmp;
    public ImageRetrieve(String tag, Bitmap bmp) {
        this.tag = tag;
        this.bmp = bmp;
    }

    public String getTag() {
        return tag;
    }

    public Bitmap getBmp() {
        return bmp;
    }
}
