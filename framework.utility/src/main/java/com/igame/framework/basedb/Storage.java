package com.igame.framework.basedb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

import com.igame.framework.basedb.annotation.Conf;

/**
 * @Title: Storage.java
 * @Package com.igame.framework.basedb
 * @Author Allen allen.ime@gmail.com
 * @Date 2014年9月29日 下午6:03:02
 * @Description: 基础数据仓库
 * @Version V1.0
 */
public class Storage<V> {
	private static final Logger LOGGER = LoggerFactory.getLogger(Storage.class);
	/**
	 * 基础数据的类对象
	 */
	private Class<V> clazz;
	/**
	 * 基础数据映射集合Map<唯一标识, 基础数据>
	 */
	private Map<Object, V> dataTable = new HashMap<Object, V>();
	/**
	 * 索引数据映射集合Map<索引字段值域的组合键名, 唯一标识>
	 */
	private Map<String, List<V>> indexTable = new HashMap<String, List<V>>();
	private ResourceReader reader;

	/**
	 * 构造一个基础数据仓库 p
	 * 
	 * @param clazz
	 * @param resourceLocation
	 * @param applicationContext
	 */
	public Storage(Class<V> clazz, ResourceReader reader) {
		this.clazz = clazz;
		this.reader = reader;
	}

	/**
	 * 基础数据仓库重新加载
	 */
	public synchronized void reload() {
		Conf conf = clazz.getAnnotation(Conf.class);
		String path = conf.path();
		Iterator<V> it = reader.read(path, clazz);
		if (it == null)
			return;

		Getter identifier = GetterBuilder.createIdGetter(clazz);
		Map<String, IndexBuilder.IndexVisitor> indexVisitors = IndexBuilder.createIndexVisitors(clazz);

		Map<Object, V> dataTable_copy = new HashMap<Object, V>();
		Map<String, List<V>> indexTable_copy = new HashMap<String, List<V>>();

		while (it.hasNext()) {
			V obj = it.next();
			if ((obj instanceof InitializeBean)) {
				try {
					((InitializeBean) obj).afterPropertiesSet();
				} catch (Exception e) {
					FormattingTuple message = MessageFormatter.format("基础数据[{}] 属性设置后处理出错!", clazz.getName());
					LOGGER.error(message.getMessage(), e);
				}
			}
			Object key = identifier.getValue(obj);
			dataTable_copy.put(key, obj);

			for (IndexBuilder.IndexVisitor indexVisitor : indexVisitors.values()) {
				String indexKey = indexVisitor.getIndexKey(obj);
				List<V> idList = indexTable_copy.get(indexKey);
				if (idList == null) {
					idList = new ArrayList<>();
					indexTable_copy.put(indexKey, idList);
				}
				idList.add(obj);
			}

		}

		indexTable.clear();
		dataTable.clear();

		dataTable.putAll(dataTable_copy);
		indexTable.putAll(indexTable_copy);
		LOGGER.info("完成加载  {} 基础数据...", clazz.getName());
	}

	public Collection<V> getAll() {
		return dataTable.values();
	}

	public V get(Object key) {
		return dataTable.get(key);
	}

	public List<V> getByIndex(String indxName, Object... args) {
		String indexKey = KeyBuilder.buildIndexKey(clazz, indxName, args);
		return indexTable.get(indexKey);
	}

}
