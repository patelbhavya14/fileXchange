package com.example.bhavya.filexchange;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageButton;

/**
 * Created by bhaVYa on 03/09/17.
 */

class FileUpload {
    public String tag;
    public String url;
    public String type;
    public String key;

    public String getTag() {
        return tag;
    }

    public String getUrl() {
        return url;
    }
    public FileUpload(String tag, String url, String type, String key) {
        this.tag = tag;
        this.url = url;
        this.type = type;
        this.key = key;
    }
    public FileUpload() {

    }

}
