package com.pioneer.base.network.dns;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public class HttpDNSCacheInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());
        return originalResponse.newBuilder()
                .header("Cache-Control", "public, max-age=240").build();//在返回header中加入缓存消息,httpdns解析结果缓存时间为4分钟
    }
}