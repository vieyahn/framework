package com.igame.framework.net.session;

import io.netty.channel.Channel;

import java.security.NoSuchAlgorithmException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.igame.framework.net.exception.ReLoginException;
import com.igame.framework.util.common.SecretUtil;

/**
 * @ClassName: SessionManager
 * @Author Allen allen.ime@gmail.com
 * @Date 2014年3月10日 下午3:22:24
 * @Description: session 管理器
 */
public class SessionManager {
	private static final Logger logger = LoggerFactory.getLogger(SessionManager.class);

	/**
	 * 初始化一个sesson
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param channel
	 * @return
	 */
	public static Session getInstance(Channel channel) {
		return Session.getInstance(channel);
	}

	/**
	 * 初始化session
	 * 
	 * @param session
	 * @param userID
	 * @return
	 */
	public static Session add(Session session, long userID) {
		String token = new StringBuffer().append(userID).append("_").append(System.currentTimeMillis()).toString();
		try {
			token = SecretUtil.md5Encryption(token);
		} catch (NoSuchAlgorithmException e) {
			logger.error("Session init error ", e);
		}

		// 如果存在oldSession cache会处理
		// Session oldSession = SessionCache.get(userID);
		// if (oldSession != null) {
		// oldSession.unbind();
		// }

		session.bind(token, userID);
		SessionCache.put(userID, session);
		return session;
	}

	/**
	 * 重连
	 * 
	 * @param token
	 * @param channel
	 * @return
	 */
	public static Session reconnect(String token, final Channel channel) {
		logger.debug("============= SessionManager reconnect  重连 ====== ");
		Session session = SessionCache.get(token);
		if (session != null) {
			boolean flag = session.bind(channel, token);
			if (!flag) {
				throw new ReLoginException("user.reconnet.error");
			}
			return session;
		} else {
			throw new ReLoginException("user.reconnet.error");
		}
	}

	/**
	 * 销毁session
	 * 
	 * @param userId
	 */
	public static void destory(long userId) {
		SessionCache.remove(userId);
	}

	public static Map<Long, Session> getAll() {
		return SessionCache.getAll();
	}

	public static Session getSession(String token) {
		return SessionCache.get(token);
	}

	public static Session getSession(long userID) {
		return SessionCache.get(userID);
	}

}
