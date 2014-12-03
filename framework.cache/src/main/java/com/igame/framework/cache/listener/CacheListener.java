package com.igame.framework.cache.listener;

/**
 * LRU缓存监听器
 * 
 * @author Rjx
 */
public interface CacheListener<K, V> {

	/**
	 * 元素被踢除后的后续处理
	 * 
	 * @param key
	 * @param value
	 */
	public void notifyElementEvicted(K key, V value);
}
