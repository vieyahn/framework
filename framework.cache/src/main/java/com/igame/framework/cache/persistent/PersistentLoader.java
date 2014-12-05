package com.igame.framework.cache.persistent;

import java.util.List;

/**
 * @ClassName: PersistentLoader
 * @Author: Allen allen.ime@gmail.com
 * @Date: 2014年12月5日 下午12:10:00
 * @Description: 持久化加载器 包括加载、删除、更新、插入操作操作
 * @param <K>
 * @param <V>
 */
public interface PersistentLoader<K, V> {

	public List<V> loadListFromDB(Object condition);

	/**
	 * 加载数据
	 * 
	 * @param key
	 * @return
	 */
	public V loadFromDB(K key);

	/**
	 * 删除一个持久化数据
	 * 
	 * @param key
	 * @return
	 */
	public boolean deleteToDB(V value);

	/**
	 * <pre>
	 * 把数据同步到数据库
	 * </pre>
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean updateToDB(V value);

	public boolean insertToDB(V value);
}
