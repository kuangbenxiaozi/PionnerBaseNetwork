package com.pioneer.base.network.interceptor;

import android.content.Context;

import com.pioneer.base.network.NetConstant;
import com.pioneer.base.network.net.NetMonitor;
import com.pioneer.base.network.net.NetworkStatus;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.Response;

public final class TimeInterceptor implements Interceptor {
    private static TimeInterceptor INSTANCE;
    private Context mContext;

    private TimeInterceptor(Context context) {
        mContext = context.getApplicationContext();
    }

    public static TimeInterceptor getInstance(Context context) {
        if (null == INSTANCE) {
            synchronized (TimeInterceptor.class) {
                if (null == INSTANCE) {
                    INSTANCE = new TimeInterceptor(context.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        return chain.proceed(setTimeOut(chain).request());
    }

    private Chain setTimeOut(Chain chain) {
        if (null != chain && null != mContext) {
            NetworkStatus netStatus = NetMonitor.getInstance(mContext).getStatus();
            int connectTimeout = NetConstant.NET_TIME_OUT_DATA_NOT_REACHABLE;
            int readTimeout = NetConstant.NET_TIME_OUT_DATA_NOT_REACHABLE;
            int writeTimeout = NetConstant.NET_TIME_OUT_DATA_NOT_REACHABLE;
            if (netStatus == NetworkStatus.Wifi) {
                connectTimeout = NetConstant.NET_TIME_OUT_DATA_WIFI;
                readTimeout = NetConstant.NET_TIME_OUT_DATA_WIFI;
                writeTimeout = NetConstant.NET_TIME_OUT_DATA_WIFI;
            } else if (netStatus == NetworkStatus.FourG) {
                connectTimeout = NetConstant.NET_TIME_OUT_DATA_4G;
                readTimeout = NetConstant.NET_TIME_OUT_DATA_4G;
                writeTimeout = NetConstant.NET_TIME_OUT_DATA_4G;
            } else if (netStatus == NetworkStatus.ThreeG) {
                connectTimeout = NetConstant.NET_TIME_OUT_DATA_3G;
                readTimeout = NetConstant.NET_TIME_OUT_DATA_3G;
                writeTimeout = NetConstant.NET_TIME_OUT_DATA_3G;
            } else if (netStatus == NetworkStatus.TwoG) {
                connectTimeout = NetConstant.NET_TIME_OUT_DATA_2G;
                readTimeout = NetConstant.NET_TIME_OUT_DATA_2G;
                writeTimeout = NetConstant.NET_TIME_OUT_DATA_2G;
            } else {
                connectTimeout = NetConstant.NET_TIME_OUT_DATA_NOT_REACHABLE;
                readTimeout = NetConstant.NET_TIME_OUT_DATA_NOT_REACHABLE;
                writeTimeout = NetConstant.NET_TIME_OUT_DATA_NOT_REACHABLE;
            }

            chain.withConnectTimeout(connectTimeout, TimeUnit.MILLISECONDS);
            chain.withReadTimeout(readTimeout, TimeUnit.MILLISECONDS);
            chain.withWriteTimeout(writeTimeout, TimeUnit.MILLISECONDS);
        }
        return chain;
    }
}
