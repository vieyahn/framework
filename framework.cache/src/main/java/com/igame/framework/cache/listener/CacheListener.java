package com.igame.framework.cache.listener;

/**
 * @ClassName: CacheListener   
 * @Author: Allen allen.ime@gmail.com  
 * @Date: 2014年12月5日 下午12:10:20  
 * @Description: LRU缓存监听器
 * @param <K>
 * @param <V>
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
