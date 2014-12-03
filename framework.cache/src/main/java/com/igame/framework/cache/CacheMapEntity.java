package com.igame.framework.cache;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CacheMapEntity<K, V extends CacheEntity<K>> {

	private ConcurrentHashMap<K, V> entities = new ConcurrentHashMap<>();

	public CacheMapEntity(List<V> entitys) {
		if (entitys != null && !entitys.isEmpty()) {
			for (V val : entitys) {
				if (val.active())
					entities.put(val.getPk(), val);
			}
		}
	}

	public V get(K key) {
		return entities.get(key);
	}

	public Map<K, V> getAll() {
		return entities;
	}

	public void put(K key, V value) {
		entities.put(key, value);
	}

	public void remove(K key) {
		entities.remove(key);
	}

}
