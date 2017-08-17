package com.timurkaSoft.AntiAgent.glide;

import android.content.Context;

import com.bumptech.glide.load.model.Headers;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.load.model.stream.BaseGlideUrlLoader;

/**
 * Created by Paulina Sadowska on 04.09.2016.
 */

class HeaderLoader extends BaseGlideUrlLoader<String> {

    private static final Headers REQUEST_HEADERS = new LazyHeaders.Builder()
            .addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.125 YaBrowser/17.7.1.720 Yowser/2.5 Safari/537.36")
            .build();

    HeaderLoader(Context context) {
        super(context);
    }

    @Override
    protected String getUrl(String model, int width, int height) {
        return model;
    }

    @Override
    protected Headers getHeaders(String model, int width, int height) {
        return REQUEST_HEADERS;
    }
}
