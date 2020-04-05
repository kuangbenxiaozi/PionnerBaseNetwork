package com.pioneer.base.network.interceptor;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.pioneer.base.network.dns.HttpDNSUtil;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public final class HttpDNSInterceptor implements Interceptor {
    private static HttpDNSInterceptor INSTANCE = null;
    private Context context;
    private List<String> hostIpList;

    public static HttpDNSInterceptor getInstance(Context context) {
        if (null == INSTANCE) {
            synchronized (HttpDNSInterceptor.class) {
                if (null == INSTANCE) {
                    INSTANCE = new HttpDNSInterceptor(context.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }

    private HttpDNSInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        boolean hasHttpDNS = HttpDNSUtil.getIsSettingHttpDns(context);
        String requestHost = chain.request().url().host();
        if (hasHttpDNS && hasProxy(requestHost)) {
            return chain.proceed(getDNSProxy(chain));
        }
        return chain.proceed(chain.request());
    }

    private boolean hasProxy(String host) {
        String scheme = Uri.parse(host).getScheme();
        String proxyHost = null;
        if ("http".equalsIgnoreCase(scheme)) {
            proxyHost = System.getProperty("http.proxyHost");
        } else if ("https".equalsIgnoreCase(scheme)) {
            proxyHost = System.getProperty("https.proxyHost");
        }
        return !TextUtils.isEmpty(proxyHost);
    }

    private Request getDNSProxy(Chain chain) {
        Request originRequest = chain.request();
        HttpUrl httpUrl = originRequest.url();

        //original host和url
        String url = httpUrl.toString();
        String host = httpUrl.host();

        String hostIP = null;
        InetAddress address = null;
        hostIpList = HttpDNSUtil.getIPByHost_withProxy(host, context);

        if (hostIpList != null) {
            for (int i = 0; i < hostIpList.size(); i++) {
                try {
                    address = InetAddress.getByName(hostIpList.get(i));
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                if (address != null && !"".equals(address)) {
                    hostIP = hostIpList.get(i);
                    break;
                }
            }
        }
        Request.Builder builder = originRequest.newBuilder();
        if (hostIP != null) {
            if (HttpDNSUtil.isIpv4orIpv6(host) != -1) {
                builder.url(HttpDNSUtil.getIpUrl(url, host, hostIP));
                //header添加原来的host
                builder.header("host", host);
            }
        } else {
            //获取不到ip解析
            Log.e("HttpDNS", "can't get the ip , can't replace the host");
        }

        return builder.build();
    }
}
