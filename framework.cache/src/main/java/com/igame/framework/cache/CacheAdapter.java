package com.igame.framework.cache;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.igame.framework.cache.listener.CacheListener;
import com.igame.framework.cache.persistent.PersistentLoader;

public class CacheAdapter<K, V extends CacheEntity<K>> implements IGameAdpter<K, V>, CacheListener<K, V> {
	private static final Logger log = LoggerFactory.getLogger(CacheAdapter.class);

	// expireAfterAccess(long, TimeUnit) 这个方法是根据某个键值对最后一次访问之后多少时间后移除
	// 　expireAfterWrite(long, TimeUnit) 这个方法是根据某个键值对被创建或值被替换后多少时间移除
	protected PersistentLoader<K, V> persistentLoader;
	private final LoadingCache<K, V> cache;

	public void setPersistentLoader(PersistentLoader<K, V> persistentLoader) {
		this.persistentLoader = persistentLoader;
	}

	public CacheAdapter() {
		this(50000, 2 * 24 * 60 * 60);
	}

	public CacheAdapter(int capacity, int expireSeconds) {
		cache = CacheBuilder.newBuilder().maximumSize(capacity).expireAfterAccess(expireSeconds, TimeUnit.SECONDS).removalListener(new RemovalListener<K, V>() {
			@Override
			public void onRemoval(RemovalNotification<K, V> removalNotification) {
				notifyElementEvicted(removalNotification.getKey(), removalNotification.getValue());
			}
		}).build(new CacheLoader<K, V>() {
			@Override
			public V load(K key) throws Exception {
				return persistentLoader.loadFromDB(key);
			}
		});
	}

	/**
	 * 缓存数量
	 * 
	 * @return
	 */
	public long size() {
		return cache.size();
	}

	/**
	 * 是否为空
	 * 
	 * @return
	 */
	public boolean isEmpty() {
		return size() == 0 ? true : false;
	}

	/**
	 * 加载数据，如果缓存没有将从数据库加载
	 * 
	 * @param key
	 * @return
	 */
	protected V getOrLoad(K key) {
		V value = null;
		try {
			value = cache.get(key);
			if (value != null && !value.active()) {
				return null;
			}
		} catch (ExecutionException e) {
			log.error("CacheAdpter --> get error ", e);
		}
		return value;
	}

	/**
	 * 以map结构得到缓存
	 * 
	 * @return
	 */
	protected ConcurrentMap<K, V> getAll() {
		return cache.asMap();
	}

	/**
	 * 更新到数据库
	 * 
	 * @param key
	 * @param value
	 */
	@Override
	public boolean updateToDB(K key, V value) {
		return persistentLoader.updateToDB(value);
	}

	@Override
	public boolean insertToDB(K key, V value) {
		cache.put(key, value);
		return persistentLoader.insertToDB(value);
	}

	/**
	 * 移除并且从数据库删除
	 */
	@Override
	public boolean removeAndUpdateToDB(K key, V value) {
		remove(key);
		return persistentLoader.deleteToDB(value);
	}

	@Override
	public void notifyElementEvicted(K key, V value) {

	}

	/**
	 * 移除指定key对应的缓存
	 * 
	 * @param key
	 */
	protected void remove(K key) {
		cache.invalidate(key);
	}

	/**
	 * 清空缓存
	 */
	protected void clear() {
		cache.invalidateAll();
	}

	/**
	 * 清空过期数据
	 */
	protected void cleanUp() {
		cache.cleanUp();
	}

}
