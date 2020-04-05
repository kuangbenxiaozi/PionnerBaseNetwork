
package com.pioneer.base.network.net.callback;

import com.pioneer.base.network.net.NetworkStatus;

/**
 * 网络监控组件对外监听者
 *
 */
public interface IMonitorListener {
	/**
	 * 网络连接类型变化
	 * 
	 * @param status
	 *            类型
	 * @see NetworkStatus
	 */
	public void onConnectionChange(NetworkStatus status);
}
