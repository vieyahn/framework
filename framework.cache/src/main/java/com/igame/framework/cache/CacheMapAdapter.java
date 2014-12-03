package com.igame.framework.cache;

import java.util.List;
import java.util.Map;
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

public class CacheMapAdapter<K, CK, V extends CacheEntity<CK>> implements IGameMapAdpter<K, CK, V>, CacheListener<K, CacheMapEntity<CK, V>> {
	private static final Logger log = LoggerFactory.getLogger(CacheMapAdapter.class);

	// expireAfterAccess(long, TimeUnit) 这个方法是根据某个键值对最后一次访问之后多少时间后移除
	// 　expireAfterWrite(long, TimeUnit) 这个方法是根据某个键值对被创建或值被替换后多少时间移除
	private PersistentLoader<CK, V> persistentLoader;
	private final LoadingCache<K, CacheMapEntity<CK, V>> cache;

	public void setPersistentLoader(PersistentLoader<CK, V> persistentLoader) {
		this.persistentLoader = persistentLoader;
	}

	public CacheMapAdapter() {
		this(50000, 2 * 24 * 60 * 60);
	}

	public CacheMapAdapter(int capacity, int expireSeconds) {
		cache = CacheBuilder.newBuilder().maximumSize(capacity).expireAfterAccess(expireSeconds, TimeUnit.SECONDS).removalListener(new RemovalListener<K, CacheMapEntity<CK, V>>() {
			@Override
			public void onRemoval(RemovalNotification<K, CacheMapEntity<CK, V>> removalNotification) {
				notifyElementEvicted(removalNotification.getKey(), removalNotification.getValue());
			}
		}).build(new CacheLoader<K, CacheMapEntity<CK, V>>() {
			@Override
			public CacheMapEntity<CK, V> load(K key) throws Exception {
				List<V> list = persistentLoader.loadListFromDB(key);
				CacheMapEntity<CK, V> cacheMapEntity = new CacheMapEntity<CK, V>(list);
				return cacheMapEntity;
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
	public V getOrLoad(K key, CK childKey) {
		V value = null;
		try {
			CacheMapEntity<CK, V> cacheMapEntity = cache.get(key);
			if (cacheMapEntity != null) {
				value = cacheMapEntity.get(childKey);
			}
		} catch (ExecutionException e) {
			log.error("CacheAdpter --> get error ", e);
		}
		return value;
	}

	public Map<CK, V> getOrLoad(K key) {
		Map<CK, V> value = null;
		try {
			CacheMapEntity<CK, V> cacheMapEntity = cache.get(key);
			if (cacheMapEntity != null) {
				value = cacheMapEntity.getAll();
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
	public ConcurrentMap<K, CacheMapEntity<CK, V>> getAll() {
		return cache.asMap();
	}

	/**
	 * 更新到数据库
	 * 
	 * @param key
	 * @param value
	 */
	public boolean updateToDB(K key, CK childKey, V value) {
		// TODO 如果存在对象重新初始化的情况下，需要更新缓存
		// cache.get(key).put(childKey, value);
		return persistentLoader.updateToDB(value);
	}

	public boolean insertToDB(K key, CK childKey, V value) {
		try {
			cache.get(key).put(childKey, value);
		} catch (ExecutionException e) {
			log.error("CacheAdpter --> insertToDB error ", e);
		}
		return persistentLoader.insertToDB(value);
	}

	/**
	 * 移除并且从数据库删除
	 */
	public boolean removeAndUpdateToDB(K key, CK childKey, V value) {
		try {
			cache.get(key).remove(childKey);
		} catch (ExecutionException e) {
			log.error("CacheAdpter --> removeAndUpdateToDB error ", e);
		}
		return persistentLoader.deleteToDB(value);
	}

	@Override
	public void notifyElementEvicted(K key, CacheMapEntity<CK, V> value) {

	}

	/**
	 * 移除指定key对应的缓存
	 * 
	 * @param key
	 */
	public void remove(K key) {
		cache.invalidate(key);
	}

	/**
	 * 清空缓存
	 */
	public void clear() {
		cache.invalidateAll();
	}

	/**
	 * 清空过期数据
	 */
	public void cleanUp() {
		cache.cleanUp();
	}

}
