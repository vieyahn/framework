package com.igame.framework.net.core;

import io.netty.channel.Channel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Title: NetSender.java
 * @Package com.igame.framework.net.core
 * @Author Allen allen.ime@gmail.com
 * @Date 2014年10月21日 下午2:18:10
 * @Description: 消息发送器
 * @Version V1.0
 */
public class NetSender {
	public static final Logger logger = LoggerFactory.getLogger(NetSender.class);

	/**
	 * 消息发送
	 * 
	 * @param channel
	 * @param response
	 */
	public static void sendMessage(Channel channel, Object response) {
		channel.writeAndFlush(response);
		logger.debug(" ================== NetSender 发送下行 消息成功 ==================== ");
	}

}
