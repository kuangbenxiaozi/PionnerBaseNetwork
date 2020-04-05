package com.pioneer.base.network.utils;

import com.pioneer.base.network.cookie.PersistentCookieStore;

import org.apache.http.NameValuePair;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

/**
 * 网络工具类
 */
public class HttpUtils {
    public static final int TYPE_WAP = 1;
    public static final int TYPE_NET = 2;
    public static final int TYPE_UNKNOWN = 3;

    public static final String WAP = "wap";
    public static final String NET = "net";
    public final static String http = "http://";
    public final static String https = "https://";
    public static final int CONNECT_TIME_OUT = 5000;
    public static final int READ_TIME_OUT = 30000;

    public static final HashMap<HttpUrl,List<Cookie>> cookieStore = new HashMap<>();
    public static PersistentCookieStore persistentCookieStore;


    /**
     * 默认的代理端口号
     */
    public final static int DEFAULT_PROXY_PORT = 80;

    public final static int HTTP_OK_CODE = 202;

    /**
     * build parameter list string in http url. Eg. k1=v1&k2=v2...
     * @param params list of key-value pair.
     * @return Return the parameter string in url.
     */
    public static String buildParamListInHttpRequest(List<NameValuePair> params) {
        StringBuffer sb = new StringBuffer();
        for(int index = 0; index < params.size(); index++) {
            try {
                String name = params.get(index).getName();
                String value = params.get(index).getValue();
                if(name != null && value != null) {
                    sb.append(URLEncoder.encode(name,"UTF-8"));
                    sb.append("=");
                    sb.append(URLEncoder.encode(value,"UTF-8"));
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
            if(index < params.size() - 1) {
                sb.append("&");
            }
        }
        return sb.toString();
    }

    public static String buildParamListInHttpRequestUrlEncode(List<NameValuePair> params) {
        StringBuffer sb = new StringBuffer();
        for(int index = 0; index < params.size(); index++) {
            try {
                sb.append(URLEncoder.encode(params.get(index).getName(),"UTF-8"));
                sb.append("=");
                sb.append(URLEncoder.encode(params.get(index).getValue(),"UTF-8"));
            } catch(UnsupportedEncodingException e) {
            }
            if(index < params.size() - 1) {
                sb.append("&");
            }
        }
        return sb.toString();
    }

    public static boolean isHttp(final String s) {
        if(s == null) {
            return false;
        }
        return s.startsWith(http);
    }

    public static boolean isHttps(final String s) {
        if(s == null) {
            return false;
        }
        return s.startsWith(https);
    }

    /**
     * parse integer text and get positive integer from it.
     * @param intValue integer string
     * @return positive integer and zero. return zero if exception or parsed
     * integer is negative.
     */
    public static int safePositiveInteger(String intValue) {
        int value = 0;
        try {
            value = Integer.parseInt(intValue);
            if(value < 0) {
                value = 0;
            }
        } catch(NumberFormatException e) {
            e.printStackTrace();
            value = 0;
        }
        return value;
    }

    public static Date strToDate(String str) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.parse(str);
    }

    /**
     * parse long text and get positive integer from it.
     * @param longValue long string
     * @return positive long and zero. return zero if exception or parsed long
     * is negative.
     */
    public static long safePositiveLong(String longValue) {
        long value = 0;
        try {
            value = Long.parseLong(longValue);
            if(value < 0) {
                value = 0;
            }
        } catch(NumberFormatException e) {
            e.printStackTrace();
            value = 0;
        }
        return value;
    }

    /**
     * 字符串替换
     * @param strVal  源
     * @param tagList 需要被替换的字符串列表
     * @return 替换完成的字符串
     */
    public static String filterXmlTags(String strVal,List<String> tagList) {
        String newVal = strVal;
        if(tagList != null) {
            for(String tag : tagList) {
                String startTag = "<" + tag + ">";
                String endTag = "</" + tag + ">";
                newVal = newVal.replaceAll(startTag,"");
                newVal = newVal.replaceAll(endTag,"");
            }
        }
        return newVal;
    }
}
