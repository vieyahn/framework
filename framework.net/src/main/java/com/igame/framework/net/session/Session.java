package com.igame.framework.net.session;

import io.netty.channel.Channel;
import io.netty.util.DefaultAttributeMap;

import java.io.Serializable;

import com.igame.framework.net.constant.SessionKey;

/**
 * @Title: Session.java
 * @Author Allen allen.ime@gmail.com
 * @Date 2014年9月12日 下午1:28:35
 * @Description: 会话缓存
 */
public class Session extends DefaultAttributeMap implements Serializable {
	private static final long serialVersionUID = 1L;
	private Channel ioSession;

	// 缓存消息队列(他人操作下发的消息)TODO 线程操作同步
	// public UserRespMessageQueue messges = new UserRespMessageQueue();

	// TODO 1. How to register Netty channel to Session? 2. how to close session
	// when channel close?
	// private Notifier notifier; // 通知/事物驱动

	// TODO 重连有待完善
	public Session(Channel channel) {
		this.ioSession = channel;
		ioSession.attr(SessionKey.CONNECT_TIME).set(System.currentTimeMillis());
	}

	public void bind(Channel channel) {
		this.ioSession = channel;
		ioSession.attr(SessionKey.CONNECT_TIME).set(System.currentTimeMillis());
		bind();
	}

	public void bind() {
		// 初始化管道内信息
		ioSession.attr(SessionKey.TOKEN).set(getToken());
		ioSession.attr(SessionKey.USERID).set(getUserId());
	}

	public boolean isLogin() {
		if (ioSession.attr(SessionKey.USERID).get() != null) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 重新初始化session
	 * 
	 * @param channel
	 */
	// public void reInit(Channel channel) {
	// this.ioSession = channel;
	// ioSession.attr(SessionKey.CONNECT_TIME).set(System.currentTimeMillis());
	// String token = getToken();
	// ioSession.attr(SessionKey.TOKEN).set(token);
	// }

	/**
	 * 取消用户绑定
	 */
	public void unbind() {
		if (ioSession != null && ioSession.isActive()) {
			ioSession.attr(SessionKey.TOKEN).remove();
			ioSession.attr(SessionKey.USERID).remove();
			ioSession.close();
		}
		ioSession = null;
	}

	public Channel getIoSession() {
		return ioSession;
	}

	// public void setIoSession(Channel ioSession) {
	// this.ioSession = ioSession;
	// }

	public long getUserId() {
		// Attribute<Long> attribute = attr(SessionKey.USERID);
		// if (attribute != null)
		return attr(SessionKey.USERID).get();
		// else
		// return 0;
	}

	public void setUserId(long userId) {
		// this.ioSession.attr(SessionKey.USERID).set(userId);
		this.attr(SessionKey.USERID).set(userId);
	}

	public String getToken() {
		return attr(SessionKey.TOKEN).get();
	}

	public void setToken(String token) {
		attr(SessionKey.TOKEN).set(token);
		// if (ioSession != null) {
		// ioSession.attr(SessionKey.TOKEN).set(token);
		// }
	}

}
