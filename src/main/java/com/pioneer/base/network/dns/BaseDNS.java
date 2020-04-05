package com.pioneer.base.network.dns;

import android.content.Context;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Dns;

public class BaseDNS implements Dns {
    private Context mContext;
    private String[] ipResult;
    private List<InetAddress> result;

    public BaseDNS(Context context) {
        mContext = context.getApplicationContext();
    }

    /**
     * @param hostname
     * @return
     * @throws UnknownHostException
     */
    @Override
    public List<InetAddress> lookup(String hostname) throws UnknownHostException {

        InetAddress addr = null;
        ipResult = HttpDNSUtil.getIPByHost_noProxy(hostname, mContext);
        if (ipResult == null) {
            result = new ArrayList<>(Dns.SYSTEM.lookup(hostname));
            return result;
        } else {
            result = new ArrayList<InetAddress>();
            for (String ip : ipResult) {
                try {
                    addr = InetAddress.getByName(ip);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                if (addr != null && !"".equals(addr)) {
                    result.add(addr);
                }
            }

            //添加系统dns解析结果到队尾
            result.addAll(new ArrayList<InetAddress>(Dns.SYSTEM.lookup(hostname)));
            return result;
        }
    }
}
