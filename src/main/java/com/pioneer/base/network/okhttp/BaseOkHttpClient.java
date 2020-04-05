package com.pioneer.base.network.okhttp;

import android.content.Context;
import android.net.Uri;

import com.pioneer.base.network.https.BaseHostnameVerifier;
import com.pioneer.base.network.https.BaseSSLSocketClient;

import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;

public class BaseOkHttpClient {
    private Context applicationContext;
    private static OkHttpClientFactory factory;
    private static Dispatcher dispatcher;
    private static ConnectionPool connectionPool;


    private static BaseOkHttpClient instance;

    public BaseOkHttpClient create(Context context) {
        this.applicationContext = context.getApplicationContext();
        return this;
    }

    public OkHttpClient.Builder getOkHttpClient(String baseUrl) {
        if (null == applicationContext) {
            return null;
        }
        factory = new OkHttpClientFactory(applicationContext);
        return factory.createOkHttpClient()
                .dispatcher(getDispatcher())
                .connectionPool(getConnectionPool())
                .sslSocketFactory(BaseSSLSocketClient.getInstance().getSSLSocketFactory(), BaseSSLSocketClient.getInstance().getTrustManager())
                .hostnameVerifier(new BaseHostnameVerifier(Uri.parse(baseUrl).getHost()));
    }

    public static synchronized BaseOkHttpClient getInstance() {
        if (instance == null) {
            instance = new BaseOkHttpClient();
        }
        return instance;
    }

    private static synchronized Dispatcher getDispatcher() {
        if (dispatcher == null) {
            dispatcher = new Dispatcher();
        }
        return dispatcher;
    }

    private static synchronized ConnectionPool getConnectionPool() {
        if (connectionPool == null) {
            connectionPool = new ConnectionPool(10, 3, TimeUnit.MINUTES);
        }
        return connectionPool;
    }
}
