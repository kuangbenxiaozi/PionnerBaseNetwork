package com.pioneer.base.network.okhttp;

import android.content.Context;

import okhttp3.OkHttpClient;

public class OkHttpClientFactory {
    private Context mContext;
    private static OkHttpClient instance;

    public OkHttpClientFactory(Context context) {
        this.mContext = context.getApplicationContext();
    }

    private static synchronized OkHttpClient getInstance() {
        if (null == instance) {
            instance = new OkHttpClient();
        }
        return instance;
    }

    public OkHttpClient.Builder createOkHttpClient() {
        return getInstance().newBuilder();
    }
}
