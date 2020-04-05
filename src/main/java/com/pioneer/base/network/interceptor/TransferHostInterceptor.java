package com.pioneer.base.network.interceptor;

import android.net.Uri;
import android.text.TextUtils;

import com.pioneer.base.network.NetConstant;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public final class TransferHostInterceptor implements Interceptor {
    private TransferHostInterceptor() {

    }

    public static class SingleInstance {
        private final static TransferHostInterceptor INSTANCE = new TransferHostInterceptor();

        public static TransferHostInterceptor getInstance() {
            return INSTANCE;
        }
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        return chain.proceed(processRequest(chain.request()));
    }


    private Request processRequest(Request request) {
        if (request == null) {
            return request;
        }

        Request.Builder newBuilder = request.newBuilder();
        String host = request.header(NetConstant.TRANSFER_HOST);
        if (!TextUtils.isEmpty(host)) {
            HttpUrl newUrl = parseUrl(host, request.url());

            if (null != newUrl) {
                return newBuilder
                        .url(newUrl)
                        .build();
            }
        }
        return newBuilder.build();
    }


    private HttpUrl parseUrl(String newHost, HttpUrl url) {
        if (TextUtils.isEmpty(newHost)) return url;
        Uri uri = Uri.parse(newHost);
        if (null != uri) {
            String scheme = uri.getScheme();
            String host = uri.getHost();

            if (!TextUtils.isEmpty(scheme) && !TextUtils.isEmpty(host)) {
                return url.newBuilder()
                        .scheme(scheme)
                        .host(host)
                        .build();
            }

        }
        return url;
    }
}
