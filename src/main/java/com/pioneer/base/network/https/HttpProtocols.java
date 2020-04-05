package com.pioneer.base.network.https;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Protocol;

public class HttpProtocols {
    private final List<Protocol> mProtocols = new ArrayList<Protocol>();

    private HttpProtocols() {
        mProtocols.add(okhttp3.Protocol.HTTP_1_1);
        mProtocols.add(okhttp3.Protocol.HTTP_2);
    }

    public static class SingleInstance {
        public static final HttpProtocols INSTANCE = new HttpProtocols();
    }

    public static HttpProtocols getInstance() {
        return SingleInstance.INSTANCE;
    }

    public List<Protocol> getProtocols() {
        return mProtocols;
    }
}
