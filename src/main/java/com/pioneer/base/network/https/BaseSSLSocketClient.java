package com.pioneer.base.network.https;

import java.security.KeyStore;
import java.util.Arrays;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class BaseSSLSocketClient {
    private final SSLSocketFactory mDefaultSSLSocketFactory;
    private final X509TrustManager mDefaultTrustManager;

    private BaseSSLSocketClient() {
        // 初始化默认的证书校验
        try {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                    TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore) null);
            TrustManager[] defaultTrustManagers = trustManagerFactory.getTrustManagers();
            if (1 != defaultTrustManagers.length || !(defaultTrustManagers[0] instanceof X509TrustManager)) {
                throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(defaultTrustManagers));
            }

            mDefaultTrustManager = (X509TrustManager) defaultTrustManagers[0];

            SSLContext sslDefaultContext = SSLContext.getInstance("TLS");
            sslDefaultContext.init(null, new TrustManager[]{mDefaultTrustManager}, null);
            mDefaultSSLSocketFactory = sslDefaultContext.getSocketFactory();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 获取这个SSLSocketFactory
    public SSLSocketFactory getSSLSocketFactory() {
        return mDefaultSSLSocketFactory;
    }

    // 获取TrustManager
    public X509TrustManager getTrustManager() {
        return mDefaultTrustManager;
    }

    private static class SingletonInstance {
        private static final BaseSSLSocketClient instance = new BaseSSLSocketClient();
    }

    public static BaseSSLSocketClient getInstance() {
        return SingletonInstance.instance;
    }
}
