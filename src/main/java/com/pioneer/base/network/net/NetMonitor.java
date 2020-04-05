
package com.pioneer.base.network.net;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.pioneer.base.network.net.callback.IMonitorListener;
import com.pioneer.base.network.net.callback.INetMonitor;

import java.util.ArrayList;

/**
 * 网络状态监控组件实现类
 *
 */
public class NetMonitor implements INetMonitor {
    private static NetMonitor sInstance = null;
    private ConnectivityManager mConnectivityManager = null;
    private WifiManager mWifiManager = null;
    private TelephonyManager mTelephonyManager = null;
    private Context mContext = null;
    private ConnectionChangeReceiver mConnectionChangeReceiver = new ConnectionChangeReceiver();
    private ArrayList<IMonitorListener> mListeners = new ArrayList<IMonitorListener>();
    private NetworkStatus mStatus = NetworkStatus.NotReachable;
    private boolean hasRegistered = false;

    /**
     * 网络变化网络监听
     * 
     * @author yuankai
     * @version 1.0
     * @data 2012-7-10
     */
    private class ConnectionChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 回调
            final NetworkStatus newStatus = getStatus();
            if (mStatus != newStatus) {
                onConnectionChange(newStatus);
            }
        }
    }

    private NetMonitor(Context context) {
        mContext = context.getApplicationContext();
        mConnectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        mTelephonyManager = (TelephonyManager) mContext
                .getSystemService(Context.TELEPHONY_SERVICE);

        mStatus = getStatus();
    }

    @Override
    public void startMonitor() {
        registerReceiver();
    }

    @Override
    public void stopMonitor() {
        unRegisterReceiver();
        mListeners.clear();
    }

    public static NetMonitor getInstance(Context context) {
        if (sInstance == null) {
            synchronized (NetMonitor.class) {
                if (sInstance == null) {
                    sInstance = new NetMonitor(context);
                }
            }
        }
        return sInstance;
    }

    private void registerReceiver() {
        if (!hasRegistered) {
            IntentFilter filter = new IntentFilter(
                    ConnectivityManager.CONNECTIVITY_ACTION);
            mContext.registerReceiver(mConnectionChangeReceiver, filter);
            hasRegistered = true;
        }
    }

    private void unRegisterReceiver() {
        if (hasRegistered) {
            mContext.unregisterReceiver(mConnectionChangeReceiver);
            hasRegistered = false;
        }
    }

    @Override
    public boolean isReachable() {
        checkConnectivity();
        if (mConnectivityManager != null) {
            NetworkInfo networkinfo = mConnectivityManager.getActiveNetworkInfo();
            if (networkinfo == null || !networkinfo.isAvailable()) {
                return false;
            }
        }
        return true;
    }

    private void checkConnectivity() {
        if (mConnectivityManager == null && mContext != null) {
            mConnectivityManager = (ConnectivityManager) mContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
        }
    }

    @Override
    public NetworkStatus getStatus() {
        if (!isReachable()) {
            return NetworkStatus.NotReachable;
        } else {
            checkConnectivity();
            if (mConnectivityManager == null) {
                return NetworkStatus.NotReachable;
            }
            NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (networkInfo == null) {
                return NetworkStatus.NotReachable;
            }
            final int type = networkInfo.getType();
            if (type == ConnectivityManager.TYPE_WIFI) {
                return NetworkStatus.Wifi;
            } else {
                final int subType = mTelephonyManager.getNetworkType();
                switch (subType) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN:
                    case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                        return NetworkStatus.TwoG;
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                        return NetworkStatus.ThreeG;
                    case TelephonyManager.NETWORK_TYPE_LTE:
                        return NetworkStatus.FourG;
                    default:
                        return NetworkStatus.TwoG;
                }
            }
        }
    }

    public String getNetworkType() {
		NetworkInfo info = mConnectivityManager.getActiveNetworkInfo();
		if (info == null) {
			return null;
		}
		if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
			int type = mTelephonyManager.getNetworkType();
			switch (type) {
			case TelephonyManager.NETWORK_TYPE_EDGE:// (Constants.TAG,
													// "网络类型为EDGE");
				return "EDGE";

			case TelephonyManager.NETWORK_TYPE_GPRS:// (Constants.TAG,
													// "网络类型为GPRS");
				return "GPRS";

			case TelephonyManager.NETWORK_TYPE_UMTS:// (Constants.TAG,
													// "网络类型为UMTS");
				return "UMTS";

			case TelephonyManager.NETWORK_TYPE_CDMA:// (Constants.TAG,
													// "网络类型为CDMA");
				return "CDMA";

			case TelephonyManager.NETWORK_TYPE_UNKNOWN:// (Constants.TAG,
														// "网络类型未知");
				return "UNKNOWN";
			default:
				return "Type:" + type;
			}
		} else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
			return "WIFI";
		}
		return null;
	}

    public String getIMEI() {
		return mTelephonyManager.getDeviceId();
	}

    @Override
    public boolean isWifiReachable() {
        return mWifiManager.isWifiEnabled();
    }

    @Override
    public boolean registListener(IMonitorListener listener) {
        synchronized (mListeners) {
            if (listener != null && !mListeners.contains(listener)) {
                return mListeners.add(listener);
            } else {
                return false;
            }
        }
    }

    @Override
    public boolean unRegistListener(IMonitorListener listener) {
        synchronized (mListeners) {
            if (listener != null) {
                return mListeners.remove(listener);
            } else {
                return false;
            }
        }
    }

    private void onConnectionChange(NetworkStatus status) {
        mStatus = status;
        synchronized (mListeners) {
            final int size = mListeners.size();
            for (int i = 0; i < size; ++i) {
                mListeners.get(i).onConnectionChange(status);
            }
        }
    }

    public boolean isWifi() {
        NetworkInfo activeNetInfo = mConnectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

	public boolean isWap() {
		final NetworkInfo info = mConnectivityManager.getActiveNetworkInfo();
		if (info != null && info.getExtraInfo() != null) {
			return info.getExtraInfo().endsWith("wap");
		} else {
			return false;
		}
	}

	public String getLocalMacAddress() {
		if (mWifiManager == null) {
			return "";
		}
		WifiInfo info = mWifiManager.getConnectionInfo();
		if (info == null) {
			return "";
		}
		return info.getMacAddress();
	}

	public void addTelephonyListener(PhoneStateListener listener, int events) {
		mTelephonyManager.listen(listener, events);
	}
}
