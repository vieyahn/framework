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
	/**
	 * session id
	 */
	private long id;

	private Channel ioSession;

	// 缓存消息队列(他人操作下发的消息)TODO 线程操作同步
	// public UserRespMessageQueue messges = new UserRespMessageQueue();

	// TODO 1. How to register Netty channel to Session? 2. how to close session
	// when channel close?
	// private Notifier notifier; // 通知/事物驱动

	public static Session getInstance(Channel channel) {
		return new Session(channel);
	}

	// 初始化session
	private Session(Channel channel) {
		this.id = System.currentTimeMillis();// channel.id
		this.ioSession = channel;
		ioSession.attr(SessionKey.CONNECT_TIME).set(id);
	}

	/**
	 * 重连绑定
	 */
	public boolean bind(Channel channel, String token) {
		Long userId = attr(SessionKey.USERID).get();
		if (token == null || userId == null) {
			return false;
		}
		if (this.ioSession != null && this.ioSession.isActive()) {
			unbind();
			ioSession.close();
		}
		this.ioSession = channel;
		this.id = System.currentTimeMillis();// channel.id
		ioSession.attr(SessionKey.CONNECT_TIME).set(id);
		ioSession.attr(SessionKey.TOKEN).set(token);
		ioSession.attr(SessionKey.USERID).set(userId);
		return true;
	}

	/**
	 * 登录成功绑定
	 */
	public void bind(String token, long userId) {
		// 初始化管道内信息
		attr(SessionKey.USERID).set(userId);
		ioSession.attr(SessionKey.TOKEN).set(token);
		ioSession.attr(SessionKey.USERID).set(userId);
	}

	/**
	 * 是否登录
	 */
	public boolean isLogin() {
		if (ioSession == null || !ioSession.isActive()) {
			return false;
		}
		if (ioSession.attr(SessionKey.USERID).get() != null) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 取消用户绑定
	 */
	public void unbind() {// 注销管道，此后就算关闭，由于没有绑定userid，断线也不会处理
		if (ioSession != null && ioSession.isActive()) {
			ioSession.attr(SessionKey.TOKEN).remove();
			ioSession.attr(SessionKey.USERID).remove();
			this.id = 0;
			ioSession.close();
		}
		ioSession = null;
	}

	public Channel getIoSession() {
		return ioSession;
	}

	public long getUserId() {
		return attr(SessionKey.USERID).get();
	}

	// public void setUserId(long userId) {
	// // this.ioSession.attr(SessionKey.USERID).set(userId);
	// this.attr(SessionKey.USERID).set(userId);
	// }

	public String getToken() {
		return attr(SessionKey.TOKEN).get();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	// public void setToken(String token) {
	// attr(SessionKey.TOKEN).set(token);
	// }

}
