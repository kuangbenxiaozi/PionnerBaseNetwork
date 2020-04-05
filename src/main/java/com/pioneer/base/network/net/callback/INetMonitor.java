package com.pioneer.base.network.net.callback;


import com.pioneer.base.network.net.NetworkStatus;

/**
 * 网络状态监听组件，单实例组件，职责： 1.监听和回调当前网络类型（2g，3g，wifi） 2.获取当前网络是否可用（reachable）
 */
public interface INetMonitor {
    /**
     * 获取当前网络是否可用
     *
     * @return 是否可用
     */
    public boolean isReachable();

    /**
     * 获取当前网络状态
     *
     * @return 类型
     * @see NetworkStatus
     */
    public NetworkStatus getStatus();

    /**
     * 获取wifi是否可用
     *
     * @return 是否可用
     */
    public boolean isWifiReachable();

    /**
     * 注册变化监听者
     *
     * @param listener 监听者
     * @return 是否注册成功，如果已存在或参数不合法则返回false
     */
    public boolean registListener(IMonitorListener listener);

    /**
     * 反注册变化监听者
     *
     * @param listener 监听者
     * @return 是否反注册成功，如果不存在或参数不合法则返回false
     */
    public boolean unRegistListener(IMonitorListener listener);

    public void startMonitor();

    public void stopMonitor();
}
