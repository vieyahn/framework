package com.igame.framework.net.constant;

import io.netty.util.AttributeKey;

/**
 * @Title: SessionKey.java
 * @Package com.igame.framework.net
 * @Author Allen allen.ime@gmail.com  
 * @Date 2014年9月12日 下午1:29:31
 * @Description: 会话绑定变量key值
 * @Version V1.0
 */
public class SessionKey {
	// 连接类型
	public static final AttributeKey<LinkType> LINK_TYPE = AttributeKey.valueOf("LINKTYPE");
	// 连接用户的token
	public static final AttributeKey<String> TOKEN = AttributeKey.valueOf("TOKEN");
	//用户id
	public static final AttributeKey<Long> USERID = AttributeKey.valueOf("USERID");
	//创建链接时间
	public static final AttributeKey<Long> CONNECT_TIME = AttributeKey.valueOf("CONNECT_TIME");
}
