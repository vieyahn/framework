package com.igame.framework.cache;

public interface IGameAdpter<K, V> {

	public boolean removeAndUpdateToDB(K key, V value);

	public boolean updateToDB(K key, V value);

	public boolean insertToDB(K key, V value);

}
