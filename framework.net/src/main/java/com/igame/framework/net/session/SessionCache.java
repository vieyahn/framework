package com.igame.framework.net.session;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

/**
 * @Title: SessionCache.java
 * @Author Allen allen.ime@gmail.com
 * @Date 2014年9月12日 下午1:28:51
 * @Description: channel session 映射关系缓存
 */
public final class SessionCache {
	// expireAfterAccess(long, TimeUnit) 这个方法是根据某个键值对最后一次访问之后多少时间后移除
	// 　expireAfterWrite(long, TimeUnit) 这个方法是根据某个键值对被创建或值被替换后多少时间移除
	private static int maximumSize = 50000;

	private static final ConcurrentMap<String, Session> token2Session = new ConcurrentHashMap<String, Session>();

	// 玩家id对应session键值缓存
	private static final LoadingCache<Long, Session> cache = CacheBuilder.newBuilder().maximumSize(maximumSize).expireAfterAccess(2, TimeUnit.HOURS)
			.removalListener(new RemovalListener<Long, Session>() {
				@Override
				public void onRemoval(RemovalNotification<Long, Session> rn) {
					String token = rn.getValue().getToken();
					if (token != null)
						token2Session.remove(token);

					rn.getValue().unbind();
				}
			}).build(new CacheLoader<Long, Session>() {
				@Override
				public Session load(Long key) throws Exception {
					return null;
				}
			});

	public static long size() {
		return cache.size();
	}

	public static boolean isEmpty() {
		return size() == 0 ? true : false;
	}

	public static Session get(Long key) {
		Session value = null;
		try {
			value = cache.get(key);
		} catch (Exception e) {
		}
		return value;
	}

	public static Session get(String token) {
		Session session = token2Session.get(token);
		if (session != null) {
			session = get(session.getUserId());
		}
		return session;
	}

	public static ConcurrentMap<Long, Session> getAll() {
		return cache.asMap();
	}

	public static Session put(Long key, Session value) {
		cache.put(key, value);
		token2Session.put(value.getToken(), value);
		return value;
	}

	public static void remove(Long key) {
		cache.invalidate(key);
	}

	public static void clear() {
		cache.invalidateAll();
	}

	public static void cleanUp() {
		cache.cleanUp();
	}

	public static Set<Long> keySet() {
		return cache.asMap().keySet();
	}

	public static Collection<Session> values() {
		return cache.asMap().values();
	}

	public static Set<java.util.Map.Entry<Long, Session>> entrySet() {
		return cache.asMap().entrySet();
	}

}
