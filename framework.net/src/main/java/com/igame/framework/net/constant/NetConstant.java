package com.igame.framework.net.constant;

import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.util.AttributeKey;

/**
 * @ClassName: NetConstant
 * @Author: Allen allen.ime@gmail.com
 * @Date: 2014年12月2日 下午5:32:58
 * @Description: 网络常量
 */
public class NetConstant {

	/**
	 * @Fields websocket HANDSHAKER
	 */
	public static final AttributeKey<WebSocketServerHandshaker> HANDSHAKER_ATTR_KEY = AttributeKey.valueOf(WebSocketServerHandshaker.class.getName() + ".HANDSHAKER");

}
