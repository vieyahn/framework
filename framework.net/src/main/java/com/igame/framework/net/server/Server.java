package com.igame.framework.net.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Title: Server.java
 * @Author Allen allen.ime@gmail.com
 * @Date 2014年10月20日 下午6:06:04
 * @Description: 服务器初始化
 */
public abstract class Server implements Runnable {
	private static final Logger log = LoggerFactory.getLogger(Server.class);

	// 服务器名字
	private String server_name;
	// 服务器Id
	public int server_id;
	// 服务器平台
	private String server_platform;
	// 默认主线程
	public static final String DEFAULT_MAIN_THREAD = "Main";

	protected Server(String server_name, int server_id, String server_platform) {
		// 服务器初始化
		this.server_name = server_name;
		this.server_id = server_id;
		this.server_platform = server_platform;
	}

	/**
	 * 服务器运行
	 */
	public void run() {
		// 注册服务器关闭线程
		Runtime.getRuntime().addShutdownHook(new Thread(new CloseByExit(server_name)));
	}

	/**
	 * 获得服务器名称
	 * 
	 * @return 服务器名称
	 */
	public String getServerName() {
		return this.server_name;
	}

	/**
	 * 获得服务器ID
	 * 
	 * @return 服务器ID
	 */
	public int getServerId() {
		return server_id;
	}

	/**
	 * 获得服务器平台
	 * 
	 * @return 服务器平台
	 */
	public String getServer_platform() {
		return server_platform;
	}

	/**
	 * 服务器关闭事件
	 */
	protected abstract void stop();

	/**
	 * 服务器关闭线程
	 * 
	 * @Title: Server.java
	 * @Package com.igame.framework.net.server
	 * @Author Allen allen.ime@gmail.com
	 * @Date 2014年10月21日 下午2:21:42
	 * @Description: TODO 描述
	 * @Version V1.0
	 */
	private class CloseByExit implements Runnable {

		/**
		 * 服务器名字
		 */
		private String server_name;

		public CloseByExit(String server_name) {
			this.server_name = server_name;
		}

		@Override
		public void run() {
			// 执行关闭事件
			stop();
			log.info(this.server_name + " Stop!");
		}

	}

}
