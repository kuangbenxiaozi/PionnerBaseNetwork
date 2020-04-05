package com.pioneer.base.network.retrofitmanager;

import android.content.Context;

import com.pioneer.base.network.NetConstant;
import com.pioneer.base.network.interceptor.CookieManagerInterceptor;
import com.pioneer.base.network.interceptor.HttpDNSInterceptor;
import com.pioneer.base.network.interceptor.TimeInterceptor;
import com.pioneer.base.network.interceptor.TransferHostInterceptor;
import com.pioneer.base.network.okhttp.BaseOkHttpClient;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class BaseRetrofitManager {
    private Retrofit mRetrofit;

    private BaseRetrofitManager() {

    }

    private static class SingleHolder {
        private static BaseRetrofitManager INSTANCE = new BaseRetrofitManager();
    }

    public static BaseRetrofitManager getInstance() {
        return SingleHolder.INSTANCE;
    }

    public Retrofit createRetrofit(Context context) {
        return createRetrofit(context,NetConstant.BASE_HOST);
    }

    public Retrofit createRetrofit(Context context,String host) {
        if (null == mRetrofit) {
            OkHttpClient.Builder httpClient = BaseOkHttpClient.getInstance()
                    .create(context.getApplicationContext())
                    .getOkHttpClient(host);

            httpClient.addInterceptor(TransferHostInterceptor.SingleInstance.getInstance());
            httpClient.addInterceptor(TimeInterceptor.getInstance(context.getApplicationContext()));
            httpClient.addInterceptor(CookieManagerInterceptor.getInstance(context.getApplicationContext()));
            httpClient.addInterceptor(HttpDNSInterceptor.getInstance(context.getApplicationContext()));

            mRetrofit = new Retrofit.Builder()
                    .client(httpClient.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .baseUrl(host)
                    .build();
        }
        return mRetrofit;
    }
}
