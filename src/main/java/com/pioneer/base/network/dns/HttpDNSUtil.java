package com.pioneer.base.network.dns;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Dispatcher;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpDNSUtil {

    private static long CACHE_MAX_SIZE = 5 * 1024 * 1024;
    private static Cache instance = null;
    //HTTPDNS 请求ip
    public static final String HTTPDNS_ENTRY = "180.76.76.112";

    private static final String SHAREDPREF_SETTING = "SHAREDPREF_SETTING";
    public static final String KEY_HTTP_DNS = "key_httpdns";
    public static final String KEY_HTTPS = "key_https";

    private static Dispatcher dispatcher;

    private static OkHttpClient okHttpClient;//OkHttpClient实例调整为单例

    //服务端下发的开关
    private static boolean hostSwitch = false;


    private static synchronized OkHttpClient getOkHttpClient(Context context) {
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient().newBuilder()
                    .addNetworkInterceptor(new HttpDNSCacheInterceptor())
                    .cache(new Cache(initCache(context, "HTTPDNS"), CACHE_MAX_SIZE))
                    .build();
        }
        return okHttpClient;
    }

    /**
     * 转换url 主机头为ip地址
     *
     * @param url  原url
     * @param host 主机头
     * @param ip   服务器ip
     * @return
     */
    public static String getIpUrl(String url, String host, String ip) {
        if (url == null) {
            Log.d("HttpDNS", "URL NULL");
        }
        if (host == null) {
            Log.d("HttpDNS", "host NULL");
        }
        if (ip == null) {
            Log.d("HttpDNS", "ip NULL");
        }
        if (url == null || host == null || ip == null) return url;
        String ipUrl = url.replaceFirst(host, ip);
        return ipUrl;
    }

    /**
     * HTTPDNS解析
     * 连接有代理时
     * interceptor使用
     *
     * @param host
     * @return
     */
    public static List<String> getIPByHost_withProxy(String host, Context context) {
        HttpUrl httpUrl = getHttpUrl(host);

        if (httpUrl != null) {
            //与正式请求独立，所以这里新建一个OkHttpClient
            //添加对httpdns解析后的ip进行缓存
            OkHttpClient okHttpClient = getOkHttpClient(context);
            Request request = new Request.Builder()
                    .cacheControl(new CacheControl.Builder()
                            .maxAge(90, TimeUnit.SECONDS)
                            .build())
                    .url(httpUrl)
                    .get()
                    .build();

            ArrayList<String> hostIpList = null;
            try {
                String[] resultArr = null;
                Response response = okHttpClient.newCall(request).execute();
//                HTTPAnalUtil.sendMTJStatistic(context,"httpdnsCount","request_withProxy");
                if (response.isSuccessful()) {
                    String body = response.body().string();
                    resultArr = body.trim().split("\\s+");
                    hostIpList = new ArrayList<>(Arrays.asList(resultArr));
                }
                response.body().close();
                return hostIpList;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * HTTPDNS 解析
     * okhttp的dns 使用
     *
     * @param host
     * @param context
     * @return
     */
    public static String[] getIPByHost_noProxy(String host, Context context) {

        HttpUrl httpUrl = getHttpUrl(host);
        if (httpUrl != null) {
            //与正式请求独立，所以这里新建一个OkHttpClient
            //对httpdns解析后的ip进行缓存
            OkHttpClient okHttpClient = getOkHttpClient(context);
            Request request = new Request.Builder()
                    .cacheControl(new CacheControl.Builder()
                            .maxAge(90, TimeUnit.SECONDS)
                            .build())
                    .url(httpUrl)
                    .get()
                    .build();
            try {
                String[] resultArr = null;
                Response response = okHttpClient.newCall(request).execute();
//                HTTPAnalUtil.sendMTJStatistic(context,"httpdnsCount","request_noProxy");
                if (response.isSuccessful()) {
                    String body = response.body().string();
                    resultArr = body.trim().split("\\s+");
                }
                response.body().close();
                return resultArr;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static synchronized Cache getCache(Context context, String name) {
        if (instance == null) {
            new Cache(initCache(context, name), 5 * 1024 * 1024);
        }
        return instance;
    }

    private static File initCache(Context context, String name) {
        if (context != null) {
            final File baseDir = context.getCacheDir();
            if (baseDir != null) {
                final File cacheDir = new File(baseDir, name);
                return cacheDir;
            }
        }
        return null;
    }

    //保存https设置
    public static final void saveSettingHttps(Context context, boolean isChecked) {
        if (context == null) {
            return;
        }
        SharedPreferences systemSharedPref = context.getSharedPreferences(SHAREDPREF_SETTING, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = systemSharedPref.edit();
        editor.putInt(KEY_HTTPS, isChecked ? 1 : 0);
        editor.commit();
    }

    public static boolean getIsSettingHttps(Context context) {
        if (context == null) {
            return true;
        }
        SharedPreferences systemSharedPref = context.getSharedPreferences(SHAREDPREF_SETTING, Context.MODE_PRIVATE);
        int result = systemSharedPref.getInt(KEY_HTTPS, 1);
        return result == 1 ? true : false;
    }


    //保存httpdns设置
    public static final void saveSettingHttpDns(Context context, boolean isChecked) {
        if (context == null) {
            return;
        }
        SharedPreferences systemSharedPref = context.getSharedPreferences(SHAREDPREF_SETTING, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = systemSharedPref.edit();
        editor.putInt(KEY_HTTP_DNS, isChecked ? 1 : 0);
        editor.commit();
    }

    public static boolean getIsSettingHttpDns(Context context) {
        if (context == null) {
            return true;
        }

        if (!hostSwitch) {
            return false;
        }
        SharedPreferences systemSharedPref = context.getSharedPreferences(SHAREDPREF_SETTING, Context.MODE_PRIVATE);
        int result = systemSharedPref.getInt(KEY_HTTP_DNS, 1);
        return result == 1;
    }

    private static synchronized Dispatcher getDispatcher() {
        if (dispatcher == null) {
            dispatcher = new Dispatcher();
        }
        return dispatcher;
    }

    public static boolean isHostSwitch() {
        return hostSwitch;
    }

    public static synchronized void setHostSwitch(boolean hostSwitch) {
        HttpDNSUtil.hostSwitch = hostSwitch;
    }

    private static HttpUrl getHttpUrl(String host) {
        Uri.Builder uriBuilder = new Uri.Builder()
                .scheme("http")
                .authority(HTTPDNS_ENTRY)
                .appendQueryParameter("dn", host);
        URI uri = null;
        try {
            uri = URI.create(uriBuilder.build().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        HttpUrl httpUrl = HttpUrl.get(uri);
//        WMLog.i("HttpDNS", "Get httpurl in HttpDNSUtil:" + httpUrl.toString());
        return httpUrl;
    }

    /**
     * 判断是否为ipv4或者ipv6规范地址
     * -1:否
     * 4：ipv4
     * 6：ivp6
     *
     * @param host
     * @return
     */
    public static int isIpv4orIpv6(String host) {
        InetAddress address = null;
        try {
            address = InetAddress.getByName(host);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return -1;
        }
        if (address instanceof Inet4Address) {
            return 4;
        }
        if (address instanceof Inet6Address) {
            return 6;
        }
        return -1;
    }
}
