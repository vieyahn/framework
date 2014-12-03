package com.igame.framework.cache;

public interface IGameMapAdpter<K, CK, V> {

	public boolean removeAndUpdateToDB(K key, CK childKey, V value);

	public boolean updateToDB(K key,CK childKey, V value);

	public boolean insertToDB(K key, CK childKey, V value);

}
