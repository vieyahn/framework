package com.igame.framework.basedb;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.igame.framework.basedb.annotation.Conf;
import com.igame.framework.basedb.util.Class2SqlUtil;
import com.igame.framework.util.common.PackageUtility;

/**  
 * @ClassName: BaseConfData   
 * @Author: Allen allen.ime@gmail.com  
 * @Date: 2014年12月5日 上午11:56:30  
 * @Description: 基本配置数据容器
 */
public class BaseConfData {
	private static final Logger logger = LoggerFactory.getLogger(BaseConfData.class);
	private DataSource dataSource;
	private String path;
	private Map<String, ResourceReader> readers = new HashMap<>();

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setPath(String path) {
		this.path = path;
	}

	private static Map<Class<?>, Storage<?>> data = new HashMap<Class<?>, Storage<?>>();

	public void init() throws Exception {

		Collection<Class<?>> classes = PackageUtility.scanPackages(path);
		if (classes != null && !classes.isEmpty()) {
			for (Class<?> clazz : classes) {
				Conf conf = clazz.getAnnotation(Conf.class);
				if (conf != null) {
					String type = conf.type();
					Collection<?> values = load(clazz, type);
					if (conf.isDB()) {
						toDb(clazz, values);
					}
				}
			}
		}
	}

	public void toDb(Class<?> confClass, Collection<?> values) throws Exception {
		Connection conn = null;
		long start = System.currentTimeMillis();
		try {
			// 创建数据库表
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
		} catch (Exception e) {
			logger.error("BaseConfData 获取数据库连接失败  ", e);
		}
		Statement st = null;
		try {
			st = conn.createStatement();
			// 创建删出表
			String createSql = Class2SqlUtil.getCreateTableSql(confClass);
			st.execute(createSql);
			// 加载到数据库
			String insertSql = Class2SqlUtil.getInsertTableSql(confClass, values);
			st.execute(insertSql);
			conn.commit();
		} catch (Exception e) {
			throw e;
		} finally {
			if (st != null) { // 关闭声明
				try {
					st.close();
				} catch (SQLException e) {
					logger.error("BaseConfData 关闭声明  ", e);
				}
			}
			if (conn != null) { // 关闭连接对象
				try {
					conn.close();
				} catch (SQLException e) {
					logger.error("BaseConfData 关闭连接对象  ", e);
				}
			}
			long end = System.currentTimeMillis();
			logger.debug("{}数据库操作时间 {}", confClass.getSimpleName(), end - start);
		}

	}

	public Collection<?> load(Class<?> clazz, String type) {
		// TODO 根据类型那个获取reader
		ResourceReader reader = readers.get(type);
		Storage<?> storage = new Storage<>(clazz, reader);
		storage.reload();
		data.put(clazz, storage);
		return storage.getAll();
	}

	@SuppressWarnings("unchecked")
	public static <V> V getKey(Class<V> clazz, Object key) {
		Object storageObj = data.get(clazz);
		if (storageObj == null)
			return null;
		Storage<V> storage = (Storage<V>) storageObj;
		V obj = storage.get(key);
		return obj;
	}

	@SuppressWarnings("unchecked")
	public static <V> List<V> getKeyByIndex(Class<?> clazz, String indexName, Object... args) {
		Object storageObj = data.get(clazz);
		if (storageObj == null)
			return null;
		Storage<V> storage = (Storage<V>) storageObj;
		List<V> objs = storage.getByIndex(indexName, args);
		return objs;
	}

	public void setReaders(Map<String, ResourceReader> readers) {
		this.readers = readers;
	}

	public Map<String, ResourceReader> getReaders() {
		return readers;
	}

}
